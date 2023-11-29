package com.example.tugasfirebase

import com.google.firebase.firestore.Exclude

data class Barang(
    @set:Exclude @get:Exclude @Exclude
    var id : String = "",
    var namaBarang : String = "",
    var deskripsi: String = "",
    var harga: String = ""
)
