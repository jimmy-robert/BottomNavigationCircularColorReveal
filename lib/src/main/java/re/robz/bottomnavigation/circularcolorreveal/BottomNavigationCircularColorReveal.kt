package re.robz.bottomnavigation.circularcolorreveal

import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewCompat
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout

/**
 * Base class to manage BottomNavigationView color animations like shown in Material Design guidelines.
 *
 * Uses a circular reveal animation on Lollipop+ (21+) and simply update background color on pre-Lollipop.
 */
class BottomNavigationCircularColorReveal(@ColorInt private val colors: IntArray) {

    companion object {
        /**
         * Default animation duration is 250ms.
         */
        private const val DEFAULT_ANIMATION_DURATION = 250 // ms
    }

    /**
     * The bound BottomNavigationView to which the color animation will be applied.
     */
    private var bottomNavigationView: BottomNavigationView? = null

    /**
     * External item selected listener.
     */
    private var onNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    /**
     * The one-stop shop for setting up this BottomNavigationColorAnim with a BottomNavigationView.
     */
    fun setuptWithBottomNavigationView(bottomNavigationView: BottomNavigationView) {
        // Remove previously bound BottomNavigationView listener
        this.bottomNavigationView?.setOnNavigationItemSelectedListener(null)

        // Update bound BottomNavigationView
        this.bottomNavigationView = bottomNavigationView

        // Update background color with current selectedId
        setSelectedColor()

        // If the view hasnot been laid out yet, set selected color on next lay out
        if (!ViewCompat.isLaidOut(bottomNavigationView) || bottomNavigationView.isLayoutRequested) {
            bottomNavigationView.doOnNextLayout {
                setSelectedColor()
            }
        }

        // Set item selected listener to update color
        bottomNavigationView.setOnNavigationItemSelectedListener {
            // Do notforget to call external selction listener to know if the item should be selected
            val itemShouldBeSelected = onNavigationItemSelectedListener?.onNavigationItemSelected(it) ?: true

            // Update color only if the item should be selected
            if (itemShouldBeSelected) {
                updateColor(it.itemId)
            }

            itemShouldBeSelected
        }
    }

    /**
     * Unbinds the reveal component from the bottom navigation view.
     */
    fun unbind() {
        bottomNavigationView?.setOnNavigationItemSelectedListener(null)
        onNavigationItemSelectedListener = null
        bottomNavigationView = null
    }

    /**
     * This class need to use itsown navigation item selected listener. So an external listener can be
     * used for your custom behavior.
     */
    fun setOnNavigationItemSelectedListener(listener: BottomNavigationView.OnNavigationItemSelectedListener?) {
        onNavigationItemSelectedListener = listener
    }

    /**
     *  Same as `setOnNavigationItemSelectedListener` but uses a lambda.
     */
    fun setOnNavigationItemSelectedListener(listener: (MenuItem) -> Boolean) {
        setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener(listener))
    }

    /**
     * Updates BottomNavigationView background color using a circular reveal animation on Lollipop+.
     *
     * For pre-Lollipo, set background is simply updated without any animation.
     */
    private fun updateColor(itemId: Int) {
        val bottomNavigationView = ensureBottomNavigationView()

        // Get color for this item id
        val color = getColorForItemId(itemId)

        // Pre-Lollipop: update background color without any animation
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bottomNavigationView.setBackgroundColor(color)
        }
        // Lollipop+: update background color using a circular reveal animation
        else {
            revealColor(color, itemId)
        }
    }

    /**
     * Gets the color for the item id parameter.
     */
    private fun getColorForItemId(itemId: Int): Int {
        val bottomNavigationView = ensureBottomNavigationView()

        // Loop over menu items to find current item id index and return corresponding color
        for (index in 0 until bottomNavigationView.menu.size()) {
            val currentItem = bottomNavigationView.menu.getItem(index)
            if (currentItem.itemId == itemId) {
                return colors[index]
            }
        }

        // Crash if color has not been found
        throw ColorNotFoundForItemException(itemId)
    }

    /**
     * Updates BottomNavigationView background color using a circular reveal animation.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun revealColor(@ColorInt color: Int, itemId: Int) {
        val bottomNavigationView = ensureBottomNavigationView()

        // Find view corresponding to the itemId parameter
        val itemView = bottomNavigationView.findViewById<View>(itemId)

        // Get the absolute position to be sure that the coordinates are correct
        val bottomNavigationViewPosition = bottomNavigationView.getAbsolutePosition()
        val itemViewPosition = itemView.getAbsolutePosition()

        // Calculate center coordinates of item view
        val centerX = itemViewPosition.x - bottomNavigationViewPosition.x + itemView.width / 2
        val centerY = itemViewPosition.y - bottomNavigationViewPosition.y + itemView.height / 2

        // Calculate start and end radii for the circular reveal animation
        val startRadius = 0f
        val endRadius = max(
                // To top left
                pythagore(centerX, centerY),
                // To top right
                pythagore(bottomNavigationView.width - centerX, centerY),
                // To bottom right
                pythagore(bottomNavigationView.width - centerX, bottomNavigationView.height - centerY),
                // To bottom left
                pythagore(centerX, bottomNavigationView.height - centerY)
        ).toFloat()

        // Create a dummy reveal view that
        val revealView = View(bottomNavigationView.context)
        revealView.layoutParams = FrameLayout.LayoutParams(bottomNavigationView.width, bottomNavigationView.height)
        revealView.setBackgroundColor(color)
        // Set elevation to -1 so the reveal view willebdrawn behind the menu items.
        revealView.elevation = -1f

        // Add the reveal view to the BottomNavigationView
        bottomNavigationView.addView(revealView)

        // Initialize circular reveal animation
        val animator = ViewAnimationUtils.createCircularReveal(revealView, centerX, centerY, startRadius, endRadius)
        // Sets animation duration to 250ms
        val duration = DEFAULT_ANIMATION_DURATION
        animator.duration = duration.toLong()
        // On animation end, set background color and remove dummy reveal view
        animator.doOnAnimationEnd {
            bottomNavigationView.setBackgroundColor(color)
            bottomNavigationView.removeView(revealView)
        }
        // Start animation
        animator.start()
    }

    /**
     * Ensure that the BottomNavigationView has been defined.
     */
    private fun ensureBottomNavigationView(): BottomNavigationView {
        return this.bottomNavigationView
                ?: throw NullPointerException("BottomNavigationView has not been defined.")
    }

    /**
     * Update background color according to selected item without any animation.
     */
    private fun setSelectedColor() {
        val bottomNavigationView = ensureBottomNavigationView()

        val selectedId = bottomNavigationView.selectedItemId
        if (selectedId > 0) {
            val selectedColor = getColorForItemId(selectedId)
            bottomNavigationView.setBackgroundColor(selectedColor)
        }
    }

}