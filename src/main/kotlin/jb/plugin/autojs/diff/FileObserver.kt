package jb.plugin.autojs.diff

import java.io.File
import kotlin.io.path.Path


class FileObserver {
    var dir = ""
    var filesMap = mutableMapOf<String, String>()
    var fileFilter: FileFilter? = null

    constructor(dir: String, fileFilter: FileFilter?) {
        this.dir = dir
        this.fileFilter = fileFilter
    }

    fun walk() {
        var changeFiles = mutableListOf<String>()
        changeFiles = this.getFiles(this.dir)
    }

    private fun getFiles(rootPath: String): MutableList<String> {
        val fileTree: FileTreeWalk = File(rootPath).walk()
        val dstFiles = mutableListOf<String>()
        fileTree.maxDepth(1)
            .filter { this.fileFilter?.invoke(null, Path(rootPath, it.name).toString()) ?: true }
            .forEach {
                if (it.isDirectory) {
                    dstFiles.addAll(this.getFiles(it.absolutePath))
                } else if (it.isFile) {
                    dstFiles.add(it.name)
                } else {
                    println("未知类型:" + it.name)
                }
            }
        return dstFiles
    }
}
