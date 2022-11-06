package moe.kmou424.common.utils

object SimpleTokenUtil {
    private const val TokenLen = 32;

    fun generate(tokenLen: Int = TokenLen): String {
        val str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+-_()"
        var token = ""
        for (i in 0 until tokenLen) {
            token += str[(1..str.length).random() - 1]
        }
        return token
    }

}