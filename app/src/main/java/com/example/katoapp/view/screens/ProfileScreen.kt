package com.example.katoapp.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.katoapp.R

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {
        Column(
            modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Profil",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,

            )


            Image(
                painter = painterResource(R.drawable.default_profile),
                contentDescription = "default profile",
                modifier = Modifier
                    .size(108.dp)
                    .clip(
                        shape = RoundedCornerShape(100.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.padding(vertical = 8.dp))

            Text(
                text = "Pengguna01",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.padding(vertical = 4.dp))
            Text(
                text = "Pengguna01@gmail.com",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier.padding(bottom = 24.dp))

            Row(modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ){
                ProfileItem(number = "12", label = "Prompt\nDisukai")
                ProfileItem(number = "12", label = "Prompt\nDibagikan")
                ProfileItem(number = "12", label = "Prompt\nDisiman")
            }
        }


        HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.onPrimary,
                    RoundedCornerShape(100))
                .height(44.dp)


        ) {
            Spacer(modifier.width(16.dp))
            Icon(
                painter = painterResource(R.drawable.ic_help),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
            )
            Spacer(modifier.width(8.dp))

            Text(
                text = "Bantuan pengguna",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.onPrimary,
                    RoundedCornerShape(100))
                .height(44.dp)
                .clickable(onClick = onLogoutClick)
        ) {

            Spacer(modifier.width(16.dp))
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier.width(8.dp))

            Text(
                text = "Logout",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

}

@Composable
fun ProfileItem(number: String, label: String) {
    Column(
        modifier = Modifier
            .width(92.dp) // Agar lebar terbagi rata
            .padding(horizontal = 4.dp)
            .height(100.dp) // Tinggi kotak
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(18.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}