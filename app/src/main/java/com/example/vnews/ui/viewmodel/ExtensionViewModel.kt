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
import com.example.vnews.utils.DateTimeUtil
import com.prof18.rssparser.RssParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
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

    private val _selectedExtension = MutableStateFlow<ExtensionEntity?>(null)
    val selectedExtension = _selectedExtension.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _extensionArticles = MutableStateFlow<List<RssItem>>(emptyList())
    val extensionArticles = _extensionArticles.asStateFlow()

    private val rssParser = RssParser()

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
            observeInstalled()
        }
    }

    fun deleteExtension(ext: ExtensionEntity) {
        viewModelScope.launch {
            extRepo.deleteInstalledExt(ext)
            observeInstalled()
        }
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }

    fun setSelectedExtension(extension: ExtensionEntity) {
        _selectedExtension.value = extension
        fetchExtensionArticles(extension)
    }

    private fun fetchExtensionArticles(extension: ExtensionEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    try {
                        val channel = rssParser.getRssChannel(extension.source)
                        
                        val items = channel.items.map { item ->
                            RssItem(
                                title = Jsoup.parse(item.title ?: "").text()
                                    .replace("&apos;", "'"),
                                summary = Jsoup.parse(item.description ?: "").text(),
                                source = item.link ?: "",
                                pubTime = DateTimeUtil.parseDateToUnix(item.pubDate ?: ""),
                                thumbnail = item.image ?: "",
                                extensionName = extension.name,
                                extensionIcon = extension.icon
                            )
                        }
                        
                        val sortedItems = items.sortedByDescending { it.pubTime }
                        _extensionArticles.value = sortedItems
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _extensionArticles.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _extensionArticles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class ExtensionUiState {
    object Loading : ExtensionUiState()
    data class Success(val extensions: List<RssSource>) : ExtensionUiState()
    data class Error(val message: String) : ExtensionUiState()
}