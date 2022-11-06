package moe.kmou424.localdb.entities.database.sys

import moe.kmou424.sqlite.entities.SQLiteTable

data class AppAuthorizedDataBaseTable(
    override var id: Int = 0,
    var databaseKey: String = "",
    var databaseName: String = ""
) : SQLiteTable()
