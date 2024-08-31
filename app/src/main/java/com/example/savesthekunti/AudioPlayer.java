package com.example.savesthekunti;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer {

    private MediaPlayer mediaPlayer;

    //ambil refrensi deklarasi audio
    public AudioPlayer(Context context, int resourcesId){

        // deklarasi Audio
        mediaPlayer= MediaPlayer.create(context, resourcesId );
        mediaPlayer.setLooping(true);
    }

    public void playMusic (){
        if (mediaPlayer != null){
            mediaPlayer.start(); }
        }

    public void stopMusik(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

