package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.donutChart.DonutChart
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle
import org.babetech.borastock.domain.usecase.ChartData
import org.babetech.borastock.domain.usecase.GetChartDataUseCase
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

data class ChartType(
    val key: String,
    val title: String,
    val icon: Painter,
    val description: String
)

@Composable
fun GraphicSwitcherScreen(
    onBackClick: (() -> Unit)? = null,
    showBackButton: Boolean = false
) {
    val getChartDataUseCase: GetChartDataUseCase = koinInject()
    val chartData by getChartDataUseCase().collectAsStateWithLifecycle(
        initialValue = ChartData(
            stockEvolutionData = emptyList(),
            categoryDistribution = emptyList(),
            supplierDistribution = emptyList(),
            movementTrends = emptyList(),
            profitabilityData = emptyList()
        )
    )
    
    var selectedChart by remember { mutableStateOf("Line") }

    val chartTypes = listOf(
        ChartType("Line", "Évolution", painterResource(Res.drawable.analytics), "Évolution du stock"),
        ChartType("Bar", "Mouvements", painterResource(Res.drawable.barchart), "Entrées/Sorties"),
        ChartType("Pie", "Catégories", painterResource(Res.drawable.piechart), "Par catégorie"),
        ChartType("Donut", "Fournisseurs", painterResource(Res.drawable.donutlarge), "Par fournisseur"),
        ChartType("Radar", "Performance", painterResource(Res.drawable.analytics), "Vue globale")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with back button
        if (showBackButton && onBackClick != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Analyse détaillée",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Chart type selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chartTypes) { chartType ->
                ElevatedCard(
                    onClick = { selectedChart = chartType.key },
                    modifier = Modifier.width(140.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selectedChart == chartType.key) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = chartType.icon,
                            contentDescription = chartType.title,
                            tint = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            chartType.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            chartType.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Dynamic chart display
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedChart) {
                "Line" -> DynamicLineChart(chartData.stockEvolutionData)
                "Bar" -> DynamicBarChart(chartData.movementTrends)
                "Pie" -> DynamicPieChart(chartData.categoryDistribution)
                "Donut" -> DynamicDonutChart(chartData.supplierDistribution)
                "Radar" -> DynamicRadarChart(chartData.profitabilityData)
            }
        }
    }
}

@Composable
fun DynamicLineChart(data: List<StockEvolutionPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée d'évolution disponible")
        return
    }

    val lineParameters = listOf(
        LineParameters(
            label = "Valeur du stock",
            data = data.map { it.totalValue },
            lineColor = Color(0xFF3B82F6),
            lineType = LineType.CURVED_LINE,
            lineShadow = true
        ),
        LineParameters(
            label = "Nombre d'articles",
            data = data.map { it.totalItems.toDouble() * 100 }, // Scale for visibility
            lineColor = Color(0xFF10B981),
            lineType = LineType.CURVED_LINE,
            lineShadow = true
        )
    )

    LineChart(
        modifier = Modifier.fillMaxSize(),
        linesParameters = lineParameters,
        isGrid = true,
        gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        xAxisData = data.map { it.date },
        animateChart = true,
        showGridWithSpacer = true,
        yAxisStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        xAxisStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W400
        ),
        yAxisRange = 10,
        oneLineChart = false,
        gridOrientation = GridOrientation.VERTICAL
    )
}

@Composable
fun DynamicBarChart(data: List<MovementTrendPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée de mouvement disponible")
        return
    }

    val barParameters = listOf(
        BarParameters(
            dataName = "Entrées",
            data = data.map { it.entries },
            barColor = Color(0xFF10B981)
        ),
        BarParameters(
            dataName = "Sorties",
            data = data.map { it.exits },
            barColor = Color(0xFFEF4444)
        ),
        BarParameters(
            dataName = "Net",
            data = data.map { it.netMovement },
            barColor = Color(0xFF3B82F6)
        )
    )

    BarChart(
        chartParameters = barParameters,
        gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        xAxisData = data.map { it.date },
        isShowGrid = true,
        animateChart = true,
        showGridWithSpacer = true,
        yAxisStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        xAxisStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W400
        ),
        yAxisRange = 10,
        barWidth = 20.dp
    )
}

