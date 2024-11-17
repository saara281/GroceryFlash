package com.internshala.flash.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.internshala.flash.R
import com.google.firebase.auth.FirebaseAuth

enum class FlashappScreen(val title : String){
    Start("FlashCart"),
    Items("Choose Items"),
    Cart("Your Cart")
}


var canNavigateBack = false
val auth = FirebaseAuth.getInstance()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashApp(
    flashViewModel: FlashViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    ) {

    auth.currentUser?.let { flashViewModel.setUser(it) }
    val user by flashViewModel.user.collectAsState()
    val isVisible by flashViewModel.isVisible.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val logoutClicked by flashViewModel.logoutClicked.collectAsState()
    val currentScreen = FlashappScreen.valueOf(
        backStackEntry?.destination?.route?: FlashappScreen.Start.name
    )
    canNavigateBack = navController.previousBackStackEntry != null


    if(isVisible){
        OfferScreen()
    }else if(user == null){
        LoginUi(flashViewModel = flashViewModel)
    }
    else{
        Scaffold (
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = currentScreen.title,
                                    fontSize = 26.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )

                            }
                            Row(
                                modifier = Modifier.clickable {
                                    flashViewModel.setLogoutStatus(true)
                                }
                            ){
                                Icon(painter = painterResource(id = R.drawable.logout ) , contentDescription = "Logout",
                                    modifier = Modifier.size(24.dp))

                                Text(text = "Logout",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(
                                        end = 14.dp,
                                        start = 4.dp
                                    )
                                )
                            }
                        }

                },
                    navigationIcon = {
                        if (canNavigateBack){
                            IconButton(onClick = {
                                navController.navigateUp()
                            })
                            {
                                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back Button")
                            }
                        }

                    }
                )
            },
            bottomBar = { FlashAppBar(
                navController = navController,
                currentScreen = currentScreen)}
        ){
            NavHost(navController = navController,
                startDestination = FlashappScreen.Start.name,
                Modifier.padding(it)
            ){
                composable(route = FlashappScreen.Start.name,

                    ){
                    StartScreen(
                        flashViewModel = flashViewModel,
                        onCategoryClicked ={
                            flashViewModel.updateSelectedCategory(it)
                            navController.navigate(FlashappScreen.Items.name)

                        }
                    )

                }

                composable(route = FlashappScreen.Items.name){
                    InternetItemScreen(
                        flashViewModel = flashViewModel,
                        itemUiState = flashViewModel.itemUiState
                    )

                }
                composable(route = FlashappScreen.Cart.name){
                    CartScreen(
                        flashViewModel = flashViewModel,
                        onHomeButtonClicked = {
                            navController.navigate(FlashappScreen.Start.name){
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
        }
        if(logoutClicked){
            AlertCheck(onYesButtonPressed = {
                flashViewModel.setLogoutStatus(false)
                auth.signOut()
                flashViewModel.clearData()
            },
                onNoButtonPressed = {
                    flashViewModel.setLogoutStatus(false)
                }
            )
        }
    }
}




@Composable
fun FlashAppBar(
    navController: NavHostController,
    currentScreen: FlashappScreen){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 70.dp,
                vertical = 10.dp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                navController.navigate(FlashappScreen.Start.name) {
                    popUpTo(0)
                }
            }
        ){
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
            Text(text = "Home",
                fontSize = 10.sp)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                if(currentScreen != FlashappScreen.Cart){
                    navController.navigate(FlashappScreen.Cart.name){
                    }
                }

            }
        ){
            Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = "Cart")
            Text(text = "Cart",
                fontSize = 10.sp)
        }


    }
}

@Composable
fun InternetItemScreen(
    flashViewModel: FlashViewModel,
    itemUiState: FlashViewModel.ItemUiState
) {
    when (itemUiState) {
        is FlashViewModel.ItemUiState.Success -> {
            ItemScreen(flashViewModel = flashViewModel, items = itemUiState.items)
        }
        is FlashViewModel.ItemUiState.Loading -> {
            LoadingScreen()
        }
        else -> {
            ErrorScreen(flashViewModel = FlashViewModel())
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.loading_screen),
            contentDescription = "Loading Screen"
        )
    }
}

@Composable
fun ErrorScreen(flashViewModel: FlashViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.error),
            contentDescription = "Error Screen"
        )
        Text(text = "Oops, Internet unavailable. Please retry after checking your connection or turn on the mobile data.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            textAlign = TextAlign.Center)
        Button(onClick = {
            flashViewModel.getFlashItems()
        }) {
            Text(text = "Retry")
            
        }
    }
}


@Composable
fun AlertCheck(
    onYesButtonPressed : () -> Unit,
    onNoButtonPressed : () -> Unit
){
    AlertDialog(
        title = {
            Text(text = "Logout?", fontWeight = FontWeight.Bold)
        },
        containerColor = Color.White,
        text = {
            Text(text = "Are you sure you want to logout?")
        },
        confirmButton =  {
            TextButton(onClick = {
                onYesButtonPressed
            }) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onNoButtonPressed
            }) {
                Text(text = "No")
            }
        },
        onDismissRequest = {
            onNoButtonPressed
        }
    )
}




