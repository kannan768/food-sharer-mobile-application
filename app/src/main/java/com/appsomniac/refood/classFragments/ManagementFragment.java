package com.appsomniac.refood.classFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appsomniac.refood.R;
import com.appsomniac.refood.adapter.manage.YoutubePlayerAdapter;
import com.google.android.youtube.player.YouTubePlayer;

public class ManagementFragment extends Fragment {

    View manageFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        manageFragment = inflater.inflate(R.layout.manage_layout, container, false);

        RecyclerView recyclerView=(RecyclerView)manageFragment.findViewById(R.id.manage_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        YoutubePlayerAdapter adapter=new YoutubePlayerAdapter(getContext());
        recyclerView.setAdapter(adapter);

        return manageFragment;
    }
}