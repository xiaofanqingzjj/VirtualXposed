package io.virtualapp.home.myhome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fortune.va.MyTestMenu;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstalledAppInfo;
import com.lody.virtual.server.interfaces.IPackageObserver;

import java.util.ArrayList;
import java.util.List;

import io.virtualapp.R;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.glide.GlideUtils;
import io.virtualapp.home.LoadingActivity;
import io.virtualapp.settings.SettingsActivity;


/**
 * 简单版的首页
 */
public class MyHomeActivity extends VActivity {

    private final List<AppManageInfo> mInstalledApps = new ArrayList<>();
    private AppManageAdapter mAdapter;

    AppContextMenuHelper appContextMenuHelper = new AppContextMenuHelper(this);

    public static void show(Context context) {
        Intent i = new Intent(context, MyHomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        findViewById(R.id.btn_set).setOnClickListener(v -> startActivity(new Intent(MyHomeActivity.this, SettingsActivity.class)));
        findViewById(R.id.btn_test).setOnClickListener(v -> startActivity(new Intent(MyHomeActivity.this, MyTestMenu.class)));

        ListView mListView = findViewById(R.id.list);
        mAdapter = new AppManageAdapter();
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            AppManageInfo appManageInfo = mInstalledApps.get(position);
            LoadingActivity.launch(getApplicationContext(), appManageInfo.pkgName, appManageInfo.userId);
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final AppManageInfo appManageInfo = mInstalledApps.get(position);
                appContextMenuHelper.showContextMenu(appManageInfo, view);
                return false;
            }
        });
        loadAsync();

        VirtualCore.get().registerObserver(packageObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VirtualCore.get().unregisterObserver(packageObserver);
    }

    final private IPackageObserver packageObserver = new VirtualCore.PackageObserver() {
        @Override
        public void onPackageInstalled(String packageName) throws RemoteException {
            loadAsync();
        }

        @Override
        public void onPackageUninstalled(String packageName) throws RemoteException {
            loadAsync();
        }

        @Override
        public void onPackageInstalledAsUser(int userId, String packageName) throws RemoteException {
            loadAsync();
        }

        @Override
        public void onPackageUninstalledAsUser(int userId, String packageName) throws RemoteException {
            loadAsync();
        }
    };

    private void loadAsync() {
        VUiKit.defer().when(this::loadApp).done((v) -> mAdapter.notifyDataSetChanged());
    }

    private void loadApp() {
        List<AppManageInfo> ret = new ArrayList<>();

        List<InstalledAppInfo> installedApps = VirtualCore.get().getInstalledApps(0);

        PackageManager packageManager = getPackageManager();
        for (InstalledAppInfo installedApp : installedApps) {
            int[] installedUsers = installedApp.getInstalledUsers();
            for (int installedUser : installedUsers) {
                AppManageInfo info = new AppManageInfo();
                info.userId = installedUser;

                final ApplicationInfo applicationInfo = installedApp.getApplicationInfo(installedUser);
                info.name = applicationInfo.loadLabel(packageManager);
                info.pkgName = installedApp.packageName;
                info.path = applicationInfo.sourceDir;
                ret.add(info);
            }
        }
        mInstalledApps.clear();
        mInstalledApps.addAll(ret);
    }

    class AppManageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mInstalledApps.size();
        }

        @Override
        public AppManageInfo getItem(int position) {
            return mInstalledApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(MyHomeActivity.this, parent);
                convertView = holder.root;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final AppManageInfo item = getItem(position);

            if (VirtualCore.get().isOutsideInstalled(item.pkgName)) {
                GlideUtils.loadInstalledPackageIcon(getContext(), item.pkgName, holder.icon, android.R.drawable.sym_def_app_icon);
            } else {
                GlideUtils.loadPackageIconFromApkFile(getContext(), item.path, holder.icon, android.R.drawable.sym_def_app_icon);
            }

            holder.tvName.setText(item.getName());
            holder.tvLabel.setText("userId: " + item.userId);

            return convertView;
        }
    }


    static class ViewHolder {
        ImageView icon;
        TextView tvName;
        TextView tvLabel;

        View root;

        ViewHolder(Context context, ViewGroup parent) {
            root = LayoutInflater.from(context).inflate(R.layout.item_home_app_cell, parent, false);
            icon = root.findViewById(R.id.item_app_icon);
            tvName = root.findViewById(R.id.item_app_name);
            tvLabel = root.findViewById(R.id.item_app_desc);
        }
    }

    static class AppManageInfo {
        CharSequence name;
        int userId;
        Drawable icon;
        String pkgName;
        String path;

        CharSequence getName() {
            if (userId == 0) {
                return name;
            } else {
                return name + "[" + (userId + 1) + "]";
            }
        }
    }
}
