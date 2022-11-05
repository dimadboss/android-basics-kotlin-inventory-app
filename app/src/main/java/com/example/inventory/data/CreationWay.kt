package com.example.inventory.data

enum class CreationWay {
    MANUAL {
        override fun toString() = "created manually"
    },
    FILE {
        override fun toString() = "uploaded from file"
    },
}