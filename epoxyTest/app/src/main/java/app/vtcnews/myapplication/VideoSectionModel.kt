package app.vtcnews.myapplication

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.video_section)
abstract class VideoSectionModel : EpoxyModelWithHolder<VideoSectionHolder>() {

    @EpoxyAttribute
    var videoList : List<DataItem> = listOf()

    @EpoxyAttribute
    var clickListener : (DataItem) -> Unit = {}

    override fun bind(holder: VideoSectionHolder) {
        super.bind(holder)

        holder.run {

            val imgs = mutableListOf<ImageView>().apply {
                add(imgVideoHeader)
                add(imgThumb1)
                add(imgThumb2)
                add(imgThumb3)
                add(imgThumb4)
            }

            val txt = mutableListOf<TextView>().apply {
                add(txtHeaderVideo)
                add(txtTitle1)
                add(txtTitle2)
                add(txtTitle3)
                add(txtTitle4)
            }

            imgs.forEachIndexed { index, imageView ->
                imageView.setImageResource(videoList[index].imageId)
                imageView.setOnClickListener {
                    clickListener.invoke(videoList[index])
                }
            }

            txt.forEachIndexed { index, textView ->
                textView.text = videoList[index].title
            }
        }
    }
}

class VideoSectionHolder : KotlinEpoxyHolder()
{
    val imgVideoHeader by bind<ImageView>(R.id.img_video_header)
    val imgThumb1 by bind<ImageView>(R.id.img_thumb_1)
    val imgThumb2 by bind<ImageView>(R.id.img_thumb_2)
    val imgThumb3 by bind<ImageView>(R.id.img_thumb_3)
    val imgThumb4 by bind<ImageView>(R.id.img_thumb_4)

    val txtHeaderVideo by bind<TextView>(R.id.txt_video_header)
    val txtTitle1 by bind<TextView>(R.id.txt_video_title_1)
    val txtTitle2 by bind<TextView>(R.id.txt_video_title_2)
    val txtTitle3 by bind<TextView>(R.id.txt_video_title_3)
    val txtTitle4 by bind<TextView>(R.id.txt_video_title_4)
}