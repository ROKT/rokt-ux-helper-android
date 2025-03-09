package com.rokt.roktux.component.extensions.countdowntimer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rokt.roktux.R
import com.rokt.roktux.component.LayoutUiModelFactory
import com.rokt.roktux.component.ModifierFactory
import com.rokt.roktux.component.color
import com.rokt.roktux.component.extensions.ExtensionComposableComponent
import com.rokt.roktux.component.extensions.ExtensionData
import com.rokt.roktux.viewmodel.layout.LayoutContract
import com.rokt.roktux.viewmodel.layout.OfferUiState
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale

internal class CountDownTimerComponent(
    private val factory: LayoutUiModelFactory,
    private val modifierFactory: ModifierFactory,
) : ExtensionComposableComponent<CountdownTimerModel> {

    @Composable
    override fun Render(
        model: CountdownTimerModel,
        data: ExtensionData,
        modifier: Modifier,
        isPressed: Boolean,
        offerState: OfferUiState,
        isDarkModeEnabled: Boolean,
        breakpointIndex: Int,
        onEventSent: (LayoutContract.LayoutEvent) -> Unit,
    ) {
        var timeLeft by remember { mutableStateOf(model.duration) }

        LaunchedEffect(offerState.currentOfferIndex) {
            timeLeft = model.duration
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
        }

        TimerText(timeLeft, model.backgroundColor, model.textColor, model.textSize)
        if (timeLeft <= 0) {
            timeLeft = model.duration
            onEventSent(LayoutContract.LayoutEvent.LayoutVariantNavigated(offerState.currentOfferIndex + 1))
        }
    }

    override fun getSerializer() = CountdownTimerModel.serializer()
}

@Composable
internal fun TimerText(secondsLeft: Int, backgroundColor: String, textColor: String, textSize: Int) {
    Row(
        modifier = Modifier
            .background(color = "#f2f4f7".color, shape = RoundedCornerShape(30.dp))
            .padding(2.dp),
    ) {
        Text(
            text = "Award",
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically),
            fontFamily = FontFamily(Font(R.font.rokt_icons)),
            color = backgroundColor.color,
        )
        Box(
            modifier = Modifier
                .background(
                    color = backgroundColor.color,
                    shape = RoundedCornerShape(30.dp),
                )
                .padding(4.dp),
        ) {
            Text(
                text = String.format(Locale.ENGLISH, "%02d:%02d", secondsLeft / 60, secondsLeft % 60),
                color = textColor.color,
                fontSize = textSize.sp,
            )
        }
    }
}

@Serializable
class CountdownTimerModel(
    @SerialName("duration")
    val duration: Int,
    @SerialName("backgroundColor")
    val backgroundColor: String,
    @SerialName("textColor")
    val textColor: String,
    @SerialName("textSize")
    val textSize: Int,
)
