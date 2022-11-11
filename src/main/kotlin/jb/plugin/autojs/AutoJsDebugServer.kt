package jb.plugin.autojs

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.*

class AutoJsDebugServer {
//    //http服务是否已启动
//    private var isHttpServerRunning = false
//    private var host: String? = null
//    private var port = 0 //端口号
//    private var server: NettyApplicationEngine? = null
//
//    //多个设备
//    private val devices = mutableListOf<IDevice>()
//
//    constructor(port: Int = 9317) {
//        this.port = port
//    }
//
//    companion object {
//        fun getInstance() = Helper.instance
//    }
//
//    private object Helper {
//        val instance = AutoJsDebugServer()
//    }
//
//    //通过id查找已连接的设备
//    fun getDeviceById(id: String): IDevice? {
//        return this.devices.find { it.id == id }
//    }
//
//    // TODO:
//    private fun newDevice() {
//    }
//
//    fun adbShell() {
//
//    }
//
//    fun getHostPort(): String {
//        return "http://$host:$port"
//    }
//
//    fun start() {
//        this.host = Utils.localIPAddress
//        if (host == "null") {
////            println("获取本机IP失败")
//            throw Exception("获取本机IP失败")
//        }
////    while (Utils.isLocalPortUsing(port)) {
////        println("端口$port 被占用,切换端口到 ${++port}")
////    }
//
//        this.server = embeddedServer(Netty, port, host = "0.0.0.0") {
//            configureSockets()
//            configureRouting()
//        }
//        try {
//            this.server!!.start(wait = false)//不阻塞
//            println("启动服务 http://$host:$port")
//        } catch (e: Exception) {
//            println(e.toString())
//            if (e.toString() == "java.net.BindException: Address already in use: bind") {
//                println("端口$this.port 被占用,切换端口到 ${++this.port}")
//                this.start()
//            } else {
//                throw Exception("启动失败,端口$this.port 被占用,切换端口到 ${++this.port}")
//            }
//        }
//    }
//
//    //将文件夹下的文件(dist)压缩后发送给手机
//    fun sendProjectCommand(folder: String, command: String) {
//        val startTimestamp = System.currentTimeMillis()
//        this.devices.forEach { device ->
//            val zipBytes = device.getZipBytes()
//            device.sendBytes(zipBytes)
//            device.sendBytesCommand(
//                command, md5 = "", data = BytesCommandData(
//                    id = folder,
//                    name = folder
//                )
//            )
//            DebugLog.log(device.toString() + "发送文件耗时:" + (System.currentTimeMillis() - startTimestamp) / 1000 + "秒")
//        }
//    }
//
//    fun Application.configureRouting() {
//        routing {
//            get("/exec") {
//                val cmd = call.request.queryParameters["cmd"]
//                val path = call.request.queryParameters["path"]
//                //todo: 沙雕写错了单词
//                call.respondText("this commond is:" + cmd + "-->" + path)
//            }
//        }
//    }
//
//    fun Application.configureSockets() {
//        install(WebSockets) {
//            pingPeriod = Duration.ofSeconds(15)
//            timeout = Duration.ofSeconds(15)
//            maxFrameSize = Long.MAX_VALUE
//            masking = false
//        }
//
//        routing {
//            val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
//            webSocket("/") { // websocketSession
//                val thisConnection = Connection(this)
//                connections += thisConnection
//                println(this.call.request.local.host)
//                println(this.call.request.local.remoteHost)
////                val origin = this.call.request.origin
//                println(this.call.mutableOriginConnectionPoint.remoteHost)
//                for (frame in incoming) {
//                    if (frame is Frame.Text) {
//                        val text = frame.readText()
//                        connections.first().session.outgoing.send(Frame.Text("卧槽"))
//                        outgoing.send(Frame.Text("YOU SAID: $text"))
//                        if (text.equals("bye", ignoreCase = true)) {
//                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
//                        }
//                    }
//                }
//            }
//        }
//    }
}