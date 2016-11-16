package com.kent.exposed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Tool implements IXposedHookLoadPackage {
    private static final String TAG = "HEHEDA_Tool";
    private Context mContext;
    private LinearLayout mFloatLayout;
    private WindowManager mWindowManager;
    private XC_LoadPackage.LoadPackageParam loadPackageParam;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        this.loadPackageParam = loadPackageParam;
        if (loadPackageParam.packageName.equals("com.tmall.wireless") || loadPackageParam.packageName.equals("com.taobao.ju.android") || loadPackageParam.packageName.equals("com.taobao.taobao") || loadPackageParam.packageName.equals("com.kent.aliexposed")) {
            //noinspection StatementWithEmptyBody
            if (loadPackageParam.packageName.equals("com.kent.aliexposed")) {
            }
            //聚划算走你
            if (loadPackageParam.packageName.equals("com.taobao.ju.android")) {
                XposedBridge.hookAllConstructors(RelativeLayout.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Class<?> buyLayoutClass = param.thisObject.getClass();
                        if (buyLayoutClass.getName().equals("com.taobao.ju.android.detail.widget.ButtonBuyLayout")) {
                            XposedBridge.hookAllMethods(buyLayoutClass, "setGoNextClickListener", setGoNextClickListener());
                        }
                    }
                });
            }
            //创建悬浮按钮 并显示
            XposedBridge.hookAllMethods(Activity.class, "onResume", onResume());
            //移除悬浮按钮
            XposedBridge.hookAllMethods(Activity.class, "onPause", onPause());

        }
    }

    private XC_MethodHook onResume() {
        return new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.thisObject.getClass().getName().equals("com.kent.aliexposed.MainActivity")
                        && !param.thisObject.getClass().getName().equals("com.taobao.tao.detail.activity.DetailActivity")
                        && !param.thisObject.getClass().getName().equals("com.tmall.wireless.detail.ui.TMItemDetailsActivity")
                        && !param.thisObject.getClass().getName().equals("com.taobao.ju.android.detail.activity.ItemDetailActivity")
                        ) return;
                mContext = (Context) param.thisObject;
                mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                initFloatBtn(mContext);
            }
        };
    }

    private XC_MethodHook onPause() {
        return new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (mWindowManager != null) mWindowManager.removeView(mFloatLayout);
            }
        };
    }

    //聚划算 测试用 发现了转换ButLayout的方法
    private XC_MethodHook setGoNextClickListener() {
        return new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    //throw new Exception();
                } catch (Exception e) {
                    Log.w(TAG, "setGoNextClickListener: ", e);
                }
            }
        };
    }

    private void initFloatBtn(final Context context) {
        WindowManager.LayoutParams wmLp = new WindowManager.LayoutParams();
        wmLp.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmLp.format = PixelFormat.RGBA_8888;
        wmLp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmLp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLp.gravity = Gravity.END | Gravity.CENTER_HORIZONTAL;
        mFloatLayout = new LinearLayout(context);
        mFloatLayout.setOrientation(LinearLayout.VERTICAL);
        //Scan btn
        Button btnScan = new Button(context);
        String scan = "Scan It First!";
        btnScan.setText(scan);
        //Activity btn
        Button btnAtyInfo = new Button(context);
        String atyInfo = "Aty Info";
        btnAtyInfo.setText(atyInfo);
        //聚划算 btn
        Button btnJu = new Button(context);
        String strJu = "聚划算";
        btnJu.setText(strJu);
        //
        mFloatLayout.addView(btnAtyInfo);
        mFloatLayout.addView(btnScan);
        mFloatLayout.addView(btnJu);
        //----------------------------------------------
        mWindowManager.addView(mFloatLayout, wmLp);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnScanLayout((Activity) context);
            }
        });
        btnAtyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogInfo(context);
            }
        });
        btnJu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnJu();
            }
        });
    }

    private View buyLayout;

    private void btnScanLayout(Activity context) {
        View content = context.getWindow().getDecorView().findViewById(android.R.id.content);
        ViewGroup rootView = ((ViewGroup) content);
        scanLayout(rootView, 0, false);
    }

    private void btnLogInfo(Object object) {
        Log.e(TAG, "---");
        Log.e(TAG, "---------------------------------------------------------------------------------------------------");
        Log.e(TAG, "---");
        Field[] fields = object.getClass().getDeclaredFields();
        Log.e(TAG, "btnLogInfo_Class_Name: " + object.getClass());
        for (final Field f : fields) {
            Object o = null;
            try {
                o = f.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "DField_Type:" + f.getType() + " ,fieldName: " + f.getName() + " ,value: " + (o != null ? o.toString() : ""));
        }
        Method[] dMethods = object.getClass().getDeclaredMethods();
        for (Method m : dMethods) {
            Class<?>[] types = m.getParameterTypes();
            String type = "";
            for (Class c : types) {
                type = type + "_" + c.getName();
            }
            Log.e(TAG, "DMethods_Name:" + m.getName() + " ,types: " + type);
        }
    }

    private void btnJu() {
        if (buyLayout == null) {
            Toast.makeText(mContext, "NullPointException.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Method method = buyLayout.getClass().getDeclaredMethod("renderJoinLayoutStarted");
            method.setAccessible(true);
            method.invoke(buyLayout);
            method.setAccessible(false);
            Log.e(TAG, "btnFixIt: renderJoinLayoutStarted");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //btnLogInfo(buyLayout);
        //logOtherInfo(buyLayout);
    }

    private void scanLayout(View view, int floor, boolean logOn) {
        if (view.getClass().getName().equals("com.taobao.ju.android.detail.widget.ButtonBuyLayout")) {
            buyLayout = view;
            //forButtonBuyLayout(view);
        }
        String ex = "";
        for (int i = 0; i < floor; i++) {
            ex += "  ";
        }
        //Log.e(TAG, ex + "floor: " + floor + " ,View_Type: " + view.getClass().getName());
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            //Log.e(TAG, ex + "floor: " + floor + " ,AlsoViewGroup. child count: " + childCount);
            for (int i = 0; i < childCount; i++) {
                scanLayout(((ViewGroup) view).getChildAt(i), floor + 1, logOn);
            }
        }
    }

    private void forButtonBuyLayout(View view) {
        getListener(view);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                forButtonBuyLayout(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    private View.OnClickListener getListener(View view) {
        Log.e(TAG, "---");
        try {
            Field infoField = View.class.getDeclaredField("mListenerInfo");
            Class infoClass = infoField.getType();
            infoField.setAccessible(true);
            Object mListenerInfo = infoField.get(view);
            Field listenerField = infoClass.getField("mOnClickListener");
            View.OnClickListener mOnClickListener = (View.OnClickListener) listenerField.get(mListenerInfo);
            Log.e(TAG, "getListener_Listener clazz: " + mOnClickListener.getClass().getName());
            Field[] dFields = mOnClickListener.getClass().getDeclaredFields();
            for (Field f : dFields) {
                Log.e(TAG, "Listener DFields_Type:" + f.getType() + " ,Name: " + f.getName());
            }
            Method[] dMethods = mOnClickListener.getClass().getDeclaredMethods();
            for (Method m : dMethods) {
                Class<?>[] types = m.getParameterTypes();
                String type = "";
                for (Class c : types) {
                    type = type + "_" + c.getName();
                }
                Log.e(TAG, "Listener DMethods:" + m.getName() + " ,types: " + type);
            }
            return mOnClickListener;
        } catch (Exception e) {
            int i;
        }
        Log.e(TAG, "---------------------------------------------------------------------------------------------------");
        return null;
    }

    private void logOtherInfo(Object view) {
        Log.w(TAG, "findAllSuperClass --------------------------------------------------------------");
        findAllSuperClass(view.getClass());
        Log.w(TAG, "findAllInterfaces --------------------------------------------------------------");
        findAllInterfaces(view);
        Log.w(TAG, "getAllInnerClass ---------------------------------------------------------------");
        getAllInnerClass(view);
    }

    private void findAllSuperClass(Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            Log.e(TAG, "SuperClassName: " + superClass.getName());
            findAllSuperClass(superClass);
        }
    }

    private void findAllInterfaces(Object object) {
        Log.e(TAG, "---");
        Class<?> clazz = object.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class c : interfaces) {
            Log.e(TAG, "Interfaces: " + c.getName());
        }
        if (interfaces.length == 0) {
            Log.e(TAG, "Interfaces_Size: 0");
        }
        Log.e(TAG, "---------------------------------------------------------------------------------------------------");
    }

    private void getAllInnerClass(Object view) {
        Log.e(TAG, "---");
        Class<?>[] classes = view.getClass().getDeclaredClasses();
        for (Class c : classes) {
            Log.e(TAG, "InnerClass_Name: " + c.getName());
        }
        if (classes.length == 0) {
            Log.e(TAG, "InnerClass_Size: 0");
        }
        Log.e(TAG, "---------------------------------------------------------------------------------------------------");
    }
}
