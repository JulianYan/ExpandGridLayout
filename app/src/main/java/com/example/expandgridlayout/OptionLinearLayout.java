package com.example.expandgridlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OptionLinearLayout extends LinearLayout {
    private final LayoutInflater mInflater;
    private ItemData mItemData;
    private List<OptionViewHolder> mActive = new ArrayList<>(4);
    private List<OptionViewHolder> mCached = new ArrayList<>(1);
    private int mPosition = -1;

    private OptionClickListener mOptionClickListener;

    public OptionLinearLayout(Context context) {
        this(context,null);
    }

    public OptionLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OptionLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mInflater = LayoutInflater.from(context);
    }

    public interface OptionClickListener{
        void onOptionClick(ItemData key, String value);
    }

    public void setOptionClickListener(OptionClickListener optionClickListener) {
        mOptionClickListener = optionClickListener;
    }

    public void setData(ItemData itemData, int position, int orientation) {
        mItemData = itemData;
        mPosition = position;
        int childCount = mActive.size();
        int tarSize = mItemData.mEntries.length;
        if (childCount < tarSize) {
            for (int i = childCount; i < tarSize; i++) {
                OptionViewHolder holder;
                if (!mCached.isEmpty()) {
                    holder = mCached.remove(0);
                } else {
                    holder = onCreateViewHolder(this);
                }
                addView(holder.mItemView);
                mActive.add(holder);
            }
        } else if (childCount > tarSize) {
            for (int i = mActive.size() - 1; i >= tarSize; i--) {
                removeViewAt(i);
                OptionViewHolder holder = mActive.remove(i);
                removeViewHolder(holder);
                mCached.add(holder);
            }
        }

        for (int i = 0; i < mActive.size(); i++) {
            OptionViewHolder viewHolder = mActive.get(i);
            viewHolder.mPosition = i;
            viewHolder.mOrientation = orientation;
            bindViewHolder(viewHolder, i);
        }
        requestLayout();
    }

    private void removeViewHolder(OptionViewHolder holder) {
        holder.mItemView.setOnClickListener(null);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    private void bindViewHolder(OptionViewHolder viewHolder, int i) {
        ItemData itemData = mItemData;
        String title = itemData.mTitle;
        String key = itemData.mKey;
        int icon = itemData.mIconIds[i];
        String entry = (String) itemData.mEntries[i];
        String value = (String) itemData.mEntryValues[i];
        int index = itemData.mIndex;
        viewHolder.mTitle = title;
        viewHolder.mKey = key;
        viewHolder.mEntry = entry;
        viewHolder.mEntryValue = value;
        viewHolder.mSelected = index == i;
        viewHolder.mIconView.setImageResource(icon);
        viewHolder.mTitleView.setText(entry);
        viewHolder.mItemView.setOnClickListener(v -> {
            if (mOptionClickListener != null) {
                mOptionClickListener.onOptionClick(itemData, value);
            }
        });
    }

    private OptionViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_view, parent, false);
        return new OptionViewHolder(view);
    }

    public static class OptionViewHolder {

        private final View mItemView;
        private ImageView mIconView;
        private TextView mTitleView;
        String mTitle;
        String mKey;
        boolean mSelected;
        String mEntry;
        String mEntryValue;
        int mOrientation = 0;
        int mPosition = -1;


        public OptionViewHolder(View itemView) {
            mItemView = itemView;
            mIconView = itemView.findViewById(R.id.icon_view);
            mTitleView = itemView.findViewById(R.id.title_view);
        }

    }
}
