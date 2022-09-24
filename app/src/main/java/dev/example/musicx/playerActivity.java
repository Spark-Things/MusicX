package dev.example.musicx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class playerActivity extends AppCompatActivity {

     Button play,next,prev,fast_farward,fast_rewind;
     ImageView songThumbnail;
     TextView songname,songStart,songStop;
     SeekBar seekBar;
     Thread updateSeekbar;

     String sname;
     public static final String EXTRA_NAME = "Song_name";
     static MediaPlayer mediaPlayer;
     int position;
     ArrayList<File> mySongs;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setContentView(R.layout.activity_player);

        songThumbnail = findViewById(R.id.ImageView);

        play = findViewById(R.id.playbtn);
        next = findViewById(R.id.SkipF);
        prev = findViewById(R.id.SkipP);
        fast_farward = findViewById(R.id.FastF);
        fast_rewind = findViewById(R.id.FastR);

        songname = findViewById(R.id.txtsn);
        songStart = findViewById(R.id.textsStart);
        songStop = findViewById(R.id.textsStop);

        seekBar = findViewById(R.id.seekbar);


           if (mediaPlayer != null){
               mediaPlayer.stop();
               mediaPlayer.release();
           }

           Intent i = getIntent();
           Bundle bundle = i.getExtras();

           mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

           String songName = i.getStringExtra("songname");
           position = bundle.getInt("pos",0);

           songname.setSelected(true);

        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName().toString().replace(".mp3","").replace(".wav","");
//        if (sname.endsWith(".mp3")){
//            sname.replace(".mp3","");
//        }
        songname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();


        updateSeekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currPos = 0;

                while (currPos < totalDuration){
                    try {
                        sleep(500);
                        currPos = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currPos);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.playing), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.playing), PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                     mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime = parseTime(mediaPlayer.getDuration());
        songStop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = parseTime(mediaPlayer.getCurrentPosition());
                songStart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    play.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else{
                    play.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });


        //Next Listener - -    - - - - -- --- - - - --

         mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
             @Override
             public void onCompletion(MediaPlayer mp) {
                 next.performClick();
             }
         });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Log.w("pos", String.valueOf(position));
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname =mySongs.get(position).getName().toString().replace(".mp3","").replace(".wav","");
                if (sname.endsWith(".mp3")){
                    sname.replace(".mp3","");
                }
                songname.setText(sname);
                String endTime = parseTime(mediaPlayer.getDuration());
                songStop.setText(endTime);
                mediaPlayer.start();
                play.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(songThumbnail);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1 < 0 ? (mySongs.size()-1) : position-1));
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName().toString().replace(".mp3","").replace(".wav","");
                songname.setText(sname);
                String endTime = parseTime(mediaPlayer.getDuration());
                songStop.setText(endTime);
                mediaPlayer.start();
                play.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(songThumbnail);
            }
        });

        fast_farward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        fast_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });

        //for visualzation video starting at 25:10

    }

    public  void  startAnimation(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(songThumbnail,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }


    public String parseTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time += min + ":";

        if(sec < 10){
            time+="0";
        }
        time+=sec;

        return time;
    }
}