package com.example.assignment_2

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.IntentCompat
import com.example.assignment_2.model.BookingRequest
import com.example.assignment_2.model.BookingResult

/** Starts BookingActivity with a BookingRequest and reads back a BookingResult (null if cancelled). */
class BookingContract : ActivityResultContract<BookingRequest, BookingResult?>() {
    // Build the explicit Intent and attach the Parcelable request.
    override fun createIntent(context: Context, input: BookingRequest): Intent =
        Intent(context, BookingActivity::class.java).putExtra(EXTRA_REQUEST, input)

    // Turn the returned Intent back into an object. null means "cancelled".
    override fun parseResult(resultCode: Int, intent: Intent?): BookingResult? =
        if (resultCode == Activity.RESULT_OK && intent != null)
            IntentCompat.getParcelableExtra(intent, EXTRA_RESULT, BookingResult::class.java)
        else null

    companion object {
        const val EXTRA_REQUEST = "booking_request"
        const val EXTRA_RESULT = "booking_result"
    }
}
