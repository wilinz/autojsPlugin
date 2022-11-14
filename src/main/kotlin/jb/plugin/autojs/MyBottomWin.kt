package jb.plugin.autojs


import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import jb.plugin.autojs.ui.ToolViewBottom


class MyBottomWin : ToolWindowFactory, Condition<Project?> {
    /**
     * 创建 tool window
     * @param project
     * @param toolWindow
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = ToolViewBottom(toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(myToolWindow.content, "输出控制台", false)
        toolWindow.contentManager.addContent(content)
    }

    /**
     * 控制tool window是否展示
     * @param project
     * @return
     */
    override fun value(project: Project?): Boolean {
        return true
    }
}