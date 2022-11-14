package jb.plugin.autojs.ui

import cn.hutool.extra.qrcode.QrCodeUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.ui.dsl.gridLayout.VerticalAlign
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.UIUtil
import icons.AutoJsIcons
import javax.swing.Action
import javax.swing.ImageIcon
import javax.swing.JTextArea
import javax.swing.text.JTextComponent

class StatusDialog(private val project: Project?) : DialogWrapper(project) {
    private var img = ImageIcon()

    init {
        title = "AutoJs服务信息"
        setOKButtonText("收到")
        setSize(600, 300)
        this.img = createQrcode()
        init()
    }

    override fun createCenterPanel() = panel {
        panel {
            row {
                cell(leftRow()).apply {
                    horizontalAlign(HorizontalAlign.FILL)
                    verticalAlign(VerticalAlign.CENTER)
                }
                cell(rightRow()).apply {
                    horizontalAlign(HorizontalAlign.FILL)
                }
            }
        }

    }

    fun rightRow() = panel {
        row {
            img
        }
    }

    fun leftRow() = panel {
        row {
            panel {
                createQrcode()
            }.horizontalAlign(HorizontalAlign.CENTER)
                .verticalAlign(VerticalAlign.FILL)
            icon(AutoJsIcons.AutoJs)
        }
    }.apply {
        setSize(200, 200)
    }

    fun createQrcode(): ImageIcon {
        val bufferedImage = QrCodeUtil.generate("www.baidu.com", 200, 200)
        val imageIcon = ImageIcon(bufferedImage)
        return imageIcon
    }

    override fun createActions(): Array<Action> {
        return arrayOf()
    }

    //文本控件初始化
    fun textInit(textArea: JTextArea) {
        textArea.apply {
            lineWrap = true
            wrapStyleWord = true
            border = UI.emptyBorder(10)
            minimumSize = JBDimension(0, minHeight(textArea))
        }
    }

    private fun minHeight(textComponent: JTextComponent): Int {
        val borderInsets = textComponent.border.getBorderInsets(textComponent)
        return UIUtil.getLineHeight(textComponent) + borderInsets.top + borderInsets.bottom
    }
}
