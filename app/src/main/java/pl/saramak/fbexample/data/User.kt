package pl.saramak.fbexample.data

import com.google.firebase.database.Exclude
import java.text.Normalizer


/**
 * Created by saramakm on 24/06/2017.
 */
data class User(val number: Long) {

    constructor() : this(0) {
    }

    var number_string: String? = null
    get() {
        if(field == null)
            field = number.toString()

        return field
    }

    var checked: Boolean = false
    val first: String? = null

    var first_normalized: String? = null
        get() {
            if (field == null)
                field = normalize(first)
            return field
        }

    val last: String? = null

    var last_normalized: String? = null
        get() {
            if (field == null)
                field = normalize(last)
            return field
        }
    val type: String? = null

    val orderid: Long? = null
    var orderid_string : String? = null
    get() {
        if(field == null)
            field = orderid.toString()
        return field
    }
    val email: String? = null
    @Exclude
    val firstLCN: String? = null
    @Exclude
    val lastLCN: String? = null

    fun normalize(str: String?): String {
        return Normalizer.normalize(str?.toLowerCase(), Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+", "").replace("ł", "l")
    }
}