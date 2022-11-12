package icons

import com.intellij.ui.IconManager
import javax.swing.Icon

object AutoJsIcons {

    @JvmField
    val AutoJs: Icon = load("/icons/autojs.svg")

    @JvmField
    val AutoJsDisable: Icon = load("/icons/autojs2.svg")

    @JvmStatic
    fun load(path: String): Icon {
        return IconManager.getInstance().getIcon(path, AutoJsIcons::class.java)
    }
}