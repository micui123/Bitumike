package org.babetech.borastock.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.babetech.borastock.data.models.*
import org.babetech.borastock.data.repository.StockRepository

data class ChartData(
    val stockEvolutionData: List<StockEvolutionPoint>,
    val categoryDistribution: List<CategoryData>,
    val supplierDistribution: List<SupplierData>,
    val movementTrends: List<MovementTrendPoint>,
    val profitabilityData: List<ProfitabilityPoint>
)

data class StockEvolutionPoint(
    val date: String,
    val totalValue: Double,
    val totalItems: Int
)

data class CategoryData(
    val category: String,
    val value: Double,
    val itemCount: Int,
    val color: androidx.compose.ui.graphics.Color
)

data class SupplierData(
    val supplierName: String,
    val value: Double,
    val itemCount: Int,
    val color: androidx.compose.ui.graphics.Color
)

data class MovementTrendPoint(
    val date: String,
    val entries: Double,
    val exits: Double,
    val netMovement: Double
)

data class ProfitabilityPoint(
    val category: String,
    val revenue: Double,
    val cost: Double,
    val profit: Double
)

class GetChartDataUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<ChartData> {
        return combine(
            repository.getAllStockItems(),
            repository.getAllStockEntries(),
            repository.getAllStockExits(),
            repository.getAllSuppliers()
        ) { stockItems, entries, exits, suppliers ->
            
            // Generate stock evolution data (last 7 days)
            val stockEvolution = generateStockEvolutionData(stockItems)
            
            // Generate category distribution
            val categoryDistribution = generateCategoryDistribution(stockItems)
            
            // Generate supplier distribution
            val supplierDistribution = generateSupplierDistribution(stockItems, suppliers)
            
            // Generate movement trends
            val movementTrends = generateMovementTrends(entries, exits)
            
            // Generate profitability data
            val profitabilityData = generateProfitabilityData(stockItems, entries, exits)
            
            ChartData(
                stockEvolutionData = stockEvolution,
                categoryDistribution = categoryDistribution,
                supplierDistribution = supplierDistribution,
                movementTrends = movementTrends,
                profitabilityData = profitabilityData
            )
        }
    }
    
    private fun generateStockEvolutionData(stockItems: List<StockItem>): List<StockEvolutionPoint> {
        // Simulate 7 days of data - in real app, this would come from historical data
        val dates = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
        val totalValue = stockItems.sumOf { it.totalValue }
        val totalItems = stockItems.size
        
        return dates.mapIndexed { index, date ->
            StockEvolutionPoint(
                date = date,
                totalValue = totalValue * (0.85 + (index * 0.02)), // Simulate growth
                totalItems = (totalItems * (0.9 + (index * 0.015))).toInt()
            )
        }
    }
    
    private fun generateCategoryDistribution(stockItems: List<StockItem>): List<CategoryData> {
        val colors = listOf(
            androidx.compose.ui.graphics.Color(0xFF3B82F6),
            androidx.compose.ui.graphics.Color(0xFF10B981),
            androidx.compose.ui.graphics.Color(0xFFF59E0B),
            androidx.compose.ui.graphics.Color(0xFFEF4444),
            androidx.compose.ui.graphics.Color(0xFF8B5CF6),
            androidx.compose.ui.graphics.Color(0xFFEC4899)
        )
        
        return stockItems
            .groupBy { it.category }
            .entries
            .mapIndexed { index, (category, items) ->
                CategoryData(
                    category = category,
                    value = items.sumOf { it.totalValue },
                    itemCount = items.size,
                    color = colors[index % colors.size]
                )
            }
    }
    
    private fun generateSupplierDistribution(
        stockItems: List<StockItem>,
        suppliers: List<Supplier>
    ): List<SupplierData> {
        val colors = listOf(
            androidx.compose.ui.graphics.Color(0xFF06B6D4),
            androidx.compose.ui.graphics.Color(0xFF84CC16),
            androidx.compose.ui.graphics.Color(0xFFF97316),
            androidx.compose.ui.graphics.Color(0xFFE11D48),
            androidx.compose.ui.graphics.Color(0xFF7C3AED)
        )
        
        return stockItems
            .groupBy { it.supplier.name }
            .entries
            .take(5) // Top 5 suppliers
            .mapIndexed { index, (supplierName, items) ->
                SupplierData(
                    supplierName = supplierName,
                    value = items.sumOf { it.totalValue },
                    itemCount = items.size,
                    color = colors[index % colors.size]
                )
            }
    }
    
    private fun generateMovementTrends(
        entries: List<StockEntry>,
        exits: List<StockExit>
    ): List<MovementTrendPoint> {
        val dates = listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4")
        
        return dates.mapIndexed { index, date ->
            val entriesValue = entries.take(10).sumOf { it.totalValue } * (0.7 + index * 0.1)
            val exitsValue = exits.take(10).sumOf { it.totalValue } * (0.6 + index * 0.15)
            
            MovementTrendPoint(
                date = date,
                entries = entriesValue,
                exits = exitsValue,
                netMovement = entriesValue - exitsValue
            )
        }
    }
    
    private fun generateProfitabilityData(
        stockItems: List<StockItem>,
        entries: List<StockEntry>,
        exits: List<StockExit>
    ): List<ProfitabilityPoint> {
        return stockItems
            .groupBy { it.category }
            .entries
            .map { (category, items) ->
                val revenue = exits.filter { exit -> 
                    items.any { it.id == exit.stockItemId } 
                }.sumOf { it.totalValue }
                
                val cost = entries.filter { entry -> 
                    items.any { it.id == entry.stockItemId } 
                }.sumOf { it.totalValue }
                
                ProfitabilityPoint(
                    category = category,
                    revenue = revenue,
                    cost = cost,
                    profit = revenue - cost
                )
            }
    }
}