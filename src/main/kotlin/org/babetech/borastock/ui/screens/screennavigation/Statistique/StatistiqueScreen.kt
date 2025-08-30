@@ .. @@
import org.babetech.borastock.data.models.TopProduct
import org.jetbrains.compose.resources.painterResource
+import org.babetech.borastock.ui.screens.dashboard.GraphicSwitcherScreen
+import org.babetech.borastock.domain.usecase.GetChartDataUseCase
+import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun StatistiqueScreen() {
+    val getChartDataUseCase: GetChartDataUseCase = koinInject()
+    val chartData by getChartDataUseCase().collectAsStateWithLifecycle(
+        initialValue = org.babetech.borastock.domain.usecase.ChartData(
+            stockEvolutionData = emptyList(),
+            categoryDistribution = emptyList(),
+            supplierDistribution = emptyList(),
+            movementTrends = emptyList(),
+            profitabilityData = emptyList()
+        )
+    )
     val navigator = rememberSupportingPaneScaffoldNavigator()
     val scope = rememberCoroutineScope()

@@ .. @@
                     // Bouton pour afficher les graphiques détaillés
                     item {
                         Button(
                             onClick = {
                                 scope.launch {
                                     navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                 }
                             },
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .height(48.dp),
                             shape = RoundedCornerShape(12.dp),
                             colors = ButtonDefaults.buttonColors(
                                 containerColor = MaterialTheme.colorScheme.primaryContainer
                             )
                         ) {
                             Row(
                                 horizontalArrangement = Arrangement.spacedBy(8.dp),
                                 verticalAlignment = Alignment.CenterVertically
                             ) {
                                 Icon(
                                     painterResource(Res.drawable.analytics),
                                     contentDescription = null,
                                     modifier = Modifier.size(18.dp)
                                 )
                                 Text(
-                                    "Voir les graphiques détaillés",
+                                    "Analyse interactive",
                                     style = MaterialTheme.typography.labelLarge.copy(
                                         fontWeight = FontWeight.Medium
                                     )
                                 )
                             }
                         }
                     }

+                    // Mini charts preview
+                    item {
+                        MiniChartsPreview(chartData)
+                    }
+
                     // Top produits
                     item {
                         TopProductsSection(topProducts)
@@ .. @@
         },
         supportingPane = {
             AnimatedPane {
-                DetailedAnalyticsPane(
-                    selectedChart = selectedChart,
-                    onChartSelected = { selectedChart = it },
-                    selectedPeriod = selectedPeriod,
+                GraphicSwitcherScreen(
+                    showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded,
                     onBackClick = {
                         scope.launch {
                             navigator.navigateBack()
                         }
-                    },
-                    showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded
+                    }
                 )
             }
         }
@@ .. @@
 }

+@Composable
+private fun MiniChartsPreview(chartData: org.babetech.borastock.domain.usecase.ChartData) {
+    Card(
+        modifier = Modifier.fillMaxWidth(),
+        shape = RoundedCornerShape(16.dp),
+        colors = CardDefaults.cardColors(
+            containerColor = MaterialTheme.colorScheme.surface
+        )
+    ) {
+        Column(
+            modifier = Modifier.padding(16.dp),
+            verticalArrangement = Arrangement.spacedBy(16.dp)
+        ) {
+            Text(
+                text = "Aperçu des données",
+                style = MaterialTheme.typography.titleLarge.copy(
+                    fontWeight = FontWeight.Bold
+                ),
+                color = MaterialTheme.colorScheme.onSurface
+            )
+
+            Row(
+                modifier = Modifier.fillMaxWidth(),
+                horizontalArrangement = Arrangement.spacedBy(12.dp)
+            ) {
+                // Mini pie chart for categories
+                Card(
+                    modifier = Modifier
+                        .weight(1f)
+                        .height(120.dp),
+                    shape = RoundedCornerShape(12.dp)
+                ) {
+                    Box(
+                        modifier = Modifier.fillMaxSize(),
+                        contentAlignment = Alignment.Center
+                    ) {
+                        if (chartData.categoryDistribution.isNotEmpty()) {
+                            val pieData = chartData.categoryDistribution.take(4).map { category ->
+                                com.aay.compose.donutChart.model.PieChartData(
+                                    partName = category.category,
+                                    data = category.value,
+                                    color = category.color
+                                )
+                            }
+                            
+                            com.aay.compose.donutChart.PieChart(
+                                modifier = Modifier.size(100.dp),
+                                pieChartData = pieData,
+                                ratioLineColor = Color.Transparent,
+                                textRatioStyle = TextStyle(fontSize = 0.sp)
+                            )
+                        } else {
+                            Text(
+                                "Catégories",
+                                style = MaterialTheme.typography.bodySmall,
+                                color = MaterialTheme.colorScheme.onSurfaceVariant
+                            )
+                        }
+                    }
+                }
+
+                // Mini bar chart for movements
+                Card(
+                    modifier = Modifier
+                        .weight(1f)
+                        .height(120.dp),
+                    shape = RoundedCornerShape(12.dp)
+                ) {
+                    Box(
+                        modifier = Modifier.fillMaxSize(),
+                        contentAlignment = Alignment.Center
+                    ) {
+                        if (chartData.movementTrends.isNotEmpty()) {
+                            val barData = listOf(
+                                com.aay.compose.barChart.model.BarParameters(
+                                    dataName = "Mouvements",
+                                    data = chartData.movementTrends.map { it.netMovement },
+                                    barColor = MaterialTheme.colorScheme.primary
+                                )
+                            )
+                            
+                            com.aay.compose.barChart.BarChart(
+                                modifier = Modifier.fillMaxSize(),
+                                chartParameters = barData,
+                                gridColor = Color.Transparent,
+                                xAxisData = chartData.movementTrends.map { it.date },
+                                isShowGrid = false,
+                                animateChart = true,
+                                showGridWithSpacer = false,
+                                yAxisStyle = TextStyle(fontSize = 0.sp),
+                                xAxisStyle = TextStyle(fontSize = 8.sp),
+                                yAxisRange = 5,
+                                barWidth = 8.dp
+                            )
+                        } else {
+                            Text(
+                                "Mouvements",
+                                style = MaterialTheme.typography.bodySmall,
+                                color = MaterialTheme.colorScheme.onSurfaceVariant
+                            )
+                        }
+                    }
+                }
+            }
+        }
+    }
+}
+
 @Composable
 private fun StatisticsHeader() {
@@ .. @@
         )
     }
 }

