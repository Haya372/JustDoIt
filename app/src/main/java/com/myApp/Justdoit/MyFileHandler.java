package com.myApp.Justdoit;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class MyFileHandler {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Memo MyFileListenner(Activity activity, String fileName){
        Memo memo=new Memo();
        try{
            if(fileName==null)fileName="";
            StringBuilder stringBuilder=new StringBuilder();
            FileInputStream fileInputStream=activity.openFileInput(fileName);
            memo.setFileName(fileName);
            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader reader=new BufferedReader(inputStreamReader);
            String title=reader.readLine();
            memo.setTitle(title);
            String editDate=reader.readLine();
            memo.setEditDate(editDate);
            String locked=reader.readLine();
            if(locked.equals("true")){
                memo.setLocked(true);
            }else if(locked.equals("false")){
                memo.setLocked(false);
            }
            String line=reader.readLine();
            while(line!=null){
                stringBuilder.append(line).append("\n");
                line=reader.readLine();
            }
            String content=stringBuilder.toString();
            memo.setContent(content);
        }catch(Exception e){
            //exception
        }
        return memo;
    }

    public static void MyFileWriter(Activity activity,Memo memo){
        try{
            OutputStream outputStream=activity.openFileOutput(memo.getFileName(), Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream,"UTF-8");
            PrintWriter writer=new PrintWriter(outputStreamWriter);
            //////fileの構成
            //title
            //edit date
            //is locked or not
            //content
            writer.println(memo.getTitle());
            writer.println(memo.getEditDate());
            if (memo.isLocked()){
                writer.println("true");
            }else{
                writer.println("false");
            }
            writer.println(memo.getContent());
            writer.close();
            outputStream.close();
        }catch (Exception e){
            //exception
            Toast.makeText(activity, "Failed to save file!", Toast.LENGTH_LONG).show();
        }
    }
}
