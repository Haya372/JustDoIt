package com.myApp.Justdoit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    private Memo memo=new Memo();
    private boolean isDelete;
    private EditText title;
    private EditText content;
    private CheckBox locked;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //Toolbar
        Toolbar toolbar=findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Floating Action Button
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });
        //Load file
        Intent intent = getIntent();
        String fileName=intent.getStringExtra("FILE_NAME");
        memo=MyFileHandler.MyFileListenner(this,fileName);
        title=(EditText)findViewById(R.id.edit_title);
        title.setText(memo.getTitle());
        content=(EditText)findViewById(R.id.edit_content);
        content.setText(memo.getContent());
        locked=(CheckBox)findViewById(R.id.edit_checkBox);
        locked.setChecked(memo.isLocked());
    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        //Set toolbar layout
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //define toolbar actions
        switch (item.getItemId()) {
            case R.id.delete:
                //If delete button is clicked...
                isDelete=true;
                if(!memo.getFileName().isEmpty()){
                    deleteFile(memo.getFileName());
                }
                this.finish();
                return true;
            /*case R.id.nav_controller_view_tag:
                //intent=new Intent(this,MainActivity.class);
                //startActivity(intent);
                this.finish();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(isDelete){
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        String editedTitle=title.getText().toString();
        String editedContent=content.getText().toString();
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN);
        if(isEdited(editedTitle,editedContent)||memo.getEditDate()==null){
            memo.setEditDate(sdf.format(date));
        }
        memo.setTitle(editedTitle);
        memo.setContent(editedContent);
        if (memo.getTitle().isEmpty()&&memo.getContent().isEmpty()){
            return;
        }
        if(memo.getFileName().isEmpty()){
            sdf.applyPattern("yyyyMMdd_HHmmssSSS");
            memo.setFileName(sdf.format(date)+".txt");
        }
        MyFileHandler.MyFileWriter(this,memo);
    }

    private boolean isEdited(String title,String content){
        if(title.equals(memo.getTitle())&&content.equals(memo.getContent())){
            return false;
        }
        return true;
    }
}
