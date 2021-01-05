package app.vtcnews.myapplication

import android.content.Context
import android.widget.Toast
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.VisibilityState
import com.airbnb.epoxy.carousel
import com.airbnb.epoxy.group


class CatController : TypedEpoxyController<List<DataItem>>() {

    private var visibleItemIndex: Int = 0
    private var dotTabLayout : dtlWrapper = dtlWrapper()
    var context : Context? = null

    override fun buildModels(data1: List<DataItem>?) {
        data1?.take(4)?.forEach {
            cat {
                id(id++)
                title(it.title)
                imgId(it.imageId)
                viewClickListener { model, parentView, clickedView, position ->
                    Toast.makeText(parentView.imgView.context, "$position", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        group {
            id("hot")
            layout(R.layout.hot_new_section)
            carousel {
                id("ViewPager")
                hasFixedSize(true)
                models(
                    data1!!.windowed(size = 2, step = 2).mapIndexed { index, p ->
                        PairModel_()
                            .id(vpid++)
                            .pair (Pair(p[0], p[1]))
                            .onVisibilityStateChanged { _, _, visibilityState ->
                                if (visibilityState == VisibilityState.FOCUSED_VISIBLE) {
                                    visibleItemIndex = index
                                    dotTabLayout.tabLayout?.setChecked(index)
                                }
                            }
                    }
                )
            }
        }

        data1?.take(3)?.forEach {
            cat {
                id(id++)
                title(it.title)
                imgId(it.imageId)
                viewClickListener { model, parentView, clickedView, position ->
                    Toast.makeText(parentView.imgView.context, "$position", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        val videos = data1!!.take(5).mapIndexed { index, dataItem ->
            DataItem(title = dataItem.title + index.toString())
        }

        videoSection {
            id("videos")
            videoList(videos)
            clickListener {
                Toast.makeText(context, it.title, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        private var id: Long = 0
        private var vpid: Long = 0

    }
}