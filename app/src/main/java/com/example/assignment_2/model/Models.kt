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
