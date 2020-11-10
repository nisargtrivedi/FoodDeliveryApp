package com.kukdudelivery;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kukdudelivery.Adapter.DocumentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityDocument extends BaseActivity {


    RecyclerView rvDocuments;
    DocumentAdapter adapter;
    List<String> docs=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_document);
        rvDocuments=findViewById(R.id.rvDocuments);

        if(getIntent().getStringArrayListExtra("docs")!=null) {
            docs = getIntent().getStringArrayListExtra("docs");
        }
        adapter=new DocumentAdapter(this,docs);
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        rvDocuments.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
