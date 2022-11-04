package moe.kmou424

import moe.kmou424.ApplicationTest.Companion.AppHome
import moe.kmou424.ApplicationTest.Companion.AppHomeBak
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.*

@FixMethodOrder(MethodSorters.JVM)
class InitTest {
    @Test
    fun startTest() {
        AppHome.run {
            if (exists() && isDirectory) {
                renameTo(AppHomeBak)
            }
        }
    }
}