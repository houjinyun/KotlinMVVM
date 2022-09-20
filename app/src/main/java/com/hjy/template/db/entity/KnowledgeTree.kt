package com.hjy.template.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_tree")
data class KnowledgeTree(
    val courseId: Int,

    @PrimaryKey
    @ColumnInfo(name = "knowledge_id")
    val id: Int,

    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
)

@Entity(tableName = "knowledge")
data class KnowledgeTable(
    val courseId: Int,
    @PrimaryKey
    @ColumnInfo(name = "knowledge_id")
    val id: Int,
    val parentId: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val visible: Int
)