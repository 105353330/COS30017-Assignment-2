package com.example.assignment_2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.assignment_2.model.Resource

/** Shows money as "$120" in the theme's main colour. Used on the card, basket, history, top bar and booking screen. */
@Composable
fun PriceTag(credits: Int, modifier: Modifier = Modifier, suffix: String = "") {
    Text(
        text = "\$$credits$suffix",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

/** A large heading at the top of a screen. Used on the basket, history and booking screens. */
@Composable
fun SectionHeading(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleLarge, modifier = modifier.padding(vertical = 8.dp))
}

/** A small rounded label: theme colours for good news, error colours for bad. Used on the card and history rows. */
@Composable
fun StatusBadge(text: String, positive: Boolean, modifier: Modifier = Modifier) {
    val bg = if (positive) MaterialTheme.colorScheme.primaryContainer
             else MaterialTheme.colorScheme.errorContainer
    val fg = if (positive) MaterialTheme.colorScheme.onPrimaryContainer
             else MaterialTheme.colorScheme.onErrorContainer
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium)
    }
}

/** Shows a 1–5 rating as five stars, filled up to the rating. */
@Composable
fun RatingStars(rating: Int) {
    // Screen readers read the whole row as one sentence instead of "star, star, star...".
    Row(Modifier.semantics { contentDescription = "Rated $rating out of 5" }) {
        repeat(5) { i ->
            Icon(
                Icons.Default.Star, contentDescription = null,
                tint = if (i < rating) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

/** One item as a card: image, details, rating, price and an Add button. Used on the Browse screen. */
@Composable
fun ResourceCard(resource: Resource, inBasket: Boolean, onAdd: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Image(
                painter = painterResource(resource.imageRes),
                contentDescription = resource.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(resource.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                StatusBadge("Available", positive = true)
            }
            Text("${resource.category} | ${resource.year} | ${resource.location}")
            // Only show the size when the item has one (taps and the cooling system don't).
            val size = resource.sizeLitres?.let { " | $it L" } ?: ""
            Text("${resource.availableDays} days available$size")
            RatingStars(resource.rating)
            Text(resource.description, style = MaterialTheme.typography.bodyMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                PriceTag(resource.costPerDay, suffix = " / day")
                Button(onClick = onAdd, enabled = !inBasket) {
                    Text(if (inBasket) "In basket" else "Add to basket")
                }
            }
        }
    }
}
