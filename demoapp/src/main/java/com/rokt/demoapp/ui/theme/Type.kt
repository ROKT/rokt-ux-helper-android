package com.rokt.demoapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rokt.demoapp.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
     */
)

// Archievo
private val regular = Font(R.font.font_archivo_regular, FontWeight.Normal)
private val bold = Font(R.font.font_archivo_semibold, FontWeight.Bold)

// Lato
private val latoNormal = Font(R.font.lato_regular, FontWeight.Normal)
private val latoBold = Font(R.font.lato_bold, FontWeight.Bold)

// Arial
private val arialNormal = Font(R.font.arial, FontWeight.Normal)
private val arialBold = Font(R.font.arial_bold, FontWeight.Bold)

// Font Families
val DefaultFontFamily = FontFamily(regular, bold)
val Lato = FontFamily(latoNormal, latoBold)
val Arial = FontFamily(arialNormal, arialBold)
