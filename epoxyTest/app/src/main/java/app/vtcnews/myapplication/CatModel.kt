package app.vtcnews.myapplication

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.*

@EpoxyModelClass(layout = R.layout.rv_item)
abstract class CatModel : EpoxyModelWithHolder<CatHolder>() {

    @EpoxyAttribute
    var title : String = ""

    @EpoxyAttribute
    var imgId : Int = R.drawable.cat

    @EpoxyAttribute
    lateinit var viewClickListener : View.OnClickListener

    override fun bind(holder: CatHolder) {
        holder.imgView.setImageResource(imgId)
        holder.txtTitle.text = title
        holder.imgView.setOnClickListener(viewClickListener)
    }
}

class CatHolder : KotlinEpoxyHolder()
{
     val txtTitle  by bind<TextView>(R.id.txt_title)
     val imgView by bind<ImageView>(R.id.imageView)
}