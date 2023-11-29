package com.example.tugasfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.example.tugasfirebase.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var updateId = ""
    private val budgetListLiveData : MutableLiveData<List<Barang>>
            by lazy {
                MutableLiveData<List<Barang>>()
            }
    private val budgetCollectionRef = firestore.collection("budgets")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            btnAdd.setOnClickListener{
                val namaBarang = edtNama.text.toString()
                val deskripsi = edtDeskripsi.text.toString()
                val harga = edtHarga.text.toString()

                val newBudget = Barang(namaBarang = namaBarang, deskripsi = deskripsi, harga = harga)
                addBudget(newBudget)
            }
            btnUpdate.setOnClickListener{
                if (updateId!="") {
                    val namaBarang = edtNama.text.toString()
                    val deskripsi = edtDeskripsi.text.toString()
                    val harga = edtHarga.text.toString()

                    val updateBudget = Barang(namaBarang = namaBarang, deskripsi = deskripsi, harga = harga)
                    updateBudget(updateBudget)
                    updateId = ""
                    setEmptyField()

                }
            }

            listData.setOnItemClickListener{
                    adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i) as Barang
                updateId = item.id
                edtNama.setText(item.namaBarang)
                edtDeskripsi.setText(item.deskripsi)
                edtHarga.setText(item.harga)
            }
            listData.onItemLongClickListener = AdapterView.OnItemLongClickListener{
                    adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i) as Barang
                deleteBudget(item)
                true
            }
        }
        observeBudget()
        getAllBudgets()
    }
    private fun getAllBudgets(){
        budgetCollectionRef.addSnapshotListener{ snapshots, error ->
            if (error!=null) {
                Log.d("MainActivity", "error listening for budget changes",
                    error)
                return@addSnapshotListener
            }
            val budgets = arrayListOf<Barang>()
            snapshots?.forEach{
                    documentReference ->
                budgets.add(
                    Barang(documentReference.id,
                        documentReference.get("namaBarang").toString(),
                        documentReference.get("deskripsi").toString(),
                        documentReference.get("harga").toString())
                )
            }
            if (budgets!=null){
                budgetListLiveData.postValue(budgets)
            }
        }
    }
    private fun observeBudget(){
        budgetListLiveData.observe(this){
                budgets ->
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                budgets.toMutableList())
            binding.listData.adapter = adapter
        }
    }
    private fun addBudget(barang: Barang){
        budgetCollectionRef.add(barang).addOnFailureListener{
            Log.d("MainActivity", "Error adding budget : ",
                it)
        }
    }
    private fun updateBudget(barang: Barang){
        budgetCollectionRef.document(updateId).set(barang)
            .addOnFailureListener{
                Log.d("MainActivity", "Error updating budget : ",
                    it)
            }
    }
    private fun deleteBudget(barang: Barang){
        budgetCollectionRef.document(barang.id).delete()
            .addOnFailureListener{
                Log.d("MainActivity", "Error deleting budget : ",
                    it)
            }
    }
    private fun setEmptyField(){
        with(binding){
            edtNama.setText("")
            edtDeskripsi.setText("")
            edtHarga.setText("")
        }
    }
}