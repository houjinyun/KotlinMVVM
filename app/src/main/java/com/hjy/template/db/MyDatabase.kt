package com.hjy.template.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hjy.template.db.dao.HotSearchDao
import com.hjy.template.db.dao.KnowledgeDao
import com.hjy.template.db.entity.HotSearch
import com.hjy.template.db.entity.KnowledgeTable
import com.hjy.template.db.entity.KnowledgeTree

@Database(entities = [HotSearch::class, KnowledgeTree::class, KnowledgeTable::class], version = 2, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {
            val tmp = INSTANCE
            if (tmp != null) {
                return tmp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_db"
                ).addMigrations(object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                    }
                }).build()
                INSTANCE = instance
                return instance
            }
        }

    }

    abstract fun hotSearchDao(): HotSearchDao

    abstract fun knowledgeDao(): KnowledgeDao

}