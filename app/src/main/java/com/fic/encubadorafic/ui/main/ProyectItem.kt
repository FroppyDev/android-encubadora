package com.fic.encubadorafic.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ProyectItem(
    modifier: Modifier = Modifier,
    name: String = "Nombre del proyecto",
    description: String = "Descripcion del proyecto",
    category: String = "Categoria del proyecto",
    categoryId: Long = 1L,
    coverUrl: String? = null,
    onClick: () -> Unit = {},
) {

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {


            if (coverUrl != null) com.fic.encubadorafic.ui.perfil.RemoteImage(coverUrl, "Portada del proyecto", enabled = false, onClick = {}, modifier = Modifier.fillMaxWidth().height(150.dp))
            else com.fic.encubadorafic.ui.projects.ProjectCover(categoryId, Modifier.height(150.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = name,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    text = description,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = category
                )

            }


        }

    }

}