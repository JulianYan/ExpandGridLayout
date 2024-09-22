package com.example.expandgridlayout;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ExpandGridLayout mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = findViewById(R.id.settings);
        Resources res = getResources();
        mSettings.setGap(res.getDimensionPixelOffset(R.dimen.gap));
        mSettings.setColumnCount(3);
        int[] flashIcons = getIds(res, R.array.camera_flashmode_icons);
        CharSequence[] flashEntries = res.getTextArray(R.array.pref_camera_flashmode_entries);
        CharSequence[] flashEntryValues = res.getTextArray(R.array.pref_camera_flashmode_entryvalues);
        ItemData itemData = new ItemData("flash", "pref_flash_key", 0, flashIcons, flashEntries, flashEntryValues);
        int[] timerIcons = getIds(res, R.array.camera_selftimer_icons);
        CharSequence[] timerEntries = res.getTextArray(R.array.pref_camera_selftimer_entries);
        CharSequence[] timerEntryValues = res.getTextArray(R.array.pref_camera_selftimer_entryvalues);
        ItemData itemData2 = new ItemData("timer", "pref_timer_key", 0, timerIcons, timerEntries, timerEntryValues);
        List<ItemData> itemDataList = new ArrayList<>();
        itemDataList.add(itemData);
        itemDataList.add(itemData);
        itemDataList.add(itemData);
        itemDataList.add(itemData2);
        itemDataList.add(itemData2);
        itemDataList.add(itemData2);
        itemDataList.add(itemData);
        itemDataList.add(itemData2);
        mSettings.setData(itemDataList);

        mSettings.setOnItemClickListener(new ExpandGridLayout.ItemClickListener() {
            @Override
            public boolean onItemClick(View view, ItemData itemData, int position, String value) {
                Log.i(TAG, "onItemClick: " + itemData + value);
                return false;
            }
        });

        int childCount = mSettings.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mSettings.getChildAt(i);
            int finalI = i;
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSettings.getExpandedIndex() < 0) {
                        mSettings.setExpandedPosition(finalI);
                    }else {
                        mSettings.setExpandedPosition(-1);
                    }
                }
            });
        }
    }

    int[] getIds(Resources res, int iconsRes) {
        if (iconsRes == 0) {
            return null;
        }
        TypedArray array = res.obtainTypedArray(iconsRes);
        int n = array.length();
        int[] ids = new int[n];
        for (int i = 0; i < n; ++i) {
            ids[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return ids;
    }


}