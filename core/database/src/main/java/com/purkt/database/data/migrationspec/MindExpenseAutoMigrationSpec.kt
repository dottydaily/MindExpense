package com.purkt.database.data.migrationspec

import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec

object MindExpenseAutoMigrationSpec {
    @RenameTable(fromTableName = "expense", toTableName = "individual_expense")
    class AutoMigrationFromVersion1To2() : AutoMigrationSpec
}