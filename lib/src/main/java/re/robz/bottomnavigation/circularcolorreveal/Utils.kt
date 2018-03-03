package re.robz.bottomnavigation.circularcolorreveal

import android.animation.Animator
import android.graphics.Point
import android.view.View

/**
 * Simple Animator extension that executes an action on animation end.
 */
internal fun Animator.doOnAnimationEnd(action: () -> Unit) = addListener(object : Animator.AnimatorListener {

    override fun onAnimationEnd(p0: Animator?) {
        action()
        p0?.removeListener(this)
    }

    override fun onAnimationRepeat(p0: Animator?) {}
    override fun onAnimationCancel(p0: Animator?) {}
    override fun onAnimationStart(p0: Animator?) {}
})

/**
 * Simple function that get the max value from a list of doubles.
 */
internal fun max(vararg values: Double): Double {
    var max = Double.MIN_VALUE
    for (value in values) {
        max = Math.max(max, value)
    }
    return max
}

/**
 * Applies Pythagore and calculate the hypotenus of the triangle (a² + b² = c²).
 */
internal fun pythagore(a: Int, b: Int): Double {
    val a2 = Math.pow(a.toDouble(), 2.0)
    val b2 = Math.pow(b.toDouble(), 2.0)
    return Math.ceil(Math.sqrt(a2 + b2))
}

/**
 * Gets the absolute position of a view (from location on screen).
 */
internal fun View.getAbsolutePosition(): Point {
    val position = IntArray(2)
    getLocationOnScreen(position)
    return Point(position[0], position[1])
}

/**
 * Performs the given action when this view is next laid out. Inspired by Android KTX (https://github.com/android/android-ktx)
 */
internal fun View.doOnNextLayout(action: (view: View) -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
                view: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)
            action(view)
        }
    })
}

/**
 * Exception thrown when a color has not been defineds for this menu item.
 */
class ColorNotFoundForItemException(itemId: Int) : RuntimeException("Corlor not found for item id $itemId")