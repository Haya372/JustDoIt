package com.myApp.Justdoit;

public class Memo {
    private String title;
    private String content;
    private String editDate;
    private boolean locked;
    private String fileName;

    Memo(){
        fileName="";
    }

    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public void setContent(String content){
        this.content=content;
    }
    public String getContent(){
        return content;
    }
    public void setEditDate(String editDate){this.editDate=editDate;}
    public String getEditDate(){return editDate;}
    public void setLocked(boolean locked){this.locked=locked;}
    public boolean isLocked(){return locked;}
    public void setFileName(String fileName){this.fileName=fileName;}
    public String getFileName(){return this.fileName;}
}
