package com.internshala.flash.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.internshala.flash.R
import com.internshala.flash.data.InternetItem

@Composable
fun ItemScreen(
    flashViewModel: FlashViewModel,
    items: List<InternetItem>
) {
    val flashUiState by flashViewModel.uiState.collectAsState()

    val selectedCategory = stringResource(id = flashUiState.selectCategory)

    val database = items.filter {
        it.itemCategory.lowercase() == selectedCategory.lowercase()
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item( span = { GridItemSpan(2) })
        {
            Column{
                Image(painter = painterResource(R.drawable.itembanner), contentDescription ="Offer")
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(108,194,111,255)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                ) {
                    Text(text = "${stringResource(id = flashUiState.selectCategory)} (${database.size}",
                        fontSize = 2.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
        }


        items(database) {
            ItemCard(
                stringResourceId = it.itemName,
                imageResourceId = it.imageUrl,
                itemQuantity = it.itemQuantity,
                itemPrice = it.itemPrice,
                flashViewModel = flashViewModel
            )
        }
    }
}
@Composable
fun InternetItemScreen(flashViewModel: FlashViewModel){
    val itemUiState: String = flashViewModel.itemUiState.toString()
    Text(text = itemUiState)
}




@Composable
fun ItemCard(
    stringResourceId: String,
    imageResourceId: String,
    itemQuantity: String,
    itemPrice: Int,
    flashViewModel: FlashViewModel
) {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        append("25% off")
    }

    val priceString = buildAnnotatedString {
        append("Rs $itemPrice")
    }

    val newPrice = buildAnnotatedString {
        append("Rs ${itemPrice * 75 / 100}")
    }
    Column(modifier = Modifier.width(150.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(248, 221, 248, 255)
            )
        ) {
            Box {
                AsyncImage(
                    model = imageResourceId,
                    contentDescription = stringResourceId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )

                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(244, 64, 54, 255)
                        )
                    ) {
                        Text(
                            text = annotatedString,
                            color = Color.White,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(
                                horizontal = 5.dp,
                                vertical = 2.dp
                            )
                        )
                    }
                }
            }
        }
        Text(
            text = stringResourceId,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            maxLines = 1,
            textAlign = TextAlign.Left
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = priceString,
                    fontSize = 6.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = Color(109, 109, 109, 255),
                    textDecoration = TextDecoration.LineThrough
                )

                Text(
                    text = newPrice,
                    fontSize = 10.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = Color(255, 116, 105, 255)
                )
            }
            Text(
                text = itemQuantity,
                fontSize = 14.sp,
                maxLines = 1,
                color = Color(114, 114, 114, 255)
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .clickable {
                    flashViewModel.addToDatabase(
                        InternetItem(
                            itemName = stringResourceId,
                            itemQuantity = itemQuantity,
                            itemPrice = itemPrice,
                            imageUrl = imageResourceId,
                            itemCategory = " "
                        )
                    )
                    Toast
                        .makeText(context, "Added to Cart", Toast.LENGTH_SHORT)
                        .show()
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(108, 194, 111, 255)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add to Cart",
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}


