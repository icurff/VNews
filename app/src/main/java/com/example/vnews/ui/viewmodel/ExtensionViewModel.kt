package com.example.vnews.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vnews.data.local.entity.ExtensionEntity
import com.example.vnews.data.remote.RepoApiService
import com.example.vnews.data.remote.dto.RssSource
import com.example.vnews.data.remote.dto.toExtensionEntity
import com.example.vnews.data.repository.ExtRepoRepository
import com.example.vnews.data.repository.ExtensionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExtensionViewModel @Inject constructor(
    private val extRepo: ExtensionRepository,
    private val repoRepository: ExtRepoRepository,
    private val repoApiService: RepoApiService
) : ViewModel() {

    private val _installed = MutableStateFlow<List<ExtensionEntity>>(emptyList())
    val installed = _installed.asStateFlow()

    private val _selectedTab = mutableStateOf("Installed")
    val selectedTab: State<String> = _selectedTab

    private val _uiState = MutableStateFlow<ExtensionUiState>(ExtensionUiState.Loading)
    val uiState: StateFlow<ExtensionUiState> = _uiState.asStateFlow()

    private val _allExtension = MutableStateFlow<List<RssSource>>(emptyList())
    val allExtension = _allExtension.asStateFlow()


    init {
        observeInstalled()
        fetchExtensions()
        observeLibrary()
    }

    private fun observeInstalled() {
        viewModelScope.launch {
            extRepo.getInstalledExt().collect { extensions ->
                _installed.value = extensions
            }
        }
    }

    private fun fetchExtensions() {
        viewModelScope.launch {
            try {
                repoRepository.getRepos().collect { repos ->
                    val fetchedExtensions = repos.flatMap { repo ->
                        repoApiService.getExtensionListFromRepo(repo.source)
                    }
                    _allExtension.value = fetchedExtensions
                }
            } catch (e: Exception) {
                _uiState.value = ExtensionUiState.Error("Failed to fetch extensions: ${e.message}")
            }
        }
    }

    private fun observeLibrary() {
        viewModelScope.launch {
            combine(installed, allExtension) { installedList, allExtList ->
                val installedLinks = installedList.map { it.source }
                allExtList.filterNot { installedLinks.contains(it.source) }
            }.collect { availableExtensions ->
                _uiState.value = ExtensionUiState.Success(availableExtensions)
            }
        }
    }

    fun addExtension(rss: RssSource) {
        viewModelScope.launch {
            extRepo.installExtension(rss.toExtensionEntity())
            observeInstalled() // Cập nhật danh sách sau khi thêm
        }
    }

    fun deleteExtension(ext: ExtensionEntity) {
        viewModelScope.launch {
            extRepo.deleteInstalledExt(ext)
            observeInstalled() // Cập nhật danh sách sau khi xóa
        }
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }
}

sealed class ExtensionUiState {
    object Loading : ExtensionUiState()
    data class Success(val extensions: List<RssSource>) : ExtensionUiState()
    data class Error(val message: String) : ExtensionUiState()
}