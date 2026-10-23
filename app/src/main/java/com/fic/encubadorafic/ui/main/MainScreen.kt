package com.fic.encubadorafic.ui.main

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
    onNavigateToProject: (Long) -> Unit,
    onCreateProject: () -> Unit,
    categories: List<com.fic.encubadorafic.data.auth.Category>,
    categoriesLoading: Boolean,
    categoriesError: String?,
    onReloadCategories: () -> Unit,
    userName: String,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCategories: () -> Unit,
) {

    androidx.compose.runtime.LaunchedEffect(Unit) { viewModel.cargarRutas() }
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsState()

    val pager = rememberPagerState(
        pageCount = { HomeTabs.entries.size })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var listaColeccion = listOf<String>(
        "Mis proyectos",
        "Proyectos guardados"
    )

    var selectedCategoryId by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<Long?>(null) }
    androidx.compose.runtime.LaunchedEffect(categories, categoriesLoading) {
        if (!categoriesLoading && categories.none { it.id == selectedCategoryId }) selectedCategoryId = null
    }
    val visibleProjects = state.proyectos.filter { selectedCategoryId == null || it.category_id == selectedCategoryId }
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

                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            onNavigateToProfile()
                        },
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
                        fontSize = 18.sp,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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

                    Column(Modifier.fillMaxWidth()) {
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
                                onClick = { selectedCategoryId = null; scope.launch { drawerState.close() } },
                            )
                        }
                    }

                    Text(
                        text = "Categorias"
                    )

                    Column(Modifier.fillMaxWidth().heightIn(max = 280.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                        if (!categoriesLoading && categories.isEmpty()) Text(if (categoriesError != null) "No se pudieron cargar las categorías" else "Sin categorías activas")
                        categories.forEach { categoria ->
                            NavigationDrawerItem(
                                label = {

                                    Row(
                                        Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                                            contentDescription = "prueba"
                                        )

                                        Text(text = categoria.name)
                                    }
                                },
                                selected = selectedCategoryId == categoria.id,
                                onClick = { selectedCategoryId = categoria.id; scope.launch { drawerState.close() } },
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        selected = false,
                        onClick = onLogout
                    )
                }
            }
        }
    ) {

        Scaffold(
            floatingActionButton = {
                androidx.compose.material3.ExtendedFloatingActionButton(onClick = onCreateProject,
                    containerColor = Color(0xFF246BCE), contentColor = Color.White) { Text("+ Crear proyecto") }
            },
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

                val projects = when (HomeTabs.entries[page]) {
                    HomeTabs.Popular -> visibleProjects.sortedByDescending { it.reactionsCount }
                    HomeTabs.Reciente -> visibleProjects.sortedByDescending { it.created_at }
                    else -> visibleProjects
                }
                Column(Modifier.fillMaxSize()) {
                    androidx.compose.material3.TextButton(onClick = viewModel::cargarRutas, enabled = !state.cargando) { Text("Actualizar proyectos") }
                    if (state.cargando) androidx.compose.material3.LinearProgressIndicator(Modifier.fillMaxWidth())
                    state.error?.let { Text(it, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error) }
                    LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 88.dp)) {
                        if (!state.cargando && state.error == null && projects.isEmpty()) item { Text("No hay proyectos en esta categoría.", Modifier.padding(20.dp)) }
                        items(projects.size, key = { projects[it].id }) { index ->
                            val project = projects[index]
                            ProyectItem(name = project.title, description = project.summary,
                                category = categories.firstOrNull { it.id == project.category_id }?.name ?: "Categoría no disponible",
                                categoryId = project.category_id, coverUrl = project.cover_image,
                                onClick = { onNavigateToProject(project.id) })
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