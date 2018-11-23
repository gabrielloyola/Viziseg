package utfpr.loyola.gabriel.viziseg.recyclerview.item

import android.os.Build
import android.view.Gravity
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import utfpr.loyola.gabriel.viziseg.R
import utfpr.loyola.gabriel.viziseg.model.Message
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import utfpr.loyola.gabriel.viziseg.AppConstants
import java.text.SimpleDateFormat

abstract class MessageItem(private val message: Message)
    : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)
    }
    private fun setTimeText(viewHolder: ViewHolder) {
        val dateFormat = SimpleDateFormat
            .getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }
    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_white
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = lParams
            }
            viewHolder.textView_message_sender.apply {
                text = "Eu"
                this.gravity = Gravity.END
            }
        }
        else {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_grey
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = lParams
            }
            viewHolder.textView_message_sender.apply {
                text = message.senderName
            }
        }
    }
}