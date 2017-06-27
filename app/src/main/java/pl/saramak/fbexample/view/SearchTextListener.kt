package pl.saramak.fbexample.view

import android.support.v7.widget.SearchView
import pl.saramak.fbexample.data.UserAdapter

/**
 * Created by saramakm on 27/06/2017.
 */
class SearchTextListener(val mAdapter : UserAdapter) : SearchView.OnQueryTextListener {

    override fun onQueryTextChange(newText: String?): Boolean {
        mAdapter.filter.filter(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        //nothing
        return false;
    }

}