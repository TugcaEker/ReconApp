package itech.com.reconapp;

import android.content.Context;
import android.content.Intent;

import com.reconinstruments.ui.list.StandardListItem;

public class ListItem extends StandardListItem {
    Intent intent;
    public ListItem(String text, Intent i) {
        super(text);
        this.intent = i;
    }
    public void onClick(Context context) {
        context.startActivity(intent);
    }
}