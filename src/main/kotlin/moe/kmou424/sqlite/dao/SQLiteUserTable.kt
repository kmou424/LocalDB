package moe.kmou424.sqlite.dao

open class SQLiteUserTable(
    open var id: Int = 0,
    open var name: String = "",
    open var password: String = "",
    open var tokenWillExpire: Boolean = false,
    open var token: String? = null,
    open var tokenExpireTime: String? = null
) : SQLiteTable()