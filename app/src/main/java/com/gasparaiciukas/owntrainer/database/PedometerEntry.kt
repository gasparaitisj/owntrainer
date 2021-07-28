package com.gasparaiciukas.owntrainer.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class PedometerEntry
(
@PrimaryKey @Required var yearAndDayOfYear: String = "",
var year: Int = 0,
var dayOfYear: Int = 0,
var steps: Int = 0,
var calories: Int = 0,
var distanceInKm: Double = 0.0,
var timeElapsedInS: Int = 0
) : RealmObject()