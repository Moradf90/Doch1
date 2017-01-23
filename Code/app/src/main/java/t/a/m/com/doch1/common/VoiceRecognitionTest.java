package t.a.m.com.doch1.common;

/**
 * Created by tom on 17-Jan-17.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.common.utils.MyCallbackInterface;


public class VoiceRecognitionTest
{
    public static class vVoiceRecognitionTest1 {
        private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 121;
        private static final int MAX_DIS_FOR_NAME = 1;
        private static final int MAX_DIS_FOR_STATUS = 2;
        private static SpeechRecognizer sr;
        private static final String TAG = "MyStt3Activity";

        private static List<User> lstMembers;
        private static List<String> mainStatuses;

        private static MyCallbackInterface myCallback;

        public static void init(Activity context) {
            sr = SpeechRecognizer.createSpeechRecognizer(context);
            sr.setRecognitionListener(new listener());
        }

        static class listener implements RecognitionListener {
            public void onReadyForSpeech(Bundle params) {
//            Log.d(TAG, "onReadyForSpeech");
            }

            public void onBeginningOfSpeech() {
//            Log.d(TAG, "onBeginningOfSpeech");
            }

            public void onRmsChanged(float rmsdB) {
//            Log.d(TAG, "onRmsChanged");
            }

            public void onBufferReceived(byte[] buffer) {
//            Log.d(TAG, "onBufferReceived");
            }

            public void onEndOfSpeech() {
//            Log.d(TAG, "onEndofSpeech");
            }

            public void onError(int error) {
//            Log.d(TAG,  "error " +  error);
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        showLog("ERROR_AUDIO");
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        showLog("ERROR_CLIENT");
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        showLog("ERROR_RECOGNIZER_BUSY");
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        showLog("ERROR_INSUFFICIENT_PERMISSIONS");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        showLog("ERROR_NETWORK_TIMEOUT");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        sr.destroy();
                        showLog("ERROR_NETWORK");
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        showLog("ERROR_SERVER");
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        showLog("ERROR_NO_MATCH");
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        showLog("ERROR_SPEECH_TIMEOUT");
                        break;
                    default:
                        assert false;
                        return;
                }
            }

            private void showLog(String error_speech) {
                Log.d(TAG, error_speech);
            }

            public void onResults(Bundle results) {
                String str = new String();
//            Log.d(TAG, "onResults " + results);
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (int i = 0; i < data.size(); i++) {
//                Log.d(TAG, "result " + data.get(i));
                    str += i + " " + data.get(i) + " \n";
                }
//                mText.setText("results: " + str);

                handleStatusesBySpeechData(data);
            }

            public void onPartialResults(Bundle partialResults) {
//            Log.d(TAG, "onPartialResults");
            }

            public void onEvent(int eventType, Bundle params) {
//            Log.d(TAG, "onEvent " + eventType);
            }
        }

        private static void handleStatusesBySpeechData(ArrayList data) {

            HashMap<User, String> userToStatus = new HashMap<>();
            HashMap<String, User> nameToUsers = new HashMap<>();
            List<String> names= new ArrayList<>();

            for (User usr : lstMembers) {
                // Get only first names for now
                names.add(usr.getName());
                nameToUsers.put(usr.getName(), usr);
            }

            for (Object s : data) {
                String sCurrName = "";
                String sCurrStatus = "";
                for (String word : s.toString().split(" ")) {
                    // If there is no name already
                    if (sCurrName.equals("")) {
                        sCurrName = getClosestName(names, word);
                    }
                    // If there is name - we looking for status
                    else {
                        sCurrStatus = getClosestStatus(mainStatuses, word);

                        // If status was found
                        if (!sCurrStatus.equals("")) {
                            if (!userToStatus.containsKey(sCurrName)) {
                                userToStatus.put(nameToUsers.get(sCurrName), sCurrStatus);

                            } else {
                                if (!userToStatus.get(sCurrName).equals(sCurrStatus)) {
                                    // Same user fit to another status
                                }
                            }
                            sCurrName = "";
                            sCurrStatus = "";
                        }
                    }
                }
            }

            myCallback.onSpeechRecognitionFinished(userToStatus);
        }

        private static String getClosestName(List<String> names, String word) {
            return getClosestString(names, word, MAX_DIS_FOR_NAME);

        }

        private static String getClosestStatus(List<String> statuses, String word) {
            return getClosestString(statuses, word, MAX_DIS_FOR_STATUS);
        }

        private static String getClosestString(List<String> strings, String word, int MAX_DIS) {
            double nMinimum = Double.POSITIVE_INFINITY;
            String strClosestString = "";
            for (String currString : strings) {
                int nDis = Utils.levenshteinDistance(currString, word);
                if (nDis < nMinimum && nDis < MAX_DIS) {
                    nMinimum = nDis;
                    strClosestString = currString;
                }
                // In case use vav ha-hibur
                else if (word.startsWith("ו")) {
                    nDis = Utils.levenshteinDistance(currString, word.substring(1));
                    if (nDis < nMinimum && nDis < MAX_DIS) {
                        nMinimum = nDis;
                        strClosestString = currString;
                    }
                } else if (word.endsWith("ים")) {
                    nDis = Utils.levenshteinDistance(currString, word.substring(0, word.length() - 2));
                    if (nDis < nMinimum && nDis < MAX_DIS) {
                        nMinimum = nDis;
                        strClosestString = currString;
                    }
                }
//                else if (currString.contains(word)) {
//                    nDis = Utils.levenshteinDistance(currString, word.substring(0, word.length() - 2));
//                    if (nDis < nMinimum && nDis < MAX_DIS) {
//                        nMinimum = nDis;
//                        strClosestString = currString;
//                    }
//                }
                else {
                    nDis = Utils.levenshteinDistance(currString.split(" ")[0], word);
                    if (nDis < nMinimum && nDis < MAX_DIS) {
                        nMinimum = nDis;
                        strClosestString = currString;
                    }
                }
            }

            return strClosestString;
        }

        public static void handleSpeech(List<User> plstMembers, List<String> plstMain, MyCallbackInterface callback) {
            myCallback = callback;
            mainStatuses = plstMain;
            lstMembers = plstMembers;

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            // time to wait after we finish hearing to consider the input complete
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000 * 60 * 200);

            // minimum time of recording
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000 * 60 * 200);

            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000 * 60 * 200);

            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            sr.startListening(intent);
        }
    }
}