package pl.saramak.fbexample.data

import com.google.firebase.database.Exclude
import java.text.Normalizer






/**
 * Created by saramakm on 24/06/2017.
 */
data class User(val number: Long) {

    constructor() : this(0) {

    }
    val first: String? = null
    val last: String? = null
    val type: String? = null
    var checked: String? = null
    val orderid: Long? = null
    val email: String? = null
    @Exclude
    val firstLCN: String? = null
    @Exclude
    val lastLCN: String? = null

    fun normalize(str: String?): String {
        return Normalizer.normalize(str?.toLowerCase(), Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+", "").replace("ł", "l")
    }
}