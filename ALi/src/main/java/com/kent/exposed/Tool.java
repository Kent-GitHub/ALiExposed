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
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Tool implements IXposedHookLoadPackage {
    private static final String TAG = "HEHEDA_Tool";
    private XC_LoadPackage.LoadPackageParam loadPackageParam;
    private WindowManager mWindowManager;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        this.loadPackageParam = loadPackageParam;
        if (loadPackageParam.packageName.equals("com.tmall.wireless")
                || loadPackageParam.packageName.equals("com.taobao.ju.android")
                || loadPackageParam.packageName.equals("com.taobao.taobao")
                || loadPackageParam.packageName.equals("com.kent.aliexposed")
                ) {
            XposedBridge.hookAllMethods(Activity.class, "onResume", onResume);
            XposedBridge.hookAllMethods(Activity.class, "onPause", onPause);
        }
    }

    private XC_MethodHook onResume = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (!param.thisObject.getClass().getName().equals("com.tmall.wireless.detail.ui.TMItemDetailsActivity")
                    && !param.thisObject.getClass().getName().equals("com.taobao.ju.android.detail.activity.ItemDetailActivity")
                    && !param.thisObject.getClass().getName().equals("com.taobao.tao.detail.activity.DetailActivity")
                    && !param.thisObject.getClass().getName().equals("com.kent.aliexposed.MainActivity")
                    ) return;
            Context context = (Context) param.thisObject;
            mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            initFloatBtn(context);
            Log.e(TAG, "onResume_className: " + param.thisObject.getClass().getName());
            Field[] fields = param.thisObject.getClass().getDeclaredFields();
            for (final Field f : fields) {
                Log.e(TAG, "onResume_fieldClass:" + f.getType() + " ,fieldName: " + f.getName());
                f.setAccessible(true);
                if (View.class.isAssignableFrom(f.getType())) {
                    Log.e(TAG, "is View: " + f.getType().getName());
                    if (ViewGroup.class.isAssignableFrom(f.getType())) {
                        ViewGroup viewgroup = (ViewGroup) f.get(param.thisObject);
                        viewgroup.setClickable(true);
                        Log.e(TAG, "is also ViewGroup_Child Count: " + viewgroup.getChildCount());
                        viewgroup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e(TAG, "onClick: 点点点 ViewName_fieldClass:" + f.getType() + " ,fieldName: " + f.getName());
                            }
                        });
                    }

                }
            }
        }
    };

    private XC_MethodHook onPause = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (mWindowManager != null) mWindowManager.removeView(mButton);
        }
    };
    private Button mButton;

    private void initFloatBtn(final Context context) {
        WindowManager.LayoutParams wmLp = new WindowManager.LayoutParams();
        wmLp.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmLp.format = PixelFormat.RGBA_8888;
        wmLp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmLp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLp.gravity = Gravity.END | Gravity.CENTER_HORIZONTAL;
        mButton = new Button(context);
        String clickMe = "Click Me!";
        mButton.setText(clickMe);
        mWindowManager.addView(mButton, wmLp);
        Log.e(TAG, "initFloatBtn: Float Button Added");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatBtnClicked((Activity) context);
            }
        });
    }

    private void floatBtnClicked(Activity context) {

        View content = context.getWindow().getDecorView().findViewById(android.R.id.content);
        ViewGroup rootView = ((ViewGroup) content);
        logViewInfo(rootView, 0);
    }

    private void logViewInfo(View view, int floor) {
        if (view.getClass().getName().equals("com.taobao.ju.android.detail.widget.ButtonBuyLayout")) {
            view.setBackgroundColor(Color.BLACK);
            Log.e(TAG, "---" );
            findAllSuperClass(view.getClass());
            Log.e(TAG, "---------------------------------------------------------------------------------------------------" );
            findAllInterfaces(view);
            getAllInnerClass(view);
            logObjectInfo(view);
        }
        String ex = "";
        for (int i = 0; i < floor; i++) {
            ex += "  ";
        }
        Log.e(TAG, ex + "floor: " + floor + " ,View_Type: " + view.getClass().getName());
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            Log.e(TAG, ex + "floor: " + floor + " ,AlsoViewGroup. child count: " + childCount);
            for (int i = 0; i < childCount; i++) {
                logViewInfo(((ViewGroup) view).getChildAt(i), floor + 1);
            }
        } else if (view instanceof TextView) {
            TextView textview = (TextView) view;
            if (textview.getText().toString().contains("只有比双十一更低")) {
                textview.setText("只有比双十一更低，才怪");
            } else if (textview.getText().toString().contains("马上抢")) {
                textview.setText("呵呵哒");
                Log.e(TAG, "logObjectInfo: Gotcha." + view.getClass().getName());
                getListener(view);
                getListener((View) view.getParent());
                getListener((View) view.getParent().getParent());
            }
        }
    }

    private void getListener(View view) {
        Log.e(TAG, "---" );
        try {
            Log.e(TAG, "getListener: in.");
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
        } catch (Exception e) {
            int i;
        }
        Log.e(TAG, "---------------------------------------------------------------------------------------------------" );
    }

    private void findAllSuperClass(Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            Log.e(TAG, "SuperClassName: " + superClass.getName());
            findAllSuperClass(superClass);
        }
    }

    private void findAllInterfaces(Object object) {
        Log.e(TAG, "---" );
        Class<?> clazz = object.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class c : interfaces) {
            Log.e(TAG, "Interfaces: " + c.getName());
        }
        if (interfaces.length==0){
            Log.e(TAG, "Interfaces_Size: 0");
        }
        Log.e(TAG, "---------------------------------------------------------------------------------------------------" );
    }

    private void getAllInnerClass(View view) {
        Log.e(TAG, "---" );
        Class<?>[] classes = view.getClass().getDeclaredClasses();
        for (Class c : classes) {
            Log.e(TAG, "InnerClass_Name: " + c.getName());
        }
        if (classes.length==0){
            Log.e(TAG, "InnerClass_Size: 0" );
        }
        Log.e(TAG, "---------------------------------------------------------------------------------------------------" );
    }

    private void logObjectInfo(View view) {
        Log.e(TAG, "---" );
        Field[] dFields = view.getClass().getDeclaredFields();
        for (Field f : dFields) {
            Log.e(TAG, "---" );
            Log.e(TAG, f.getType() + " ,Name: " + f.getName());
            f.setAccessible(true);
            try {
                Object o = f.get(view);
                if (o != null) {
                    Log.e(TAG, "       logObjectInfo: " + o.toString());
                } else {
                    Log.e(TAG, "       logObjectInfo: o==null.");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Method[] dMethods = view.getClass().getDeclaredMethods();
        for (Method m : dMethods) {
            Class<?>[] types = m.getParameterTypes();
            String type = "";
            for (Class c : types) {
                type = type + " _ " + c.getName();
            }
            Log.e(TAG, "DMethods:" + m.getName() + " ,types: " + type);
        }
        Log.e(TAG, "---------------------------------------------------------------------------------------------------" );
    }
    //我要Commit
}
