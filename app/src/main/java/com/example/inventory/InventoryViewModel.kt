package com.example.inventory


import androidx.lifecycle.*
import com.example.inventory.data.CreationWay
import com.example.inventory.utils.EncSharedPreferences
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import com.example.inventory.validator.BaseValidator
import com.example.inventory.validator.EmailValidator
import com.example.inventory.validator.NameValidator
import com.example.inventory.validator.PhoneValidator
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
    val encSharedPreferences = EncSharedPreferences()

    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    fun checkEmailValid(providerEmail: String): Int? {
        val emailValidations = BaseValidator.validate(EmailValidator(providerEmail))
        return if (emailValidations.isSuccess) null else emailValidations.message
    }

    fun checkPhoneValid(providerPhone: String): Int? {
        val phoneValidations = BaseValidator.validate(PhoneValidator(providerPhone))
        return if (phoneValidations.isSuccess) null else phoneValidations.message
    }

    fun checkNameValid(providerName: String): Int? {
        val nameValidations = BaseValidator.validate(NameValidator(providerName))
        return if (nameValidations.isSuccess) null else nameValidations.message
    }

    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    fun addItemFromFile(item: Item): Boolean {
        if (item.creationWay != CreationWay.FILE) {
            throw Exception("expected creation way FILE got ${item.creationWay}")
        }

        val alreadyInserted = allItems.value?.any { it -> it.id == item.id }
        if (alreadyInserted == true) {
            return false
        }

        insertItem(item)
        return true
    }

    fun addNewItem(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        providerEmail: String,
        providerPhone: String,
        providerName: String,
    ) {
        val newItem = getNewItemEntry(
            itemName,
            itemPrice,
            itemCount,
            providerEmail,
            providerPhone,
            providerName,
        )
        insertItem(newItem)
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String,
        providerEmail: String,
        providerPhone: String,
        providerName: String,
    ) {
        val updatedItem = getUpdatedItemEntry(
            itemId,
            itemName,
            itemPrice,
            itemCount,
            providerEmail,
            providerPhone,
            providerName,
        )
        updateItem(updatedItem)
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String,
        providerEmail: String,
        providerPhone: String,
        providerName: String,
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt(),
            providerEmail = providerEmail,
            providerPhone = providerPhone,
            providerName = providerName,
        )
    }

    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    private fun getNewItemEntry(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        providerEmail: String,
        providerPhone: String,
        providerName: String,
    ): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt(),
            providerEmail = providerEmail,
            providerPhone = providerPhone,
            providerName = providerName,
        )
    }


}

class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

