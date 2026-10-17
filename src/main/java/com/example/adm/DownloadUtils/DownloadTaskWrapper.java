package com.example.adm.DownloadUtils;

import javafx.concurrent.Task;

public class DownloadTaskWrapper implements Runnable{
    Task<Void> task;
    String filename;
    public DownloadTaskWrapper(Task<Void> task, String filename){
        this.task = task;
        this.filename = filename;
    }

    @Override
    public void run() {
        task.run();
    }
}
