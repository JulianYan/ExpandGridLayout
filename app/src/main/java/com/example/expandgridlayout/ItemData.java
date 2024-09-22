package com.example.expandgridlayout;

public class ItemData {
    String mTitle;
    String mKey;
    int mIndex;
    int[] mIconIds;
    CharSequence[] mEntries;
    CharSequence[] mEntryValues;

    Type type;

    public enum Type{
        LIST,
        TOGGLE,
        MENU
    }

    public ItemData(String mTitle, String mKey, int mIndex, int[] mIconIds, CharSequence[] mEntries, CharSequence[] mEntryValues) {
        this.mTitle = mTitle;
        this.mKey = mKey;
        this.mIndex = mIndex;
        this.mIconIds = mIconIds;
        this.mEntries = mEntries;
        this.mEntryValues = mEntryValues;
        if (mEntries == null || mEntries.length > 5) {
            type = Type.MENU;
        } else if (mEntries.length == 2) {
            type = Type.TOGGLE;
        } else {
            type = Type.LIST;
        }
    }
}
