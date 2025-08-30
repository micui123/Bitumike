@@ .. @@
import org.babetech.borastock.domain.usecase.AddStockEntryUseCase
import org.babetech.borastock.domain.usecase.AddStockExitUseCase
+import org.babetech.borastock.domain.usecase.GetChartDataUseCase
import org.babetech.borastock.domain.usecase.CreateStockItemUseCase
@@ .. @@
    factory { GetStockMovementsUseCase(get()) }
    factory { GetStockStatisticsUseCase(get()) }
    factory { GetSuppliersDistributionUseCase(get()) }
+    factory { GetChartDataUseCase(get()) }

}