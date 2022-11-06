package moe.kmou424.common.utils

object TypeUtil {
    inline fun <reified T, reified R> getPairMapper(p: Any?): Pair<T, R>? {
        if (p !is Pair<*, *>)
            return null
        val first = p.first
        val second = p.second
        if (first is T && second is R) {
            return Pair(first, second)
        }
        return null
    }
}