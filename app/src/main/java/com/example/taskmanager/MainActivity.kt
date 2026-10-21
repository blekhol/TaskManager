package com.example.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class Feladat(
    val id: Int,
    val name: String,
    val category: String,
    val priority: Int,
    var done: Boolean
)

val prioritasok = arrayOf("alacsony", "közepes", "magas")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                TaskListScreen()
            }
        }
    }
}

@Composable
fun TaskListScreen() {
    val context = LocalContext.current

    var nextId: Int by remember {
        mutableStateOf(1)
    }

    var name: String by remember {
        mutableStateOf("")
    }

    var category: String by remember {
        mutableStateOf("")
    }

    var priority: String by remember {
        mutableStateOf("")
    }

    var nameError: Boolean by remember {
        mutableStateOf(false)
    }

    var categoryError: Boolean by remember {
        mutableStateOf(false)
    }

    var priorityError: Boolean by remember {
        mutableStateOf(false)
    }

    var priorityRangeError: Boolean by remember {
        mutableStateOf(false)
    }

    var selectedFilter by remember { mutableStateOf("Összes") }

    val tasks = remember {
        mutableStateListOf<Feladat>()
    }

    val filteredTasks = when (selectedFilter) {
        "Aktív" -> tasks.filter { !it.done }
        "Elkészült" -> tasks.filter { it.done }
        else -> tasks
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Feladatkezelő",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Szervezd meg a napi teendőidet!",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = { Text("Feladat: ") },
            supportingText = {
                if (nameError)
                    Text(
                        "A feladat megadása kötelező!",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
                categoryError = false
            },
            label = { Text("Kategória") },
            supportingText = {
                if (categoryError)
                    Text(
                        "A kategória megadása kötelező!",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = priority,
            onValueChange = {
                priority = it
                priorityError = false
                priorityRangeError = false
            },
            label = { Text("Prioritás") },
            supportingText = {
                if (priorityError) {
                    Text(
                        "A prioritás megadása kötelező!",
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (priorityRangeError) {
                    Text(
                        "A prioritásnak 1 és 3 közötti számnak kell lennie",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Button(
            onClick = {
                nameError = name.isBlank()
                categoryError = category.isBlank()
                priorityError = priority.isBlank()

                val priorityInt = priority.trim().toIntOrNull()
                if (priorityInt == null || priorityInt !in 1..3) {
                    priorityRangeError = true
                }

                if (nameError || categoryError || priorityError || priorityRangeError) {
                    Toast.makeText(
                        context, "Ellenőrizd a megadott adatokat!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val newTask = Feladat(
                        id = nextId,
                        name = name.trim(),
                        category = category.trim(),
                        priority = priorityInt!!,
                        done = false
                    )
                    tasks.add(newTask)
                    nextId++
                    Toast.makeText(
                        context,
                        "Feladat sikeresen hozzáadva!",
                        Toast.LENGTH_SHORT
                    ).show()
                    name = ""
                    category = ""
                    priority = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hozzáadás")
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Összes", "Aktív", "Elkészült").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        if (filteredTasks.isEmpty()) {
            Text(
                when (selectedFilter) {
                    "Aktív" -> "Nincsenek aktív feladatok"
                    "Elkészült" -> "Nincsenek elkészült feladatok"
                    else -> "Nincsenek feladatok felvéve a rendszerben"
                },
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filteredTasks,
                    key = { task -> task.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onDelete = {
                            tasks.remove(task)
                        },
                        onEdit = { editedTask ->
                            val index = tasks.indexOfFirst { it.id == editedTask.id }
                            if (index != -1) tasks[index] = editedTask
                        },
                        onToggle = { editedTask ->
                            val index = tasks.indexOfFirst { it.id == editedTask.id }
                            if (index != -1) tasks[index] = editedTask
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Feladat,
    onDelete: () -> Unit,
    onEdit: (Feladat) -> Unit,
    onToggle: (Feladat) -> Unit
) {
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showToggleDoneDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }
    var editPriority by remember { mutableStateOf("") }

    var editNameError by remember { mutableStateOf(false) }
    var editCategoryError by remember { mutableStateOf(false) }
    var editPriorityError by remember { mutableStateOf(false) }
    var editPriorityRangeError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskAvatar(task.priority)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prioritasok[task.priority - 1] + " prioritás",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (task.done) "Kész" else "Nincs kész"
                )
            }
            IconButton(
                onClick = { showToggleDoneDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.done_icon),
                    contentDescription = "Feladat kész/nincs kész"
                )
            }
            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "Feladat törlése"
                )
            }
            IconButton(
                onClick = {
                    showEditDialog = true
                    editName = task.name
                    editCategory = task.category
                    editPriority = task.priority.toString()
                    editNameError = false
                    editCategoryError = false
                    editPriorityError = false
                    editPriorityRangeError = false
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "Feladat szerkesztése"
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Feladat törlése") },
            text = { Text("Biztosan törölni szeretnéd a(z) ${task.name} című feladatot?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) { Text("Törlés") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Mégsem") }
            }
        )
    }

    if (showToggleDoneDialog) {
        AlertDialog(
            onDismissRequest = { showToggleDoneDialog = false },
            title = { Text("Feladat kész státusz váltása") },
            text = { Text("Biztosan váltani szeretnéd?") },
            confirmButton = {
                TextButton(onClick = {
                    showToggleDoneDialog = false
                    val editedTask = task.copy(done = !task.done)
                    onToggle(editedTask)
                }) { Text("Váltás") }
            },
            dismissButton = {
                TextButton(onClick = { showToggleDoneDialog = false }) { Text("Mégsem") }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Feladat szerkesztése") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            editName = it
                            editNameError = false
                        },
                        label = { Text("Feladat") },
                        supportingText = {
                            if (editNameError)
                                Text(
                                    "A feladat megadása kötelező!",
                                    color = MaterialTheme.colorScheme.error
                                )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editCategory,
                        onValueChange = {
                            editCategory = it
                            editCategoryError = false
                        },
                        label = { Text("Kategória") },
                        supportingText = {
                            if (editCategoryError)
                                Text(
                                    "A kategória megadása kötelező!",
                                    color = MaterialTheme.colorScheme.error
                                )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPriority,
                        onValueChange = {
                            editPriority = it
                            editPriorityError = false
                            editPriorityRangeError = false
                        },
                        label = { Text("Prioritás") },
                        supportingText = {
                            if (editPriorityError)
                                Text(
                                    "A prioritás megadása kötelező!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            else if (editPriorityRangeError) {
                                Text(
                                    "A prioritás 1-3 közötti számnak kell lennie",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editNameError = editName.isBlank()
                        editCategoryError = editCategory.isBlank()
                        editPriorityError = editPriority.isBlank()

                        val priorityInt = editPriority.trim().toIntOrNull()
                        if (priorityInt == null || priorityInt !in 1..3) {
                            editPriorityRangeError = true
                        }

                        if (editNameError || editCategoryError || editPriorityError || editPriorityRangeError) {
                            Toast.makeText(
                                context, "Ellenőrizd a megadott adatokat!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val editedTask = task.copy(
                                name = editName.trim(),
                                category = editCategory.trim(),
                                priority = priorityInt!!
                            )
                            onEdit(editedTask)
                            showEditDialog = false
                        }
                    }
                ) { Text("Mentés") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Mégsem") }
            }
        )
    }
}

@Composable
fun TaskAvatar(priority: Int) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}