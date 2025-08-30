package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import org.babetech.borastock.domain.usecase.GetChartDataUseCase
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphiquesDetailsScreen(
    onBackClick: () -> Unit
) {
    val getChartDataUseCase: GetChartDataUseCase = koinInject()
    val chartData by getChartDataUseCase().collectAsStateWithLifecycle(
        initialValue = org.babetech.borastock.domain.usecase.ChartData(
            stockEvolutionData = emptyList(),
            categoryDistribution = emptyList(),
            supplierDistribution = emptyList(),
            movementTrends = emptyList(),
            profitabilityData = emptyList()
        )
    )
    
    var selectedPeriod by remember { mutableStateOf("7j") }
    var refreshing by remember { mutableStateOf(false) }
    
    val periods = listOf(
        "7j" to "7 jours",
        "30j" to "30 jours", 
        "3m" to "3 mois",
        "1a" to "1 an"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Analytics Avancées",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            refreshing = true
                            // Simulate refresh
                            refreshing = false
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh, 
                            contentDescription = "Actualiser",
                            modifier = if (refreshing) Modifier.animateContentSize() else Modifier
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Period selector
            item {
                PeriodSelectorCard(
                    periods = periods,
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { selectedPeriod = it }
                )
            }

            // Stock Evolution Chart
            item {
                ChartCard(
                    title = "Évolution du Stock",
                    subtitle = "Valeur et quantité dans le temps"
                ) {
                    DynamicLineChart(chartData.stockEvolutionData)
                }
            }

            // Movement Trends Chart
            item {
                ChartCard(
                    title = "Tendances des Mouvements",
                    subtitle = "Entrées vs Sorties"
                ) {
                    DynamicBarChart(chartData.movementTrends)
                }
            }

            // Category Distribution
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ChartCard(
                        title = "Répartition par Catégorie",
                        subtitle = "Valeur du stock",
                        modifier = Modifier.weight(1f)
                    ) {
                        DynamicPieChart(chartData.categoryDistribution)
                    }
                    
                    ChartCard(
                        title = "Top Fournisseurs",
                        subtitle = "Contribution au stock",
                        modifier = Modifier.weight(1f)
                    ) {
                        DynamicDonutChart(chartData.supplierDistribution)
                    }
                }
            }

            // Profitability Analysis
            item {
                ChartCard(
                    title = "Analyse de Profitabilité",
                    subtitle = "Performance par catégorie"
                ) {
                    DynamicRadarChart(chartData.profitabilityData)
                }
            }

            // Key Insights Section
            item {
                KeyInsightsSection(chartData)
            }
        }
    }
}

@Composable
private fun PeriodSelectorCard(
    periods: List<Pair<String, String>>,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                text = "Période d'analyse",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(periods) { (value, label) ->
                    FilterChip(
                        onClick = { onPeriodSelected(value) },
                        label = { Text(label) },
                        selected = selectedPeriod == value,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                content()
            }
        }
    }
}

@Composable
private fun KeyInsightsSection(chartData: org.babetech.borastock.domain.usecase.ChartData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.analytics),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Insights Clés",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Generate insights based on data
            val insights = generateInsights(chartData)
            
            insights.forEach { insight ->
                InsightItem(
                    title = insight.title,
                    description = insight.description,
                    type = insight.type
                )
            }
        }
    }
}

@Composable
private fun InsightItem(
    title: String,
    description: String,
    type: InsightType
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when (type) {
                        InsightType.POSITIVE -> Color(0xFF22c55e).copy(alpha = 0.1f)
                        InsightType.WARNING -> Color(0xFFf59e0b).copy(alpha = 0.1f)
                        InsightType.NEGATIVE -> Color(0xFFef4444).copy(alpha = 0.1f)
                        InsightType.INFO -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = when (type) {
                    InsightType.POSITIVE -> painterResource(Res.drawable.trendingup)
                    InsightType.WARNING -> painterResource(Res.drawable.warning)
                    InsightType.NEGATIVE -> painterResource(Res.drawable.trendingdown)
                    InsightType.INFO -> painterResource(Res.drawable.analytics)
                },
                contentDescription = null,
                tint = when (type) {
                    InsightType.POSITIVE -> Color(0xFF22c55e)
                    InsightType.WARNING -> Color(0xFFf59e0b)
                    InsightType.NEGATIVE -> Color(0xFFef4444)
                    InsightType.INFO -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(16.dp)
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class Insight(
    val title: String,
    val description: String,
    val type: InsightType
)

enum class InsightType {
    POSITIVE, WARNING, NEGATIVE, INFO
}

private fun generateInsights(chartData: org.babetech.borastock.domain.usecase.ChartData): List<Insight> {
    val insights = mutableListOf<Insight>()
    
    // Stock evolution insight
    if (chartData.stockEvolutionData.size >= 2) {
        val latest = chartData.stockEvolutionData.last()
        val previous = chartData.stockEvolutionData[chartData.stockEvolutionData.size - 2]
        val growth = ((latest.totalValue - previous.totalValue) / previous.totalValue * 100)
        
        insights.add(
            Insight(
                title = "Évolution du stock",
                description = if (growth > 0) {
                    "Croissance de ${String.format("%.1f", growth)}% de la valeur du stock"
                } else {
                    "Diminution de ${String.format("%.1f", kotlin.math.abs(growth))}% de la valeur du stock"
                },
                type = if (growth > 0) InsightType.POSITIVE else InsightType.WARNING
            )
        )
    }
    
    // Category distribution insight
    val topCategory = chartData.categoryDistribution.maxByOrNull { it.value }
    if (topCategory != null) {
        val totalValue = chartData.categoryDistribution.sumOf { it.value }
        val percentage = (topCategory.value / totalValue * 100)
        
        insights.add(
            Insight(
                title = "Catégorie dominante",
                description = "${topCategory.category} représente ${String.format("%.1f", percentage)}% de la valeur totale",
                type = InsightType.INFO
            )
        )
    }
    
    // Movement trends insight
    val latestMovement = chartData.movementTrends.lastOrNull()
    if (latestMovement != null) {
        insights.add(
            Insight(
                title = "Balance des mouvements",
                description = if (latestMovement.netMovement > 0) {
                    "Excédent de ${String.format("%.0f", latestMovement.netMovement)}€ en entrées"
                } else {
                    "Déficit de ${String.format("%.0f", kotlin.math.abs(latestMovement.netMovement))}€ en sorties"
                },
                type = if (latestMovement.netMovement > 0) InsightType.POSITIVE else InsightType.WARNING
            )
        )
    }
    
    return insights
}