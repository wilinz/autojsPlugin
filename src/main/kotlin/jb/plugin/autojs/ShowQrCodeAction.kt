package jb.plugin.autojs

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.jetbrains.rd.util.string.println

class ShowQrCodeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val applicationService = service<AutoJsServer>()
//        applicationService.stop()
        applicationService.isRunning()
        println(applicationService.getDeviceCount())
        e.place
    }
}