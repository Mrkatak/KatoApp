package com.example.katoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.katoapp.view.screens.DashboardUserScreen
import com.example.katoapp.view.screens.LoginScreen
import com.example.katoapp.view.screens.ProfileScreen
import com.example.katoapp.view.screens.RegisterScreen
import com.example.katoapp.view.screens.ResetPassScreen
import com.example.katoapp.view.screens.SavePromptScreen
import com.example.katoapp.view.screens.SharingPromptScreen
import com.example.katoapp.viewModel.AuthViewModel


// Data class untuk navbar
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    object Dashboard : BottomNavItem(
        "dashboard",
        "Dashboard",
        R.drawable.ic_home,
        R.drawable.ic_home
    )
    object Sharing : BottomNavItem(
        "sharing",
        "Sharing",
        R.drawable.ic_sharing,
        R.drawable.ic_sharing
    )
    object Simpan : BottomNavItem(
        "simpan",
        "Simpan",
        R.drawable.ic_save,
        R.drawable.ic_save
    )
    object Profil : BottomNavItem(
        "profil",
        "Profil",
        R.drawable.ic_profile,
        R.drawable.ic_profile
    )
}


@Composable
fun Navigation(
    startDestination: String
) {
    // NavController INDUK: Untuk pindah dari Login -> Dashboard
    val rootNavController = rememberNavController()
    val viewModel = hiltViewModel<AuthViewModel>()


    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {
        composable( "LoginScreen") { LoginScreen(
                navController = rootNavController,
                viewModel = viewModel
        ) }

        composable( "RegisterScreen") { RegisterScreen(
            navController = rootNavController,
            viewModel = viewModel
        ) }

        composable("ResetPassScreen") { ResetPassScreen(
            navController = rootNavController,
            viewModel = viewModel
        ) }

        // Saat masuk ke Dashboard, kita panggil Screen Container di bawah
        composable("MainUserScreen") {
            MainUserScreen(rootNavController = rootNavController, viewModel = viewModel)
        }
    }
}

// --- 3. DASHBOARD CONTAINER (Scaffold + BottomBar) ---
@Composable
fun MainUserScreen(
    rootNavController: NavController, // Diterima jika nanti butuh Logout
    viewModel: AuthViewModel
) {
    // NavController ANAK: Khusus untuk pindah-pindah TAB (Dashboard, Sharing, dll)
    val dashboardNavController = rememberNavController()

    // Daftar Menu
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Sharing,
        BottomNavItem.Simpan,
        BottomNavItem.Profil
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                // Logic mendeteksi tab mana yang aktif
                val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            dashboardNavController.navigate(item.route) {
                                // Agar saat back button ditekan tidak menumpuk stack
                                popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(text = item.title) },
                        icon = {
                            Icon(
                                // Menggunakan painterResource karena icon Anda dari Drawable (Int)
                                painter = painterResource(id = if (isSelected) item.selectedIcon else item.unselectedIcon),
                                contentDescription = item.title
                            )
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        // --- 4. NAV HOST ANAK (NESTED NAVIGATION) ---
        // Ini mengatur konten yang berubah-ubah di atas Bottom Bar
        NavHost(
            navController = dashboardNavController,
            startDestination = BottomNavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding) // PENTING: Agar konten tidak tertutup navbar
        ) {
            composable(BottomNavItem.Dashboard.route) {
                DashboardUserScreen()
            }
            composable(BottomNavItem.Sharing.route) {
                SharingPromptScreen()
            }
            composable(BottomNavItem.Simpan.route) {
                SavePromptScreen()
            }
            composable(BottomNavItem.Profil.route) {
                ProfileScreen(
                    onLogoutClick = {

                        println("Tombol Logout ditekan!")
                        // Hapus Sesi
                        viewModel.logout()

                        // 2. Navigasi pakai ROOT NavController (Induk)
                        // Karena Si Induk-lah yang tahu jalan ke "LoginScreen"
                        rootNavController.navigate("LoginScreen") {
                            popUpTo("MainUserScreen") {inclusive = true}
                        }
                    }

                )
            }
        }
    }
}
