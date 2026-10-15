package com.fic.encubadorafic.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fic.encubadorafic.R

@Preview
@Composable
fun ProyectItem(
    modifier: Modifier = Modifier,
    name: String = "Nombre del proyecto",
    description: String = "Descripcion del proyecto",
    category: String = "Categoria del proyecto",
    categoryId: Long = 1L,
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


            com.fic.encubadorafic.ui.projects.ProjectCover(categoryId, Modifier.height(150.dp))

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