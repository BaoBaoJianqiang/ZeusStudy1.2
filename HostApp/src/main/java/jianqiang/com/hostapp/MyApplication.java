package jianqiang.com.hostapp;

import android.app.Application;
import android.content.Context;

import com.example.jianqiang.mypluginlibrary.PluginManager;
import com.example.jianqiang.mypluginlibrary.Utils;

import java.io.File;

import dalvik.system.DexClassLoader;
import jianqiang.com.hostapp.ams_hook.AMSHookHelper;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        PluginManager.init(this);

        try {
            AMSHookHelper.hookAMN();
            AMSHookHelper.attachContext();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
