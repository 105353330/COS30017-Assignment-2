package com.example.assignment_2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.assignment_2.model.BookingItem
import com.example.assignment_2.model.BookingRequest
import com.example.assignment_2.model.MAX_UNITS
import com.example.assignment_2.model.validateBooking
import com.example.assignment_2.ui.components.PriceTag
import com.example.assignment_2.ui.components.SectionHeading
import com.example.assignment_2.viewmodel.BookingViewModel
import kotlin.math.roundToInt

/** Basket: pick days for each item, see the total and any rule errors, then Book. */
@Composable
fun BasketScreen(
    vm: BookingViewModel,
    onBook: (BookingRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val errors = validateBooking(state.basket, state.credits)
    Column(modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("Your basket")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.basket, key = { it.resourceId }) { item ->
                BasketRow(
                    item = item,
                    onUnitsChange = { vm.setUnits(item.resourceId, it) },
                    onRemove = { vm.removeFromBasket(item.resourceId) }
                )
            }
        }
        HorizontalDivider()
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", style = MaterialTheme.typography.titleMedium)
            PriceTag(state.basketTotal)
        }
        errors.forEach { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))
        // Book stays disabled while any rule is broken, so an invalid booking can't leave this screen.
        Button(
            onClick = { onBook(BookingRequest(state.basket, state.basketTotal, state.credits)) },
            enabled = errors.isEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Book") }
    }
}

/** One basket line: name, Remove button, days slider and cost. */
@Composable
private fun BasketRow(item: BookingItem, onUnitsChange: (Int) -> Unit, onRemove: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onRemove,
                    modifier = Modifier.semantics { contentDescription = "Remove ${item.name}" }
                ) { Text("Remove") }
            }
            Text("Days: ${item.units}")
            // The slider gives a decimal (e.g. 3.0); roundToInt turns it into whole days.
            // steps = notches between the ends, so MAX_UNITS - 2 gives one stop per day.
            Slider(
                value = item.units.toFloat(),
                onValueChange = { onUnitsChange(it.roundToInt()) },
                valueRange = 1f..MAX_UNITS.toFloat(),
                steps = MAX_UNITS - 2
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("\$${item.costPerDay} x ${item.units} days")
                PriceTag(item.cost)
            }
        }
    }
}
