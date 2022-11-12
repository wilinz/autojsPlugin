package jb.plugin.autojs

import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.impl.coroutineDispatchingContext
import com.intellij.openapi.components.*
import com.intellij.openapi.extensions.PluginId
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import jb.plugin.autojs.ui.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.*

class AutoJsServerImpl : AutoJsServer, AppLifecycleListener {
    private val uiDispatcher get() = AppUIExecutor.onUiThread(ModalityState.any()).coroutineDispatchingContext()

    //http服务是否已启动
    private var isHttpServerRunning = false
    private var host: String? = null
    private var port = 9317 //端口号
    private var server: NettyApplicationEngine? = null

    //    private val devices = mutableListOf<IDevice>()//多个设备
    private val devices = Collections.synchronizedSet<Device?>(LinkedHashSet())//多个设备

    private var pluginVersion: String

    init {
        val plugin = PluginManagerCore.getPlugin(PluginId.getId("jb.plugin.autojs"))
        pluginVersion = plugin?.version ?: "1.109.0"
    }

    //通过id查找已连接的设备
    override fun getDeviceById(id: Int): Device? {
        return this.devices.find { it.id == id }
    }

    override fun getDeviceCount(): Int {
        return this.devices.size
    }

    //获取启动的服务地址
    override fun getHostPort(): String {
        return "http://$host:$port"
    }

    @Synchronized
    override fun start() {
        if (isHttpServerRunning) {
            Toast("程序已经启动在 " + this.getHostPort()).showAndGet()
            println("程序已经启动在 " + this.getHostPort())
            return
        }
        this.host = Utils.localIPAddress
        if (host == "null") {
//            println("获取本机IP失败")
            Toast("获取本机IP失败").showAndGet()
            throw Exception("获取本机IP失败")
        }

        this.server = embeddedServer(Netty, port, host = "0.0.0.0") {
            configureSockets()
            configureRouting()
        }
        try {
            this.server?.environment?.monitor?.subscribe(ApplicationStarted, this::switchStatusStart)
            this.server?.environment?.monitor?.subscribe(ApplicationStopped, this::switchStatusStop)
            this.server!!.start(wait = false)//不阻塞
            Notifications.showInfoNotification(
                "提示",
                "启动服务 http://$host:$port"
            )

        } catch (e: Exception) {
            this.server?.environment?.monitor?.unsubscribe(ApplicationStarted, this::switchStatusStart)
            this.server?.environment?.monitor?.unsubscribe(ApplicationStopped, this::switchStatusStop)
            println(e.toString())
            if (e.toString() == "java.net.BindException: Address already in use: bind") {
                println("端口$this.port 被占用,切换端口到 ${++this.port}")
                this.start()
            } else {
//                this.server?.environment?.monitor?.unsubscribe()
                this.server = null
                throw Exception("启动失败,端口$this.port 被占用,切换端口到 ${++this.port}")
            }
        }
    }

    @Synchronized
    override fun stop() {
        this.server?.stop()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun switchStatusStop(ApplicationStopped: Application) {
        this.isHttpServerRunning = false
//        println("变更程序状态" + this.isHttpServerRunning)
        updateIcon()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun switchStatusStart(ApplicationStopped: Application) {
        this.isHttpServerRunning = true
//        println("变更程序状态" + this.isHttpServerRunning)
        updateIcon()
    }


    override fun isRunning(): Boolean {
//        println("程序状态" + this.isHttpServerRunning)
        return this.isHttpServerRunning
    }

    //将文件夹下的文件(dist)压缩后发送给手机
    suspend fun sendProjectCommand(folder: String, command: String) {
        val startTimestamp = System.currentTimeMillis()
        this.devices.forEach { device ->
            val zipBytes = device.getZipBytes()
            device.sendBytes(zipBytes)
            device.sendBytesCommand(
                command,
                md5 = "",
                data = CommandData(
                    id = folder,
                    name = folder
                )
            )
            DebugLog.log(device.toString() + "发送文件耗时:" + (System.currentTimeMillis() - startTimestamp) / 1000 + "秒")
        }
    }

    private fun Application.configureRouting() {
        routing {
            get("/exec") {
                val cmd = call.request.queryParameters["cmd"]
                val path = call.request.queryParameters["path"]
                //todo: 沙雕写错了单词
                call.respondText("this commond is:" + cmd + "-->" + path)
            }
        }
    }

    private fun Application.configureSockets() {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(21)//对方10秒ping一次,我们这里设置为21秒给对方两次机会
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            webSocket("/") { // websocketSession
                val ip = this.call.mutableOriginConnectionPoint.remoteHost
                println(ip)
                var device: Device? = null
                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
//                            println("收到来自${ip}的消息:$text")
                            val parseType = Json { ignoreUnknownKeys = true }.decodeFromString<ParseType>(text)
                            if (parseType.type == "hello") {
                                val hello = Json.decodeFromString<Req<LinkData>>(text)
                                hello.data.ip = ip
                                device = Device(outgoing, hello.data)
                                this@AutoJsServerImpl.devices += device
                                var rspText: String
                                if (hello.data.appVersionCode >= 629) {
                                    rspText = Json.encodeToString(
                                        LinkRspUp629(
                                            messageId = Utils.genMessageId(),
                                            data = "ok",
                                            version = pluginVersion,
                                            debug = false,
                                            type = "hello"
                                        )
                                    )
                                } else {
                                    rspText = Json.encodeToString(
                                        LinkRsp(
                                            messageId = Utils.genMessageId(),
                                            data = "连接成功",
                                            debug = false,
                                            type = "hello"
                                        )
                                    )
                                }
                                println("回复设备${device.info.ip}消息:$rspText")
                                outgoing.send(Frame.Text(rspText))
                            } else {
                                if (device == null) {
                                    println("设备尚未完成握手,拒绝接收消息")
                                } else {
                                    device.onMessage(parseType.type, text)
                                }
                            }
                            if (text.equals("bye", ignoreCase = true)) {//这里基本不可能收到这个,autojs App没设计这个功能
                                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                            }
                        } else {
                            println("此条数据非文本数据${frame.readBytes()}")
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose-> ${closeReason.await()}")
                } catch (e: Exception) {
                    println("Exception->" + e.localizedMessage)
                } finally {
                    println("移除设备-> $device!")
                    if (device != null) {
                        this@AutoJsServerImpl.devices -= device
                    }
                }


            }
        }
    }

    //修改导航栏上的autojs图标
    private fun updateIcon() {
        val actionManager = ActionManager.getInstance()
        val action = actionManager.getAction(ShowAllAction.ACTION_ID) as? ShowAllAction ?: return
        action.updateIcon(this.isRunning())
    }

    override fun appClosing() {//这个在appWillBeClosed之前执行
        super.appClosing()
        this.stop()
        println("appClosing.")
    }

    //给所有设备发送Bytes
    override suspend fun sendBytes(bytes: ByteArray) {
        if (this.devices.isEmpty()) {
            GlobalScope.launch(uiDispatcher) {
                Toast("没有设备连接").showAndGet()
            }
            return
        }
        this.devices.forEach {
            it.sendBytes(bytes)
        }
    }

    //给所有设备发送Bytes指令
    override suspend fun sendBytesCommand(command: String, md5: String, data: CommandData) {
        if (this.devices.isEmpty()) {
            GlobalScope.launch(uiDispatcher) {
                Toast("没有设备连接").showAndGet()
            }
            return
        }
        this.devices.forEach {
            it.sendBytesCommand(command, md5, data)
        }
    }
}