package com.example.assignment_2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.assignment_2.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Everything the app knows at one moment. Replaced with a new copy on every change. */
data class AppState(
    val allResources: List<Resource> = sampleResources,   // source list: never edited in place
    val query: String = "",
    val sort: SortOption = SortOption.NONE,
    val index: Int = 0,                                    // which item Browse is showing
    val basket: List<BookingItem> = emptyList(),
    val bookings: List<Booking> = emptyList(),
    val credits: Int = START_CREDITS,
    val message: String? = null,                           // one-off text for a Snackbar
    val nextBookingId: Int = 1
) {
    // Filtered + sorted COPY of allResources, worked out fresh each time it is read.
    // allResources itself is never changed, which the spec requires.
    val visible: List<Resource>
        get() {
            val filtered = allResources.filter { r ->
                r.isAvailable && (r.name.contains(query, ignoreCase = true) ||
                        r.category.contains(query, ignoreCase = true))
            }
            return when (sort) {
                SortOption.NONE -> filtered
                SortOption.RATING -> filtered.sortedByDescending { it.rating }
                SortOption.YEAR -> filtered.sortedByDescending { it.year }
                SortOption.PRICE -> filtered.sortedBy { it.costPerDay }
            }
        }
    val basketTotal: Int get() = basket.sumOf { it.cost }
}

/** Holds the app state and every action the screens can trigger. Shared by all Main screens. */
class BookingViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    // ---- browse ----
    fun setQuery(q: String) = _state.update { it.copy(query = q, index = 0) }
    fun setSort(s: SortOption) = _state.update { it.copy(sort = s, index = 0) }
    fun next() = _state.update { s ->
        val n = s.visible.size
        // % wraps around: after the last item, go back to the first.
        if (n == 0) s else s.copy(index = (s.index + 1) % n)
    }
    fun previous() = _state.update { s ->
        val n = s.visible.size
        // Adding n before % stops the index going negative when wrapping from first to last.
        if (n == 0) s else s.copy(index = (s.index - 1 + n) % n)
    }

    // ---- basket ----
    fun addToBasket(r: Resource) = _state.update { s ->
        if (s.basket.any { it.resourceId == r.id }) {
            s.copy(message = "${r.name} is already in your basket.")
        } else {
            s.copy(
                basket = s.basket + BookingItem(r.id, r.name, r.costPerDay, r.availableDays),
                message = "${r.name} added to basket."
            )
        }
    }
    fun removeFromBasket(id: Int) = _state.update { s ->
        s.copy(basket = s.basket.filterNot { it.resourceId == id }, message = "Removed from basket.")
    }
    fun setUnits(id: Int, units: Int) = _state.update { s ->
        // Replace only the matching basket line with a copy that has the new days; leave the rest as they are.
        s.copy(basket = s.basket.map { if (it.resourceId == id) it.copy(units = units) else it })
    }

    // ---- result from BookingActivity ----
    fun applyBooking(result: BookingResult?) = _state.update { s ->
        if (result == null || !result.confirmed) {
            s.copy(message = "Booking cancelled")
        } else {
            s.book(result)
        }
    }

    // ---- history ----
    fun cancelBooking(bookingId: Int) = _state.update { s ->
        val b = s.bookings.find { it.id == bookingId }
        // Do nothing if the booking is missing or already cancelled, so credits can't be refunded twice.
        if (b == null || b.status == BookingStatus.CANCELLED) s
        else s.copy(
            bookings = s.bookings.map {
                if (it.id == bookingId) it.copy(status = BookingStatus.CANCELLED) else it
            },
            allResources = s.allResources.map {
                if (it.id == b.resourceId) it.copy(isAvailable = true) else it
            },
            credits = s.credits + b.totalCost,
            index = 0,
            message = "Booking cancelled"
        )
    }
    fun messageShown() = _state.update { it.copy(message = null) }
}

/** Commits a confirmed booking. Re-checks the rules first as a safety net. */
private fun AppState.book(result: BookingResult): AppState {
    if (validateBooking(result.items, credits).isNotEmpty()) {
        return copy(message = "Booking cancelled")
    }
    val total = result.items.sumOf { it.cost }
    var nextId = nextBookingId
    // One Booking per basket item, each with its own id (nextId++ uses the id, then adds 1).
    val newBookings = result.items.map { item ->
        Booking(nextId++, item.resourceId, item.name, item.units, item.cost, result.guestName)
    }
    val bookedIds = result.items.map { it.resourceId }.toSet()
    return copy(
        allResources = allResources.map { if (it.id in bookedIds) it.copy(isAvailable = false) else it },
        bookings = bookings + newBookings,
        credits = credits - total,
        basket = emptyList(),
        index = 0,
        nextBookingId = nextId,
        message = "Booking confirmed"
    )
}
