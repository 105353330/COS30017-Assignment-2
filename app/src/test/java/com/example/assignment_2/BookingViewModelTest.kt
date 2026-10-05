package com.example.assignment_2

import com.example.assignment_2.model.BookingResult
import com.example.assignment_2.model.BookingStatus
import com.example.assignment_2.model.START_CREDITS
import com.example.assignment_2.model.SortOption
import com.example.assignment_2.viewmodel.BookingViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Checks the ViewModel's basket, sorting, booking and cancelling logic (Given / When / Then). */
class BookingViewModelTest {
    @Test fun addingSameItemTwice_keepsOneEntry() {
        val vm = BookingViewModel()
        val keg = vm.state.value.allResources.first()
        vm.addToBasket(keg); vm.addToBasket(keg)
        assertEquals(1, vm.state.value.basket.size)
    }

    @Test fun sorting_doesNotChangeSourceList() {
        val vm = BookingViewModel()
        val before = vm.state.value.allResources.map { it.id }
        vm.setSort(SortOption.PRICE)
        val prices = vm.state.value.visible.map { it.costPerDay }
        assertEquals(prices.sorted(), prices)                               // visible is cheapest first
        assertEquals(before, vm.state.value.allResources.map { it.id })     // source list unchanged
    }

    @Test fun bookThenCancel_refundsAndRestores() {
        val vm = BookingViewModel()
        vm.addToBasket(vm.state.value.allResources.first())        // Pale Ale Keg, 1 day = $35
        val items = vm.state.value.basket
        vm.applyBooking(BookingResult(true, "Patrick", items, items.sumOf { it.cost }))
        assertEquals(START_CREDITS - 35, vm.state.value.credits)
        assertFalse(vm.state.value.allResources.first().isAvailable)
        vm.cancelBooking(1)
        assertEquals(START_CREDITS, vm.state.value.credits)
        assertTrue(vm.state.value.allResources.first().isAvailable)
        assertEquals(BookingStatus.CANCELLED, vm.state.value.bookings.first().status)
    }
}
