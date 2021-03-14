package io.github.shinhyo.brba.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.shinhyo.brba.R
import io.github.shinhyo.brba.data.Character
import io.github.shinhyo.brba.ui.detail.DetailScreen
import io.github.shinhyo.brba.ui.favorite.FavoriteScreen
import io.github.shinhyo.brba.ui.list.ListScreen
import io.github.shinhyo.brba.utils.backHandler

sealed class NavScreens(val route: String) {
    object MAIN : NavScreens("main")
    object DETAIL : NavScreens("detail")
}

@Preview
@Composable
fun NavGraph(startDestination: NavScreens = NavScreens.MAIN) {
    val navController = rememberNavController()
    val actions = remember(navController) { MainActions(navController) }
    val selectedTab = remember { mutableStateOf(BottomNavTabs.LIST) }
    NavHost(navController = navController, startDestination = startDestination.route) {
        composable(
            route = NavScreens.MAIN.route
        ) { NavScreen(actions = actions, selectedTab) }
        composable(
            route = "${NavScreens.DETAIL.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { DetailScreen(viewModel = hiltNavGraphViewModel()) }
    }
}

// NavTabs
enum class BottomNavTabs(val label: String, val icon: Int) {
    LIST("Character", R.drawable.ic_account_cowboy_hat),
    FAVORITE("Favorite", R.drawable.ic_heart),
}

@Composable
fun NavScreen(
    actions: MainActions,
    selectedTab: MutableState<BottomNavTabs>
) {
    Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.navigationBarsPadding(),
                backgroundColor = MaterialTheme.colors.background
            ) {
                for (tab in BottomNavTabs.values()) {
                    BottomNavigationItem(
                        selected = selectedTab.value == tab,
                        onClick = {
                            if (selectedTab.value == tab) return@BottomNavigationItem
                            selectedTab.value = tab
                        },
                        icon = {
                            Icon(
                                painterResource(id = tab.icon),
                                contentDescription = tab.label
                            )
                        },
                        unselectedContentColor = Color.LightGray,
                        selectedContentColor = MaterialTheme.colors.primaryVariant,
                    )
                }
            }
        },
    ) {
        val modifier = Modifier.padding(it)
        when (selectedTab.value) {
            BottomNavTabs.LIST -> ListScreen(
                hiltNavGraphViewModel(),
                actions.moveDetail,
                modifier
            )
            BottomNavTabs.FAVORITE -> FavoriteScreen(
                hiltNavGraphViewModel(),
                actions.moveDetail,
                modifier
            )
        }
    }
//    }
    backHandler(
        enabled = selectedTab.value != BottomNavTabs.LIST,
        onBack = { selectedTab.value = BottomNavTabs.LIST }
    )
}

class MainActions(navController: NavHostController) {

    val moveDetail: (Character) -> Unit = { character ->
        navController.navigate("${NavScreens.DETAIL.route}/${character.charId}")
    }
}
