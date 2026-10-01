package com.example.assignment_2.model

import androidx.annotation.DrawableRes

const val MAX_UNITS = 7            // days per booking: 1..7
const val MAX_BOOKING_COST = 400   // credits per booking
const val START_CREDITS = 1000

/** One item that can be hired: a keg or a piece of keg equipment. */
data class Resource(
    val id: Int,
    val name: String,
    val category: String,          // type, e.g. keg or equipment
    val year: Int,
    val rating: Int,               // 1 to 5
    val availableDays: Int,        // most days it can be hired for
    val costPerDay: Int,           // credits
    val description: String,
    val location: String,
    val sizeLitres: Int?,          // litres; can be null for items that are not kegs
    @DrawableRes val imageRes: Int,
    val isAvailable: Boolean = true    // false once booked; true again if cancelled
)

enum class BookingStatus { ACTIVE, CANCELLED }

/** One confirmed hire of one item. Stays in history even after it is cancelled. */
data class Booking(
    val id: Int,
    val resourceId: Int,           // which item was booked
    val resourceName: String,
    val units: Int,                // days hired
    val totalCost: Int,
    val guestName: String,
    val status: BookingStatus = BookingStatus.ACTIVE
)

// An enum keeps the sort choices to a fixed list, so an invalid sort can't be picked.
// Each choice carries the label shown in the Browse sort menu.
enum class SortOption(val label: String) {
    NONE("None"),
    RATING("Rating (high to low)"),
    YEAR("Year (newest first)"),
    PRICE("Price (low to high)")
}
