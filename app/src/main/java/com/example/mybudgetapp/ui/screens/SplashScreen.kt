import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(navController: NavController) {
    // State for animation
    var scale by remember { mutableStateOf(0.5f) }  // Start small

    // Animate the scale value
    val animatedScale by animateFloatAsState(
        targetValue = if (scale == 0.5f) 1.2f else 0.5f,  // Scale between 0.5 to 1.2
        animationSpec = tween(
            durationMillis = 1000,        // Animation duration
            easing = FastOutSlowInEasing  // Smooth easing
        )
    )

    // Delay for 3 seconds before navigating
    LaunchedEffect(Unit) {
        while (true) {
            scale = if (scale == 0.5f) 1.2f else 0.5f  // Toggle scaling
            delay(1000L)  // Repeat every second
        }
    }

    // Navigate based on authentication status
    LaunchedEffect(Unit) {
        delay(3000L) // Wait for animation

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {Log.d("Splash", "User is logged in")
            navController.navigate("home") {
                popUpTo("sign_in") { inclusive = true }
            }
        } else {
            Log.d("Splash", "User is not logged in")
            navController.navigate("sign_in") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    // UI for Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        // Animated Image
        Image(
            painter = painterResource(id = R.drawable.logomain),
            contentDescription = "App Logo",
            modifier = Modifier.size((750 * animatedScale).dp) // Scale the size
        )
    }
}
