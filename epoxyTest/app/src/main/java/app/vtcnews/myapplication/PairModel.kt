package app.vtcnews.myapplication

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.*

@EpoxyModelClass(layout = R.layout.rv_item_vertical)
abstract class PairModel : EpoxyModelWithHolder<PairHolder>() {
    @EpoxyAttribute
    lateinit var pair : Pair<DataItem, DataItem>

    override fun bind(holder: PairHolder) {
       holder.apply {
           img1.setImageResource(pair.first.imageId)
           txt1.text = pair.first.title

           img2.setImageResource(pair.second.imageId)
           txt2.text = pair.second.title
       }
    }
}

class PairHolder : KotlinEpoxyHolder()
{
    val img1  by bind<ImageView>(R.id.img1)
    val txt1 by bind<TextView>(R.id.txt1)
    val img2  by bind<ImageView>(R.id.img2)
    val txt2 by bind<TextView>(R.id.txt2)
}