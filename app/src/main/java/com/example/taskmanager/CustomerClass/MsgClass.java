package com.example.taskmanager.CustomerClass;

public class MsgClass {
    String title, msg, documentId;

    public MsgClass() {
        // empty constructor for Firebase
    }

    public MsgClass(String title, String msg, String documentId) {
        this.title = title;
        this.msg = msg;
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
