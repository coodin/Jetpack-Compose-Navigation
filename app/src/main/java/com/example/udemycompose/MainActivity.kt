package com.example.udemycompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.udemycompose.ui.theme.UdemyComposeTheme
import com.example.udemycompose.ui.theme.lightGreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UdemyComposeTheme {
                UserApplication()
            }
        }
    }
}

@Composable
fun UserApplication(userProfiles: List<UserProfile> = userProfileList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "user_list") {
        composable("user_list") {
            UserListScreen(userProfiles, navController)
        }
        composable(
            route = "user_details/{userId}", arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                })
        ) { navBackStackEntry ->
            UserProfileDetailScreen(
                navBackStackEntry.arguments!!.getInt("userId"),
                navController
            )
        }
    }
}

@Composable
fun UserListScreen(
    userProfiles: List<UserProfile>,
    navController: NavHostController?
) {
    Scaffold(
        topBar = {
            AppBar(
                title = "Users list",
                icon = Icons.Default.Home
            ) {}
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn() {
                items(userProfiles) { userProfile ->
                    ProfileCard(userProfile = userProfile) {
                        navController?.navigate("user_details/${userProfile.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileDetailScreen(userId: Int, navController: NavHostController?) {
    val userProfile = userProfileList.first { userProfile -> userProfile.id == userId }
    Scaffold(
        topBar = {
            AppBar(
                title = "User profile details",
                icon = Icons.Default.ArrowBack
            ) {
                navController?.navigateUp()
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ProfilePicture(
                    userProfile.pictureUrl,
                    userProfile.status,
                    240.dp
                )
                ProfileContent(
                    userProfile.name,
                    userProfile.status,
                    Alignment.CenterHorizontally
                )
            }
        }
    }
}


@Composable
fun AppBar(title: String, icon: ImageVector, iconClickAction: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "Home Icon",
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable {
                        iconClickAction.invoke()
                    }
            )
        },
        title = { Text(title) }

    )
}

@Composable
fun ProfileCard(userProfile: UserProfile, clickAction: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable { clickAction() },
        elevation = 8.dp,
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(userProfile.pictureUrl, userProfile.status, 72.dp)
            ProfileContent(
                userProfile.name,
                userProfile.status
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfilePicture(pictureUrl: String, onlineStatus: Boolean, imageSize: Dp) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            2.dp, color =
            if (onlineStatus)
                MaterialTheme.colors.lightGreen
            else
                Color.Red
        ),
        modifier = Modifier.padding(16.dp),
        elevation = 4.dp
    ) {
        val context = LocalContext.current
        val imageLoader = ImageLoader.Builder(context)
            .availableMemoryPercentage(0.25)
            .build()
        // Set
        CompositionLocalProvider(LocalImageLoader provides imageLoader) {
            Image(
                painter = rememberImagePainter(
                    data = pictureUrl,
                    builder = {
                        crossfade(true) //Crossfade animation between images
                        placeholder(R.drawable.default_profile)  //Used while loading
                        fallback(R.drawable.no_files_found) //Used if data is null
                        error(R.drawable.no_image_found) //Used when loading returns with  error
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = "Profile picture description",
                modifier = Modifier.size(imageSize),
                contentScale = ContentScale.Crop
            )
        }

    }
//        Image(
//            painter = painterResource(
//                id = drawableId
//            ),
//            contentDescription = "Profile Picture",
//            modifier = Modifier.size(72.dp),
//            contentScale = ContentScale.Crop
//        )

}

@Composable
fun ProfileContent(
    userName: String, onlineStatus: Boolean,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = Modifier
            .padding(8.dp), horizontalAlignment = alignment
    ) {
        CompositionLocalProvider(
            LocalContentAlpha provides (
                    if (onlineStatus)
                        1f
                    else
                        ContentAlpha.medium
                    )
        ) {
            Text(text = userName, style = MaterialTheme.typography.h5)
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = if (onlineStatus) "Active now" else "Offline",
                style = MaterialTheme.typography.body2
            )
        }

    }

}


//@Composable
//fun MainScreen2(viewModel: MainViewModel = MainViewModel()) {
//
//    val newNameStateContent = viewModel.textFieldState.observeAsState("")
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.SpaceEvenly,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        GreetingMessage(
//            newNameStateContent.value
//        ) { newName -> viewModel.onTextChange(newName) }
//    }
//
//}

//@Composable
//fun GreetingMessage(
//    textFieldValue: String,
//    textFieldUpdate: (newName: String) -> Unit,
//) {
//
//    TextField(value = textFieldValue, onValueChange = textFieldUpdate)
//    Button(onClick = {}) {
//        Text(textFieldValue)
//    }
//}
//
//
//@Composable
//fun Greeting(name: String) {
//    Text(
//        text = "Hello $name",
//        style = MaterialTheme.typography.h5
//    )
//}

@Composable
fun MainScreen2() {
    Surface(
        color = Color.DarkGray,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColoredSquare(Color.Red)
                ColoredSquare(Color.Magenta)
            }
            ColoredSquare(Color.Cyan)
            ColoredSquare(Color.Yellow)
            ColoredSquare(Color.Blue)
        }

    }
}

@Composable
fun ColoredSquare(color: Color) {
    Surface(
        color = color,
        modifier = Modifier
            .height(100.dp)
            .width(100.dp)

    ) { }
}

@Preview(showBackground = true)
@Composable
fun UserProfileDetailsPreview() {
    UdemyComposeTheme {
        UserProfileDetailScreen(userId = 0, null)
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    UdemyComposeTheme {
        UserListScreen(userProfileList, null)
    }
}