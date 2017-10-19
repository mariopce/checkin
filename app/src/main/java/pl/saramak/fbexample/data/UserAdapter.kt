package pl.saramak.fbexample.data


import android.content.Context
import android.support.v4.content.ContextCompat.getColor
import android.widget.TextView
import pl.saramak.fbexample.R
import java.util.regex.Pattern
import java.text.Normalizer


class UserAdapter(val database: com.google.firebase.database.FirebaseDatabase) : android.support.v7.widget.RecyclerView.Adapter<UserAdapter.ViewHolder>(), android.widget.Filterable {

    companion object Colors {
        val COLOR_MAP: Map<String, Int> = mapOf(
                Pair("bli", R.color.blind_bird),
                Pair("ear", R.color.early_bird),
                Pair("reg", R.color.regular),
                Pair("lat", R.color.late_bird),
                Pair("last bird", R.color.last_bird),
                Pair("org", R.color.organizer),
                Pair("vip", R.color.vip),
                Pair("spe", R.color.speaker)
        )
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    }


    override fun getFilter(): android.widget.Filter {
        return UserAdapter.FilterUsers(this, myDataset)
    }

    class FilterUsers() : android.widget.Filter() {
        private val regex = Pattern.compile("\\s+")

        lateinit var userAdapter: UserAdapter;
        lateinit var filteredList: List<User>
        lateinit var orginalList: List<User>

        constructor(adapter: UserAdapter, orginal: List<User>) : this() {
            userAdapter = adapter
            orginalList = orginal
            filteredList = ArrayList(orginal)
        }

        override fun publishResults(constraint: CharSequence?, results: android.widget.Filter.FilterResults?) {
            userAdapter.filtered = results!!.values as List<User>
            userAdapter.notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): android.widget.Filter.FilterResults {
            val res = android.widget.Filter.FilterResults()
            var filtered: List<User> = orginalList

            if (!constraint.isNullOrBlank()) {
                val constraints = constraint!!.split(regex)

                for (element in constraints) {
                    filtered = filtered.filter { it.last_normalized!!.toLowerCase().contains(element ?: "") || it.first_normalized!!.toLowerCase().contains(element ?: "") || it.email!!.toLowerCase().contains(element ?: "") }
                }
            }

            res.values = filtered
            res.count = filtered.size
            return res
        }

        fun deAccent(str: CharSequence): String {
            val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
            return pattern.matcher(nfdNormalizedString).replaceAll("")
        }
    }

    var filtered: List<User> = emptyList()

    var myDataset: List<User> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int {
        if (filtered.isNotEmpty()) {
            return filtered.size
        }
        return myDataset.size
    }


    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        var user: User;
        if (filtered.isNotEmpty()) {
            user = filtered.get(position)
        } else {
            user = myDataset.get(position);
        }
        //Log.d("user", "${user?.last} ${user?.first}" )
        holder.personalName.text = "${user?.last} ${user?.first}";
        holder.personalEmail.text = "${user.email}"
        holder.type.text = "${user?.type}"
        holder.type.setBackgroundColor(getColor(holder.personalName.context, user))
//        Log.d("user", "${user?.last} ${user?.first} ${user.email} ${user.checked}" )
        //in some cases, it will prevent unwanted situations
        holder.checkin.setOnCheckedChangeListener(null);
        holder.checkingParent.setOnClickListener(null)
        holder.checkingParent.setOnClickListener { holder.checkin.isChecked = !holder.checkin.isChecked }
        holder.checkin.isChecked = user.checked;
        holder.checkin.setOnCheckedChangeListener { buttonView, isChecked ->
            val myRef = database.getReference("/${user.number}")
            user.checked = holder.checkin.isChecked
            myRef.setValue(user)
        }

    }

    private fun getColor(context: Context, user: User) = getColor(context, COLOR_MAP.get(user?.type?.toLowerCase()?.substring(0, 3)) ?: android.R.color.white)

    override fun onCreateViewHolder(parent: android.view.ViewGroup?, viewType: Int): UserAdapter.ViewHolder {
        val v = android.view.LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.user_item_layout, parent, false) as android.support.constraint.ConstraintLayout
        return UserAdapter.ViewHolder(v);
    }

    class ViewHolder(itemView: android.view.View) : android.support.v7.widget.RecyclerView.ViewHolder(itemView) {
        var personalName = itemView.findViewById(R.id.person_detal) as android.widget.TextView
        var personalEmail = itemView.findViewById(R.id.person_email) as android.widget.TextView;
        var checkin = itemView.findViewById(R.id.checkin) as android.support.v7.widget.AppCompatCheckBox
        var checkingParent = itemView;
        var type = itemView.findViewById(R.id.type) as TextView

    }

}