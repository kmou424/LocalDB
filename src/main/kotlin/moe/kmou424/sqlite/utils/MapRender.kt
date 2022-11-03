package moe.kmou424.sqlite.utils

import org.apache.commons.beanutils.BeanUtils

object MapRender {
    fun <T> HashMap<String, Any?>.toTypedObject(creator: Class<T>): T {
        val typedObj = creator.getDeclaredConstructor().newInstance()
        BeanUtils.populate(typedObj, this)
        return typedObj
    }
}