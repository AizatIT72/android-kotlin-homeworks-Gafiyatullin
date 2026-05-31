package ru.itis.android.homework7.presentation.piechart

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.itis.android.homework7.R
import ru.itis.android.homework7.ui.theme.PieChartPalette

private const val CHART_HEIGHT_DP = 300

private data class PieDataset(
    @StringRes val titleRes: Int,
    val data: List<Pair<Int, Int>>,
)

private val datasets = listOf(
    PieDataset(R.string.pie_dataset_3, listOf(1 to 25, 2 to 42, 3 to 33)),
    PieDataset(R.string.pie_dataset_4, listOf(1 to 46, 2 to 18, 3 to 27, 4 to 9)),
    PieDataset(R.string.pie_dataset_6, listOf(1 to 30, 2 to 22, 3 to 18, 4 to 14, 5 to 9, 6 to 7)),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PieChartScreen(
    onBack: () -> Unit,
) {
    var selectedDataset by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.pie_screen_title), fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.pie_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            val current = datasets[selectedDataset]
            PieChartView(
                data = current.data,
                colors = PieChartPalette,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CHART_HEIGHT_DP.dp),
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.pie_dataset_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))

            datasets.forEachIndexed { index, ds ->
                val selected = index == selectedDataset
                val percents = ds.data.joinToString { "${it.second}%" }
                Button(
                    onClick = { selectedDataset = index },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = if (selected) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    },
                ) {
                    Text(stringResource(R.string.pie_dataset_button, stringResource(ds.titleRes), percents))
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.pie_input_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}