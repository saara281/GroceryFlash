package com.internshala.flash.data
import androidx.annotation.StringRes
import com.internshala.flash.R

object DataSource {
fun loadCategories(): List<Categories>{
    return listOf<Categories>(
        Categories(stringResourceID = R.string.fresh_fruits, imageResourceID = R.drawable.fruit_basket1),
        Categories(stringResourceID = R.string.sweet_tooth, imageResourceID = R.drawable.sweet),
        Categories(stringResourceID = R.string.stationary, imageResourceID = R.drawable.stationary),
        Categories(stringResourceID = R.string.pet_food, imageResourceID = R.drawable.pet_food),
        Categories(stringResourceID = R.string.packaged_food, imageResourceID = R.drawable.packaged_food),
        Categories(stringResourceID = R.string.munchies, imageResourceID = R.drawable.munchies),
        Categories(stringResourceID = R.string.kitchen_essentials, imageResourceID = R.drawable.kitchen_essentials),
        Categories(stringResourceID = R.string.vegetables, imageResourceID = R.drawable.fresh_vegetables),
        Categories(stringResourceID = R.string.cleaning_essentials, imageResourceID = R.drawable.cleaning_essentials),
        Categories(stringResourceID = R.string.bread_biscuits, imageResourceID = R.drawable.breadbiscuit),
        Categories(stringResourceID = R.string.beverages, imageResourceID = R.drawable.beverages),
        Categories(stringResourceID = R.string.bath_body, imageResourceID = R.drawable.bath_body),


    )
}
    fun loadItems(
        @StringRes categoryName : Int
    ): List<Item>{
        return listOf(
            Item(R.string.banana_robust,R.string.fresh_fruits,"1 kg", 100,R.drawable.banana_bg),
            Item(R.string.shimla_apple,R.string.fresh_fruits,"1 kg", 250,R.drawable.apple),
            Item(R.string.papaya_semi_ripe,R.string.fresh_fruits,"500 g", 150,R.drawable.papaya),
            Item(R.string.pomegranate,R.string.fresh_fruits,"1 kg", 150,R.drawable.pomogranate),
            Item(R.string.pineapple,R.string.fresh_fruits,"1 kg", 130,R.drawable.pineapples),
            Item(R.string.pepsi_can,R.string.beverages,"1", 40,R.drawable.pepsican),
        ).filter {
            it.itemCategoryId == categoryName
        }
    }
}