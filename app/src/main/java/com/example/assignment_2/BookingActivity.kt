package com.example.assignment_2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import com.example.assignment_2.model.BookingRequest
import com.example.assignment_2.model.BookingResult
import com.example.assignment_2.model.validateBooking
import com.example.assignment_2.model.validateName
import com.example.assignment_2.ui.components.PriceTag
import com.example.assignment_2.ui.components.SectionHeading
import com.example.assignment_2.ui.theme.Assignment_2Theme

/** Second Activity: shows the booking summary, asks for a name, and returns Confirm or Cancel to Main. */
class BookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // This Activity can't see the ViewModel, so everything it needs comes from the Intent.
        val request = IntentCompat.getParcelableExtra(
            intent, BookingContract.EXTRA_REQUEST, BookingRequest::class.java
        )
        if (request == null) { finish(); return }
        setContent {
            Assignment_2Theme {
                BookingScreen(
                    request = request,
                    onConfirm = { name ->
                        val result = BookingResult(true, name, request.items, request.totalCost)
                        setResult(RESULT_OK, Intent().putExtra(BookingContract.EXTRA_RESULT, result))
                        Log.d("BookingActivity", "Confirmed: $result")
                        Toast.makeText(this@BookingActivity, "Booking confirmed", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        Log.d("BookingActivity", "Cancelled")
                        Toast.makeText(this@BookingActivity, "Booking cancelled", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

/** The booking summary screen. Confirm only works when the rules pass and a name is entered. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(request: BookingRequest, onConfirm: (String) -> Unit, onCancel: () -> Unit) {
    // rememberSaveable keeps the typed name when the screen rotates; plain remember would lose it.
    var name by rememberSaveable { mutableStateOf("") }
    var showNameError by rememberSaveable { mutableStateOf(false) }
    val nameError = validateName(name)
    val errors = validateBooking(request.items, request.credits)
    Scaffold(topBar = { TopAppBar(title = { Text("Confirm booking") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeading("Booking summary")
            request.items.forEach { item ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${item.name}: ${item.units} day(s) x \$${item.costPerDay}", Modifier.weight(1f))
                    PriceTag(item.cost)
                }
            }
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleMedium)
                PriceTag(request.totalCost)
            }
            Text("Balance available: \$${request.credits}")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your name") },
                singleLine = true,
                isError = showNameError && nameError != null,
                supportingText = { if (showNameError && nameError != null) Text(nameError) },
                modifier = Modifier.fillMaxWidth()
            )
            errors.forEach { Text(it, color = MaterialTheme.colorScheme.error) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // A blank name shows the error and stays on this screen instead of finishing.
                Button(
                    enabled = errors.isEmpty(),
                    onClick = { if (nameError == null) onConfirm(name.trim()) else showNameError = true }
                ) { Text("Confirm") }
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}
