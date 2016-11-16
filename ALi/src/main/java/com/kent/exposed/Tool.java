package com.kent.exposed;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Tool implements IXposedHookLoadPackage {
    private Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.contains("com.tmall.wireless")
                || loadPackageParam.packageName.contains("com.taobao.ju.android")
                || loadPackageParam.packageName.contains("com.taobao.taobao")
                || loadPackageParam.packageName.contains("com.kent.aliexposed")) {
            XposedBridge.hookAllMethods(Application.class, "onCreate", onCreate(loadPackageParam));
        }
    }

    private XC_MethodHook onCreate(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        return new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context mContext = (Context) param.thisObject;
                if (mContext == null) return;
                Context targetContext = mContext.createPackageContext("com.kent.exposedtool", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
                Class<?> toolClass = targetContext.getClassLoader().loadClass("com.kent.exposed.ExposedTool");
                Object tool = toolClass.getConstructor().newInstance();
                Method handleLoadPackage = toolClass.getMethod("handleLoadPackage", loadPackageParam.getClass());
                handleLoadPackage.invoke(tool, loadPackageParam);
            }
        };
    }
}
