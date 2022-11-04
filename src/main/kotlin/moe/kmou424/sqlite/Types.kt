package moe.kmou424.sqlite

import moe.kmou424.sqlite.enums.ColumnRestrict
import moe.kmou424.sqlite.enums.ColumnType

typealias ColumnMapper = Map<ColumnSummary, List<ColumnRestrict>>
typealias ColumnSummary = Pair<ColumnName, ColumnType>
typealias ColumnName = String