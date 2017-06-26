package pl.saramak.fbexample

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_signed_in.*
import com.google.firebase.database.*
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.text.Normalizer


class SignedInActivity : AppCompatActivity() {

    lateinit var usersList : RecyclerView;
    lateinit var mAdapter :UserAdapter;
    lateinit var database : FirebaseDatabase;

    lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in)
        setSupportActionBar(toolbar)
        usersList =  findViewById(R.id.users) as RecyclerView
        searchEditText = findViewById(R.id.search) as EditText

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
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.key
                Log.d("DB", "CC: " + dataSnapshot.childrenCount)
                val users = dataSnapshot.children.map{
                    it -> it.getValue(User::class.java)
                }.filterNotNull();
                mAdapter.myDataset = users
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("DB", "Failed to read value.", error.toException())
            }
        })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mAdapter.filter.filter(s)
            }
        }
        )

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.app_bar_search ->
                // User chose the "Settings" item, show the app settings UI...
                return true

            R.id.action_logout -> {
                logout()
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

}

class UserAdapter(val database: FirebaseDatabase) : RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {

    override fun getFilter(): Filter {
        return FilterUsers(this, myDataset)
    }

    class FilterUsers() : Filter() {
        lateinit var userAdapter : UserAdapter;
        lateinit var filteredList : List<User>
        lateinit var orginalList : List<User>
        constructor(adapter: UserAdapter, orginal:List<User>): this(){
            userAdapter = adapter
            orginalList = orginal
            filteredList = ArrayList(orginal)
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            userAdapter.filtered = results!!.values as List<User>
            userAdapter.notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val res = FilterResults()
            var filtered : List<User> = emptyList()
            if (constraint.isNullOrEmpty()){
                filtered = orginalList
            }else {
                filtered = filteredList.filter { normalize(it.last!!).toLowerCase().startsWith(constraint ?: "") || normalize(it.first!!).toLowerCase().startsWith(constraint ?: "") || it.email.toLowerCase().startsWith(constraint ?: "")}
            }
            res.values = filtered
            res.count = filtered.size
            return res
        }
        fun normalize(str: CharSequence) : String{
            return Normalizer.normalize(str, Normalizer.Form.NFD).replace("ł", "l");
        }
    }
    var filtered : List<User> = emptyList()

    var myDataset: List<User> = emptyList()
        set(value){
            field = value
            notifyDataSetChanged()
        }




    override fun getItemCount(): Int {
        if (filtered.isNotEmpty()){
            return filtered.size
        }
        return myDataset.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user : User;
        if (filtered.isNotEmpty()){
            user = filtered.get(position)
        } else {
            user =  myDataset.get(position);
        }
        holder.personalName.text =  "${user?.first} ${user?.last}";
        holder.personalEmail.text = "${user.email}"

        //in some cases, it will prevent unwanted situations
        holder.checkin.setOnCheckedChangeListener(null);
        holder.checkin.isChecked = user.checked;
        holder.checkin.setOnCheckedChangeListener { buttonView, isChecked ->
            val myRef = database.getReference("/${user.number}")
            user.checked = holder.checkin.isChecked
            myRef.setValue(user)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.user_item_layout, parent, false) as ConstraintLayout
        return ViewHolder(v);
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var personalName = itemView.findViewById(R.id.person_detal) as TextView
        var personalEmail = itemView.findViewById(R.id.person_email) as TextView;
        var checkin = itemView.findViewById(R.id.checkin) as AppCompatCheckBox;

    }

}
