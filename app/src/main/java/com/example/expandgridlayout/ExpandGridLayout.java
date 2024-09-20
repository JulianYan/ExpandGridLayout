package com.example.expandgridlayout;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ExpandGridLayout extends FrameLayout {
    private static final String TAG = "ExpandGridLayout";
    private int mColumnCount = 3;

    private int mGap = 0;

    public static final int NO_POSITION = -1;
    private int mExpandedIndex = NO_POSITION;

    private List<View> mItemViews = new ArrayList<>();

    private List<View> mAddedViews = new ArrayList<>();

    private List<View> mRemovedViews = new ArrayList<>();

    public ExpandGridLayout(Context context) {
        this(context,null);
    }

    public ExpandGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }
        Log.i(TAG, "ExpandGridLayout: " + layoutTransition);

    }

    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
    }

    public void setGap(int gap) {
        this.mGap = gap;
    }

    public void setExpandedIndex(int mExpandedIndex) {
        this.mExpandedIndex = mExpandedIndex;
        requestLayout();
    }

    public int getExpandedIndex() {
        return mExpandedIndex;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false /* no force left gravity */);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

//    public static class ViewHolder {
//        View mItemView;
//        int mPosition = NO_POSITION;
//        ItemData mModeData;
//
//        public ViewHolder(View itemView) {
//            this.mItemView = itemView;
//        }
//
//        public void bind(ModeData modeData, int position) {
//            mPosition = position;
//            mModeData = modeData;
//            mItemView.setContentDescription(mItemView.getResources().getString(R.string.accessibility_switch_mode,
//                    modeData.getName()));
//        }
//    }
//
//    public ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ViewHolder viewHolder;
//        if (viewType == IMAGE) {
//            View view = mInflater.inflate(R.layout.bottom_mode_image_item_layout, parent, false);
//            viewHolder = new ModeImageItemViewHolder(view);
//        } else {
//            View view = mInflater.inflate(R.layout.bottom_mode_text_item_layout, parent, false);
//            viewHolder = new ModeTextItemViewHolder(view);
//        }
//        viewHolder.mItemView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnItemClickListener.onItemClick(viewHolder);
//            }
//        });
//
//        return viewHolder;
//    }

    void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
        final int count = getChildCount();
        int columns = mColumnCount;
        int rows = (int) Math.ceil((double) count / columns);
        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();
        int expandedIndex = mExpandedIndex;
        if (count > 0) {
            int layoutTop = parentTop;
            for (int i = 0; i < rows; i++) {
                int maxItemHeight = 0;
                Log.i(TAG, "layoutChildren: "+layoutTop);
                //expanded item in current row
                if (expandedIndex >= i * columns && expandedIndex < (i + 1) * columns) {
                    final View expandedChild = getChildAt(expandedIndex);
                    int expandedChildLeft = parentLeft;
                    int expandedChildTop = layoutTop;
                    int expandedChildWidth = parentRight - parentLeft;
                    int expandedChildHeight = expandedChild.getMeasuredHeight();
                    maxItemHeight = Math.max(expandedChildHeight, maxItemHeight);
                    expandedChild.layout(expandedChildLeft, expandedChildTop, expandedChildLeft + expandedChildWidth, expandedChildTop + expandedChildHeight);
                    int layoutLeft = 0;
                    for (int j = expandedIndex - 1; j >= i * columns; j--) {
                        View child = getChildAt(j);
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        maxItemHeight = Math.max(height, maxItemHeight);
                        child.layout(layoutLeft - width, layoutTop, layoutLeft, layoutTop + height);
                        layoutLeft = layoutLeft - width;
                    }
                    int layoutRight = right;
                    for (int j = expandedIndex + 1; j < (i + 1) * columns && j < count; j++) {
                        View child = getChildAt(j);
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        maxItemHeight = Math.max(height, maxItemHeight);
                        child.layout(layoutRight, layoutTop, layoutRight + width, layoutTop + height);
                        layoutRight += width;
                    }
                } else {
                    int layoutLeft = parentLeft;
                    for (int j = 0; j < columns; j++) {
                        int row = i;
                        int col = j;
                        int index = row * columns + col;
                        if (index >= count) {
                            break;
                        }
                        Log.i(TAG, "layoutChildren: row=" + row + ",col=" + col + ",layoutLeft" + layoutLeft);
                        final View child = getChildAt(index);
                        if (child.getVisibility() != GONE) {
                            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                            final int width = child.getMeasuredWidth();
                            final int height = child.getMeasuredHeight();
                            maxItemHeight = Math.max(height, maxItemHeight);
                            int childLeft;
                            int childTop;
                            childLeft = layoutLeft + lp.leftMargin;
                            childTop = layoutTop + lp.topMargin;
                            child.layout(childLeft, childTop, childLeft + width, childTop + height);
                            layoutLeft += width;
                        }
                    }
                }
                layoutTop += maxItemHeight;
            }
        }

    }
}