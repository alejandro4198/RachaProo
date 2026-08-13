package com.example.rachapro.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rachapro.WelcomeScreen
import com.example.rachapro.auth.AuthViewModel
import com.example.rachapro.auth.AuthUiState
import com.example.rachapro.auth.LoginScreen
import com.example.rachapro.auth.RegisterScreen
import com.example.rachapro.main.MainScreen
import com.example.rachapro.main.MainViewModel
import com.example.rachapro.onboarding.OnboardingScreen
import com.example.rachapro.activities.ActivitiesViewModel
import androidx.compose.runtime.remember
import com.example.rachapro.activities.ActivitiesUiState
import com.example.rachapro.activities.NewActivityScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.rachapro.activities.EditActivityScreen
import androidx.compose.material3.Text
import com.example.rachapro.activities.SubtasksViewModel
import com.example.rachapro.activities.ReminderViewModel
import com.example.rachapro.pomodoro.PomodoroViewModel
import com.example.rachapro.progress.ProgressViewModel


object Routes {

    const val APP_START = "app_start"
    const val WELCOME = "welcome"
    const val ONBOARDING = "onboarding"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val HOME = "home"
    const val NEW_ACTIVITY = "new_activity"
    const val EDIT_ACTIVITY =
        "edit_activity/{activityId}"

    fun editActivity(
        activityId: Long
    ): String {

        return "edit_activity/$activityId"
    }
}

