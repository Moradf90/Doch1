package t.a.m.com.doch1.common.utils;

import java.util.HashMap;

import t.a.m.com.doch1.Models.User;

/**
 * Created by tom on 23-Jan-17.
 */
public abstract class MyCallbackInterface {
    public abstract void onSpeechRecognitionFinished(HashMap<User, String> result);
}
