package com.internshala.flash.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Categories (
    @StringRes val stringResourceID: Int,
    @DrawableRes val imageResourceID : Int

    )