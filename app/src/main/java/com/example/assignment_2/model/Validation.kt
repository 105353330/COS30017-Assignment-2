package com.example.assignment_2.model

/** Checks every booking rule. Returns a list of broken rules; an empty list means the booking is valid. */
fun validateBooking(items: List<BookingItem>, credits: Int): List<String> {
    val errors = mutableListOf<String>()
    if (items.isEmpty()) errors += "Select at least one item."
    for (item in items) {
        if (item.units !in 1..MAX_UNITS) errors += "${item.name}: choose 1-$MAX_UNITS days."
        if (item.units > item.availableDays) {
            errors += "${item.name}: only ${item.availableDays} days available."
        }
    }
    val total = items.sumOf { it.cost }
    if (total > MAX_BOOKING_COST) errors += "Total \$$total exceeds the \$$MAX_BOOKING_COST limit."
    if (total > credits) errors += "Total \$$total exceeds your balance of \$$credits."
    return errors
}

/** Returns an error message if the name is blank, or null if the name is fine. */
fun validateName(name: String): String? =
    if (name.isBlank()) "Name is required." else null
