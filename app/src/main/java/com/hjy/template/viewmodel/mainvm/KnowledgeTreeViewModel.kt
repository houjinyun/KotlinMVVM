package com.hjy.template.viewmodel.mainvm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.hjy.template.base.BaseViewModel
import com.hjy.template.bean.Knowledge
import com.hjy.template.bean.KnowledgeTreeBody
import com.hjy.template.global.Constants
import com.hjy.template.global.Resource
import com.hjy.template.repository.KnowledgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class KnowledgeTreeViewModel(app: Application) : BaseViewModel(app) {

    private val knowledgeRepository = KnowledgeRepository()

    private val _treeListFlow = MutableStateFlow(Resource<List<KnowledgeTreeBody>>(null))
    val treeListFlow = _treeListFlow.asStateFlow()
    private val _secondListFlow = MutableStateFlow(Resource<List<Knowledge>>(null))
    val secondListFlow = _secondListFlow.asStateFlow()

    private val _loadingStateFlow = MutableStateFlow(Resource(Constants.STATE_LOADING))
    val loadingStateFlow = _loadingStateFlow.asStateFlow()

    fun getKnowledgeTreeList() {
        viewModelScope.launch {
            knowledgeRepository.getKnowledgeTree()
                .catch {
                    _loadingStateFlow.value = Resource(Constants.STATE_LOAD_ERROR)
                }.collect {
                    if (it.isNullOrEmpty()) {
                        _loadingStateFlow.value = Resource(Constants.STATE_EMPTY)
                    } else {
                        _loadingStateFlow.value = Resource(Constants.STATE_LOAD_SUCCESS)
                    }
                    if (!it.isNullOrEmpty()) {
                        it[0].selected = true
                        _treeListFlow.value = Resource(it)
                        _secondListFlow.value = Resource(it[0].children)
                    }
                }
        }
    }

    fun selectCategory(knowledgeTreeBody: KnowledgeTreeBody) {
        val list = _treeListFlow.value.data
        list?.forEach { item ->
            item.selected = false
        }
        knowledgeTreeBody.selected = true
        _treeListFlow.value = Resource(list)
        _secondListFlow.value = Resource(knowledgeTreeBody.children)
    }

    fun getSelectedKnowledge(): KnowledgeTreeBody? {
        val list = _treeListFlow.value.data
        list?.let {
            for (item in it) {
                if (item.selected) {
                    return item
                }
            }
        }
        return null
    }

}