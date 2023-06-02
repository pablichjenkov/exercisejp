package io.github.pablichj.exercisejp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.pablichj.exercisejp.ui.SearchPage
import io.github.pablichj.exercisejp.ui.SearchPageViewModel
import io.github.pablichj.exercisejp.ui.SearchPageContent
import io.github.pablichj.exercisejp.ui.theme.ExerciseJPTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //val searchPageViewModel by viewModels<DefaultSearchPageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExerciseJPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = MainGraph.route,
                    ) {
                        mainGraph(navController)
                    }

                }


            }
        }
    }
}
