package jb.plugin.autojs

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface IWsConn {
    //发送数据
    fun sendUTF(data: Any)

    //发送byte数据
    fun sendBytes(bytes: ByteArray)

    //关闭连接
    fun close()
}

class WsConn {
    fun sendUTF(data: Any) {
        val closeMessageJson = Json.encodeToString(data)

    }
}