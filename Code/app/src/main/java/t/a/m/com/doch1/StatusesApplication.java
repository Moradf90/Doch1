package t.a.m.com.doch1;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Morad on 12/16/2016.
 */
public class StatusesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
