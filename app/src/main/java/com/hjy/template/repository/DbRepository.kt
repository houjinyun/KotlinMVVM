package com.hjy.template.repository

import android.content.Context
import com.hjy.template.bean.HotSearchBean
import com.hjy.template.bean.Knowledge
import com.hjy.template.bean.KnowledgeTreeBody
import com.hjy.template.db.MyDatabase
import com.hjy.template.db.entity.HotSearch
import com.hjy.template.db.entity.KnowledgeTable
import com.hjy.template.db.entity.KnowledgeTree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip

class DbRepository(val context: Context): BaseRepository() {

    private val hotSearchDao = MyDatabase.getDatabase(context).hotSearchDao()
    private val knowledgeDao = MyDatabase.getDatabase(context).knowledgeDao()

    fun deleteAllHotSearchData() {
        hotSearchDao.deleteAll()
    }

    fun insertHotSearch(list: List<HotSearchBean>) {
        var l = list.map { item ->
            HotSearch(item.id, item.link, item.name, item.order, item.visible)
        }
        hotSearchDao.insertAll(l)
    }

    fun getHotSearchList(): List<HotSearchBean> {
        return hotSearchDao.getAllHotSearchList().map { item ->
            HotSearchBean(item.id, item.link, item.name, item.order, item.visible)
        }
    }

    fun getAllHotSearchListByFlow() = hotSearchDao.getAllHotSearchListByFlow().map {
        it.map { item ->
            HotSearchBean(item.id, item.link, item.name, item.order, item.visible)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * 删除所有知识体系相关的数据
     */
    fun deleteAllKnowledgeData() {
        knowledgeDao.deleteAllKnowledgeTree()
        knowledgeDao.deleteAllKnowledgeList()
    }

    fun insetKnowledgeData(list: List<KnowledgeTreeBody>) {
        val knowledgeList = mutableListOf<KnowledgeTable>()
        val treeList = list.map { tree ->
            val childrenList = tree.children
            childrenList.forEach { child ->
                knowledgeList.add(
                    KnowledgeTable(
                        child.courseId,
                        child.id,
                        tree.id,
                        child.name,
                        child.order,
                        child.parentChapterId,
                        child.visible
                    )
                )
            }
            return@map KnowledgeTree(
                tree.courseId,
                tree.id,
                tree.name,
                tree.order,
                tree.parentChapterId,
                tree.visible
            )
        }
        knowledgeDao.insertKnowledgeTree(treeList)
        knowledgeDao.insertKnowledgeList(knowledgeList)
    }

    fun getAllKnowledgeDataByFlow(): Flow<List<KnowledgeTreeBody>> =
        knowledgeDao.getKnowledgeTreeByFlow()
            .zip(knowledgeDao.getSubKnowledgeListByFlow()) { l1, l2 ->
                val list = mutableListOf<KnowledgeTreeBody>()
                val map = mutableMapOf<Int, MutableList<Knowledge>>()
                for (knowledge in l2) {
                    val l = map[knowledge.parentId] ?: mutableListOf()
                    l.add(
                        Knowledge(
                            listOf(),
                            knowledge.courseId,
                            knowledge.id,
                            knowledge.name,
                            knowledge.order,
                            knowledge.parentChapterId,
                            knowledge.visible
                        )
                    )
                    map[knowledge.parentId] = l
                }
                for (tree in l1) {
                    val t1 = KnowledgeTreeBody(
                        map[tree.id] ?: mutableListOf(),
                        tree.courseId,
                        tree.id,
                        tree.name,
                        tree.order,
                        tree.parentChapterId,
                        tree.visible,
                        false
                    )
                    list.add(t1)
                }
                list
            }.flowOn(Dispatchers.IO)

}