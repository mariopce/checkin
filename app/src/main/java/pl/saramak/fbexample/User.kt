package pl.saramak.fbexample

import com.google.firebase.database.Exclude
import java.text.Normalizer






/**
 * Created by saramakm on 24/06/2017.
 */
data class User(val email:String) {

    constructor() : this("") {

    }
    val first: String? = null
    val last: String? = null
    val type: String? = null
    val number: String? = null
    var checked: Boolean = false
    val orderid: String? = null
    @Exclude
    val firstLCN: String? = null
    @Exclude
    val lastLCN: String? = null

    fun normalize(str: String?): String {
        return Normalizer.normalize(str?.toLowerCase(), Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+", "").replace("ł", "l")
    }
}