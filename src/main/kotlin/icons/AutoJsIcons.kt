package icons

import com.intellij.ui.IconManager
import javax.swing.Icon

object AutoJsIcons {

    @JvmField
    val AutoJs: Icon = load("/icons/autojs.svg")

    @JvmField
    val AutoJsDisable: Icon = load("/icons/autojs2.svg")

    @JvmField
    val StatusOk: Icon = load("/icons/status_ok.svg")

    @JvmField
    val StatusNo: Icon = load("/icons/status_no.svg")

    @JvmStatic
    fun load(path: String): Icon {
        return IconManager.getInstance().getIcon(path, AutoJsIcons::class.java)
    }
}