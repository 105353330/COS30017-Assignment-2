package com.example.assignment_2.model

import com.example.assignment_2.R

// The original list of items. It is never sorted or edited; screens use filtered copies.
val sampleResources = listOf(
    Resource(1, "Pale Ale Keg", "Beer", 1912, 4, 7, 35,
        "Hoppy golden pale ale with a crisp finish.", "Glenferrie", 30,
        R.drawable.pale_ale_keg),
    Resource(2, "Standard Lager Keg", "Beer", 2011, 3, 7, 30,
        "Easy-drinking lager, great for parties.", "Glenferrie", 30,
        R.drawable.standard_lager_keg),
    Resource(3, "Premium German Lager Keg", "Beer", 1772, 5, 7, 70,
        "Classic German lager from a 1772 recipe.", "Glenferrie", 50,
        R.drawable.premium_german_lager_keg),
    Resource(4, "Esky", "Equipment", 2018, 3, 4, 15,
        "Large insulated esky, keeps drinks cold all day.", "Glenferrie", 20,
        R.drawable.esky),
    Resource(5, "Beer Taps", "Equipment", 2015, 4, 4, 20,
        "Tap tower that connects to any keg.", "Glenferrie", null,
        R.drawable.beer_taps),
    Resource(6, "Cooling System", "Equipment", 2024, 5, 4, 25,
        "Inline cooler that chills beer as it pours.", "Glenferrie", null,
        R.drawable.cooling_system)
)
