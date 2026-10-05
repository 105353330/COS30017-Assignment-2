package com.example.assignment_2.model

import android.os.Parcel
import android.os.Parcelable

// Parcelable is written by hand here because the @Parcelize plugin does not work with
// AGP 9's built-in Kotlin. Rule for every class: read fields back in the SAME order they were written.

/** One line in the basket: an item and how many days are wanted. */
data class BookingItem(
    val resourceId: Int,
    val name: String,
    val costPerDay: Int,
    val availableDays: Int,
    val units: Int = 1,            // days wanted
) : Parcelable {
    // Worked out each time it is read, not stored, so it always matches the days chosen.
    val cost: Int get() = costPerDay * units

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(resourceId)
        parcel.writeString(name)
        parcel.writeInt(costPerDay)
        parcel.writeInt(availableDays)
        parcel.writeInt(units)
    }

    override fun describeContents() = 0

    /** Rebuilds a BookingItem from a Parcel when it arrives in the other Activity. */
    companion object CREATOR : Parcelable.Creator<BookingItem> {
        override fun createFromParcel(parcel: Parcel) = BookingItem(
            parcel.readInt(),
            parcel.readString().orEmpty(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )
        override fun newArray(size: Int) = arrayOfNulls<BookingItem>(size)
    }
}

/** Sent from MainActivity to BookingActivity when the user taps Book. */
data class BookingRequest(
    val items: List<BookingItem>,
    val totalCost: Int,
    val credits: Int,              // balance in dollars at the time of booking
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(items)   // each BookingItem packs itself with its own writeToParcel
        parcel.writeInt(totalCost)
        parcel.writeInt(credits)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<BookingRequest> {
        override fun createFromParcel(parcel: Parcel) = BookingRequest(
            parcel.createTypedArrayList(BookingItem.CREATOR).orEmpty(),
            parcel.readInt(),
            parcel.readInt()
        )
        override fun newArray(size: Int) = arrayOfNulls<BookingRequest>(size)
    }
}

/** Sent back from BookingActivity to MainActivity when the user taps Confirm. */
data class BookingResult(
    val confirmed: Boolean,
    val guestName: String = "",
    val items: List<BookingItem> = emptyList(),
    val totalCost: Int = 0
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(if (confirmed) 1 else 0)   // stored as 1/0; writeBoolean needs a newer Android than minSdk 24
        parcel.writeString(guestName)
        parcel.writeTypedList(items)
        parcel.writeInt(totalCost)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<BookingResult> {
        override fun createFromParcel(parcel: Parcel) = BookingResult(
            parcel.readInt() == 1,
            parcel.readString().orEmpty(),
            parcel.createTypedArrayList(BookingItem.CREATOR).orEmpty(),
            parcel.readInt()
        )
        override fun newArray(size: Int) = arrayOfNulls<BookingResult>(size)
    }
}
