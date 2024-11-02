package com.example.savesthekunti.Config;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.SeekBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savesthekunti.R;

public class seekBar extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBarVol;
    private AudioManager audioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);



        // Inisialisasi SeekBar
        seekBarVol = findViewById(R.id.seekBarVol); // Pastikan SeekBar ada di layout popup_layout

        // Inisialisasi AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Atur maksimum nilai SeekBar
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekBarVol.setMax(maxVolume);
        // Set posisi SeekBar ke nilai volume saat ini
        seekBarVol.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        // Listener untuk mengubah volume berdasarkan SeekBar
        seekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                float volume = (float) progress / maxVolume; // Menghitung volume dari 0.0 hingga 1.0
                mediaPlayer.setVolume(volume, volume); // Mengatur volume MediaPlayer
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tidak ada aksi yang perlu dilakukan saat mulai menyentuh SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Tidak ada aksi yang perlu dilakukan saat selesai menyentuh SeekBar
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Melepaskan sumber daya MediaPlayer saat aktivitas dihancurkan
            mediaPlayer = null; // Mengatur objek menjadi null
        }
    }
}
