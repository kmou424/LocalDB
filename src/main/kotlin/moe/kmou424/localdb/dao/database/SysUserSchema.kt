package moe.kmou424.localdb.dao.database

import moe.kmou424.sqlite.dao.SQLiteUserTable

data class SysUserSchema(
    override var id: Int = 0,
    override var name: String = "",
    override var password: String = "",
    override var tokenWillExpire: Boolean = false,
    override var token: String? = null,
    override var tokenExpireTime: String? = null
) : SQLiteUserTable()