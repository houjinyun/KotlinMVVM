package com.hjy.template.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hot_search")
data class HotSearch(
    @PrimaryKey
    @ColumnInfo(name = "hot_id")
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)