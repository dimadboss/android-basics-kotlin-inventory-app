package com.example.inventory.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val itemName: String,
    @ColumnInfo(name = "price")
    val itemPrice: Double,
    @ColumnInfo(name = "quantity")
    val quantityInStock: Int,
    @ColumnInfo(name = "provider_email")
    val providerEmail: String,
    @ColumnInfo(name = "provider_phone")
    val providerPhone: String,
    @ColumnInfo(name = "provider_name")
    val providerName: String,
    @Transient
    @ColumnInfo(name = "creation_way")
    val creationWay: CreationWay = CreationWay.MANUAL,
    )

fun Item.getFileName(): String = "${id}_${itemName}.json"

fun Item.getFormattedPrice(): String =
    NumberFormat.getCurrencyInstance().format(itemPrice)

fun Item.toStringPretty(): String =
    """
        id: $id
        name: $itemName
        price: ${getFormattedPrice()}
        quantity: $quantityInStock
        provider name: $providerName
        provider email: $providerEmail
        provider phone: $providerPhone
    """.trimIndent()

fun Item.providerDetails(): String =
    """
        provider name: $providerName
        provider email: $providerEmail
        provider phone: $providerPhone
    """.trimIndent()

fun providerDetailsHidden(): String =
    """
        provider name: ****
        provider email: ****
        provider phone: ****
    """.trimIndent()