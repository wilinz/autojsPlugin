package jb.plugin.autojs

import com.intellij.openapi.components.service
import com.intellij.openapi.util.NlsSafe
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicInteger


class Device(
// 输出通道
    private val outgoing: SendChannel<Frame>,// 设备信息
    var info: LinkData,
) {
    var type = "" // 设备类型
    var id = 0 // 设备id

    //    var mWebsocket: ServerSocket? = null // websocket
    var attached = false // 是否已经连接,@deprecated 理论上Device对象创建后就已经连接了
    //    private val incoming: ReceiveChannel<Frame> // 输入通道

    companion object {
        val lastId = AtomicInteger(0)
    }

    private val applicationService: AutoJsServer = service<AutoJsServer>()

    init {
        this.id = lastId.getAndIncrement()
    }

    //向autojs App发送关闭连接的信号
    suspend fun close() {
        val messageId = Utils.genMessageId()
        val closeMessage = Json.encodeToString(MessageData(messageId, "close", "close"))
        outgoing.send(Frame.Text(closeMessage))
        outgoing.close()
    }
    // 提供给 Java 使用的封装函数，Java 代码可以直接使用

    fun close4Java(): Unit = runBlocking {// invoke suspend fun
        close()
    }


    suspend fun send(type: String, data: Any) {
        val messageId = Utils.genMessageId()
        val message = Json.encodeToString(MessageData(messageId, type, data))
        print("发送数据 ->$data")
        this.outgoing.send(Frame.Text(message))
    }

    suspend fun sendBytes(bytes: ByteArray) {
        try {
            val x = Frame.Binary(true, bytes)

            outgoing.send(Frame.Binary(true, bytes))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun sendBytesCommand(command: String, md5: String, data: CommandData) {
        val messageId = Utils.genMessageId()
        data.command = command
        print("计算出的md5 ->$md5")
        val message = Json.encodeToString(
            MessageData(
                messageId,
                "bytes_command",
                md5 = md5,
                data = data
            )
        )
        this.outgoing.send(Frame.Text(message))
    }

    suspend fun sendCommand(data: ICommand) {
        this.send("command", data)
    }

    override fun toString(): String {
        return "Device(name='${info.deviceName}',ip='${info.ip}', type='${type}', id='$id', attached=$attached)"
    }

    fun print(msg: String) {
        applicationService.printLog("📱设备${info.deviceName}-${info.ip}📱: $msg")
    }

    //读取数据,可以调用projectObserver.diff()来获取压缩文件流
    fun getZipBytes(): ByteArray {
        return ByteArray(0)// TODO:  
    }

    //处理App发送过来的消息
    suspend fun onMessage(type: String, text: String) {
        when (type) {
            "ping" -> {//心跳
                //{"data":1666454660154,"type":"ping"}
                //{"data":1666454660154,"type":"pong"}
                this.outgoing.send(Frame.Text(text.replace("ping", "pong")))
            }

            "log" -> {
                val log = Json.decodeFromString<Req<LogData>>(text)
                this.print(log.data.log)
            }

            else -> {
                this.print("未知消息类型 ->$type-$text")
            }
        }
    }

    //向autojs App发送压缩好的项目文件
    suspend fun sendProject6Zip(byteStream: ByteArrayOutputStream, id: @NlsSafe String, name: @NlsSafe String) {
        val bs = byteStream.toByteArray()
        val md5 = Utils.computeMd5(bs)
        print("MD5:${md5}")
        this.sendBytes(bs)
        this.sendBytesCommand(
            "save_project",
            md5,
            CommandData(
                id,
                name,
            )
        )
    }

    //向autojs App发送指令,停止所有脚本
    //    stopAll() {
    //        server.sendCommand('stopAll');
    //    }
    suspend fun stopAllScript() {
        val messageId = Utils.genMessageId()
        val message = Json.encodeToString(MessageData(messageId, "command", StopAllReq("stopAll")))
        this.outgoing.send(Frame.Text(message))
//        this.send("command",StopAllReq("stopAll"))//无法使用,因为Any抹掉了类型,会报错kotlinx.serialization.SerializationException: Serializer for class 'Any' is not found.
    }

    fun stopAllScript4Java(): Unit = runBlocking {// invoke suspend fun
        stopAllScript()
    }

    // 向autojs App发送指令,停止指定脚本
    //        server.sendCommand('stop', {
    //            'id': vscode.window.activeTextEditor.document.fileName,
    //        });
    suspend fun stopScript() {
        this.sendCommand(StopReq("stop", ""))
    }

    fun stopScript4Java(): Unit = runBlocking {// invoke suspend fun
        stopScript()
    }
}