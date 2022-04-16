package com.gasparaiciukas.owntrainer.utils

object Constants {
    const val DATABASE_NAME = "owntrainer"

    const val AGE_MINIMUM = 18
    const val AGE_MAXIMUM = 99
    const val WEIGHT_MINIMUM = 10
    const val WEIGHT_MAXIMUM = 800
    const val HEIGHT_MINIMUM = 80
    const val HEIGHT_MAXIMUM = 300


    const val NOTIFICATION_REMINDER_ID = 1
    const val NOTIFICATION_REMINDER_TITLE_EXTRA = "NOTIFICATION_REMINDER_TITLE_EXTRA"
    const val REMINDER_WAKE_LOCK_TAG = "owntrainer::ReminderWakeLock"

    object Api {
        const val BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/"
        const val QUERY_PAGE_SIZE = 25

        object DataType {
            const val DATATYPE_BRANDED = "Branded"
            const val DATATYPE_FOUNDATION = "Foundation"
            const val DATATYPE_SURVEY = "Survey (FNDDS)"
            const val DATATYPE_SR_LEGACY = "SR Legacy"
        }

        object SortBy {
            const val SORT_BY_DATATYPE_KEYWORD = "dataType.keyword"
            const val SORT_BY_LOWERCASE_DESCRIPTION_KEYWORD = "lowercaseDescription.keyword"
            const val SORT_BY_FDC_ID = "fdcId"
            const val SORT_BY_PUBLISHED_DATE = "publishedDate"
        }

        object SortOrder {
            const val SORT_ORDER_ASCENDING = "asc"
            const val SORT_ORDER_DESCENDING = "desc"
        }
    }

    object EN {
        const val male = "Male"
        const val female = "Female"
        const val sedentary = "Sedentary"
        const val lightly_active = "Lightly active"
        const val moderately_active = "Moderately active"
        const val very_active = "Very active"
        const val extra_active = "Extra active"
    }
}

