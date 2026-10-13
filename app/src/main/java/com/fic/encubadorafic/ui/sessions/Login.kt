package com.fic.driver_app_ti.ui.theme.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF142D4E)
private val Blue = Color(0xFF246BCE)
private val Background = Color(0xFFF3F6FA)
private val Muted = Color(0xFF77869A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    onLogin: (String, String) -> Unit,
    onGoToRegister: () -> Unit,
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Encubadora FIC",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = Navy
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Encabezado
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                color = Blue.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "D",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = Blue
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "¡Bienvenido!",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Inicia sesión para continuar",
                        fontSize = 14.sp,
                        color = Muted
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Usuario
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Usuario",
                            fontWeight = FontWeight.SemiBold,
                            color = Navy,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = usuario,
                            onValueChange = { usuario = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Ingresa tu correo")
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue,
                                unfocusedBorderColor = Color(0xFFDCE3EC),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Contraseña
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Contraseña",
                            fontWeight = FontWeight.SemiBold,
                            color = Navy,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Ingresa tu contraseña")
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            visualTransformation =
                                if (mostrarPassword) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            trailingIcon = {
                                TextButton(
                                    onClick = {
                                        mostrarPassword = !mostrarPassword
                                    }
                                ) {
                                    Text(
                                        text = if (mostrarPassword)
                                            "Ocultar" else "Mostrar",
                                        color = Blue,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue,
                                unfocusedBorderColor = Color(0xFFDCE3EC),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Botón de inicio de sesión
                    Button(
                        onClick = {
                            onLogin(usuario.trim(), password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue
                        ),
                        enabled = usuario.isNotBlank() &&
                                password.isNotBlank()
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¿No tienes una cuenta?",
                            color = Muted,
                            fontSize = 13.sp
                        )

                        TextButton(
                            onClick = onGoToRegister,
                            contentPadding = PaddingValues(
                                start = 6.dp,
                                end = 0.dp
                            )
                        ) {
                            Text(
                                text = "Regístrate",
                                color = Blue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}