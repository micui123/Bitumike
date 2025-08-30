package org.babetech.borastock.data.mappers

import org.babe.sqldelight.data.db.*
import org.babetech.borastock.data.models.*

// Extension functions to map database entities to domain models

fun SelectAllStockItems.toDomainModel(): StockItem {
    return StockItem(
        id = id,
        name = name,
        category = category,
        description = description,
        currentStock = current_stock.toInt(),
        minStock = min_stock.toInt(),
        maxStock = max_stock.toInt(),
        unitPrice = unit_price,
        supplier = Supplier(
            id = supplier_id,
            name = supplier_name,
            category = supplier_category,
            contactPerson = contact_person,
            email = supplier_email,
            phone = supplier_phone,
            address = null,
            city = null,
            country = null,
            rating = supplier_rating.toFloat(),
            status = SupplierStatus.valueOf(supplier_status),
            reliability = SupplierReliability.valueOf(supplier_reliability),
            lastOrderDate = null,
            paymentTerms = null,
            notes = null,
            createdAt = created_at,
            updatedAt = updated_at
        ),
        status = StockItemStatus.valueOf(status),
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockItemById.toDomainModel(): StockItem {
    return StockItem(
        id = id,
        name = name,
        category = category,
        description = description,
        currentStock = current_stock.toInt(),
        minStock = min_stock.toInt(),
        maxStock = max_stock.toInt(),
        unitPrice = unit_price,
        supplier = Supplier(
            id = supplier_id,
            name = supplier_name,
            category = supplier_category,
            contactPerson = contact_person,
            email = supplier_email,
            phone = supplier_phone,
            address = null,
            city = null,
            country = null,
            rating = supplier_rating.toFloat(),
            status = SupplierStatus.valueOf(supplier_status),
            reliability = SupplierReliability.valueOf(supplier_reliability),
            lastOrderDate = null,
            paymentTerms = null,
            notes = null,
            createdAt = created_at,
            updatedAt = updated_at
        ),
        status = StockItemStatus.valueOf(status),
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectAllStockEntries.toDomainModel(): StockEntry {
    return StockEntry(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        entryDate = entry_date,
        batchNumber = batch_number,
        expiryDate = expiry_date,
        supplier = supplier_name,
        supplierId = supplier_id,
        status = EntryStatus.valueOf(status),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockEntriesByItemId.toDomainModel(): StockEntry {
    return StockEntry(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        entryDate = entry_date,
        batchNumber = batch_number,
        expiryDate = expiry_date,
        supplier = supplier_name,
        supplierId = supplier_id,
        status = EntryStatus.valueOf(status),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectAllStockExits.toDomainModel(): StockExit {
    return StockExit(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        exitDate = exit_date,
        customer = customer,
        orderNumber = order_number,
        deliveryAddress = delivery_address,
        status = ExitStatus.valueOf(status),
        urgency = ExitUrgency.valueOf(urgency),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockExitsByItemId.toDomainModel(): StockExit {
    return StockExit(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        exitDate = exit_date,
        customer = customer,
        orderNumber = order_number,
        deliveryAddress = delivery_address,
        status = ExitStatus.valueOf(status),
        urgency = ExitUrgency.valueOf(urgency),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectAllSuppliers.toDomainModel(): Supplier {
    return Supplier(
        id = id,
        name = name,
        category = category,
        contactPerson = contact_person,
        email = email,
        phone = phone,
        address = address,
        city = city,
        country = country,
        rating = rating.toFloat(),
        status = SupplierStatus.valueOf(status),
        reliability = SupplierReliability.valueOf(reliability),
        lastOrderDate = last_order_date,
        paymentTerms = payment_terms,
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectSupplierById.toDomainModel(): Supplier {
    return Supplier(
        id = id,
        name = name,
        category = category,
        contactPerson = contact_person,
        email = email,
        phone = phone,
        address = address,
        city = city,
        country = country,
        rating = rating.toFloat(),
        status = SupplierStatus.valueOf(status),
        reliability = SupplierReliability.valueOf(reliability),
        lastOrderDate = last_order_date,
        paymentTerms = payment_terms,
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockStatistics.toDomainModel(): StockStatistics {
    return StockStatistics(
        totalItems = total_items.toInt(),
        itemsInStock = items_in_stock.toInt(),
        itemsLowStock = items_low_stock.toInt(),
        itemsOutOfStock = items_out_of_stock.toInt(),
        itemsOverstocked = items_overstocked.toInt(),
        totalStockValue = total_stock_value
    )
}

fun SelectRecentEntries.toRecentMovement(): RecentMovement {
    return RecentMovement(
        id = id,
        productName = product_name,
        quantity = quantity.toInt(),
        date = entry_date,
        type = MovementType.ENTRY,
        description = "Entrée: ${quantity} x ${product_name} de ${supplier_name}"
    )
}

fun SelectRecentExits.toRecentMovement(): RecentMovement {
    return RecentMovement(
        id = id,
        productName = product_name,
        quantity = quantity.toInt(),
        date = exit_date,
        type = MovementType.EXIT,
        description = "Sortie: ${quantity} x ${product_name} pour ${customer}"
    )
}