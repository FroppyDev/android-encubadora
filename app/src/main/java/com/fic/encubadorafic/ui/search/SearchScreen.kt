package com.fic.encubadorafic.ui.search

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fic.encubadorafic.ui.main.HomeTabs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScren() {

    var searchText by remember {
        mutableStateOf("")
    }
    var expanded by remember { mutableStateOf(false) }
    var listaCategorias = listOf<String>(
        "Arte",
        "Cine y video",
        "Comida",
        "Juegos",
        "Musica"
    )


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                TopAppBar(title = {

                    OutlinedTextField(

                        value = searchText,

                        onValueChange = {
                            searchText = it
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        placeholder = {
                            "Busca un proyecto..."
                        },

                        leadingIcon = {
                            Icon(Icons.Default.Search, null)
                        },

                        singleLine = true,

                        shape = RoundedCornerShape(20.dp)
                    )


                }, navigationIcon = {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                })

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {

                    Text("137 resultados encontrados")

                    Spacer(modifier = Modifier.weight(1f))

                    Box() {
                        OutlinedButton(
                            shape = RoundedCornerShape(5.dp),
                            onClick = {
                                expanded = !expanded
                            }
                        ) {
                            Text("Categoria")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listaCategorias.forEach { categoria ->

                                DropdownMenuItem(
                                    text = {
                                        Text(text = categoria)
                                    },
                                    onClick = {
                                        expanded = false
                                    }
                                )

                            }
                        }
                    }

                }
            }
        }) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { }

    }

}