package moe.kmou424.localdb.entities.database.sys

import moe.kmou424.sqlite.entities.SQLiteTable

data class AppApplicationTable(
    override var id: Int = 0,
    var applicationKey: String = "",
    var database: String = ""
) : SQLiteTable()