-@OptIn(ExperimentalMaterial3Api::class)
-@Composable
-private fun DetailedAnalyticsPane(
-    selectedChart: String,
-    onChartSelected: (String) -> Unit,
-    selectedPeriod: String,
-    onBackClick: () -> Unit,
-    showBackButton: Boolean
-) {
-    val scrollState = rememberScrollState()
-    val chartTypes = listOf("Ventes", "Revenus", "Commandes", "Clients")
-
-    Scaffold(
-        topBar = {
-            TopAppBar(
-                title = { Text("Analytics détaillées") },
-                navigationIcon = {
-                    if (showBackButton) {
-                        IconButton(onClick = onBackClick) {
-                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
-                        }
-                    }
-                }
-            )
-        }
-    ) { paddingValues ->
-        Column(
-            modifier = Modifier
-                .fillMaxSize()
-                .padding(paddingValues)
-                .padding(16.dp)
-                .verticalScroll(scrollState),
-            verticalArrangement = Arrangement.spacedBy(16.dp)
-        ) {
-            // Sélecteur de type de graphique
-            Card(
-                modifier = Modifier.fillMaxWidth(),
-                shape = RoundedCornerShape(12.dp)
-            ) {
-                Column(
-                    modifier = Modifier.padding(16.dp),
-                    verticalArrangement = Arrangement.spacedBy(12.dp)
-                ) {
-                    Text(
-                        text = "Type d'analyse",
-                        style = MaterialTheme.typography.titleMedium.copy(
-                            fontWeight = FontWeight.Bold
-                        )
-                    )
-
-                    LazyRow(
-                        horizontalArrangement = Arrangement.spacedBy(8.dp)
-                    ) {
-                        items(chartTypes) { type ->
-                            FilterChip(
-                                onClick = { onChartSelected(type) },
-                                label = { Text(type) },
-                                selected = selectedChart == type,
-                                colors = FilterChipDefaults.filterChipColors(
-                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
-                                    selectedLabelColor = MaterialTheme.colorScheme.primary
-                                )
-                            )
-                        }
-                    }
-                }
-            }
-
-            // Zone pour les graphiques (placeholder)
-            Card(
-                modifier = Modifier
-                    .fillMaxWidth()
-                    .height(300.dp),
-                shape = RoundedCornerShape(16.dp)
-            ) {
-                Box(
-                    modifier = Modifier.fillMaxSize(),
-                    contentAlignment = Alignment.Center
-                ) {
-                    Column(
-                        horizontalAlignment = Alignment.CenterHorizontally,
-                        verticalArrangement = Arrangement.spacedBy(8.dp)
-                    ) {
-                        Icon(
-                            painter = painterResource(Res.drawable.analytics),
-                            contentDescription = null,
-                            modifier = Modifier.size(48.dp),
-                            tint = MaterialTheme.colorScheme.primary
-                        )
-                        Text(
-                            text = "Graphique $selectedChart",
-                            style = MaterialTheme.typography.titleMedium,
-                            textAlign = TextAlign.Center
-                        )
-                        Text(
-                            text = "Période: $selectedPeriod",
-                            style = MaterialTheme.typography.bodyMedium,
-                            color = MaterialTheme.colorScheme.onSurfaceVariant,
-                            textAlign = TextAlign.Center
-                        )
-                    }
-                }
-            }
-
-            // Métriques détaillées
-            Card(
-                modifier = Modifier.fillMaxWidth(),
-                shape = RoundedCornerShape(16.dp)
-            ) {
-                Column(
-                    modifier = Modifier.padding(16.dp),
-                    verticalArrangement = Arrangement.spacedBy(12.dp)
-                ) {
-                    Text(
-                        text = "Métriques détaillées",
-                        style = MaterialTheme.typography.titleMedium.copy(
-                            fontWeight = FontWeight.Bold
-                        )
-                    )
-
-                    repeat(4) { index ->
-                        Row(
-                            modifier = Modifier.fillMaxWidth(),
-                            horizontalArrangement = Arrangement.SpaceBetween
-                        ) {
-                            Text(
-                                text = "Métrique ${index + 1}",
-                                style = MaterialTheme.typography.bodyMedium
-                            )
-                            Text(
-                                text = "${(index + 1) * 1234}",
-                                style = MaterialTheme.typography.bodyMedium.copy(
-                                    fontWeight = FontWeight.Bold
-                                ),
-                                color = MaterialTheme.colorScheme.primary
-                            )
-                        }
-                        if (index < 3) {
-                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
-                        }
-                    }
-                }
-            }
-        }
-    }
-}