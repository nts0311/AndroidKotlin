package app.vtcnews.myapplication

import android.widget.Toast
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.VisibilityState
import com.airbnb.epoxy.carousel
import com.airbnb.epoxy.group


class CatController : TypedEpoxyController<List<DataItem>>() {

    private var visibleItemIndex: Int = 0
    private var dotTabLayout : dtlWrapper = dtlWrapper()

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
            dotTabLayout {
                id("tabs")
                count(data1!!.size / 2)
                checked(visibleItemIndex)
                 getThisFuckingView(dotTabLayout)
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
    }

    companion object {
        private var id: Long = 0
        private var vpid: Long = 0

    }
}