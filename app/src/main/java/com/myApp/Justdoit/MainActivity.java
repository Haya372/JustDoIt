package com.myApp.Justdoit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ArrayList<Memo> memos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        //banner
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-5802533135593818~7073786726");
        AdView adView=(AdView)findViewById(R.id.adView_main);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume(){
        super.onResume();
        String filePath=getFilesDir().getPath();
        File[] files=new File(filePath).listFiles();
        //dataset
        memos=new ArrayList<Memo>();
        for(File file:files){
            Memo memo=MyFileHandler.MyFileListenner(this,file.getName());
            if(haveToDelete(memo.getEditDate(),3)&&!memo.isLocked()){
                deleteFile(memo.getFileName());
                continue;
            }
            memos.add(memo);
        }
        //RecyclerView
        final RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler_view_main);
        recyclerView.setHasFixedSize(true);//
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MyRecyclerAdapter mAdapter=new MyRecyclerAdapter(this, memos, new MyRecyclerListener() {
            @Override
            public void onRecyclerClicked(View v, int position) {
                Memo memo=memos.get(position);
                switch (v.getId()){
                    case R.id.constraintLayout:
                        Intent intent=new Intent(MainActivity.this,EditActivity.class);
                        intent.putExtra("FILE_NAME",memo.getFileName());
                        startActivity(intent);
                        break;
                    case R.id.recyclerView_checkBox:
                        boolean state=memo.isLocked();
                        memo.setLocked(!state);
                        MyFileHandler.MyFileWriter(MainActivity.this,memo);
                        break;
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
        //decoration
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        //swipe action
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private Drawable deleteIcon = ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_menu_delete);

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // delete file
                int swipedPosition = viewHolder.getAdapterPosition();
                MyRecyclerAdapter adapter = (MyRecyclerAdapter) recyclerView.getAdapter();
                deleteFile(memos.get(swipedPosition).getFileName());//ファイルを先に消さないとバグが発生
                adapter.remove(swipedPosition);
            }

            @Override
            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                // if cancelled
                if (dX == 0f && !isCurrentlyActive) {
                    clearCanvas(c, itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
                    return;
                }

                ColorDrawable background = new ColorDrawable();
                background.setColor(Color.parseColor("#f44336"));
                background.setBounds(itemView.getRight() + (int)dX, itemView.getTop(),  itemView.getRight(), itemView.getBottom());
                background.draw(c);

                int deleteIconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
                int deleteIconRight = itemView.getRight() - deleteIconMargin;
                int deleteIconBottom = deleteIconTop +  deleteIcon.getIntrinsicHeight();

                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                deleteIcon.draw(c);
            }

            private void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
                Paint paint = new Paint();
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                c.drawRect(left, top, right, bottom, paint);
            }
        };
        (new ItemTouchHelper(callback)).attachToRecyclerView(recyclerView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.to_edit_activity:
                //Edit Button clicked
                Intent intent=new Intent(this,EditActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private boolean haveToDelete(String editDate,int storagePeriod /*(day)*/){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN);
        Date edit=null;
        try{
            edit=sdf.parse(editDate);
        }catch (Exception e){

        }
        Date today=new Date(System.currentTimeMillis());
        long editTime=edit.getTime();
        long now=today.getTime();
        long div=(now-editTime)/(1000*60*60*24);
        if(div>storagePeriod){
            return true;
        }
        return false;
    }

}
