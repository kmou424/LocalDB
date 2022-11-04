package moe.kmou424

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.*

@FixMethodOrder(MethodSorters.JVM)
class AfterTest {
    @Test
    fun exitTest() {
        ApplicationTest.AppHome.run {
            if (exists() && isDirectory) {
                renameTo(ApplicationTest.AppHomeTestResult)
            }
        }
        ApplicationTest.AppHomeBak.run {
            if (exists() && isDirectory) {
                renameTo(ApplicationTest.AppHome)
            }
        }
    }
}