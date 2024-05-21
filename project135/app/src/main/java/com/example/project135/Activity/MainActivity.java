package com.example.project135.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project135.Adapter.TrendsAdapter;
import com.example.project135.Domain.TrendSDomain;
import com.example.project135.R;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterTrendsList;
    private RecyclerView recyclerViewTrends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initRecyclerView();
        BottomNavigation();
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //   return insets;});
    }

    private void BottomNavigation() {
        LinearLayout profileBtn=findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //startActivities(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });
    }

    private void initRecyclerView() {
        ArrayList<TrendSDomain> items=new ArrayList<>();

        items.add(new TrendSDomain("Future in AI, What will tomorrow be like?","The National","trends"));
        items.add(new TrendSDomain("Important points in concluding a work contract","Reuters","trends2"));
        items.add(new TrendSDomain("Future in AI, What will tomorrow be like?","The National","trends"));

        //recyclerViewTrends=findViewById(R.id.view);
        recyclerViewTrends.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        adapterTrendsList=new TrendsAdapter(items);
        recyclerViewTrends.setAdapter(adapterTrendsList);
    }
}