package com.aplha.pianoguru;

import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Exchanger;

public class MainActivity extends AppCompatActivity {

    private List<String> tokens;
    private Thread myAudioThread;
    private MediaPlayer mediaPlayer;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.play_button).setOnClickListener(playButtonClickListener);
        findViewById(R.id.stop_button).setOnClickListener(stopButtonClickListener);
    }

    private View.OnClickListener playButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            tokens = new ArrayList<>();
            String cord = ((EditText) findViewById(R.id.cords_input)).getText().toString().toLowerCase();
            StringTokenizer st = new StringTokenizer(cord);
            while (st.hasMoreElements()) {
                tokens.add(st.nextElement().toString());
            }
            i = 0;
            myAudioThread = new Thread(new MyAudioThread());
            myAudioThread.start();
        }
    };

    private View.OnClickListener stopButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mediaPlayer != null) {
                i = 0;
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            if (myAudioThread != null) {
                myAudioThread.interrupt();
                myAudioThread = null;
            }
        }
    };

    private MediaPlayer.OnCompletionListener onMedialCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            playMusic(i);
        }
    };

    private void playMusic(int position) {
        if (position < tokens.size()) {
            if (mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            if (tokens.get(position).contains(".")){
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer = new MediaPlayer();
            }else {
                Uri uriPath = Uri.parse("android.resource://" + getPackageName() + "/" + "raw/" + tokens.get(position));
                mediaPlayer = MediaPlayer.create(getBaseContext(),uriPath);
            }
            if (mediaPlayer != null) {
                try {
                    ++i;
                    mediaPlayer.setOnCompletionListener(onMedialCompletionListener);
                    mediaPlayer.start();
                }catch ( Exception e){
                    e.printStackTrace();
                }
            }
        }
        else {
            i=0;
        }
    }

    private class MyAudioThread implements Runnable {

        @Override
        public void run() {
            playMusic(i);
            mediaPlayer.setOnCompletionListener(onMedialCompletionListener);
            mediaPlayer.start();
        }

    }

    @Override
    protected void onPause() {
        if (myAudioThread != null) {
            myAudioThread.interrupt();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (myAudioThread != null) {
            myAudioThread.interrupt();
        }
        super.onDestroy();
    }
}
