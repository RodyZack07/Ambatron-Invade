package com.example.savesthekunti.Model;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.savesthekunti.R;
import com.google.firebase.firestore.FirebaseFirestore;
import android.graphics.drawable.Drawable;

public class Score extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView scoreText, starsText;
    private ImageView bintangKosong1, bintangkosong2, bintangkosong3, bintangkuning1,bintangkuning2,bintangkuning3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_win);

        db = FirebaseFirestore.getInstance();
        bintangKosong1 = findViewById(R.id.bintang_kosong);

        int score = getIntent().getIntExtra("score", 0);
        int stars = getIntent().getIntExtra("stars", 0);

        scoreText.setText("Score: " + score);
        starsText.setText("Stars: " + stars);

    }
    }
//
//    private void storeScore(int score, int stars) {
//        // Assuming user ID is available, for example from FirebaseAuth or SharedPreferences
//        String userId = "user_id_example"; // Replace with actual user ID
//
//        // Create a ScoreData object to store the score and stars
//        ScoreData scoreData = new ScoreData(score, stars);
//
//        // Store the score data in Firestore
//        db.collection("scores")
//                .document(userId)
//                .set(scoreData)
//                .addOnSuccessListener(aVoid -> Log.d("Score", "Score saved successfully"))
//                .addOnFailureListener(e -> Log.e("Score", "Error saving score", e));
//    }
//}
