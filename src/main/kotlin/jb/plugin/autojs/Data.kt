package jb.plugin.autojs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloseData(
    @SerialName("message_id")
    val messageId: String,
    val data: String,
    val debug: Boolean,
    val type: String,
)

@Serializable
data class MessageData<T>(
    @SerialName("message_id")
    val messageId: String,
    val type: String,
    val data: T,
    val debug: Boolean = false,
    val md5: String = "",
)

@Serializable
data class CommandData(
    val id: String,
    val name: String,
    var command: String? = "",
)

@Serializable
data class LogData(
    val log: String,
)

interface Temp {
    var command: String
}

@Serializable
data class Req<T>(
    @SerialName("data")
    val `data`: T,
    @SerialName("type")
    val type: String, // hello
)

@Serializable
data class ParseType(
    @SerialName("type")
    val type: String, // hello
)


@Serializable
data class LinkData(
    @SerialName("app_version")
    val appVersion: String, // 6.2.9
    @SerialName("app_version_code")
    val appVersionCode: Int, // 629
    @SerialName("client_version")
    val clientVersion: Int, // 2
    @SerialName("device_name")
    val deviceName: String, // OnePlus PGP110
    var ip: String = "",//设备Ip
)

//大于等于629的设备连接回复
@Serializable
data class LinkRspUp629(
    @SerialName("message_id")
    val messageId: String,
    val data: String,
    val debug: Boolean,
    val version: String,
    val type: String,
)

@Serializable
data class LinkRsp(
    @SerialName("message_id")
    val messageId: String,
    val data: String,
    val debug: Boolean,
    val type: String,
)