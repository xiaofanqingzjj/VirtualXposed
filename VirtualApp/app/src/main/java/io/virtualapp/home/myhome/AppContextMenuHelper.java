package io.virtualapp.home.myhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VirtualStorageManager;
import com.lody.virtual.helper.ArtDexOptimizer;
import com.lody.virtual.os.VEnvironment;

import java.io.File;
import java.io.IOException;

import io.virtualapp.R;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.settings.AppManageActivity;
import io.virtualapp.settings.NougatPolicy;

public class AppContextMenuHelper {


    private Context context;

    public AppContextMenuHelper(Context context) {
        this.context = context;
    }

    void showContextMenu(MyHomeActivity.AppManageInfo appManageInfo, View anchor) {
        if (appManageInfo == null) {
            return;
        }
        Context context = anchor.getContext();
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.inflate(R.menu.app_manage_menu);
        MenuItem redirectMenu = popupMenu.getMenu().findItem(R.id.action_redirect);

        try {
            final String packageName = appManageInfo.pkgName;
            final int userId = appManageInfo.userId;
            boolean virtualStorageEnable = VirtualStorageManager.get().isVirtualStorageEnable(packageName, userId);
            redirectMenu.setTitle(virtualStorageEnable ? R.string.app_manage_redirect_off : R.string.app_manage_redirect_on);
        } catch (Throwable e) {
            redirectMenu.setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_uninstall:
                    showUninstallDialog(appManageInfo, appManageInfo.getName());
                    break;
                case R.id.action_repair:
                    showRepairDialog(appManageInfo);
                    break;
                case R.id.action_redirect:
                    showStorageRedirectDialog(appManageInfo);
                    break;
            }
            return false;
        });
        try {
            popupMenu.show();
        } catch (Throwable ignored) {
        }
    }

    private void showRepairDialog(MyHomeActivity.AppManageInfo item) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(context.getResources().getString(R.string.app_manage_repairing));
        try {
            dialog.setCancelable(false);
            dialog.show();
        } catch (Throwable e) {
            return;
        }

        VUiKit.defer().when(() -> {
            NougatPolicy.fullCompile(context.getApplicationContext());

            String packageName = item.pkgName;
            String apkPath = item.path;

            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(apkPath)) {
                return;
            }

            // 1. kill package
            VirtualCore.get().killApp(packageName, item.userId);

            // 2. backup the odex file
            File odexFile = VEnvironment.getOdexFile(packageName);
            if (odexFile.delete()) {
                try {
                    ArtDexOptimizer.compileDex2Oat(apkPath, odexFile.getPath());
                } catch (IOException e) {
                    throw new RuntimeException("compile failed.");
                }
            }
        }).done((v) -> {
            dismiss(dialog);
            showAppDetailDialog();
        }).fail((v) -> {
            dismiss(dialog);
            Toast.makeText(context, R.string.app_manage_repair_failed_tips, Toast.LENGTH_SHORT).show();
        });
    }

    private void showAppDetailDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.app_manage_repair_success_title)
                .setMessage(context.getResources().getString(R.string.app_manage_repair_success_content))
                .setPositiveButton(R.string.app_manage_repair_reboot_now, (dialog, which) -> {
                    String packageName = context.getPackageName();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                })
                .create();

        alertDialog.setCancelable(false);

        try {
            alertDialog.show();
        } catch (Throwable ignored) {
        }
    }

    private void showUninstallDialog(MyHomeActivity.AppManageInfo item, CharSequence name) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
//                .setTitle(com.android.launcher3.R.string.home_menu_delete_title)
//                .setMessage(getResources().getString(com.android.launcher3.R.string.home_menu_delete_content, name))
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage("确认卸载吗？")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    VirtualCore.get().uninstallPackageAsUser(item.pkgName, item.userId);
//                    loadAsync();
                })
                .setNegativeButton(android.R.string.no, null)
                .create();
        try {
            alertDialog.show();
        } catch (Throwable ignored) {
        }
    }

    private void showStorageRedirectDialog(MyHomeActivity.AppManageInfo item) {
        final String packageName = item.pkgName;
        final int userId = item.userId;
        boolean virtualStorageEnable;
        try {
            virtualStorageEnable = VirtualStorageManager.get().isVirtualStorageEnable(packageName, userId);
        } catch (Throwable e) {
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(virtualStorageEnable ? R.string.app_manage_redirect_off : R.string.app_manage_redirect_on)
                .setMessage(context.getResources().getString(R.string.app_manage_redirect_desc))
                .setPositiveButton(virtualStorageEnable ? R.string.app_manage_redirect_off_confirm : R.string.app_manage_redirect_on_confirm,
                        (dialog, which) -> {
                            try {
                                VirtualStorageManager.get().setVirtualStorageState(packageName, userId, !virtualStorageEnable);
                            } catch (Throwable ignored) {
                            }
                        })
                .setNegativeButton(android.R.string.no, null)
                .create();
        try {
            alertDialog.show();
        } catch (Throwable ignored) {
        }
    }

    private static void dismiss(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Throwable ignored) {
        }
    }

}
