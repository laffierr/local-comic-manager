package com.example.comiclibrary.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.comiclibrary.ui.bookshelf.BookshelfScreen
import com.example.comiclibrary.ui.comicdetail.ComicDetailScreen
import com.example.comiclibrary.ui.favorites.CollectionDetailScreen
import com.example.comiclibrary.ui.favorites.FavoritesScreen
import com.example.comiclibrary.ui.home.HomeScreen
import com.example.comiclibrary.ui.reader.ReaderScreen
import com.example.comiclibrary.ui.tagcomics.TagComicsScreen
import com.example.comiclibrary.ui.tags.TagScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onThemeSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "tabs",
        modifier = modifier
    ) {
        composable("tabs") {
            MainTabsContent(
                onComicClick = { comicId ->
                    navController.navigate(Screen.ComicDetail.createRoute(comicId))
                },
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.CollectionDetail.createRoute(collectionId))
                },
                onNavigateToTagComics = { tagId, tagName ->
                    navController.navigate(Screen.TagComics.createRoute(tagId, tagName))
                },
                onNavigateToTags = {
                    navController.navigate(Screen.Tags.route)
                },
                onThemeSettings = onThemeSettings
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onComicClick = { comicId ->
                    navController.navigate(Screen.ComicDetail.createRoute(comicId))
                },
                onGoToBookshelf = {
                    navController.navigate("tabs") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = Screen.CollectionDetail.route,
            arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: return@composable
            CollectionDetailScreen(
                onComicClick = { comicId ->
                    navController.navigate(Screen.ComicDetail.createRoute(comicId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ComicDetail.route,
            arguments = listOf(navArgument("comicId") { type = NavType.LongType })
        ) { backStackEntry ->
            val comicId = backStackEntry.arguments?.getLong("comicId") ?: return@composable
            ComicDetailScreen(
                onBack = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() },
                onStartReading = { id ->
                    navController.navigate(Screen.Reader.createRoute(id))
                },
                onNavigateToTags = {
                    navController.navigate(Screen.Tags.route)
                }
            )
        }

        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("comicId") { type = NavType.LongType })
        ) {
            ReaderScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TagComics.route,
            arguments = listOf(
                navArgument("tagId") { type = NavType.LongType },
                navArgument("tagName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tagName = backStackEntry.arguments?.getString("tagName") ?: ""
            TagComicsScreen(
                onBack = { navController.popBackStack() },
                onComicClick = { comicId ->
                    navController.navigate(Screen.ComicDetail.createRoute(comicId))
                }
            )
        }

        composable(Screen.Tags.route) {
            TagScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainTabsContent(
    onComicClick: (Long) -> Unit,
    onCollectionClick: (Long) -> Unit,
    onNavigateToTagComics: (tagId: Long?, tagName: String) -> Unit,
    onNavigateToTags: () -> Unit,
    onThemeSettings: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { BottomNavItem.entries.size },
        initialPage = 0
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = index,
                                    animationSpec = tween(durationMillis = 200)
                                )
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (pagerState.currentPage == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            beyondViewportPageCount = 1,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapAnimationSpec = tween(100)
            )
        ) { page ->
            when (page) {
                0 -> BookshelfScreen(
                    onComicClick = onComicClick,
                    onNavigateToCollection = onCollectionClick,
                    onNavigateToTagComics = onNavigateToTagComics,
                    onThemeSettings = onThemeSettings
                )
                1 -> FavoritesScreen(
                    onCollectionClick = onCollectionClick,
                    onNavigateToTags = onNavigateToTags,
                    onThemeSettings = onThemeSettings
                )
            }
        }
    }
}
