package com.gasparaiciukas.owntrainer.utils

object Constants {
    const val DATABASE_NAME = "owntrainer"

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
}

