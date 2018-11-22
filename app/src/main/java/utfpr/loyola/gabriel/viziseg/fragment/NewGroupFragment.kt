package utfpr.loyola.gabriel.viziseg.fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_new_group.*
import org.jetbrains.anko.support.v4.toast
import utfpr.loyola.gabriel.viziseg.R
import utfpr.loyola.gabriel.viziseg.recyclerview.item.GroupPersonItem
import utfpr.loyola.gabriel.viziseg.util.FirestoreUtil
import com.xwray.groupie.OnItemClickListener
import kotlinx.android.synthetic.main.item_group_person.view.*
import utfpr.loyola.gabriel.viziseg.MainActivity
import utfpr.loyola.gabriel.viziseg.model.User


class NewGroupFragment : Fragment() {
    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var peopleSection: Section
    private lateinit var currentUser: User

    var checkedUserIds: MutableList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        userListenerRegistration =
                FirestoreUtil.addGroupUsersListener(this.activity!!, this::updateRecyclerView)

        FirestoreUtil.getCurrentUser {
            currentUser = it
        }

        checkedUserIds.add(FirebaseAuth.getInstance().currentUser!!.uid)

        val view = inflater.inflate(R.layout.fragment_new_group, container, false)
        val saveGroupBtn = view.findViewById<Button>(R.id.btn_save_group)


        saveGroupBtn.setOnClickListener {
            FirestoreUtil.createGroup(editText_name.text.toString(),
                editText_description.text.toString(), checkedUserIds)
            toast("Alterações salvas.")

            val activity = activity as MainActivity?
            activity!!.replaceFragment(GroupsFragment())
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_group_people.apply {
                layoutManager = LinearLayoutManager(this@NewGroupFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() {

        }

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()
    }

    private val onItemClick = OnItemClickListener { item, View ->
        if (item is GroupPersonItem) {
            if (!item.isChecked) {
                item.isChecked = true
                View.cardView_item_group_person.setBackgroundColor(Color.parseColor("#2aa660"))
                checkedUserIds.add(item.userId)
            } else if (item.isChecked) {
                item.isChecked = false
                View.cardView_item_group_person.setBackgroundColor(Color.WHITE)
                checkedUserIds.remove(item.userId)
            }
        }
    }
}