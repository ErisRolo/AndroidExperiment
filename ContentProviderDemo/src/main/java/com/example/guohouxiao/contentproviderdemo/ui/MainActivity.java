package com.example.guohouxiao.contentproviderdemo.ui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.guohouxiao.contentproviderdemo.R;
import com.example.guohouxiao.contentproviderdemo.adapter.UserAdapter;
import com.example.guohouxiao.contentproviderdemo.bean.User;
import com.example.guohouxiao.contentproviderdemo.utils.Config;
import com.example.guohouxiao.contentproviderdemo.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView rv_user;
    private LinearLayoutManager mLayoutManager;
    private UserAdapter mAdapter;
    private List<User> mList = new ArrayList<>();//数据库获取到的数据

    private Button btn_getdata;
    private Button btn_update;
    private Button btn_query;

    private EditText et_mod_username;
    private EditText et_selection;
    private EditText et_content;
    private EditText et_qry_selection;
    private EditText et_qry_content;

    private ContentResolver contentResolver;
    private List<User> queryList = new ArrayList<>();//查询到的数据
    private Uri uri = Uri.parse("content://com.example.guohouxiao.accountmanagedemo.contentProvider.MyProvider/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        rv_user = findViewById(R.id.rv_user);
        mLayoutManager = new LinearLayoutManager(this);
        rv_user.setLayoutManager(mLayoutManager);
        setupAdapter();

        btn_getdata = findViewById(R.id.btn_getdata);
        btn_update = findViewById(R.id.btn_update);
        btn_query = findViewById(R.id.btn_query);

        btn_getdata.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_query.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_getdata:
                setupAdapter();
                break;
            case R.id.btn_update:
                showUpdateDialog();
                break;
            case R.id.btn_query:
                showQueryDialog();
                break;
        }
    }

    private void setupAdapter() {
        mList.clear();
        mList.addAll(getContent());
        mAdapter = new UserAdapter(this, mList);
        rv_user.setAdapter(mAdapter);
    }

    private void showUpdateDialog() {
        contentResolver = getContentResolver();
        View dialogUpdate = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        et_mod_username = dialogUpdate.findViewById(R.id.et_mod_username);
        et_selection = dialogUpdate.findViewById(R.id.et_selection);
        et_content = dialogUpdate.findViewById(R.id.et_content);
        new AlertDialog.Builder(this)
                .setView(dialogUpdate)
                .setTitle("修改数据")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mod_username = et_mod_username.getText().toString().trim();
                        String selection = et_selection.getText().toString().trim();
                        String content = et_content.getText().toString().trim();
                        ContentValues cv = new ContentValues();
                        switch (selection) {
                            case "邮箱":
                                cv.put(Config.EMAIL, content);
                                break;
                            case "性别":
                                cv.put(Config.SEX, content);
                                break;
                            case "爱好":
                                cv.put(Config.HOBBY, content);
                                break;
                            case "生日":
                                cv.put(Config.BIRTHDAY, content);
                                break;
                        }
                        if (!TextUtils.isEmpty(mod_username) && !TextUtils.isEmpty(selection) && !TextUtils.isEmpty(content)) {
                            if (contentResolver.update(uri, cv, mod_username, null) == 0) {
                                ShowToast.showShortToast(MainActivity.this, "修改成功！");
                                mList.clear();
                                mList.addAll(getContent());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                ShowToast.showShortToast(MainActivity.this, "修改失败！");
                            }
                        } else {
                            ShowToast.showShortToast(MainActivity.this, "输入框不能为空！");
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void showQueryDialog() {
        View dialogQuery = LayoutInflater.from(this).inflate(R.layout.dialog_query, null);
        et_qry_selection = dialogQuery.findViewById(R.id.et_qry_selection);
        et_qry_content = dialogQuery.findViewById(R.id.et_qry_content);
        new AlertDialog.Builder(this)
                .setView(dialogQuery)
                .setTitle("查询数据")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String qry_selection = et_qry_selection.getText().toString().trim();
                        String qry_content = et_qry_content.getText().toString().trim();
                        if (!TextUtils.isEmpty(qry_selection) && !TextUtils.isEmpty(qry_content)) {
                            queryList.clear();
                            switch (qry_selection) {
                                case "账号":
                                    for (User user : mList) {
                                        if (user.getUsername().equals(qry_content)) {
                                            queryList.add(user);
                                        }
                                    }
                                    break;
                                case "邮箱":
                                    for (User user : mList) {
                                        if (user.getEmail().equals(qry_content)) {
                                            queryList.add(user);
                                        }
                                    }
                                    break;
                                case "性别":
                                    for (User user : mList) {
                                        if (user.getSex().equals(qry_content)) {
                                            queryList.add(user);
                                        }
                                    }
                                    break;
                                case "爱好":
                                    for (User user : mList) {
                                        if (user.getHobby().equals(qry_content)) {
                                            queryList.add(user);
                                        }
                                    }
                                    break;
                                case "生日":
                                    for (User user : mList) {
                                        if (user.getBirthday().equals(qry_content)) {
                                            queryList.add(user);
                                        }
                                    }
                                    break;
                            }
                            if (queryList.isEmpty()) {
                                ShowToast.showShortToast(MainActivity.this, "没有当前数据，查询失败");
                            } else {
                                ShowToast.showShortToast(MainActivity.this, "查询成功！");
                                mAdapter = new UserAdapter(getApplicationContext(), queryList);
                                rv_user.setAdapter(mAdapter);
                            }
                        } else {
                            ShowToast.showShortToast(MainActivity.this, "输入框不能为空！");
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    /**
     * 从远程端获取数据库
     */
    public List<User> getContent() {
        List<User> userList = new ArrayList<>();
        contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                uri,
                null,
                "query_where",
                null,
                null);
        while (cursor.moveToNext()) {
            userList.add(parseUser(cursor));
        }
        return userList;
    }

    /**
     * 数据库行元素转换为user
     *
     * @param cursor 数据库行元素
     * @return 转换结果
     */
    private User parseUser(Cursor cursor) {
        User user = new User();
        user.id = (cursor.getInt(cursor.getColumnIndex(Config.USER_ID)));
        user.username = (cursor.getString(cursor.getColumnIndex(Config.USERNAME)));
        user.password = (cursor.getString(cursor.getColumnIndex(Config.PASSWORD)));
        user.email = (cursor.getString(cursor.getColumnIndex(Config.EMAIL)));
        user.sex = (cursor.getString(cursor.getColumnIndex(Config.SEX)));
        user.hobby = (cursor.getString(cursor.getColumnIndex(Config.HOBBY)));
        user.birthday = (cursor.getString(cursor.getColumnIndex(Config.BIRTHDAY)));
        return user;
    }

}
