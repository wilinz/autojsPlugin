package jb.plugin.autojs.ui

interface ServerStatus {
    //切换服务状态
    fun switchServerStatus(status: Boolean)

    //刷新设备列表
    fun refreshDeviceList()
}