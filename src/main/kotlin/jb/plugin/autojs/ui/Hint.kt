package jb.plugin.autojs.ui

import com.intellij.openapi.ui.popup.JBPopupFactory

class Hint {
    companion object {
        fun hint() {
            //这个展示,是纵向排列的
            JBPopupFactory.getInstance().createConfirmation(
                "标题",
                "qd",
                "取消",
                Runnable {
                    println("qd")
                }, {
                    println("取消")
                },
                0
            ).showInFocusCenter()
        }
    }
}