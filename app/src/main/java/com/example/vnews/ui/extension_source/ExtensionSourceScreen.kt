package com.example.vnews.ui.extension_source

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.vnews.data.local.entity.RepositoryEntity
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionSourceScreen(
    extensionSourceViewModel: ExtensionSourceViewModel
) {
    val repositories by extensionSourceViewModel.repositories.collectAsState()
    val isLoading by extensionSourceViewModel.isLoading.collectAsState()
    val error by extensionSourceViewModel.error.collectAsState()
    val successMessage by extensionSourceViewModel.successMessage.collectAsState()
    var showRepoDialog by remember { mutableStateOf(false) }
    var newRepoUrl by remember { mutableStateOf("") }
    var newRepoName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repositories") },
                actions = {
                    IconButton(onClick = { showRepoDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Repository")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(repositories) { repository ->
                    RepositoryItem(
                        repository = repository,
                        onDelete = { extensionSourceViewModel.deleteRepository(it) },
                        viewModel = extensionSourceViewModel
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            error?.let { errorMessage ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(errorMessage)
                }
            }

            successMessage?.let { message ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(message)
                }

                // Clear the success message after 3 seconds
                LaunchedEffect(message) {
                    delay(3000)
                    extensionSourceViewModel.clearSuccessMessage()
                }
            }
        }

        // Add New Repo Dialog
        if (showRepoDialog) {
            Dialog(onDismissRequest = { showRepoDialog = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        "Add Repository",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    OutlinedTextField(
                        value = newRepoName,
                        onValueChange = { newRepoName = it },
                        label = { Text("Repository Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    OutlinedTextField(
                        value = newRepoUrl,
                        onValueChange = { newRepoUrl = it },
                        label = { Text("Repository URL") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showRepoDialog = false }) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                extensionSourceViewModel.addRepository(newRepoName, newRepoUrl)
                                newRepoName = ""
                                newRepoUrl = ""
                                showRepoDialog = false
                            }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoryItem(
    repository: RepositoryEntity,
    onDelete: (RepositoryEntity) -> Unit,
    viewModel: ExtensionSourceViewModel
) {
    var showEditSheet by remember { mutableStateOf(false) }
    var editRepoName by remember { mutableStateOf(repository.sourceName) }
    var editRepoUrl by remember { mutableStateOf(repository.source) }

    // Edit Repository Dialog
    if (showEditSheet) {
        Dialog(onDismissRequest = { showEditSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    "Edit Repository",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                OutlinedTextField(
                    value = editRepoName,
                    onValueChange = { editRepoName = it },
                    label = { Text("Repository Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                OutlinedTextField(
                    value = editRepoUrl,
                    onValueChange = { editRepoUrl = it },
                    label = { Text("Repository URL") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showEditSheet = false }) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            viewModel.updateRepository(repository, editRepoName, editRepoUrl)
                            showEditSheet = false
                        }
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (!repository.isDefault) {
                            editRepoName = repository.sourceName
                            editRepoUrl = repository.source
                            showEditSheet = true
                        }
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repository.sourceName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = repository.source,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!repository.isDefault) {
                IconButton(onClick = { onDelete(repository) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Repository"
                    )
                }
            } else {
                Text("")
            }
        }
    }
} 