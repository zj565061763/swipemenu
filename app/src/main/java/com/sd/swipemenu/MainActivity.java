package com.sd.swipemenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fanwe.swipemenu.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_simple:
                startActivity(new Intent(this, SimpleActivity.class));
                break;
            case R.id.btn_listview:
                startActivity(new Intent(this, ListViewActivity.class));
                break;
            case R.id.btn_recyclerview:
                startActivity(new Intent(this, RecyclerViewActivity.class));
                break;
        }
    }
}
