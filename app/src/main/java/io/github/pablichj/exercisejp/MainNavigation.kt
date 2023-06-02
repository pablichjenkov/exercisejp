package io.github.pablichj.exercisejp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.pablichj.exercisejp.ui.SearchPage
import io.github.pablichj.exercisejp.ui.SearchPageViewModel

open class Destination(val route: String)
object MainGraph: Destination("mainGraph")
object SearchPage: Destination("mainGraph:searchPage")

internal fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    navigation(
        route = MainGraph.route,
        startDestination = SearchPage.route
    ) {
        composable(SearchPage.route) { backStackEntry ->
            val searchPageViewModel = hiltViewModel<SearchPageViewModel>(backStackEntry)
            SearchPage(viewModel = searchPageViewModel)
        }
    }
}