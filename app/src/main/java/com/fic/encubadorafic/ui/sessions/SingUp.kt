package com.fic.driver_app_ti.ui.theme.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

private val RegistroNavy = Color(0xFF142D4E)
private val RegistroBlue = Color(0xFF246BCE)
private val RegistroBackground = Color(0xFFF3F6FA)
private val RegistroMuted = Color(0xFF77869A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(
    onRegister: (
        nombre: String,
        usuario: String,
        password: String
    ) -> Unit,
    onBackToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val contrasenasCoinciden =
        password == confirmarPassword

    val formularioValido =
        nombre.isNotBlank() &&
                usuario.isNotBlank() &&
                password.isNotBlank() &&
                confirmarPassword.isNotBlank() &&
                contrasenasCoinciden

    Scaffold(
        containerColor = RegistroBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Encubadora FIC",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = RegistroNavy
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackToLogin) {
                        Text(
                            text = "←",
                            fontSize = 26.sp,
                            color = RegistroNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RegistroBackground
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.TopCenter
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
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Encabezado
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                RegistroBlue.copy(alpha = 0.10f),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegistroBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Crear cuenta",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegistroNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Regístrate para comenzar",
                        fontSize = 14.sp,
                        color = RegistroMuted
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Nombre completo
                    CampoRegistro(
                        titulo = "Nombre completo",
                        valor = nombre,
                        placeholder = "Ingresa tu nombre",
                        onValueChange = { nombre = it }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Usuario
                    CampoRegistro(
                        titulo = "Usuario",
                        valor = usuario,
                        placeholder = "Crea un nombre de usuario",
                        onValueChange = { usuario = it }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Contraseña
                    CampoRegistro(
                        titulo = "Contraseña",
                        valor = password,
                        placeholder = "Crea una contraseña",
                        onValueChange = { password = it },
                        esPassword = true,
                        mostrarPassword = mostrarPassword,
                        onTogglePassword = {
                            mostrarPassword = !mostrarPassword
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Confirmación de contraseña
                    CampoRegistro(
                        titulo = "Confirmar contraseña",
                        valor = confirmarPassword,
                        placeholder = "Repite tu contraseña",
                        onValueChange = {
                            confirmarPassword = it
                        },
                        esPassword = true,
                        mostrarPassword = mostrarConfirmacion,
                        onTogglePassword = {
                            mostrarConfirmacion =
                                !mostrarConfirmacion
                        },
                        esConfirmacion = true,
                        passwordCoincide = contrasenasCoinciden
                    )

                    if (
                        confirmarPassword.isNotEmpty() &&
                        !contrasenasCoinciden
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Las contraseñas no coinciden",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Botón de registro
                    Button(
                        onClick = {
                            onRegister(
                                nombre.trim(),
                                usuario.trim(),
                                password
                            )
                        },
                        enabled = formularioValido,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RegistroBlue
                        )
                    ) {
                        Text(
                            text = "Crear cuenta",
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
                            text = "¿Ya tienes una cuenta?",
                            color = RegistroMuted,
                            fontSize = 13.sp
                        )

                        TextButton(
                            onClick = onBackToLogin,
                            contentPadding = PaddingValues(
                                start = 6.dp,
                                end = 0.dp
                            )
                        ) {
                            Text(
                                text = "Inicia sesión",
                                color = RegistroBlue,
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

@Composable
private fun CampoRegistro(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    esPassword: Boolean = false,
    mostrarPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    esConfirmacion: Boolean = false,
    passwordCoincide: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            fontWeight = FontWeight.SemiBold,
            color = RegistroNavy,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            visualTransformation = if (
                esPassword && !mostrarPassword
            ) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (esPassword) {
                {
                    TextButton(
                        onClick = {
                            onTogglePassword?.invoke()
                        }
                    ) {
                        Text(
                            text = if (mostrarPassword)
                                "Ocultar" else "Mostrar",
                            color = RegistroBlue,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                null
            },
            isError = esConfirmacion &&
                    valor.isNotEmpty() &&
                    !passwordCoincide,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esPassword)
                    KeyboardType.Password
                else
                    KeyboardType.Text
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RegistroBlue,
                unfocusedBorderColor = Color(0xFFDCE3EC),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}