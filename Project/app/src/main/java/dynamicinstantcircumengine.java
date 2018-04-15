import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by pfsar on 30/09/2017.
 */

public class dynamicinstantcircumengine extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
