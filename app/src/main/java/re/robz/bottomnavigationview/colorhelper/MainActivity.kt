package re.robz.bottomnavigationview.colorhelper

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.FrameLayout
import re.robz.bottomnavigation.circularcolorreveal.BottomNavigationCircularColorReveal

/**
 * Sample main activity.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the bottom navigation view.
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Get the colors for the bottom navigation view.
        val colors = resources.getIntArray(R.array.menu_colors)

        // Create the circular color reval and bind it to the bottom navigation view
        val reveal = BottomNavigationCircularColorReveal(colors)
        reveal.setuptWithBottomNavigationView(bottomNavigationView)

        // A custom navigation item selected listener can be bound to the reveal component
        reveal.setOnNavigationItemSelectedListener {
            // Do your custom operations on item selection here (e.g display a fragment)
            // ...
            // Allow selection
            true
        }

        // === UI enhancement =>
        // The restof the code below is not mandatory, it is made like this to reproduce the
        // Material Design Guidelines demo. If you don't want to draw behind the navigation bar
        // you can skip this part.

        // Set a fake transparent background to the first child of the bottom navigation view
        // so the ripple effet will draw on this view instead of drawing on the bottom navigation view
        // (ripple effect draws on first non transparent background)
        bottomNavigationView.getChildAt(0).setBackgroundResource(R.color.fake_transparent)

        // Update correct margins on apply window insets so the bottom navigationview can be drawn
        // Behind the navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, { _, insets ->
            // If bottom inset is greater than 0
            if (insets.systemWindowInsetBottom > 0) {
                // Get first subview of the bottom navigation view
                // Thsi view contains the items
                val subview = bottomNavigationView.getChildAt(0)
                val layoutParams = subview.layoutParams as FrameLayout.LayoutParams
                // Set gravity to top so the subview is drawn to the top of the bottom navigation view
                layoutParams.gravity = Gravity.TOP
                // Apply the bottom margin to the subview, so the menu items can be drawn above the navigation bar
                layoutParams.bottomMargin = insets.systemWindowInsetBottom
                // Set the layout params
                subview.layoutParams = layoutParams
            }

            // Consume le system windows insets
            insets.consumeSystemWindowInsets()
        })
    }
}
