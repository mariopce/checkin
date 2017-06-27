package pl.saramak.fbexample.data


import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.*
import pl.saramak.fbexample.R


class UserAdapter(val database: com.google.firebase.database.FirebaseDatabase) : android.support.v7.widget.RecyclerView.Adapter<UserAdapter.ViewHolder>(), android.widget.Filterable {

    companion object Colors {
        var COLOR_MAP: Map<String, Int> = mapOf(
                Pair("blind bird", R.color.blind_bird),
                Pair("early bird", R.color.early_bird),
                Pair("regular", R.color.regular),
                Pair("late bird", R.color.late_bird),
                Pair("last bird", R.color.last_bird),
                Pair("organizer", R.color.organizer),
                Pair("vip", R.color.vip),
                Pair("speaker", R.color.speaker)
        )
    }


    override fun getFilter(): android.widget.Filter {
        return UserAdapter.FilterUsers(this, myDataset)
    }

    class FilterUsers() : android.widget.Filter() {
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
            var filtered: List<User> = emptyList()
            if (constraint.isNullOrEmpty()) {
                filtered = orginalList
            } else {
                filtered = filteredList.filter { normalize(it.last!!).toLowerCase().startsWith(constraint ?: "") || normalize(it.first!!).toLowerCase().startsWith(constraint ?: "") || it.email.toLowerCase().startsWith(constraint ?: "") }
            }
            res.values = filtered
            res.count = filtered.size
            return res
        }

        fun normalize(str: CharSequence): String {
            return java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD).replace("ł", "l");
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
        holder.personalName.text = "${user?.last} ${user?.first}";
        holder.personalEmail.text = "${user.email}"

        holder.personalName.setBackgroundColor(getColor(holder.personalName.context, user))
        //in some cases, it will prevent unwanted situations
        holder.checkin.setOnCheckedChangeListener(null);
        holder.checkin.isChecked = user.checked;
        holder.checkin.setOnCheckedChangeListener { buttonView, isChecked ->
            val myRef = database.getReference("/${user.number}")
            user.checked = holder.checkin.isChecked
            myRef.setValue(user)
        }

    }

    private fun getColor(context: Context, user: User) = getColor(context, COLOR_MAP.get(user?.type?.toLowerCase()) ?: android.R.color.white)

    override fun onCreateViewHolder(parent: android.view.ViewGroup?, viewType: Int): UserAdapter.ViewHolder {
        val v = android.view.LayoutInflater.from(parent?.getContext())
                .inflate(R.layout.user_item_layout, parent, false) as android.support.constraint.ConstraintLayout
        return UserAdapter.ViewHolder(v);
    }

    class ViewHolder(itemView: android.view.View) : android.support.v7.widget.RecyclerView.ViewHolder(itemView) {
        var personalName = itemView.findViewById(R.id.person_detal) as android.widget.TextView
        var personalEmail = itemView.findViewById(R.id.person_email) as android.widget.TextView;
        var checkin = itemView.findViewById(R.id.checkin) as android.support.v7.widget.AppCompatCheckBox;

    }

}