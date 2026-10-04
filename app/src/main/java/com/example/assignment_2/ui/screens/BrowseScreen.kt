package com.example.assignment_2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignment_2.model.SortOption
import com.example.assignment_2.ui.components.ResourceCard
import com.example.assignment_2.viewmodel.BookingViewModel

/** Browse: search, sort, and step through the available items one at a time. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(vm: BookingViewModel, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsStateWithLifecycle()
    val items = state.visible
    val current = items.getOrNull(state.index)     // null when nothing matches
    var menuOpen by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Column(
        modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.query,
                    onQueryChange = vm::setQuery,
                    // Results already update while typing, so Search just closes the keyboard.
                    onSearch = { focusManager.clearFocus() },
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search by name or type") },
                    trailingIcon = {
                        TextButton(onClick = { focusManager.clearFocus() }) { Text("Search") }
                    }
                )
            },
            expanded = false,
            onExpandedChange = {},
            windowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {}
        Box {
            OutlinedButton(onClick = { menuOpen = true }) { Text("Sort: ${state.sort.label}") }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = { vm.setSort(option); menuOpen = false }
                    )
                }
            }
        }
        if (current == null) {
            Text("No results", Modifier.padding(32.dp))
        } else {
            ResourceCard(
                resource = current,
                inBasket = state.basket.any { it.resourceId == current.id },
                onAdd = { vm.addToBasket(current) }
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = vm::previous) { Text("Previous") }
                Text("${state.index + 1} of ${items.size}")
                Button(onClick = vm::next) { Text("Next") }
            }
        }
    }
}
