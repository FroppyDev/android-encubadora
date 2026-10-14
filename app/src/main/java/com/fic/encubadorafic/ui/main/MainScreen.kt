package com.fic.encubadorafic.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fic.encubadorafic.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit,
    userName: String,
    onLogout: () -> Unit,
) {

    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsState()

    val pager = rememberPagerState(
        pageCount = { HomeTabs.entries.size })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var listaColeccion = listOf<String>(
        "Todos los proyectos"
    )
    var listaCategorias = listOf<String>(
        "Arte",
        "Cine y video",
        "Comida",
        "Juegos",
        "Musica"
    )

    ModalNavigationDrawer(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RectangleShape),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.widthIn(max = 300.dp),
                drawerShape = RectangleShape,
                drawerContainerColor = Color(Color.White.value)
            ) {

                NavigationDrawerItem(label = { Text("Cerrar sesión") }, selected = false, onClick = onLogout)
                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        text = userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 10.dp))


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = "Colecciones"
                    )

                    Column (Modifier.fillMaxWidth()) {
                        listaColeccion.forEach { coleccion ->
                            NavigationDrawerItem(
                                label = {

                                    Row(
                                        Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                                            contentDescription = "prueba"
                                        )

                                        Text(text = coleccion)
                                    }
                                },
                                selected = false,
                                onClick = {

                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = "Categorias"
                    )

                    Column (Modifier.fillMaxWidth()) {
                        listaCategorias.forEach { categoria ->
                            NavigationDrawerItem(
                                label = {

                                    Row(
                                        Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                                            contentDescription = "prueba"
                                        )

                                        Text(text = categoria)
                                    }
                                },
                                selected = false,
                                onClick = {

                                },
                            )
                        }
                    }
                }
            }
        }
    ) {

        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TopAppBar(title = {
                        Text("Encubadora FIC")
                    }, navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    }, actions = {
                        IconButton(onClick = {
                            onNavigateToSearch()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Localized description"
                            )
                        }
                    })

                    SecondaryTabRow(
                        selectedTabIndex = pager.currentPage
                    ) {
                        HomeTabs.entries.forEachIndexed { index, tab ->
                            Tab(selected = pager.currentPage == index, onClick = {
                                scope.launch {
                                    pager.animateScrollToPage(index)
                                }
                            }, text = {
                                Text(
                                    text = tab.text, maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }, icon = {
                                Icon(
                                    imageVector = if (pager.currentPage == index) {
                                        tab.selectedIcon
                                    } else {
                                        tab.unSelectedIcon
                                    }, contentDescription = tab.text
                                )
                            })
                        }
                    }
                }
            }) { paddingValues ->

            HorizontalPager(
                state = pager,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                userScrollEnabled = false
            ) { page ->

                when (HomeTabs.entries[page]) {
                    HomeTabs.Explorar -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {

                            items(state.proyectos.size, key = { item -> item }) { item ->

                                ProyectItem(
                                    name = state.proyectos[item].title,
                                    description = state.proyectos[item].description,
                                    category = state.proyectos[item].summary
                                )

                            }


                        }
                    }

                    HomeTabs.Popular -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            Text("Pagina Popular")

                        }
                    }

                    HomeTabs.Reciente -> {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            Text("Pagina Reciente")

                        }
                    }
                }
            }
        }

    }

}

enum class HomeTabs(
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val text: String,
) {
    Explorar(
        selectedIcon = Icons.Filled.Explore,
        unSelectedIcon = Icons.Outlined.Explore,
        text = "Explorar"
    ),

    Popular(
        selectedIcon = Icons.Filled.Star,
        unSelectedIcon = Icons.Outlined.StarBorder,
        text = "Popular"
    ),

    Reciente(
        selectedIcon = Icons.Filled.AccessTimeFilled,
        unSelectedIcon = Icons.Outlined.AccessTime,
        text = "Lo mas Reciente"
    )


}