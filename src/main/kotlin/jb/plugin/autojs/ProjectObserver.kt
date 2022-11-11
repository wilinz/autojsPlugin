package jb.plugin.autojs

interface IProjectObserver {
    /**
     * @description: 项目变化,请使用getZipBytes获取压缩文件流
     */
    fun diff()

    //获取压缩文件流 记得,需要调用config来过滤不需要压缩的文件
    fun getZipBytes(): ByteArray
}

class ProjectObserver {
    var folder = ""// 项目文件夹

}