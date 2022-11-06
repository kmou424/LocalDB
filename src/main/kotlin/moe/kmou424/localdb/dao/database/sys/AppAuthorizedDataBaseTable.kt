package moe.kmou424.localdb.dao.database.sys

import moe.kmou424.sqlite.dao.SQLiteTable

data class AppAuthorizedDataBaseTable(
    override var id: Int = 0,
    var databaseKey: String = "",
    var databaseName: String = ""
) : SQLiteTable()
