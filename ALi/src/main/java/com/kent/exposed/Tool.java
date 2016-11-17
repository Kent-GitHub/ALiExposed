package com.kent.exposed;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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
        if (loadPackageParam.packageName.equals("com.tmall.wireless")
                || loadPackageParam.packageName.equals("com.taobao.ju.android")
                || loadPackageParam.packageName.equals("com.taobao.taobao")
                || loadPackageParam.packageName.equals("com.kent.aliexposed")) {
            //noinspection StatementWithEmptyBody
            if (loadPackageParam.packageName.equals("com.kent.aliexposed")) {
            }
            //聚划算
//            if (loadPackageParam.packageName.contains("com.taobao.ju.android")) {
//                XposedBridge.hookAllConstructors(RelativeLayout.class, new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        Class<?> buyLayoutClass = param.thisObject.getClass();
//                        if (buyLayoutClass.getName().equals("com.taobao.ju.android.detail.widget.ButtonBuyLayout")) {
//                            XposedBridge.hookAllMethods(buyLayoutClass, "setGoNextClickListener", new XC_MethodHook() {
//                                @Override
//                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                    try {
//                                        throw new Exception();
//                                    } catch (Exception e) {
//                                        Log.w(TAG, "setGoNextClickListener: ", e);
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });
//            }
            //天猫
            if (loadPackageParam.packageName.contains("com.tmall.wireless")
                    || loadPackageParam.packageName.contains("com.taobao.taobao")
                    ) {
                XposedBridge.hookAllMethods(View.class, "setOnClickListener", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Class<?> clazz = param.thisObject.getClass();
//                        if (clazz.getName().equals("com.taobao.android.detail.kit.view.widget.base.DetailIconFontTextView")) {
//                            Log.e(TAG, "DetailIconFontTextView: HEHEDA");
//                            try {
//                                throw new Exception();
//                            } catch (Exception e) {
//                                Log.w(TAG, "setOnClickListener: ", e);
//                            }
//                        }

                        if (clazz.getName().equals("android.widget.TextView")) {
                            try {
                                throw new Exception();
                            } catch (Exception e) {
                                Log.e(TAG, "setOnClickListener: ", e);
                            }
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
                if (mWindowManager != null && mFloatLayout != null && mFloatLayout.isAttachedToWindow())
                    mWindowManager.removeView(mFloatLayout);
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
        String scan = "Scan Layout!";
        btnScan.setText(scan);
        //Activity btn 1838682369@qq.com
        Button btnAtyInfo = new Button(context);
        String atyInfo = "Aty Info";
        btnAtyInfo.setText(atyInfo);
        //聚划算 btn
        Button btnJu = new Button(context);
        String strJu = "Ju";
        btnJu.setText(strJu);
        //淘宝 btn
        Button btnTb = new Button(context);
        String strTb = "Tb";
        btnTb.setText(strTb);
        //聚划算 btn
        Button btnTm = new Button(context);
        String strTm = "Tm";
        btnTm.setText(strTm);
        //
        mFloatLayout.addView(btnAtyInfo);
        //mFloatLayout.addView(btnScan);
        mFloatLayout.addView(btnTb);
        mFloatLayout.addView(btnTm);
        mFloatLayout.addView(btnJu);
        //----------------------------------------------
        mWindowManager.addView(mFloatLayout, wmLp);
//        btnScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scanActivity((Activity) context);
//            }
//        });
        btnAtyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInfo(context);
            }
        });
        btnJu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnJu();
            }
        });
        btnTb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTb();
            }
        });
        btnTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTm();
            }
        });
    }

    private void scanActivity(Activity context, boolean logOn) {
        View content = context.getWindow().getDecorView().findViewById(android.R.id.content);
        ViewGroup rootView = ((ViewGroup) content);
        scanView(rootView, 0, logOn);
    }

    private void logInfo(Object object) {
        Log.e(TAG, "---");
        Log.e(TAG, "---------------------------------------------------------------------------------------------------");
        Log.e(TAG, "---");
        Field[] fields = object.getClass().getDeclaredFields();
        Log.e(TAG, "LogInfo_Class_Name: " + object.getClass());
        for (final Field f : fields) {
            f.setAccessible(true);
            Object o = null;
            try {
                o = f.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "DField_Type:" + f.getType() + " ,fieldName: " + f.getName() + " ,value: " + (o != null ? o.toString() : "null"));
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


    private View juBuyLayout;
    private boolean juAction1;

    private void btnJu() {
        juAction1 = true;
        scanActivity((Activity) mContext, false);
    }

    private void juAction1() {
        try {
            Method method = juBuyLayout.getClass().getDeclaredMethod("renderJoinLayoutStarted");
            method.setAccessible(true);
            method.invoke(juBuyLayout);
            method.setAccessible(false);
            Log.e(TAG, "btnJu: renderJoinLayoutStarted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void btnTb() {
        logInfo(mContext);
        scanActivity((Activity) mContext, true);
    }

    private void btnTm() {
        logInfo(mContext);
        scanActivity((Activity) mContext, true);
    }

    private void scanView(View view, int floor, boolean logOn) {
        if (view.getClass().getName().equals("com.taobao.ju.android.detail.widget.ButtonBuyLayout")) {
            juBuyLayout = view;
            if (juAction1) {
                juAction1 = false;
                juAction1();
            }
        }
        String ex = "";
        for (int i = 0; i < floor; i++) {
            ex += "  ";
        }
        if (logOn) {
            Log.e(TAG, ex + "floor: " + floor + " ,View_Type: " + view.getClass().getName());
        }
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            if (logOn) {
                Log.e(TAG, ex + "floor: " + floor + " ,Is ViewGroup. child count: " + childCount);
            }
            for (int i = 0; i < childCount; i++) {
                scanView(((ViewGroup) view).getChildAt(i), floor + 1, logOn);
            }
        } else if (view instanceof TextView) {
            if (((TextView) view).getText().toString().contains("开团提醒")
                    || ((TextView) view).getText().toString().contains("立即购买")) {
                Log.e(TAG, "scanView: gotcha");
                ((TextView) view).setText("买买买");
                getListener(view, true);
                logInfo(view);
            } else if (((TextView) view).getText().toString().contains("加入购物车")) {
                ((TextView) view).setText("加购物车先");
                getListener(view, true);
                logInfo(view);
            }
        }
    }

    private void forButtonBuyLayout(View view) {
        getListener(view, false);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                forButtonBuyLayout(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    private View.OnClickListener getListener(View view, boolean logOn) {
        Log.e(TAG, "---");
        try {
            Field infoField = View.class.getDeclaredField("mListenerInfo");
            Class infoClass = infoField.getType();
            infoField.setAccessible(true);
            Object mListenerInfo = infoField.get(view);
            Field listenerField = infoClass.getField("mOnClickListener");
            View.OnClickListener mOnClickListener = (View.OnClickListener) listenerField.get(mListenerInfo);
            if (logOn) {
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
