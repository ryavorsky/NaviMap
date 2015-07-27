package com.navimap.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.navimap.MainMenu;
import com.navimap.utils.LogUtils;

/**
 * Created by Makvit on 27.07.2015.
 */
public class DeepLinkingActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        LogUtils.d(action);
        LogUtils.d(data.toString());

        Intent mainActivity = new Intent(this, MainMenu.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivity.putExtra(MainMenu.EXTRA_URL, data.getPath().replace("/",""));
        startActivity(mainActivity);


    }
}
