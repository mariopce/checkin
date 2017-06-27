package pl.saramak.fbexample.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


/**
 * Created by saramakm on 27/06/2017.
 */
class UserChangeDataListener(val adapter: UserAdapter) : ValueEventListener{
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        Log.d("DB", "Numbers of items: " + dataSnapshot.childrenCount)
        val users = dataSnapshot.children.map {
            it ->
            it.getValue(User::class.java)
        }.filterNotNull().sortedBy { it.last };

        adapter.myDataset = users
    }

    override fun onCancelled(error: DatabaseError) {
        // Failed to read value
        Log.w("DB", "Failed to read value.", error.toException())
    }

}