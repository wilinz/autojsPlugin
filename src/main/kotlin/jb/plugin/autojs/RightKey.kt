package jb.plugin.autojs

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VfsUtilCore
import jb.plugin.autojs.Utils.Companion.computeMd5
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class RightKey : AnAction() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun actionPerformed(e: AnActionEvent) {
        val applicationService = service<AutoJsServer>()
        /**
         * 从Action中得到一个虚拟文件
         */
        val virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE)
//        if (virtualFile!!.isDirectory) {
//            virtualFile = virtualFile.parent
//        }
        val parentFile = virtualFile!!.parent
        println(parentFile.path)

        val byteStream = ByteArrayOutputStream()//这个用来最后获取压缩后的字节流
        val outputStream = ZipOutputStream(byteStream)

        //遍历文件夹
        VfsUtilCore.iterateChildrenRecursively(virtualFile, null) { virtualFile ->
            if (virtualFile.isDirectory) {
                return@iterateChildrenRecursively true
            }
            val byteArr = virtualFile.contentsToByteArray()
            val zipName =
                StringUtils.substringAfter(virtualFile.path, parentFile.path + "/")//压缩文件命名 (例dist\xx\yy\zz.txt)
//            println("zipName $zipName")
            outputStream.putNextEntry(ZipEntry(zipName))//思就是我下面io操作(写入)都是在z这个文件条目下进行的。
            outputStream.write(byteArr)
            outputStream.closeEntry() //代表要结束当前条目的写入
//            digest.update(byteArr) //更新摘要,计算md5,因为客户端那边是整体计算的,所以这里不用
            return@iterateChildrenRecursively true
        }
        outputStream.close()
        //压缩好文件,然后就可以发送给设备了
        GlobalScope.launch {
            val bs = byteStream.toByteArray()
            val md5 = computeMd5(bs)
            println("MD5:${md5}")
            applicationService.sendBytes(bs)
            applicationService.sendBytesCommand(
                "save_project",
                md5,
                CommandData(
                    id = virtualFile.name,
                    name = virtualFile.name,
                )
            )
        }

    }
}