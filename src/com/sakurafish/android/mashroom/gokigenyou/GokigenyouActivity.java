/*
 * Copyright 2011 sakura_fish<neko3genki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sakurafish.android.mashroom.gokigenyou;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

/***
 * お嬢様言葉マッシュルーム
 * 
 * @author sakura
 */
public class GokigenyouActivity extends Activity {
    private static final int REQUESTCODE = 1234;
    PreferenceHolder mPreferenceHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferences();

        Intent it = getIntent();
        String action = it.getAction();

        if (action != null && Defines.ACTION_INTERCEPT.equals(action)) {
            /** Simejiから呼出された時 */
            Intent intent = new Intent(this, MyExpandableListAdapter.class);
            intent.setAction(Defines.ACTION_INTERCEPT);
            startActivityForResult(intent, REQUESTCODE);
        } else {
            /** Simeji以外から呼出された時 */
            setContentView(R.layout.main);

            if (mPreferenceHolder.version == "" || mPreferenceHolder.noDialog) {
                // 初回起動時、又はダイアログを表示する設定になっていればダイアログを表示
                showDialog();
            } else {
                Intent intent = new Intent(getApplicationContext(), MyExpandableListAdapter.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * Simeji以外から呼び出された場合ダイアログボックスを表示
     */
    private void showDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // タイトルを設定
        alertDialogBuilder.setTitle(getResources().getText(R.string.AlertDialog_Title));
        // メッセージを設定
        alertDialogBuilder.setMessage(getResources().getText(R.string.AlertDialog_Message));
        // アイコンを設定
        alertDialogBuilder.setIcon(R.drawable.ic_launcher);

        alertDialogBuilder.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPreferenceHolder.noDialog = false;
                // OKボタンが押されたときの処理
                Intent intent = new Intent(getApplicationContext(), MyExpandableListAdapter.class);
                startActivity(intent);
                finish();
            }
        });
        // Positiveボタンとリスナを設定
        alertDialogBuilder.setNegativeButton("次回からこのメッセージを表示しない", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPreferenceHolder.noDialog = true;

                Intent intent = new Intent(getApplicationContext(), MyExpandableListAdapter.class);
                startActivity(intent);
                finish();
            }
        });
        // ダイアログを表示
        alertDialogBuilder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setPreferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                /** 結果を元の画面に戻す */
                String result = data.getStringExtra(Defines.REPLACE_KEY).toString();
                Intent intent = new Intent();
                intent.putExtra(Defines.REPLACE_KEY, result);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }

    /***
     * プリファレンスの値を読み込む
     */
    private void getPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        /** アプリのバージョン情報 */
        String pref_version = pref.getString(
                (String) getResources().getText(R.string.pref_key_version), "");
        /** ダイアログを表示するか */
        boolean pref_showDialog = pref.getBoolean(
                (String) getResources().getText(R.string.pref_key_noDialog), false);

        mPreferenceHolder = new PreferenceHolder(pref_version, pref_showDialog);

    }

    /***
     * プリファレンスの値を保存する
     */
    private void setPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edt = pref.edit();

        /** アプリのバージョン情報 */
        String pref_version = getVersionNumber("", this);
        edt.putString((String) getResources().getText(R.string.pref_key_version), pref_version);
        /** ダイアログを表示するか */
        edt.putBoolean((String) getResources().getText(R.string.pref_key_noDialog),
                mPreferenceHolder.noDialog);

        edt.commit();
    }

    /***
     * アプリのバージョン情報を取得
     * 
     * @param prefix
     * @param context
     * @return
     */
    public static String getVersionNumber(String prefix, Context context) {
        String versionName = prefix;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            versionName += info.versionName;
        } catch (NameNotFoundException e) {
            versionName += "0";
        }
        return versionName;
    }

    private class PreferenceHolder {
        String version;
        boolean noDialog;

        /**
         * constructor
         */
        public PreferenceHolder(String version, boolean noDialog) {
            this.version = version;
            this.noDialog = noDialog;
        }
    }
}
