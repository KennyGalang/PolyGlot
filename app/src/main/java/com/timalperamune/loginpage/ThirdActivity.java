package com.timalperamune.loginpage;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by timalperamune on 2017-12-03.
 */

public class ThirdActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    private VideoView videoView;
    private Button button_play, button_pause, button_resume;
    int stopPosition = 0;
    private static boolean running = false;

    private boolean ready;

    private String current_time = "";
    static long seconds = 0;
    static long mins = 0;
    static long millis = 0;
    static int mCount = 0;
    Timer timer;
    TextView timeflg;
    Map<String, String> transcript_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        String str = "https://firebasestorage.googleapis.com/v0/b/android-5fe7a.appspot.com/o/stash.mp4?alt=media&token=fad3b7df-9811-4582-aec9-77b20656fffa";
        final Uri uri = Uri.parse(str);
        videoView = (VideoView) findViewById(R.id.videoView);


        button_play = (Button) findViewById(R.id.play);
        button_pause = (Button) findViewById(R.id.stop);
        button_resume = (Button) findViewById(R.id.resume);


        Thread thread = new Thread() {
            public void run() {
                Log.i("RESPNSE", "WOW");
                try {

                    String textResponse = getJSONObjectFromURL("https://www.videoindexer.ai/Api/Widget/Breakdowns/7d38f0ade8/7d38f0ade8/Vtt?language=" + SecondActivity.TEXT);
                    List<String> sentences = new ArrayList<>();
                    transcript_map = new HashMap<>();
                    String[] tokens = textResponse.toString().split("\n");
                    for (int i = 3; i < tokens.length; i += 3) {
                        sentences.add(tokens[i]);
                    }
                    int arrayList_index = 0;
                    String[] time_stamps = textResponse.toString().split("\n");
                    for (int i = 2; i < time_stamps.length; i += 3) {
                        String[] first_time_stamp = time_stamps[i].split("-->");
                        transcript_map.put(first_time_stamp[0], sentences.get(arrayList_index));
                        arrayList_index++;

                    }
                    for (Map.Entry<String, String> entry : transcript_map.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        Log.i("KEY", key);
                        Log.i("VALUE", value);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        };thread.start();



        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.e("TTS", "TextToSpeech.OnInitListener.onInit...");
                printOutSupportedLanguages();
                setTextToSpeechLanguage();

            }
        });
        textToSpeech.setPitch((float) 1.0);
        textToSpeech.setSpeechRate((float) 1);




        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.setVideoURI(uri);
                videoView.setOnPreparedListener(PreparedListener);
                videoView.requestFocus();
                videoView.start();
                SystemClock.sleep(2000);
//                speakOut();




                timeflg = (TextView)findViewById(R.id.textView);
                running = true;

                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        timerMethod();
                    }
                }, 1,100);

//                if(current_time.equals("00:12:9")) {
//                    speakOut();
//                }

            }
        });


    }

    public static String getJSONObjectFromURL(String urlString) throws IOException, JSONException{
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);

        urlConnection.setDoOutput(true);
        urlConnection.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while((line = br.readLine()) != null){
            sb.append(line +"\n");

        }
        br.close();
        String jsonString = sb.toString();

        return new String(jsonString);

    }






    private void printOutSupportedLanguages()  {
        // Supported Languages
        Set<Locale> supportedLanguages = textToSpeech.getAvailableLanguages();
        if(supportedLanguages!= null) {
            for (Locale lang : supportedLanguages) {
                Log.e("TTS", "Supported Language: " + lang);
            }
        }
    }

    private void setTextToSpeechLanguage() {
        Locale language = this.getUserSelectedLanguage();
        if (language == null) {
            this.ready = false;
            Toast.makeText(this, "Not language selected", Toast.LENGTH_SHORT).show();
            return;
        }
        int result = textToSpeech.setLanguage(language);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            this.ready = false;
            Toast.makeText(this, "Missing language data", Toast.LENGTH_SHORT).show();
            return;
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            this.ready = false;
            Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            return;
        } else {
            this.ready = true;
            Locale currentLanguage = textToSpeech.getVoice().getLocale();
            Toast.makeText(this, "Language " + currentLanguage, Toast.LENGTH_SHORT).show();
        }
    }
    private void timerMethod(){

//        try {
//            Thread.currentThread().join();
            Log.i("TIMER THREAD", "HMM?");
            this.runOnUiThread(generate);

//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }



    private Runnable generate= new Runnable() {





        @Override
        public void run () {
            String tempMin = Integer.toString((int) mins);
            String tempSec = Integer.toString((int) seconds);
            if (mins < 10) {
                tempMin = "0" + tempMin;
            }
            if (seconds < 10) {
                tempSec = "0" + tempSec;
            }
            current_time = tempMin + ":" + tempSec + ":" + mCount;

            timeflg.setText(current_time);

            millis = mCount;

            mCount++;

            if (seconds == 60) {
                seconds = 0;
                mins++;
            }
            if (mCount == 10) {
                mCount = 0;
                millis = mCount;
                seconds++;
            }
//            System.out.println(current_time + "- is equal to-00:12:9");
            if (transcript_map.containsKey(current_time)) {
//                Log.i("AM I EVEN ", "FUCKING HEREEEEE")
                ;
                speakOut();
            }

        }
    };
    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener(){

        @Override
        public void onPrepared(MediaPlayer m) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = new MediaPlayer();
                }
                m.setVolume(0f, 0f);
                m.setLooping(false);
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Locale getUserSelectedLanguage() {
        String temp = SecondActivity.TEXT;
        if (temp.equals("English")) {
            return Locale.ENGLISH;
        } else if (temp.equals( "French")) {
            System.out.println("F" + "");
            return Locale.FRANCE;
        } else if(temp.equals("German")){
            return Locale.GERMAN;
        } else if(temp.equals("Chinese")){
            textToSpeech.setPitch((float)1.00);
            return Locale.SIMPLIFIED_CHINESE;

        } else if(temp.equals("Italian")){
            return Locale.ITALIAN;
        }
//        System.out.println("ED" + "");
        return Locale.ENGLISH;

    }
    private void speakOut() {

        if (!ready) {
            Toast.makeText(this, "Text to Speech not ready", Toast.LENGTH_LONG).show();
            return;
        }

        String toSpeak = transcript_map.get(current_time);


        Toast.makeText(this, toSpeak, Toast.LENGTH_SHORT).show();
        String utteranceId = UUID.randomUUID().toString();
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId);

    }


}