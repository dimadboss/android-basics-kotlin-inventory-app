package com.example.inventory


import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import com.example.inventory.validator.BaseValidator
import com.example.inventory.validator.EmailValidator
import com.example.inventory.validator.PhoneValidator
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
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

    fun addNewItem(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        providerEmail: String,
        providerPhone: String,
    ) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount, providerEmail, providerPhone)
        insertItem(newItem)
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String,
        providerEmail: String,
        providerPhone: String,
    ) {
        val updatedItem = getUpdatedItemEntry(
            itemId,
            itemName,
            itemPrice,
            itemCount,
            providerEmail,
            providerPhone,
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
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt(),
            providerEmail = providerEmail,
            providerPhone = providerPhone,
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
    ): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt(),
            providerEmail = providerEmail,
            providerPhone = providerPhone,
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

