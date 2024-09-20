package com.example.expandgridlayout;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ExpandGridLayout mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mSettings = findViewById(R.id.settings);
        mSettings.setGap(getResources().getDimensionPixelOffset(R.dimen.gap));
        mSettings.setColumnCount(3);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        int childCount = mSettings.getChildCount();
        View item = getLayoutInflater().inflate(R.layout.item_view, mSettings, false);
        for (int i = 0; i < childCount; i++) {
            View childAt = mSettings.getChildAt(i);
            int finalI = i;
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSettings.getExpandedIndex() < 0) {
                        mSettings.setExpandedIndex(finalI);
                    }else {
                        mSettings.setExpandedIndex(-1);
                    }
                }
            });
        }
    }

}