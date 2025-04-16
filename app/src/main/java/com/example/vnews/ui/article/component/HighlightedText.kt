//package com.example.vnews.ui.article.component
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.LocalTextStyle
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//
//
//@Composable
//fun HighlightedText(
//    text: String,
//    highlightStart: Int = -1,
//    highlightEnd: Int = -1,
//    style: TextStyle = LocalTextStyle.current,
//    textAlign: TextAlign? = null,
//    highlightColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
//    modifier: Modifier = Modifier
//) {
//    val shouldHighlight = highlightStart in 0..<highlightEnd
//
//    if (shouldHighlight) {
//        Box(
//            modifier = modifier
//                .clip(RoundedCornerShape(8.dp))
//                .background(highlightColor)
//                .padding(horizontal = 8.dp, vertical = 4.dp)
//        ) {
//            Text(
//                text = text,
//                style = style,
//                textAlign = textAlign
//            )
//        }
//    } else {
//        Text(
//            text = text,
//            style = style,
//            textAlign = textAlign,
//            modifier = modifier
//        )
//    }
//}