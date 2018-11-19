package utfpr.loyola.gabriel.viziseg.recyclerview.item

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_group.*
import utfpr.loyola.gabriel.viziseg.R
import utfpr.loyola.gabriel.viziseg.glide.GlideApp
import utfpr.loyola.gabriel.viziseg.model.Group
import utfpr.loyola.gabriel.viziseg.util.StorageUtil

class GroupItem(val group: Group,
                val groupId: String,
                private val context: Context
)
    : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = group.name
        viewHolder.textView_description.text = group.description
        if (group.groupPicturePath != null)
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(group.groupPicturePath))
                .placeholder(R.drawable.ic_group_black_24dp)
                .into(viewHolder.imageView_group_picture)
    }

    override fun getLayout() = R.layout.item_group
}