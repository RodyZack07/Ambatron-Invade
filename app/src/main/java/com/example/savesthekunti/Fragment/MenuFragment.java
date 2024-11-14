//package com.example.savesthekunti.Fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import androidx.fragment.app.Fragment;
//
//import com.example.savesthekunti.Activity.GameActivity;
//import com.example.savesthekunti.R;
//
//public class MenuFragment extends Fragment {
//
//    public MenuFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.gameactivity_pause, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Menangani tombol resume
//        Button resumeButton = view.findViewById(R.id.resumeButton);
//        resumeButton.setOnClickListener(v -> {
//            if (getActivity() instanceof GameActivity) {
//                ((GameActivity) getActivity()).resumeGame();
//            }
//        });
//    }
//}
//
