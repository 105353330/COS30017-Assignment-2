package com.example.assignment_2

import com.example.assignment_2.model.BookingItem
import com.example.assignment_2.model.validateBooking
import com.example.assignment_2.model.validateName
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Checks each booking rule in validateBooking() and validateName(). */
class ValidationTest {
    // Helper: one basket line with the given days, daily cost and capacity.
    private fun item(units: Int, cost: Int = 50, days: Int = 7) =
        BookingItem(1, "Keg", cost, days, units)

    @Test fun emptyBasket_isInvalid() =
        assertTrue(validateBooking(emptyList(), 1000).isNotEmpty())

    @Test fun overCostLimit_isInvalid() =            // 7 x 70 = 490 > 400
        assertTrue(validateBooking(listOf(item(7, 70)), 1000).isNotEmpty())

    @Test fun overBalance_isInvalid() =              // 3 x 50 = 150 > 100
        assertTrue(validateBooking(listOf(item(3, 50)), 100).isNotEmpty())

    @Test fun moreDaysThanAvailable_isInvalid() =    // 5 days wanted, only 3 available
        assertTrue(validateBooking(listOf(item(5, 10, days = 3)), 1000).isNotEmpty())

    @Test fun validBooking_hasNoErrors() =           // 2 x 50 = 100, within every rule
        assertTrue(validateBooking(listOf(item(2, 50)), 1000).isEmpty())

    @Test fun blankName_isInvalid() = assertNotNull(validateName("   "))
}
