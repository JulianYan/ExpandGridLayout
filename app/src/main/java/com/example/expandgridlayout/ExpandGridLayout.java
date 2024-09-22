package com.example.expandgridlayout;

import static android.view.View.MeasureSpec.makeMeasureSpec;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ExpandGridLayout extends FrameLayout {
    private static final String TAG = "ExpandGridLayout";
    private final LayoutInflater mInflater;
    private int mColumnCount = 3;

    private int mGap = 0;

    public static final int NO_POSITION = -1;
    private int mExpandedPosition = NO_POSITION;

    private List<ViewHolder> mItems = new ArrayList<>();

    private List<ViewHolder> mActive = new ArrayList<>(9);
    private List<ViewHolder> mCached = new ArrayList<>(3);
    private int mOrientation;

    private OptionLinearLayout mExpandedOptionView;

    private ItemClickListener mOnItemClickListener;

    public ExpandGridLayout(Context context) {
        this(context,null);
    }

    public ExpandGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
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

    public void setOnItemClickListener(ItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setExpandedPosition(int index) {
        if (index > NO_POSITION) {
            ViewHolder viewHolder = mActive.get(index);
            if (mExpandedOptionView == null) {
                mExpandedOptionView = new OptionLinearLayout(getContext());
                mExpandedOptionView.setOrientation(LinearLayout.HORIZONTAL);
                ShapeDrawable divider = new ShapeDrawable();
                divider.setIntrinsicWidth(mGap);
                divider.setAlpha(0);
                mExpandedOptionView.setDividerDrawable(divider);
                mExpandedOptionView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                final int parentLeft = getPaddingLeft();
                final int parentRight = getRight() - getLeft() - getPaddingRight();
                mExpandedOptionView.setLayoutParams(new ViewGroup.LayoutParams(parentRight - parentLeft, ViewGroup.LayoutParams.MATCH_PARENT));
                mExpandedOptionView.setOptionClickListener(new OptionLinearLayout.OptionClickListener() {
                    @Override
                    public void onOptionClick(ItemData itemData, String value) {
                        if (mOnItemClickListener != null) {
                            List<ItemData> list = mItemDataList;
                            int index = list.indexOf(itemData);
                            ViewGroup view = mActive.get(index).mItemView;
                            boolean handled = mOnItemClickListener.onItemClick(view, itemData, index, value);
                            setExpandedPosition(-1);
                        }

                    }
                });
            }
            mExpandedOptionView.setData(viewHolder.mItemData, index, mOrientation);
            viewHolder.mItemView.addView(mExpandedOptionView);
        } else {
            ViewHolder viewHolder = mActive.get(mExpandedPosition);
            viewHolder.mItemView.removeView(mExpandedOptionView);
        }
        this.mExpandedPosition = index;
        requestLayout();
    }

    public int getExpandedIndex() {
        return mExpandedPosition;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false /* no force left gravity */);
    }

    static int adjust(int measureSpec, int delta) {
        return makeMeasureSpec(
                MeasureSpec.getSize(measureSpec + delta),  MeasureSpec.getMode(measureSpec));
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        int totalHeight = 0;

        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();

        int widthSpecSansPadding =  adjust(widthMeasureSpec, -hPadding);
        int heightSpecSansPadding = adjust(heightMeasureSpec, -vPadding);

        int column = mColumnCount;
        int row = (int) Math.ceil((double) count / column);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                int index = i * column + j;
                if (index >= count) {
                    break;
                }
                final View child = getChildAt(index);
                if (child.getVisibility() != GONE) {
                    measureChildWithMargins(child, widthSpecSansPadding, 0, heightSpecSansPadding, 0);
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    maxWidth = Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                    maxHeight = Math.max(maxHeight,
                            child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                    childState = combineMeasuredStates(childState, child.getMeasuredState());
                }
            }
            totalHeight += maxHeight;
            if (i != 0) {
                totalHeight += mGap;
            }
        }

        // Account for padding too
        maxWidth += hPadding;
        totalHeight += vPadding;


        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(totalHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    public class ViewHolder {

        private ImageView mIconView;
        private TextView mTitleView;
        ViewGroup mItemView;
        int mPosition = NO_POSITION;
        ItemData mItemData;
        int mOrientation = 0;


        public ViewHolder(ViewGroup itemView) {
            this.mItemView = itemView;
            mIconView = mItemView.findViewById(R.id.icon_view);
            mTitleView = mItemView.findViewById(R.id.title_view);
        }

        public void bind(ItemData itemData, ItemClickListener itemClickListener) {
            mItemData = itemData;
            int index = itemData.mIndex;
            int iconId = itemData.mIconIds[index];
            Log.i(TAG, "bind: " + iconId);
            mIconView.setImageResource(iconId);
            mTitleView.setText(itemData.mTitle);
            mItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mExpandedPosition != NO_POSITION) {
                        setExpandedPosition(-1);
                    } else {
                        ItemData data = mItemData;
                        switch (data.type) {
                            case TOGGLE:
                                if (itemClickListener != null) {
                                    int length = data.mEntryValues.length;
                                    int index = (data.mIndex + 1) % length;
                                    itemClickListener.onItemClick(v, data, mPosition, (String) data.mEntryValues[index]);
                                }
                                break;
                            case LIST:
                                if (mExpandedPosition != NO_POSITION) {
                                    setExpandedPosition(-1);
                                } else {
                                    setExpandedPosition(mPosition);
                                }
                                break;
                            case MENU:
                                if (itemClickListener != null) {
                                    itemClickListener.onItemClick(v, data, mPosition, (String) data.mEntryValues[data.mIndex]);
                                }
                                break;
                        }
                    }
                }
            });
        }
    }

    public interface ItemClickListener {
        boolean onItemClick(View view, ItemData itemData, int position, String value);

    }


    private List<ItemData> mItemDataList;

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent){
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.item_view,parent,false);
        return new ViewHolder(view);
    }



    public final void bindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mPosition = position;
        holder.mOrientation = mOrientation;
        holder.bind(mItemDataList.get(position), mOnItemClickListener);
    }

    public int getItemCount(){
        if (mItemDataList != null) {
            return mItemDataList.size();
        }else {
            return 0;
        }
    }

    public void setData(List<ItemData> itemDataList) {
        mItemDataList = itemDataList;
        int childCount = mActive.size();
        int tarSize = getItemCount();
        if (childCount < tarSize) {
            for (int i = childCount; i < tarSize; i++) {
                ViewHolder holder;
                if (!mCached.isEmpty()) {
                    holder = mCached.remove(0);
                } else {
                    holder = onCreateViewHolder(ExpandGridLayout.this);
                }
                addView(holder.mItemView);
                mActive.add(holder);
            }
        } else if (childCount > tarSize) {
            for (int i = mActive.size() - 1; i >= tarSize; i--) {
                removeViewAt(i);
                ViewHolder holder = mActive.remove(i);
                mCached.add(holder);
            }
        }

        for (int i = 0; i < mActive.size(); i++) {
            ViewHolder viewHolder = mActive.get(i);
            viewHolder.mPosition = i;
            viewHolder.mOrientation = mOrientation;
            bindViewHolder(viewHolder, i);
        }
        requestLayout();
    }

    void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
        final int count = getChildCount();
        int columns = mColumnCount;
        int rows = (int) Math.ceil((double) count / columns);
        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();
        int expandedIndex = mExpandedPosition;
        if (count > 0) {
            int layoutTop = parentTop;
            for (int i = 0; i < rows; i++) {
                int maxItemHeight = 0;
                if (i != 0) {
                    layoutTop += mGap;
                }
                //has expanded item in current row
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
                        final View child = getChildAt(index);
                        if (child.getVisibility() != GONE) {
                            if (j != 0) {
                                layoutLeft += mGap;
                            }
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