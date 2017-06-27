package pl.saramak.fbexample.view

import pl.saramak.fbexample.data.UserAdapter


/**
 * Created by saramakm on 27/06/2017.
 */
class SearchCloseListener(val adapter: UserAdapter) : android.support.v7.widget.SearchView.OnCloseListener {
    override fun onClose(): Boolean {
        //some operation
        adapter.filter.filter("")
        return true;
    }

}