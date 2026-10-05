package com.example.assignment_2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignment_2.model.BookingStatus
import com.example.assignment_2.ui.components.PriceTag
import com.example.assignment_2.ui.components.SectionHeading
import com.example.assignment_2.ui.components.StatusBadge
import com.example.assignment_2.viewmodel.BookingViewModel

/** History: every booking with its status. Active bookings can be cancelled for a refund. */
@Composable
fun HistoryScreen(vm: BookingViewModel, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsStateWithLifecycle()
    Column(modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("My bookings")
        if (state.bookings.isEmpty()) Text("No bookings yet.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.bookings, key = { it.id }) { b ->
                val active = b.status == BookingStatus.ACTIVE
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(b.resourceName, style = MaterialTheme.typography.titleMedium)
                            Text("${b.units} day(s) | ${b.guestName}")
                            PriceTag(b.totalCost)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            StatusBadge(if (active) "Active" else "Cancelled", positive = active)
                            // Cancelled bookings stay listed but can't be cancelled again.
                            if (active) {
                                TextButton(onClick = { vm.cancelBooking(b.id) }) { Text("Cancel") }
                            }
                        }
                    }
                }
            }
        }
    }
}
