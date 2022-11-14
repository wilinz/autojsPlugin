package jb.plugin.autojs

import jb.plugin.autojs.ui.ServerDialogListener

interface AutoJsServer {

    //启动http服务
    fun start()

    //关闭http服务
    fun stop()

    //程序启动的地址
    fun getHostPort(): String

    //程序是否已启动
    fun isRunning(): Boolean

    //通过id查找已连接的设备
    fun getDeviceById(id: Int): Device?

    //通过设备ip查找已连接的设备
    fun getDevice4Ip(ip: String): Device?

    //获取已连接的设备数量
    fun getDeviceCount(): Int

    //获取已连接的设备列表
    fun getDevices(): Set<Device>

    //给所有设备发送Bytes
    suspend fun sendBytes(bytes: ByteArray)

    //给所有设备发送Bytes指令
    suspend fun sendBytesCommand(command: String, md5: String, data: CommandData)

    var listener: ServerDialogListener?

    //绑定更新UI的监听器
    fun addServerDialogListener(listener: ServerDialogListener)

    //解绑更新UI的监听器
    fun removeServerDialogListener()

}