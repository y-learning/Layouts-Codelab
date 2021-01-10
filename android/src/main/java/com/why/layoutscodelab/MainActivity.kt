package com.why.layoutscodelab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.why.layoutscodelab.themes.LayoutsCodelabTheme
import kotlin.math.max

val topics = listOf(
    "Arts & Crafts",
    "Beauty",
    "Books",
    "Business",
    "Comics",
    "Culinary",
    "Design",
    "Fashion",
    "Film",
    "History",
    "Maths",
    "Music",
    "People",
    "Philosophy",
    "Religion",
    "Social sciences",
    "Technology",
    "TV",
    "Writing"
)

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rowsCount: Int = 3,
    content: @Composable () -> Unit
) = Layout(
    modifier = modifier,
    content = content
) { measurables, constraints ->
    // Keep track of the width and max height of each row.
    val rowWidths = IntArray(rowsCount) { 0 }
    val rowMaxHeights = IntArray(rowsCount) { 0 }

    val placeables = measurables.mapIndexed { index, measurable ->
        val placeable = measurable.measure(constraints)

        val rowIndex = index % rowsCount

        rowWidths[rowIndex] = rowWidths[rowIndex] + placeable.width
        rowMaxHeights[rowIndex] = max(rowMaxHeights[rowIndex], placeable.height)

        placeable
    }

    val width = rowWidths.maxOrNull()
        ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))
        ?: constraints.minWidth

    val height = rowMaxHeights.sum()
        .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

    val rowsYCoordinates = IntArray(rowsCount) { 0 }

    for (i in 1 until rowsCount) {
        val pre = i - 1
        rowsYCoordinates[i] = rowsYCoordinates[pre] + rowMaxHeights[pre]
    }

    layout(width, height) {
        placeables.foldIndexed(IntArray(rowsCount) { 0 }) { i, itemsXs, item ->
            val rowIndex = i % rowsCount

            item.placeRelative(itemsXs[rowIndex], rowsYCoordinates[rowIndex])

            itemsXs[rowIndex] += item.width

            itemsXs
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .preferredSize(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.preferredWidth(4.dp))
            Text(text = text)
        }
    }
}

@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) = Layout(
    content = children,
    modifier = modifier
) { measurables, constraints ->
    val placeables = measurables.map { it.measure(constraints) }

    layout(constraints.maxWidth, constraints.maxHeight) {
        placeables.fold(0) { y, placeable ->
            placeable.placeRelative(0, y)

            y + placeable.height
        }
    }
}

fun Modifier.firstBaselineToTop(firstBaselineToTop: Dp) =
    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        // Calculate y position for placeable, where firstBaseline is
        // not a position relative to 0.
        val placeableY = firstBaselineToTop.toIntPx() - firstBaseline

        // the height of the placeable is the position y + the placeable height
        val height = placeableY + placeable.height

        layout(placeable.width, height) {
            placeable.placeRelative(0, placeableY)
        }
    }

@Composable
fun Avatar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.preferredSize(50.dp),
        shape = CircleShape,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
    ) {}
}

@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    Row(
        modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.surface)
            .clickable(onClick = { /*TODO*/ })
            .padding(8.dp)
    ) {
        Avatar()
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text("Alfred Sisley", fontWeight = FontWeight.Bold)
            Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                Text("3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
private fun ScreenContent(modifier: Modifier = Modifier) {
    ScrollableRow(modifier = modifier) {
        StaggeredGrid(rowsCount = 5) {
            topics.forEach { topic ->
                Chip(modifier = Modifier.padding(8.dp), text = topic)
            }
        }
    }
}

@Composable
fun NavigationDrawer() {
    Column {
        Row(
            modifier = Modifier
                .padding(vertical = 32.dp, horizontal = 16.dp)
        ) {
            Avatar(Modifier.preferredSize(72.dp))
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    "Alfred Sisley",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        Divider()
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Item 1")
            Text("Item 2")
            Text("Item 3")
        }
    }
}

@Composable
fun LayoutsCodelab() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "LayoutsCodelab") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Favorite)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation {
                Row(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                ) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Add)
                    }
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Create)
                    }
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Email)
                    }
                }
            }
        },
        drawerContent = {
            NavigationDrawer()
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.ThumbUp)
            }
        }
    ) { innerPadding ->
        ScreenContent(
            Modifier
                .padding(innerPadding)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationDrawerPreview() {
    LayoutsCodelabTheme {
        NavigationDrawer()
    }
}

@Preview(showBackground = true)
@Composable
fun LayoutsCodelabPreview() {
    LayoutsCodelabTheme {
        LayoutsCodelab()
    }
}

@Preview(showBackground = true)
@Composable
fun PhotographerCardPreview() {
    LayoutsCodelabTheme {
        PhotographerCard()
    }
}

@Preview(showBackground = true)
@Composable
fun TextWithPaddingToBaselinePreview() {
    LayoutsCodelabTheme {
        Text(text = "Hi There!", modifier = Modifier.firstBaselineToTop(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TextWithNormalPaddingPreview() {
    LayoutsCodelabTheme {
        Text("Hi there!", Modifier.padding(top = 24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MyOwnColumnPreview() {
    LayoutsCodelabTheme {
        MyOwnColumn {
            Text("Hi 1!")
            Text("Hi 2!")
            Text("Hi 3!")
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    LayoutsCodelabTheme {
        Chip(text = "Hi there")
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LayoutsCodelab()
        }
    }
}
