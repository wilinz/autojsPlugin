package jb.plugin.autojs.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindow
import jb.plugin.autojs.AutoJsServer
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea


class ToolViewBottom(toolWindow: ToolWindow) : LogListener {
    var content: JPanel? = null
        private set
    private var clean: JButton? = null
    var textArea: JTextArea? = null

    init {
        init()
        clean!!.addActionListener {
            toolWindow.setTitle("清理成功")
            textArea?.text = ""
        }
        val applicationService = service<AutoJsServer>()
        applicationService.addLogListener(this)
    }

    private fun init() {
        clean = JButton("清除")
        content = JPanel()
        content!!.layout = BorderLayout()
        val panel = JPanel()
        content!!.add(panel, BorderLayout.NORTH)
        panel.add(clean)
        val scrollPane = JScrollPane()
        content!!.add(scrollPane, BorderLayout.CENTER)
        textArea = JTextArea()
        textArea!!.document = LimitativeDocument(textArea!!, 1000)
        val x = Font("Serif", 0, 15)
        textArea!!.font = x
        scrollPane.setViewportView(textArea)
    }

    @Synchronized
    override fun appendLog(text: String) {
        textArea?.append(text + "\r\n")
    }
}