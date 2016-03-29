package com.example.hp.refreshlist;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.hp.refreshlist.Dialog.MyProgressDialog;
import com.example.hp.refreshlist.RefreshListView.RefreshListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RefreshListView.OnLoadMoreListener, RefreshListView.OnRefreshListener {

    private RefreshListView mListView;
    private List<String> mDatas;
    private ArrayAdapter<String> mAdapter;
    private final static int REFRESH_COMPLETE = 0;
    private final static int LOAD_COMPLETE = 1;

    private MyProgressDialog pb;

    private Handler mHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    mListView.setOnRefreshComplete();
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(0);
                    break;
                case LOAD_COMPLETE:
                    mListView.setOnLoadMoreComplete();
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(mDatas.size());
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (RefreshListView) findViewById(R.id.listview);
        pb = new MyProgressDialog(this);
        pb.setCancelable(true);
        pb.setMessage("正在加载数据...");
        pb.show();

        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] data = new String[]{"仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新",
                        "仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新",
                        "仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新", "仿美团下拉刷新",};
                mDatas = new ArrayList<String>(Arrays.asList(data));//字符串数组转换为List
                mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mDatas);
                mListView.setAdapter(mAdapter);
                mListView.setmOnLoadMoreListener(MainActivity.this);
                mListView.setmOnRefreshListener(MainActivity.this);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getApplication(), "功能开发中", Toast.LENGTH_LONG).show();
                    }
                });
                pb.dismiss();
            }
        }, 1000);
    }

    @Override
    public void OnLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    mDatas.add("more 美团外卖");
                    mHandle.sendEmptyMessage(LOAD_COMPLETE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    mDatas.add(0, "new仿美团下拉刷新");
                    mHandle.sendEmptyMessage(REFRESH_COMPLETE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
