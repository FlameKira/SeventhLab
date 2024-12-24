package com.example.mydialer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), ClickInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val search_text = sharedPref.getString("SEARCH_FILTER", "")
        val etSearch: EditText = findViewById(R.id.et_search)
        etSearch.setText(search_text)

        Timber.plant(Timber.DebugTree())

        val gson = Gson()

        val itemType = object : TypeToken<List<Contact>>() {}.type
        val url = URL("https://drive.google.com/u/0/uc?id=1-KO-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&export=download")
        val rView: RecyclerView = findViewById(R.id.rView)
        val adapter = Adapter(this)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter

        Thread(Runnable {
            val con = url.openConnection() as HttpURLConnection
            val dataString = con.inputStream.bufferedReader().readText()
            val contacts: List<Contact> = gson.fromJson<List<Contact>>(dataString, itemType)
            for (contact in contacts) {
                Timber.v(contact.toString())
            }
            adapter.setItems(contacts)
            runOnUiThread { adapter.notifyDataSetChanged() }


            etSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
                    with (sharedPref.edit()) {
                        putString("SEARCH_FILTER", etSearch.text.toString())
                        apply()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val filteredContacts = contacts.filter {
                        it.name.contains(etSearch.text) or it.phone.contains(etSearch.text) or it.type.contains(
                            etSearch.text
                        )
                    }
                    adapter.setItems(filteredContacts)
                    runOnUiThread { adapter.notifyDataSetChanged() }
                }
            })
        }).start()
    }

    override fun onContactClick(contact: Contact) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:" + contact.phone))
        startActivity(intent)
    }
}