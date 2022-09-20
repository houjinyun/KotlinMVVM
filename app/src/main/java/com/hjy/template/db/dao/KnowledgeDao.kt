package com.hjy.template.db.dao

import androidx.room.*
import com.hjy.template.db.entity.KnowledgeTable
import com.hjy.template.db.entity.KnowledgeTree
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeDao {

    @Query("SELECT * from knowledge_tree")
    fun getKnowledgeTreeByFlow(): Flow<List<KnowledgeTree>>

    @Query("SELECT * from knowledge")
    fun getSubKnowledgeListByFlow(): Flow<List<KnowledgeTable>>

    @Query("DELETE from knowledge_tree")
    fun deleteAllKnowledgeTree(): Int

    @Query("DELETE from knowledge")
    fun deleteAllKnowledgeList(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKnowledgeTree(list: List<KnowledgeTree>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKnowledgeList(list: List<KnowledgeTable>)

}