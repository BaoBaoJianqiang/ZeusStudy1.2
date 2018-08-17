package jianqiang.com.hostapp.ams_hook;

import android.app.Instrumentation;
import android.os.Handler;

import com.example.jianqiang.mypluginlibrary.RefInvoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * @author weishu
 * @date 16/3/21
 */
public class AMSHookHelper {

    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    /**
     * Hook AMS
     * 主要完成的操作是  "把真正要启动的Activity临时替换为在AndroidManifest.xml中声明的替身Activity",进而骗过AMS
     */
    public static void hookAMN() throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {

        //获取AMN的gDefault单例gDefault，gDefault是final静态的
        Object gDefault = null;
        if (android.os.Build.VERSION.SDK_INT <= 25) {
            //获取AMN的gDefault单例gDefault，gDefault是静态的
            gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");
        } else {
            //获取ActivityManager的单例IActivityManagerSingleton，他其实就是之前的gDefault
            gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManager", "IActivityManagerSingleton");
        }

        // gDefault是一个 android.util.Singleton<T>对象; 我们取出这个单例里面的mInstance字段
        Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", gDefault, "mInstance");

        // 创建一个这个对象的代理对象MockClass1, 然后替换这个字段, 让我们的代理对象帮忙干活
        Class<?> classB2Interface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { classB2Interface },
                new MockClass1(mInstance));

        //把gDefault的mInstance字段，修改为proxy
        RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);
    }

    public static void attachContext() throws Exception{
        // 先获取到当前的ActivityThread对象
        Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread");

        // 拿到原始的 mInstrumentation字段
        Instrumentation mInstrumentation = (Instrumentation) RefInvoke.getFieldObject(currentActivityThread, "mInstrumentation");

        // 创建代理对象
        Instrumentation evilInstrumentation = new EvilInstrumentation(mInstrumentation);

        // 偷梁换柱
        RefInvoke.setFieldObject(currentActivityThread, "mInstrumentation", evilInstrumentation);
    }
}
