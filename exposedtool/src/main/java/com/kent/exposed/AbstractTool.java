package com.kent.exposed;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

abstract class AbstractTool {
    abstract void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable;
}
