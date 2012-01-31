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

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * お嬢様言葉まっしゅリスト画面 {@link ExpandableListAdapter} from
 * {@link BaseExpandableListAdapter}.
 */
public class MyExpandableListAdapter extends ExpandableListActivity {

    ExpandableListAdapter mAdapter;

    boolean mLaunchFromSimeji = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // どこから呼び出されたか
        Intent it = getIntent();
        String action = it.getAction();

        if (action != null && Defines.ACTION_INTERCEPT.equals(action)) {
            mLaunchFromSimeji = true;
        }

        // インナークラスでアダプターの内容をセット
        mAdapter = new MyExpandableListAdapterInner();
        // アダプターをExpandableListViewにセット
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {

        /** 子要素がクリックされた場合の処理 */
        String result = (mAdapter.getChild(groupPosition, childPosition)).toString();

        /** Simeji以外から呼び出された場合、クリップボードにコピー */
        if (!mLaunchFromSimeji) {
            ClipboardManager cm =
                    (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setText(result);
            // Toastでメッセージを表示する
            Toast.makeText(getApplicationContext(), "「" + result + "」をクリップボードにコピーしました",
                    Toast.LENGTH_SHORT).show();

        } else {

            /** 結果を戻す */
            Intent intent = new Intent();
            intent.putExtra(Defines.REPLACE_KEY, result);
            setResult(RESULT_OK, intent);

            finish();
        }

        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    /***
     * アダプタークラス
     */
    public class MyExpandableListAdapterInner extends BaseExpandableListAdapter {
//        private String TAG = "MyExpandableListAdapterInner";

        private String[] groups = Defines.GROUPS;
        private String[][][] children = Defines.CHILDREN;

        /** レイアウトをインフレートするための変数 */
        private LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);;

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition][0];
        }

        /***
         * 子要素を返す（オーバーロード）
         * 
         * @param groupPosition
         * @param childPosition
         * @param textPosition
         * @return
         */
        public String getChild(int groupPosition, int childPosition, int textPosition) {
            return children[groupPosition][childPosition][textPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {

            // convertViewがnullの時だけインフレートする（１行ごとに呼ばれるので使いまわす）
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.expandchildview,
                        null);
            }

            LinearLayout linearLayout = (LinearLayout)
                    convertView.findViewById(R.id.ChildLinearLayout);
            if ((childPosition % 2) == 0) {
                linearLayout.setBackgroundColor(Color.WHITE);
            } else {
                linearLayout.setBackgroundColor(getResources().getColor(R.color.color_pink1));
            }

            // 子要素のViewを決める処理
            TextView textView1 = (TextView) convertView.findViewById(R.id.TextView01);
            TextView textView2 = (TextView) convertView.findViewById(R.id.TextView02);
            textView1.setText(getChild(groupPosition, childPosition, 0).toString());
            textView2.setText(getChild(groupPosition, childPosition, 1).toString());

            return convertView;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            // グループのViewを作る処理
            // convertViewがnullの時だけインフレートする（１行ごとに呼ばれるので使いまわす）
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.expandgroupview,
                        null);
            }

            LinearLayout linearLayout = (LinearLayout) convertView
                    .findViewById(R.id.GroupLinearLayout);
            linearLayout.setBackgroundColor(getResources().getColor(R.color.color_yellow1));

            TextView textView = (TextView) convertView.findViewById(R.id.GroupTextView01);
            textView.setText(getGroup(groupPosition).toString());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
