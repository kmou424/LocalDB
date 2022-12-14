package moe.kmou424.localdb.entities.database.sys

import moe.kmou424.sqlite.entities.SQLiteTable

data class SysUserTable(
    override var id: Int = 0,
    var name: String = "",
    var password: String = "",
    var tokenWillExpire: Boolean = false,
    var token: String = "",
    var tokenExpireTime: String? = null,
    var applicationKeyOwned: String = ""
) : SQLiteTable()