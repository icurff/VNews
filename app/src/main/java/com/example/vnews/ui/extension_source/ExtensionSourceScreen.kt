package com.example.vnews.ui.extension_source

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.example.vnews.R
import com.example.vnews.data.local.entity.RepositoryEntity
import com.example.vnews.ui.theme.NewsGradient
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
                title = {
                    Text(
                        text = stringResource(R.string.repository),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            brush = Brush.horizontalGradient(
                                NewsGradient
                            )
                        )
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRepoDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Repository",
                    tint = Color.White
                )
            }
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
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { extensionSourceViewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMessage)
                }

                // Clear the error message after 5 seconds
                LaunchedEffect(errorMessage) {
                    delay(5000)
                    extensionSourceViewModel.clearError()
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
            Dialog(onDismissRequest = {
                showRepoDialog = false
                newRepoName = ""
                newRepoUrl = ""
                extensionSourceViewModel.clearError() // Clear any errors when dialog is dismissed
            }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            "Add Repository",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )

                        TextField(
                            value = newRepoName,
                            onValueChange = { newRepoName = it },
                            label = { Text("Repository Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        TextField(
                            value = newRepoUrl,
                            onValueChange = { newRepoUrl = it },
                            label = { Text("Repository URL") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, end = 24.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                showRepoDialog = false
                                newRepoName = ""
                                newRepoUrl = ""
                                extensionSourceViewModel.clearError()
                            }) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    extensionSourceViewModel.addRepository(newRepoName, newRepoUrl)
                                    // Error handling is now in the ViewModel
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }
    }

    // Observe error state to clear fields conditionally
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            // Only clear fields on success
            newRepoName = ""
            newRepoUrl = ""
            showRepoDialog = false
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
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Edit Repository Dialog
    if (showEditSheet) {
        Dialog(onDismissRequest = {
            showEditSheet = false
            viewModel.clearError() // Clear any errors when dialog is dismissed
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        "Edit Repository",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    TextField(
                        value = editRepoName,
                        onValueChange = { editRepoName = it },
                        label = { Text("Repository Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    TextField(
                        value = editRepoUrl,
                        onValueChange = { editRepoUrl = it },
                        label = { Text("Repository URL") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            showEditSheet = false
                            viewModel.clearError()
                        }) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                viewModel.updateRepository(repository, editRepoName, editRepoUrl)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Update")
                        }
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
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repository.sourceName,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = repository.source,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!repository.isDefault) {
                IconButton(onClick = { onDelete(repository) }) {
                    Icon(
                        imageVector = Lucide.Trash2,
                        contentDescription = "Delete extension",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                Text("")
            }
        }
    }

    // Observe error state to close dialog conditionally
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            // Only close dialog on success
            showEditSheet = false
        }
    }
} 