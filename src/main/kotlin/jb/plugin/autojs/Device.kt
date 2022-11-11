package jb.plugin.autojs

import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicInteger


class Device {
    var type = "" // 设备类型
    var id = 0 // 设备id
    var info: LinkData // 设备信息

    //    var mWebsocket: ServerSocket? = null // websocket
    var attached = false // 是否已经连接,@deprecated 理论上Device对象创建后就已经连接了
    private val outgoing: SendChannel<Frame> // 输出通道
//    private val incoming: ReceiveChannel<Frame> // 输入通道

    companion object {
        val lastId = AtomicInteger(0)
    }

    constructor(outgoing: SendChannel<Frame>, info: LinkData) {
        this.outgoing = outgoing
        this.id = lastId.getAndIncrement()
        this.info = info
    }

    //向autojs App发送关闭连接的信号
    suspend fun close() {
        val messageId = Utils.genMessageId()
        val closeMessage = Json.encodeToString(MessageData(messageId, "close", "close"))
        outgoing.send(Frame.Text(closeMessage))
        outgoing.close()
    }

    suspend fun send(type: String, data: Any) {
        val messageId = Utils.genMessageId()
        val message = Json.encodeToString(MessageData(messageId, type, data))
        println("发送数据 ->$data")
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
        println("计算出的md5 ->$md5")
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

    suspend fun sendCommand(command: String, data: Temp) {
        data.command = command
        this.send("command", data)
    }

    override fun toString(): String {
        return "Device(name='${info.deviceName}',ip='${info.ip}', type='${type}', id='$id', attached=$attached)"
    }

    fun print(msg: String) {
        println("📱设备${info.deviceName}-${info.ip}📱: $msg")
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
}