@Composable
fun RachaProNavHost() {

    val navController =
        rememberNavController()

    val authViewModel: AuthViewModel =
        viewModel(
            factory = AuthViewModel.Factory
        )

    val authUiState by
    authViewModel.uiState
        .collectAsStateWithLifecycle()

    val appStartViewModel: AppStartViewModel =
        viewModel(
            factory = AppStartViewModel.Factory
        )

    val appStartState by
    appStartViewModel.uiState
        .collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.APP_START
    ) {

        /*
         * ---------------------------------------------------------
         * ARRANQUE DE LA APLICACIÓN
         * ---------------------------------------------------------
         */

        composable(Routes.APP_START) {

            LaunchedEffect(appStartState) {

                when (appStartState) {

                    AppStartState.Loading -> {
                        // Esperamos a que DataStore termine de leer.
                    }

                    AppStartState.NeedsOnboarding -> {

                        navController.navigate(
                            Routes.WELCOME
                        ) {

                            popUpTo(Routes.APP_START) {
                                inclusive = true
                            }
                        }
                    }

                    AppStartState.LoggedOut -> {

                        navController.navigate(
                            Routes.LOGIN
                        ) {

                            popUpTo(Routes.APP_START) {
                                inclusive = true
                            }
                        }
                    }

                    is AppStartState.LoggedIn -> {

                        navController.navigate(
                            Routes.HOME
                        ) {

                            popUpTo(Routes.APP_START) {
                                inclusive = true
                            }
                        }
                    }
                }
            }

            AppLoadingScreen()
        }

        /*
         * ---------------------------------------------------------
         * BIENVENIDA
         * ---------------------------------------------------------
         */

        composable(Routes.WELCOME) {

            WelcomeScreen(
                onStart = {

                    navController.navigate(
                        Routes.ONBOARDING
                    )
                }
            )
        }

        /*
         * ---------------------------------------------------------
         * ONBOARDING
         * ---------------------------------------------------------
         */

        composable(Routes.ONBOARDING) {

            OnboardingScreen(
                onNext = {

                    appStartViewModel.completeOnboarding()

                    navController.navigate(
                        Routes.REGISTER
                    )
                }
            )
        }

        /*
         * ---------------------------------------------------------
         * REGISTRO
         * ---------------------------------------------------------
         */

        composable(Routes.REGISTER) {

            RegisterScreen(
                uiState = authUiState,

                onBack = {

                    authViewModel.resetState()

                    navController.popBackStack()
                },

                onLogin = {

                    authViewModel.resetState()

                    navController.navigate(
                        Routes.LOGIN
                    )
                },

                onRegister = {
                        fullName,
                        email,
                        password,
                        confirmPassword,
                        semester,
                        acceptedPrivacyPolicy ->

                    authViewModel.register(
                        fullName = fullName,
                        email = email,
                        password = password,
                        confirmPassword =
                            confirmPassword,
                        semester = semester,
                        acceptedPrivacyPolicy =
                            acceptedPrivacyPolicy
                    )
                },

                onRegistrationSuccess = {

                    authViewModel.resetState()

                    navController.navigate(
                        Routes.LOGIN
                    ) {

                        popUpTo(Routes.REGISTER) {
                            inclusive = true
                        }
                    }
                }
            )
        }


        composable(Routes.LOGIN) {

            LoginScreen(
                uiState = authUiState,

                onBack = {

                    authViewModel.resetState()

                    navController.popBackStack()
                },

                onRegister = {

                    authViewModel.resetState()

                    navController.navigate(
                        Routes.REGISTER
                    ) {
                        launchSingleTop = true
                    }
                },

                onLogin = { email, password ->

                    authViewModel.login(
                        email = email,
                        password = password
                    )
                },

                onLoginSuccess = {

                    authViewModel.resetState()

                    navController.navigate(
                        Routes.HOME
                    ) {

                        popUpTo(Routes.WELCOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        /*
         * ---------------------------------------------------------
         * INICIO
         * ---------------------------------------------------------
         */

        composable(Routes.HOME) {

            val mainViewModel: MainViewModel =
                viewModel(
                    factory = MainViewModel.Factory
                )

            val mainUiState by
            mainViewModel.uiState
                .collectAsStateWithLifecycle()

            val activitiesViewModel: ActivitiesViewModel =
                viewModel(
                    factory = ActivitiesViewModel.Factory
                )

            val activitiesUiState by
            activitiesViewModel.uiState
                .collectAsStateWithLifecycle()

            val activityActionState by
            activitiesViewModel.actionState
                .collectAsStateWithLifecycle()

            val pomodoroViewModel: PomodoroViewModel =
                viewModel(
                    factory = PomodoroViewModel.Factory
                )

            val pomodoroUiState by
            pomodoroViewModel.uiState
                .collectAsStateWithLifecycle()

            val pomodoroActionState by
            pomodoroViewModel.actionState
                .collectAsStateWithLifecycle()

            val progressViewModel: ProgressViewModel =
                viewModel(
                    factory = ProgressViewModel.Factory
                )

            val progressUiState by
            progressViewModel.uiState
                .collectAsStateWithLifecycle()

            LaunchedEffect(authUiState) {

                if (authUiState is AuthUiState.LogoutSuccess) {

                    authViewModel.resetState()

                    navController.navigate(
                        Routes.LOGIN
                    ) {

                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                }
            }

            MainScreen(
                mainUiState = mainUiState,

                activitiesUiState =
                    activitiesUiState,

                activityActionState =
                    activityActionState,

                isLoggingOut =
                    authUiState is AuthUiState.Loading,

                onRetryUser = {
                    mainViewModel.retry()
                },

                onRetryActivities = {
                    activitiesViewModel.retry()
                },

                onLogout = {
                    authViewModel.logout()
                },

                onNewActivity = {
                    navController.navigate(
                        Routes.NEW_ACTIVITY
                    )
                },

                onEditActivity = { activityId ->

                    activitiesViewModel
                        .resetActionState()

                    navController.navigate(
                        Routes.editActivity(
                            activityId
                        )
                    )
                },

                onCompleteActivity = { activityId ->

                    activitiesViewModel.completeActivity(
                        activityId = activityId
                    )
                },

                onDeleteActivity = { activityId ->

                    activitiesViewModel.deleteActivity(
                        activityId = activityId
                    )
                },

                onResetActivityActionState = {

                    activitiesViewModel.resetActionState()
                },

                onActivityFilterSelected = { filter ->

                    activitiesViewModel
                        .selectFilter(
                            filter = filter
                        )
                },

                onActivitySearchQueryChange = { query ->

                    activitiesViewModel.updateSearchQuery(
                        query = query
                    )
                },

                onRefreshActivityStatuses = {

                    activitiesViewModel
                        .refreshStatuses()
                },

                pomodoroUiState =
                    pomodoroUiState,

                pomodoroActionState =
                    pomodoroActionState,

                onStartPomodoro = {
                    pomodoroViewModel.startFocus()
                },

                onStartShortBreak = {
                    pomodoroViewModel.startShortBreak()
                },

                onStartLongBreak = {
                    pomodoroViewModel.startLongBreak()
                },

                onPausePomodoro = {
                    pomodoroViewModel.pause()
                },

                onResumePomodoro = {
                    pomodoroViewModel.resume()
                },

                onCancelPomodoro = {
                    pomodoroViewModel.cancel()
                },

                progressUiState =
                    progressUiState,

                onRetryProgress = {
                    progressViewModel.retry()
                },

                onProgressPeriodSelected = { period ->

                    progressViewModel.selectPeriod(
                        period = period
                    )
                },

            )
        }

        composable(Routes.NEW_ACTIVITY) { backStackEntry ->

            val homeBackStackEntry =
                remember(backStackEntry) {

                    navController.getBackStackEntry(
                        Routes.HOME
                    )
                }

            val activitiesViewModel: ActivitiesViewModel =
                viewModel(
                    viewModelStoreOwner =
                        homeBackStackEntry,
                    factory =
                        ActivitiesViewModel.Factory
                )

            val activitiesUiState by
            activitiesViewModel.uiState
                .collectAsStateWithLifecycle()

            val activityActionState by
            activitiesViewModel.actionState
                .collectAsStateWithLifecycle()

            val categories =
                when (val state = activitiesUiState) {

                    is ActivitiesUiState.Success ->
                        state.categories

                    else ->
                        emptyList()
                }

            NewActivityScreen(
                categories = categories,

                actionState =
                    activityActionState,

                onBack = {

                    activitiesViewModel
                        .resetActionState()

                    navController
                        .popBackStack()
                },

                onCreateActivity = {
                        title,
                        description,
                        categoryId,
                        dueDateEpochDay,
                        dueTimeMinutes,
                        priority ->

                    activitiesViewModel.createActivity(
                        title = title,
                        description = description,
                        categoryId = categoryId,
                        dueDateEpochDay =
                            dueDateEpochDay,
                        dueTimeMinutes =
                            dueTimeMinutes,
                        priority = priority
                    )
                },

                onCreated = {

                    navController.popBackStack()
                },

                onResetActionState = {

                    activitiesViewModel
                        .resetActionState()
                }
            )
        }

        composable(
            route = Routes.EDIT_ACTIVITY,

            arguments = listOf(

                navArgument(
                    "activityId"
                ) {

                    type =
                        NavType.LongType
                }
            )
        ) { backStackEntry ->

            val activityId =
                backStackEntry
                    .arguments
                    ?.getLong(
                        "activityId"
                    )

            /*
             * Compartimos el mismo ActivitiesViewModel
             * perteneciente a HOME.
             */

            val homeBackStackEntry =
                remember(
                    backStackEntry
                ) {

                    navController
                        .getBackStackEntry(
                            Routes.HOME
                        )
                }

            val activitiesViewModel:
                    ActivitiesViewModel =
                viewModel(
                    viewModelStoreOwner =
                        homeBackStackEntry,

                    factory =
                        ActivitiesViewModel.Factory
                )

            val activitiesUiState by
            activitiesViewModel
                .uiState
                .collectAsStateWithLifecycle()

            val activityActionState by
            activitiesViewModel
                .actionState
                .collectAsStateWithLifecycle()

            val subtasksViewModel: SubtasksViewModel =
                viewModel(
                    viewModelStoreOwner = backStackEntry,
                    factory = SubtasksViewModel.Factory
                )

            val reminderViewModel: ReminderViewModel =
                viewModel(
                    viewModelStoreOwner =
                        backStackEntry,

                    factory =
                        ReminderViewModel.Factory
                )

            val reminderUiState by
            reminderViewModel
                .uiState
                .collectAsStateWithLifecycle()

            val reminderActionState by
            reminderViewModel
                .actionState
                .collectAsStateWithLifecycle()

            val subtasksUiState by
            subtasksViewModel
                .uiState
                .collectAsStateWithLifecycle()

            val subtaskActionState by
            subtasksViewModel
                .actionState
                .collectAsStateWithLifecycle()

            when (
                val state =
                    activitiesUiState
            ) {

                ActivitiesUiState.Loading -> {

                    AppLoadingScreen()
                }

                is ActivitiesUiState.Success -> {

                    val activity =
                        activityId
                            ?.let { id ->

                                state.activities
                                    .firstOrNull {
                                        it.id == id
                                    }
                            }

                    if (activity != null) {

                        LaunchedEffect(
                            activity.id
                        ) {

                            subtasksViewModel.loadSubtasks(
                                activityId =
                                    activity.id
                            )

                            reminderViewModel.loadReminders(
                                activityId =
                                    activity.id
                            )
                        }

                        EditActivityScreen(
                            activity = activity,

                            categories =
                                state.categories,

                            actionState =
                                activityActionState,

                            subtasksUiState =
                                subtasksUiState,

                            subtaskActionState =
                                subtaskActionState,

                            reminderUiState =
                                reminderUiState,

                            reminderActionState =
                                reminderActionState,

                            onBack = {

                                activitiesViewModel
                                    .resetActionState()

                                subtasksViewModel
                                    .resetActionState()

                                navController
                                    .popBackStack()
                            },

                            onUpdateActivity = {
                                    id,
                                    title,
                                    description,
                                    categoryId,
                                    dueDateEpochDay,
                                    dueTimeMinutes,
                                    priority ->

                                activitiesViewModel
                                    .updateActivity(
                                        activityId = id,
                                        title = title,
                                        description = description,
                                        categoryId = categoryId,
                                        dueDateEpochDay =
                                            dueDateEpochDay,
                                        dueTimeMinutes =
                                            dueTimeMinutes,
                                        priority = priority
                                    )
                            },

                            onDeleteSubtask = { subtaskId ->

                                subtasksViewModel.deleteSubtask(
                                    subtaskId = subtaskId
                                )
                            },

                            onCreateSubtask = { title ->

                                subtasksViewModel
                                    .createSubtask(
                                        title = title
                                    )
                            },

                            onSetSubtaskCompleted = {
                                    subtaskId,
                                    isCompleted ->

                                subtasksViewModel
                                    .setSubtaskCompleted(
                                        subtaskId =
                                            subtaskId,

                                        isCompleted =
                                            isCompleted
                                    )
                            },

                            onUpdateSubtask = {
                                    subtaskId,
                                    title ->

                                subtasksViewModel
                                    .updateSubtask(
                                        subtaskId =
                                            subtaskId,

                                        title =
                                            title
                                    )
                            },

                            onUpdated = {

                                navController
                                    .popBackStack()
                            },

                            onResetActionState = {

                                activitiesViewModel
                                    .resetActionState()
                            },

                            onResetSubtaskActionState = {

                                subtasksViewModel
                                    .resetActionState()
                            },

                            onCreateReminder = {
                                    title,
                                    message,
                                    triggerAtMillis ->

                                reminderViewModel
                                    .createReminder(
                                        title =
                                            title,

                                        message =
                                            message,

                                        triggerAtMillis =
                                            triggerAtMillis
                                    )
                            },

                            onCancelReminder = {
                                    reminderId ->

                                reminderViewModel
                                    .cancelReminder(
                                        reminderId =
                                            reminderId
                                    )
                            },

                        )

                    } else {

                        Box(
                            modifier =
                                Modifier.fillMaxSize(),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    "La actividad ya no está disponible."
                            )
                        }
                    }
                }

                ActivitiesUiState.NoActiveSession -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "No hay una sesión activa."
                        )
                    }
                }

                ActivitiesUiState.Error -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "No fue posible cargar la actividad."
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun AppLoadingScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()
    }
}