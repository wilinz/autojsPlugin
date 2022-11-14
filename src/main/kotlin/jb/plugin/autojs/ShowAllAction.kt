package jb.plugin.autojs

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import icons.AutoJsIcons
import jb.plugin.autojs.ui.ServerDialog

class ShowAllAction : AnAction() {
    var e: AnActionEvent? = null
    private var dialog: ServerDialog? = null


    //    private val server = AutoJsDebugServer.getInstance()
    override fun actionPerformed(e: AnActionEvent) {
        this.e = e
        if (dialog == null) {
            dialog = ServerDialog()
        }
        dialog?.isVisible = true
//        StatusDialog(e.project).show()

//        val applicationService = service<AutoJsServer>()

    }

    fun updateIcon(isRunning: Boolean) {
        if (e != null) {
            e?.presentation?.icon = if (isRunning) AutoJsIcons.AutoJs else AutoJsIcons.AutoJsDisable
        }
    }

    companion object {
        const val ACTION_ID = "ShowAllServer"
        fun getAction(): ShowAllAction? {
            val actionManager = ActionManager.getInstance()
            val action = actionManager.getAction(ACTION_ID) as? ShowAllAction ?: return null
            return action
        }
    }
}