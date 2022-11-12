package jb.plugin.autojs

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import icons.AutoJsIcons

class ShowAllAction : AnAction() {
    var e: AnActionEvent? = null

    //    private val server = AutoJsDebugServer.getInstance()
    override fun actionPerformed(e: AnActionEvent) {
        this.e = e
        val applicationService = service<AutoJsServer>()
        applicationService.start()
        applicationService.isRunning()
//        e.presentation.icon = AutoJsIcons.AutoJs
    }

    fun updateIcon(isRunning:Boolean) {
        if (e != null) {
            e?.presentation?.icon = if (isRunning) AutoJsIcons.AutoJs else AutoJsIcons.AutoJsDisable
        }
    }

    companion object {
        const val ACTION_ID = "ShowAllServer"
    }
}