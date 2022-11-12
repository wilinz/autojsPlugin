package jb.plugin.autojs

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service

class ShowQrCodeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val applicationService = service<AutoJsServer>()
//        applicationService.stop()
        applicationService.isRunning()
        println(applicationService.getDeviceCount())
        applicationService.stop()
    }
}