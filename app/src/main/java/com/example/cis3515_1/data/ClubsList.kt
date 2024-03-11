package com.example.cis3515_1.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ClubsList(
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int,
    @StringRes val stringResourceId1: Int,
    @StringRes val stringResourceId2: Int
)