package jb.plugin.autojs.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import javax.swing.Action


class Toast(private val dialogContent: String) : DialogWrapper(true) {
    init {
        title = "提示"
        setOKButtonText("收到")
        init()
    }

    override fun createCenterPanel() = panel {
        row {
            label(dialogContent)
        }
    }

    override fun createActions(): Array<Action> {
//        return super.createActions()
        return arrayOf(okAction)
    }

    override fun createLeftSideActions(): Array<Action> {
        return super.createLeftSideActions()
    }
}