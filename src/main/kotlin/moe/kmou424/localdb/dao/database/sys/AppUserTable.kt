package moe.kmou424.localdb.dao.database.sys

import moe.kmou424.sqlite.dao.SQLiteTable

data class AppUserTable(
    override var id: Int = 0,
    var name: String = "",
    var password: String = "",
    var tokenWillExpire: Boolean = false,
    var token: String? = null,
    var tokenExpireTime: String? = null,
    var databaseKeyOwned: String = "",
    var databaseKeyAccessible: String? = null
) : SQLiteTable()