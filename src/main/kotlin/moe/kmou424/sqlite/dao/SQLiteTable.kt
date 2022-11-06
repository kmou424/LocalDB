package moe.kmou424.sqlite.dao

import org.apache.commons.beanutils.BeanMap

open class SQLiteTable(
    open var id: Int = 0
) {
    inline fun <reified T> toPairList(): List<Pair<String, Any?>> {
        return emptyList<Pair<String, Any?>>().toMutableList().also {
            val mBeanMap = BeanMap(this)
            T::class.java.declaredFields.forEach { field ->
                it.add(Pair<String, Any?>(field.name, mBeanMap[field.name]))
            }
        }.toList()
    }
}