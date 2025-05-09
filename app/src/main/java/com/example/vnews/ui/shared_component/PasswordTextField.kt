//package com.example.vnews.ui.components
//
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//
//@Composable
//fun PasswordTextField(password: String, onPasswordChange: (String) -> Unit) {
//    var passwordVisible by remember { mutableStateOf(false) }
//
//    OutlinedTextField(
//        value = password,
//        onValueChange = onPasswordChange,
//        label = { Text("Password") },
//        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//        trailingIcon = {
//            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
//            IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
//            }
//        },
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//        singleLine = true
//    )
//}
