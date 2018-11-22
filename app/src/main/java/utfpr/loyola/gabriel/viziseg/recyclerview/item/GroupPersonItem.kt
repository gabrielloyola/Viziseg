package utfpr.loyola.gabriel.viziseg.recyclerview.item


import android.content.Context
import utfpr.loyola.gabriel.viziseg.R
import utfpr.loyola.gabriel.viziseg.glide.GlideApp
import utfpr.loyola.gabriel.viziseg.model.User
import utfpr.loyola.gabriel.viziseg.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*

class GroupPersonItem(val person: User,
                      val userId: String,
                      var isChecked: Boolean = false,
                      private val context: Context)
        : Item() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.textView_name.text = person.name
            viewHolder.textView_bio.text = person.bio
            if (person.profilePicturePath != null)
                GlideApp.with(context)
                    .load(StorageUtil.pathToReference(person.profilePicturePath))
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .into(viewHolder.imageView_profile_picture)
        }
        override fun getLayout() = R.layout.item_group_person
    }