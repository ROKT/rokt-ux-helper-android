package com.rokt.demoapp.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rokt.demoapp.R
import com.rokt.demoapp.ui.theme.DefaultFontFamily

const val BUTTON_HEIGHT = 64
const val BUTTON_FONT_SIZE = 20
const val BUTTON_CORNER_RADIUS = 100
const val TUTORIAL_ITEM_HEIGHT = 92

@Composable
fun ButtonDark(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp),
    ) {
        ButtonText(text = text)
    }
}

@Composable
fun ButtonLight(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .height(BUTTON_HEIGHT.dp),
        ),
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.tertiary,
        ),
        shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
    ) {
        ButtonText(text = text)
    }
}

@Composable
fun ButtonWithDescriptionLight(modifier: Modifier = Modifier, text: String, description: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .height(TUTORIAL_ITEM_HEIGHT.dp),
        ),
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.tertiary,
        ),
        shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1.0F), horizontalAlignment = Alignment.CenterHorizontally) {
                ButtonText(text = text)
                Text(
                    text = description,
                    fontSize = 16.sp,
                    fontFamily = DefaultFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_primary_button),
                contentDescription = stringResource(R.string.content_description_arrow_go),
                modifier = Modifier.clip(CircleShape),
            )
        }
    }
}

@Composable
fun ButtonText(text: String) {
    Text(
        text,
        fontSize = BUTTON_FONT_SIZE.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun BackButton(backPressed: () -> Unit, tintColor: Color = Color.White) {
    IconButton(onClick = { backPressed.invoke() }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = stringResource(R.string.content_description_back),
            tint = tintColor,
        )
    }
}
