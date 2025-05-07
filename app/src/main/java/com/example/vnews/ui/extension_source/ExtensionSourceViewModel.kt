package com.example.vnews.ui.extension_source

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vnews.data.local.entity.RepositoryEntity
import com.example.vnews.data.repository.ExtensionSourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionSourceViewModel @Inject constructor(
    private val repoRepository: ExtensionSourceRepository
) : ViewModel() {
    private val _repositories = MutableStateFlow<List<RepositoryEntity>>(emptyList())
    val repositories: StateFlow<List<RepositoryEntity>> = _repositories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

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
                // Check for duplicate URL
                val existingRepos = _repositories.value
                val duplicateUrl =
                    existingRepos.any { it.source.equals(sourceUrl, ignoreCase = true) }
                val duplicateName =
                    existingRepos.any { it.sourceName.equals(repoName, ignoreCase = true) }

                when {
                    duplicateUrl -> {
                        _error.value = "A repository with this URL already exists"
                    }

                    duplicateName -> {
                        _error.value = "A repository with this name already exists"
                    }

                    else -> {
                        repoRepository.addRepo(repoName, sourceUrl)
                        _error.value = null
                        _successMessage.value = "Repository added successfully"
                    }
                }
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
                _successMessage.value = "Repository deleted successfully"
            } catch (e: Exception) {
                _error.value = "Failed to delete repository: ${e.message}"
            }
        }
    }

    fun updateRepository(repository: RepositoryEntity, newName: String, newUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Check for duplicate URL and name when updating
                val existingRepos = _repositories.value
                val duplicateUrl = existingRepos.any {
                    it.id != repository.id && it.source.equals(newUrl, ignoreCase = true)
                }
                val duplicateName = existingRepos.any {
                    it.id != repository.id && it.sourceName.equals(newName, ignoreCase = true)
                }

                when {
                    duplicateUrl -> {
                        _error.value = "A repository with this URL already exists"
                    }

                    duplicateName -> {
                        _error.value = "A repository with this name already exists"
                    }

                    else -> {
                        val updatedRepo = repository.copy(sourceName = newName, source = newUrl)
                        repoRepository.updateRepo(updatedRepo)
                        _error.value = null
                        _successMessage.value = "Repository updated successfully"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to update repository: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearError() {
        _error.value = null
    }
} 