package com.internshala.flash.ui


import com.internshala.flash.R
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internshala.flash.data.DataSource


@Composable
fun StartScreen(
    flashViewModel: FlashViewModel,
    onCategoryClicked: (Int) -> Unit
) {
    val context = LocalContext.current
    val flashUiState by flashViewModel.uiState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.offer),
                    contentDescription = "Offer",
                    modifier = Modifier.fillMaxWidth()
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(108, 194, 111, 255)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Text(
                        text = "Shop by Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
        }

        items(DataSource.loadCategories()) {
            CategoryCard(
                context = context,
                stringResourceID = it.stringResourceID,
                imageResourceID = it.imageResourceID,
                flashViewModel = flashViewModel,
                onCategoryClicked = onCategoryClicked
            )
        }
    }
}





@Composable
fun CategoryCard(
    context: Context,
    stringResourceID: Int,
    imageResourceID: Int,
    flashViewModel: FlashViewModel,
    onCategoryClicked: (Int) -> Unit

) {
    val categoryName = stringResource(id = stringResourceID)
    Card(modifier = Modifier.clickable {
        flashViewModel.updateClickText(categoryName)
        onCategoryClicked(stringResourceID)

    },
        colors = CardDefaults.cardColors(
            containerColor = Color(248,221,248,255)
        )
    )    {
        Column(modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = categoryName,
                fontSize = 17.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(imageResourceID),
                contentDescription = "Fresh Fruits")
        }

    }
}





