package com.deconstructors.kronoui.utility;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.deconstructors.kronoui.R;
import com.google.android.material.appbar.AppBarLayout;

public class ToolbarBehavior extends CoordinatorLayout.Behavior<RelativeLayout>
{
    private Context Context;
    private int StartMarginLeft;
    private int EndMarginLeft;
    private int MarginRight;
    private int StartMarginBottom;
    private boolean isHidden;

    public ToolbarBehavior(Context context, AttributeSet attrs)
    {
        this.Context = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency)
    {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child, View dependency)
    {
        shouldInitProperties(child, dependency);

        int maxScroll = ((AppBarLayout) dependency).getTotalScrollRange();
        float percentage = Math.abs(dependency.getY()) / (float) maxScroll;

        float childPosition = dependency.getHeight()
                + dependency.getY()
                - child.getHeight()
                - (getToolbarHeight() - child.getHeight()) * percentage / 2;


        childPosition = childPosition - this.StartMarginBottom * (1f - percentage);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.leftMargin = (int) (percentage * this.EndMarginLeft) + this.StartMarginLeft;
        lp.rightMargin = this.MarginRight;
        child.setLayoutParams(lp);

        child.setY(childPosition);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            if (this.isHidden && percentage < 1)
            {
                child.setVisibility(View.VISIBLE);
                this.isHidden = false;
            }
            else if (!isHidden && percentage == 1)
            {
                child.setVisibility(View.GONE);
                this.isHidden = true;
            }
        }

        return true;
    }

    private void shouldInitProperties(RelativeLayout child, View dependency)
    {
        if (this.StartMarginLeft == 0)
        {
            this.StartMarginLeft = this.Context
                    .getResources()
                    .getDimensionPixelOffset(R.dimen.header_view_start_margin_left);
        }

        if (EndMarginLeft == 0)
        {
            this.EndMarginLeft = this.Context
                    .getResources()
                    .getDimensionPixelOffset(R.dimen.header_view_end_margin_left);
        }

        if (StartMarginBottom == 0)
        {
            this.StartMarginBottom = this.Context
                    .getResources()
                    .getDimensionPixelOffset(R.dimen.header_view_start_margin_bottom);
        }

        if (this.MarginRight == 0)
        {
            this.MarginRight = this.Context
                    .getResources()
                    .getDimensionPixelOffset(R.dimen.header_view_end_margin_right);
        }
    }


    public int getToolbarHeight()
    {
        int result = 0;
        TypedValue tv = new TypedValue();

        if (this.Context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            result = TypedValue.complexToDimensionPixelSize(tv.data,
                                                            this.Context.getResources().getDisplayMetrics());
        }

        return result;
    }
}
