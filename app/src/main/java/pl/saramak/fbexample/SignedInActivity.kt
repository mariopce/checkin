package pl.saramak.fbexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_signed_in.*
import com.google.firebase.database.*
import android.widget.EditText
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import pl.saramak.fbexample.data.UserAdapter
import pl.saramak.fbexample.data.UserChangeDataListener


import pl.saramak.fbexample.view.SearchCloseListener
import pl.saramak.fbexample.view.SearchTextListener


class SignedInActivity : AppCompatActivity() {

    lateinit var usersList: RecyclerView;
    lateinit var mAdapter: UserAdapter;
    lateinit var database: FirebaseDatabase;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)
        setSupportActionBar(toolbar)
        usersList = findViewById(R.id.users) as RecyclerView

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            supportActionBar?.title = "${auth.currentUser?.displayName}"
            supportActionBar?.subtitle = "${auth.currentUser?.email}"
        }
        database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("/")
        usersList.setHasFixedSize(true)
        // use a linear layout manager
        var mLayoutManager = LinearLayoutManager(this);
        usersList.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = UserAdapter(database);
        usersList.setAdapter(mAdapter);
        // Read from the database
        myRef.addValueEventListener(UserChangeDataListener(mAdapter))
    }


    private lateinit var searchEdit: EditText

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings, menu)
        val searchItem = menu.findItem(R.id.action_search);
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchEdit = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as EditText
        searchView.setOnCloseListener(SearchCloseListener(mAdapter))
        searchView.setOnQueryTextListener(SearchTextListener(mAdapter))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_logout -> {
                logout()
                return true
            }
            R.id.action_scan -> {
                startActivityForResult(Intent(this, SimpleScannerActivity::class.java), 0)
                return true
            }
            else ->
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item)
        }
    }

    fun logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(OnCompleteListener {
                    // user is now signed out
                    startActivity(Intent(this, MainActivity::class.java));
                    finish();

                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == 0 && resultCode == Activity.RESULT_OK) {
            data?.let {
                val t:String = it.getStringExtra("RESULT")
                searchEdit.setText(t)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}

