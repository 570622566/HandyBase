package com.handy.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Android6.0权限处理工具类
 * <p/>
 * Created by LiuJie on 2016/7/20.
 */
public class PermissionsUtils {

    private volatile static PermissionsUtils instance;
    private List<String> Permissions = new ArrayList<>();

    /**
     * 获取单例
     */
    public static PermissionsUtils getInstance() {
        if (instance == null) {
            synchronized (PermissionsUtils.class) {
                if (instance == null) {
                    instance = new PermissionsUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化全校扫描内容
     */
    private void initPermissions() {
        Permissions = new ArrayList<String>() {{
            add(Manifest.permission.INTERNET);
            add(Manifest.permission.RECORD_AUDIO);
            add(Manifest.permission.READ_PHONE_STATE);
            add(Manifest.permission.ACCESS_WIFI_STATE);
            add(Manifest.permission.ACCESS_NETWORK_STATE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }};
    }

    public List<String> getPermissions() {
        return new ArrayList<>(Permissions);
    }

    /**
     * 设置应用敏感权限
     * <pre>
     * 设置后请通过public boolean checkDeniedPermissions(Activity activity, boolean isRequest)方法扫描权限。
     * 请注意：配置的权限内容必须与AndroidManifest中使用的敏感权限保持一致！
     *
     * 框架默认已配置的权限有：
     *  add(Manifest.permission.INTERNET);
     *  add(Manifest.permission.READ_PHONE_STATE);
     *  add(Manifest.permission.ACCESS_NETWORK_STATE);
     *  add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
     * </pre>
     *
     * @param permissions
     */
    public void addPermissions(List<String> permissions) {
        Permissions.addAll(permissions);
    }

    /**
     * 扫描权限集合 如果发现未启用权限，停止扫描并启动权限动态请求。
     * 使用时请注意判断SDK版本：if (Build.VERSION.SDK_INT >= 23)
     *
     * @param isRequest 是否发起系统权限请求
     * @return 如果有未启用的权限返回false
     */
    private boolean checkDeniedPermissionsBase(Activity activity, boolean isRequest) {
        if (Permissions == null || Permissions.size() == 0)
            initPermissions();

        for (int index = 0; index < Permissions.size(); index++) {
            if (ContextCompat.checkSelfPermission(activity, Permissions.get(index)) == PackageManager.PERMISSION_DENIED) {
                if (isRequest)
                    ActivityCompat.requestPermissions(activity, new String[]{Permissions.get(index)}, index);
                return true;
            }
        }
        return false;
    }

    private boolean checkDeniedPermissionsBase(Activity activity, boolean isRequest, List<String> permissions) {
        for (int index = 0; index < permissions.size(); index++) {
            if (ContextCompat.checkSelfPermission(activity, permissions.get(index)) == PackageManager.PERMISSION_DENIED) {
                if (isRequest)
                    ActivityCompat.requestPermissions(activity, new String[]{permissions.get(index)}, index);
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描权限集合 如果发现未启用权限，停止扫描并启动权限动态请求。
     * 使用时请注意判断SDK版本：if (Build.VERSION.SDK_INT >= 23)
     *
     * @param activity  发起请求的Activity
     * @param isRequest 是否发起系统权限请求
     * @return 如果有未启用的权限返回true
     */
    public boolean checkDeniedPermissions(Activity activity, boolean isRequest) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkDeniedPermissionsBase(activity, isRequest)) {
                LogUtils.getInstance().w("发现未启用权限");
                return true;
            } else {
                LogUtils.getInstance().d("应用权限已全部开启");
            }
        } else {
            LogUtils.getInstance().d("系统版本小于23，无需扫描权限");
        }
        return false;
    }

    /**
     * 扫描权限集合 如果发现未启用权限，停止扫描并启动权限动态请求。
     * 使用时请注意判断SDK版本：if (Build.VERSION.SDK_INT >= 23)
     *
     * @param activity    发起请求的Activity
     * @param isRequest   是否发起系统权限请求
     * @param permissions 权限扫描的参数
     * @return 如果有未启用的权限返回true
     */
    public boolean checkDeniedPermissions(Activity activity, List<String> permissions, boolean isRequest) {
        if (permissions.size() != 0) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkDeniedPermissionsBase(activity, isRequest, permissions)) {
                    LogUtils.getInstance().w("发现未启用权限");
                    return true;
                } else {
                    LogUtils.getInstance().d("应用权限已全部开启");
                }
            } else {
                LogUtils.getInstance().d("系统版本小于23，无需扫描权限");
            }
        } else {
            LogUtils.getInstance().e("未发现需扫描的权限内容");
        }
        return false;
    }

    /**
     * 扫描权限集合（带跳转Activity） 如果发现未启用权限，停止扫描并启动权限动态请求。
     *
     * @param activity    发起请求的Activity
     * @param intentClass 扫描通过后被跳转的Class
     * @param isRequest   是否发起系统权限请求
     * @param isFinish    是否关闭当前Activity
     * @return 如果有未启用的权限返回true
     */
    public boolean checkDeniedPermissions(Activity activity, Class intentClass, boolean isRequest, boolean isFinish) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkDeniedPermissionsBase(activity, isRequest)) {
                LogUtils.getInstance().w("发现未启用权限");
                return true;
            } else {
                if (isFinish) activity.finish();
                LogUtils.getInstance().d("应用权限已全部开启");
                IntentUtils.getInstance().openActivity(activity, intentClass, isFinish);
            }
        } else {
            if (isFinish) activity.finish();
            LogUtils.getInstance().d("系统版本小于23，无需扫描权限");
            IntentUtils.getInstance().openActivity(activity, intentClass, isFinish);
        }
        return false;
    }

    /**
     * 扫描权限集合（带跳转Activity） 如果发现未启用权限，停止扫描并启动权限动态请求。
     *
     * @param activity    发起请求的Activity
     * @param intentClass 扫描通过后被跳转的Class
     * @param isRequest   是否发起系统权限请求
     * @param isFinish    是否关闭当前Activity
     * @param permissions 权限扫描的参数
     * @return 如果有未启用的权限返回true
     */
    public boolean checkDeniedPermissions(Activity activity, Class intentClass, List<String> permissions, boolean isRequest, boolean isFinish) {
        if (permissions != null && permissions.size() != 0) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkDeniedPermissionsBase(activity, isRequest, permissions)) {
                    LogUtils.getInstance().w("发现未启用权限");
                    return true;
                } else {
                    if (isFinish) activity.finish();
                    LogUtils.getInstance().d("应用权限已全部开启");
                    IntentUtils.getInstance().openActivity(activity, intentClass, isFinish);
                }
            } else {
                if (isFinish) activity.finish();
                LogUtils.getInstance().d("系统版本小于23，无需扫描权限");
                IntentUtils.getInstance().openActivity(activity, intentClass, isFinish);
            }
        } else {
            LogUtils.getInstance().e("未发现需扫描的权限内容");
        }
        return false;
    }
}
