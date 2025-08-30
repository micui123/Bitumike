@@ .. @@
import org.babetech.borastock.ui.screens.screennavigation.AccueilUiState
import org.babetech.borastock.ui.screens.screennavigation.AccueilViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
+import org.koin.compose.koinInject
+import org.babetech.borastock.domain.usecase.GetChartDataUseCase

/**
 * A simple data class to represent a dashboard metric.
@@ .. @@
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AccueilScreen(viewModel: AccueilViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
    val paneState = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
@@ .. @@
                        is AccueilUiState.Success -> {
                            MainDashboardPane(
                                statistics = state.statistics,
                                recentMovements = state.recentMovements,
                                criticalStockItems = state.criticalStockItems,
+                                chartData = chartData,
                                showChartButton = !showSupporting,
                                onToggleChart = {
@@ .. @@
            supportingPane = {
                if (showSupporting) {
                    AnimatedPane(modifier = Modifier
                        .width(maxWidth * 0.8f)
                        .fillMaxHeight()) {
-                        SupportingChartPane(
+                        GraphicSwitcherScreen(
+                            showBackButton = true,
                             onBack = {
                                 scope.launch { navigator.navigateBack() }
                             }
@@ .. @@
fun ThreePaneScaffoldScope.MainDashboardPane(
    statistics: StockStatistics,
    recentMovements: List<RecentMovement>,
    criticalStockItems: List<StockItem>,
+    chartData: org.babetech.borastock.domain.usecase.ChartData,
     showChartButton: Boolean,
@@ .. @@
        ) {
             item { DashboardMetricsGrid(statistics) }
+            item { CompactStockChart(chartData) }
             item { RecentMovementsList(recentMovements) }
             if (criticalStockItems.isNotEmpty()) {
@@ .. @@
                         ) {
                             Icon(
                                 painter = painterResource(Res.drawable.analytics),
                                 contentDescription = null,
                                 modifier = Modifier.size(18.dp)
                             )
                             Text(
-                                "Afficher l'analyse",
+                                "Analyse détaillée",
                                 style = MaterialTheme.typography.labelLarge.copy(
@@ .. @@
     }
 }

-// --- Supporting Chart Pane Composable ---
-@OptIn(ExperimentalMaterial3AdaptiveApi::class)
-@Composable
-fun ThreePaneScaffoldScope.SupportingChartPane(
-    onBack: () -> Unit
-) {
-    val navigator = rememberSupportingPaneScaffoldNavigator()
-
-    Column(
-        modifier = Modifier
-            .fillMaxSize()
-            .padding(24.dp),
-        verticalArrangement = Arrangement.spacedBy(16.dp)
-    ) {
-        if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded) {
-            IconButton(
-                onClick = onBack,
-                modifier = Modifier
-                    .align(Alignment.TopStart as Alignment.Horizontal)
-                    .clip(RoundedCornerShape(12.dp))
-                    .background(MaterialTheme.colorScheme.surfaceContainer)
-            ) {
-                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
-            }
-        }
-
-        Text(
-            text = "Analyse des performances",
-            style = MaterialTheme.typography.titleLarge.copy(
-                fontWeight = FontWeight.Bold
-            ),
-            color = MaterialTheme.colorScheme.onSurface
-        )
-        // Add your chart composable here
-        Box(
-            modifier = Modifier
-                .fillMaxWidth()
-                .height(300.dp)
-                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
-            contentAlignment = Alignment.Center
-        ) {
-            Text("Graphiques en construction...", color = MaterialTheme.colorScheme.onSurfaceVariant)
-        }
-    }
-}
-
 // --- Dashboard Metrics Grid Composable ---
 @Composable
 fun DashboardMetricsGrid(statistics: StockStatistics) {