@Composable
fun DynamicPieChart(data: List<CategoryData>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée de catégorie disponible")
        return
    }

    val pieChartData = data.map { category ->
        PieChartData(
            partName = category.category,
            data = category.value,
            color = category.color
        )
    }

    PieChart(
        modifier = Modifier.fillMaxSize(),
        pieChartData = pieChartData,
        ratioLineColor = MaterialTheme.colorScheme.outline,
        textRatioStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )
    )
}

@Composable
fun DynamicDonutChart(data: List<SupplierData>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée de fournisseur disponible")
        return
    }

    val pieChartData = data.map { supplier ->
        PieChartData(
            partName = supplier.supplierName,
            data = supplier.value,
            color = supplier.color
        )
    }

    DonutChart(
        modifier = Modifier.fillMaxSize(),
        pieChartData = pieChartData,
        centerTitle = "Fournisseurs",
        centerTitleStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        outerCircularColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        innerCircularColor = MaterialTheme.colorScheme.surface,
        ratioLineColor = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun DynamicRadarChart(data: List<ProfitabilityPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée de profitabilité disponible")
        return
    }

    val labels = data.map { it.category }
    val profitValues = data.map { maxOf(it.profit, 0.0) } // Ensure positive values
    val revenueValues = data.map { it.revenue }

    RadarChart(
        modifier = Modifier.fillMaxSize(),
        radarLabels = labels,
        labelsStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        ),
        netLinesStyle = NetLinesStyle(
            netLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            netLinesStrokeWidth = 1f,
            netLinesStrokeCap = StrokeCap.Round
        ),
        scalarSteps = 5,
        scalarValue = profitValues.maxOrNull() ?: 100.0,
        scalarValuesStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        ),
        polygons = listOf(
            Polygon(
                values = profitValues,
                unit = "€",
                style = PolygonStyle(
                    fillColor = Color(0xFF10B981),
                    fillColorAlpha = 0.3f,
                    borderColor = Color(0xFF10B981),
                    borderColorAlpha = 0.8f,
                    borderStrokeWidth = 2f,
                    borderStrokeCap = StrokeCap.Round
                )
            ),
            Polygon(
                values = revenueValues,
                unit = "€",
                style = PolygonStyle(
                    fillColor = Color(0xFF3B82F6),
                    fillColorAlpha = 0.2f,
                    borderColor = Color(0xFF3B82F6),
                    borderColorAlpha = 0.6f,
                    borderStrokeWidth = 2f,
                    borderStrokeCap = StrokeCap.Round
                )
            )
        )
    )
}

@Composable
private fun EmptyChartPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.analytics),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Compact chart preview for AccueilScreen
@Composable
fun CompactStockChart(chartData: ChartData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Évolution du stock",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (chartData.stockEvolutionData.isNotEmpty()) {
                val lineParameters = listOf(
                    LineParameters(
                        label = "Valeur",
                        data = chartData.stockEvolutionData.map { it.totalValue },
                        lineColor = MaterialTheme.colorScheme.primary,
                        lineType = LineType.CURVED_LINE,
                        lineShadow = true
                    )
                )

                LineChart(
                    modifier = Modifier.fillMaxSize(),
                    linesParameters = lineParameters,
                    isGrid = false,
                    xAxisData = chartData.stockEvolutionData.map { it.date },
                    animateChart = true,
                    showGridWithSpacer = false,
                    yAxisStyle = TextStyle(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    xAxisStyle = TextStyle(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    yAxisRange = 5,
                    oneLineChart = true,
                    gridOrientation = GridOrientation.HORIZONTAL
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Chargement des données...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}