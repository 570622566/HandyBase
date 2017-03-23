package com.handy.base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * Activity栈管理
 * <p>
 * Created by LiuJie on 2016/1/19.
 */
public class ActivityStackUtils {

    public static Stack<Activity> activityStack; //Activity栈

    private ActivityStackUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 添加Activity到堆栈
     */
    public static void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        if (activityStack != null) {
            Activity activity = activityStack.lastElement();
            return activity;
        }
        return null;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public static void finishActivity() {
        if (activityStack != null) {
            Activity activity = activityStack.lastElement();
            finishActivity(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null && activityStack != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (activityStack != null) {
            for (int i = 0; i < activityStack.size(); i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        }
    }

    /**
     * 结束第一个Activity之后的所有Activity
     */
    public static void finishToFirstActivity() {
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                LogUtils.d(activity.getPackageName() + activity.getLocalClassName());
            }
            for (int i = 1; i < activityStack.size(); i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                    activityStack.remove(i);
                }
            }
        }
    }

    /**
     * 退出应用程序
     */
    public static void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
