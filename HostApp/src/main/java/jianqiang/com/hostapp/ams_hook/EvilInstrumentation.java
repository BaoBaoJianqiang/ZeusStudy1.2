package jianqiang.com.hostapp.ams_hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

public class EvilInstrumentation extends Instrumentation {

    private static final String TAG = "EvilInstrumentation";

    // ActivityThread中原始的对象, 保存起来
    Instrumentation mBase;

    public EvilInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {

        // 把替身恢复成真身
        Intent rawIntent = intent.getParcelableExtra(AMSHookHelper.EXTRA_TARGET_INTENT);
        if(rawIntent == null) {
            return mBase.newActivity(cl, className, intent);
        }

        String newClassName = rawIntent.getComponent().getClassName();
        return mBase.newActivity(cl, newClassName, rawIntent);
    }
}
