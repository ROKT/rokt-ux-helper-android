package com.rokt.demoapp.ui.screen.tutorials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.common.BackButton
import com.rokt.demoapp.ui.common.ButtonWithDescriptionLight
import com.rokt.demoapp.ui.common.Heading
import com.rokt.demoapp.ui.common.LARGE_SPACE
import com.rokt.demoapp.ui.common.LargeSpace
import com.rokt.demoapp.ui.common.SmallSpace
import com.rokt.demoapp.ui.screen.layouts.HEADER_TOP_PADDING

@Composable
fun TutorialPageContent(backPressed: () -> Unit) {
    val navController = rememberNavController()
    val actions = remember(navController) { TutorialActions(navController) }
    val tutorialScreenState = rememberTutorialPageState(actions.backPressed)
    NavHost(navController = navController, startDestination = TutorialDestinations.TUTORIAL_HOME) {
        composable(TutorialDestinations.TUTORIAL_HOME) {
            TutorialHome(backPressed, tutorialScreenState, actions)
        }
        tutorialScreenState.items.filterIsInstance<TutorialPageListItem.ComposablePageListItem>().forEach { item ->
            composable(TutorialDestinations.TUTORIAL_DESTINATION + item.navAction) {
                item.content()
            }
        }
    }
}

@Composable
fun TutorialHome(backPressed: () -> Unit, tutorialScreenState: TutorialScreenState, tutorialActions: TutorialActions) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val context = LocalContext.current
        Box(Modifier.padding(PaddingValues(start = 3.dp, top = HEADER_TOP_PADDING.dp))) {
            BackButton(backPressed, MaterialTheme.colorScheme.secondary)
        }
        Column(
            modifier = Modifier
                .padding(30.dp),
        ) {
            Heading(text = stringResource(id = R.string.menu_button_tutorials))
            SmallSpace()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(PaddingValues(0.dp, 0.dp, 0.dp, LARGE_SPACE.dp)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(tutorialScreenState.items) { item ->
                    ButtonWithDescriptionLight(
                        text = item.title,
                        description = item.description,
                        onClick = {
                            if (item is TutorialPageListItem.AndroidXmlPageListItem) {
                                item.startActivity(context)
                            } else if (item is TutorialPageListItem.ComposablePageListItem) {
                                tutorialActions.navigateToDemoDestination(item.navAction)
                            }
                        },
                    )
                    LargeSpace()
                }
            }
        }
    }
}
