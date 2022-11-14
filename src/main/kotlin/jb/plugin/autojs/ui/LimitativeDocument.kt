package jb.plugin.autojs.ui

import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException

import javax.swing.text.JTextComponent

import javax.swing.text.PlainDocument


class LimitativeDocument : PlainDocument {
    private var textComponent: JTextComponent
    private var lineMax = 10

    constructor(tc: JTextComponent, lineMax: Int) {
        textComponent = tc
        this.lineMax = lineMax
    }

    constructor(tc: JTextComponent) {
        textComponent = tc
    }

    @Throws(BadLocationException::class)
    override fun insertString(offset: Int, s: String?, attributeSet: AttributeSet?) {
        val value = textComponent.text
        var overrun = 0
        if (value != null && value.indexOf('\n') >= 0 && value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray().size >= lineMax) {
            overrun = value.indexOf('\n') + 1
            super.remove(0, overrun)
        }
        super.insertString(offset - overrun, s, attributeSet)
    }
}