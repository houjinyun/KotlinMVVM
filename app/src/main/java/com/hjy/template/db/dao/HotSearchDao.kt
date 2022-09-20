package com.hjy.template.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hjy.template.db.entity.HotSearch
import kotlinx.coroutines.flow.Flow

@Dao
interface HotSearchDao {

    @Query("SELECT * from hot_search")
    fun getAllHotSearchListByFlow(): Flow<List<HotSearch>>

    @Query("SELECT * from hot_search")
    fun getAllHotSearchList(): List<HotSearch>

    @Query("DELETE from hot_search")
    fun deleteAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<HotSearch>)

}