package com.example.vnews.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vnews.data.local.entity.RepositoryEntity
import com.example.vnews.data.repository.ExtRepoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val repoRepository: ExtRepoRepository
) : ViewModel() {
    private val _repositories = MutableStateFlow<List<RepositoryEntity>>(emptyList())
    val repositories: StateFlow<List<RepositoryEntity>> = _repositories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeRepositories()
    }

    private fun observeRepositories() {
        viewModelScope.launch {
            repoRepository.getRepos().collect {
                _repositories.value = it
            }
        }
    }

    fun addRepository(repoName: String, sourceUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repoRepository.addRepo(repoName,sourceUrl)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to add repository: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRepository(repository: RepositoryEntity) {
        viewModelScope.launch {
            try {
                repoRepository.deleteRepo(repository)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to delete repository: ${e.message}"
            }
        }
    }
} 