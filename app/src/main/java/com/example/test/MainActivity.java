package com.example.test;

import android.app.AppGlobals;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 触发pms sync package-restrictions.xml
        Intent i = new Intent(this, MainActivity.class);
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        try {
            String resolvedType = i.resolveTypeIfNeeded(getContentResolver());
            IntentFilter inf = new IntentFilter(Intent.ACTION_MAIN);
            inf.addCategory(Intent.CATEGORY_DEFAULT);
//            AppGlobals.getPackageManager().setLastChosenActivity(i, resolvedType, PackageManager.MATCH_DEFAULT_ONLY,
//                    inf, 0, new ComponentName(this, this.getClass()));

            // 触发 Settings.writePackageRestrictionsLPr() in PMS.findPreferredActivity(), 不论是否成功
            ResolveInfo ri = AppGlobals.getPackageManager().resolveIntent(i, i.resolveTypeIfNeeded(getContentResolver()), 0, 0);
            ResolveInfo riLast = AppGlobals.getPackageManager().getLastChosenActivity(i, resolvedType, 0);
            Log.d(TAG, "onResume: "+"resolvedType="+resolvedType+", ri="+ri+", riLast="+riLast);
        } catch (RemoteException e) {
            Log.e(TAG, "error:", e);
        }
    }
}
