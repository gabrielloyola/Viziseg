package utfpr.loyola.gabriel.viziseg.fragment

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import com.xwray.groupie.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_groups.*
import org.jetbrains.anko.support.v4.startActivity
import utfpr.loyola.gabriel.viziseg.AppConstants
import utfpr.loyola.gabriel.viziseg.ChatActivity
import utfpr.loyola.gabriel.viziseg.MainActivity
import utfpr.loyola.gabriel.viziseg.R
import utfpr.loyola.gabriel.viziseg.recyclerview.item.GroupItem
import utfpr.loyola.gabriel.viziseg.util.FirestoreUtil

class GroupsFragment : Fragment() {
    private lateinit var groupsListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var groupsSection: Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        groupsListenerRegistration =
                FirestoreUtil.addGroupsListener(this.activity!!, this::updateRecyclerView)

        val view = inflater.inflate(R.layout.fragment_groups, container, false)
        val button = view.findViewById<FloatingActionButton>(R.id.btn_addGroup)
        button.setOnClickListener {
            val activity = activity as MainActivity?
            activity!!.replaceFragment(NewGroupFragment())
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(groupsListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {

        fun init() {
            recycler_view_groups.apply {
                layoutManager = LinearLayoutManager(this@GroupsFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    groupsSection = Section(items)
                    add(groupsSection)
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

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is GroupItem) {
            startActivity<ChatActivity>(
                AppConstants.GROUP_NAME to item.group.name,
                AppConstants.GROUP_ID to item.groupId,
                AppConstants.GROUP_CHAT to "TRUE"
            )
        }
    }
}