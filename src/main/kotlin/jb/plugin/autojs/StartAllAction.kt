package jb.plugin.autojs

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service

class StartAllAction : AnAction() {
    //    private val server = AutoJsDebugServer.getInstance()
    override fun actionPerformed(e: AnActionEvent) {
        val applicationService = service<AutoJsServer>()
        applicationService.start()
        applicationService.isRunning()
    }
}