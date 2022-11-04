package moe.kmou424.common.utils

import moe.kmou424.localdb.appConfiguration
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AesUtil {

    fun encrypt(encryptStr: String): String {
        val cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(appConfiguration.encrypt.key.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypt = cipher.doFinal(encryptStr.toByteArray())
        return Base64.getEncoder().encodeToString(encrypt)
    }

    fun decrypt(decryptStr: String): String {
        val cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(appConfiguration.encrypt.key.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decrypt = cipher.doFinal(Base64.getDecoder().decode(decryptStr))
        return String(decrypt)
    }

}