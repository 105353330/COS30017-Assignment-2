package com.example.assignment_2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** One line in the basket: an item and how many days are wanted. */
@Parcelize
data class BookingItem(
    val resourceId: Int,
    val name: String,
    val costPerDay: Int,
    val availableDays: Int,
    val units: Int = 1,            // days wanted
) : Parcelable {
    // Worked out each time it is read, not stored, so it always matches the days chosen.
    val cost: Int get() = costPerDay * units
}

/** Sent from MainActivity to BookingActivity when the user taps Book. */
@Parcelize
data class BookingRequest(
    val items: List<BookingItem>,
    val totalCost: Int,
    val credits: Int,              // balance in dollars at the time of booking
) : Parcelable

/** Sent back from BookingActivity to MainActivity when the user taps Confirm. */
@Parcelize
data class BookingResult(
    val confirmed: Boolean,
    val guestName: String = "",
    val items: List<BookingItem> = emptyList(),
    val totalCost: Int = 0
) : Parcelable
