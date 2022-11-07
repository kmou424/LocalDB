package moe.kmou424.sqlite.utils

import moe.kmou424.common.utils.SimpleTokenUtil
import moe.kmou424.localdb.dao.AppSQLiteManager
import moe.kmou424.sqlite.entities.SQLiteTable

object TokenUtil {

    fun AppSQLiteManager.getUniqueToken(): String {
        var token = SimpleTokenUtil.generate()

        while (this.sysUsersData[token] != null) {
            token = SimpleTokenUtil.generate()
        }

        return token
    }

}