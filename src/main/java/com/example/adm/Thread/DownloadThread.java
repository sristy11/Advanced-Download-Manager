package com.example.adm.Thread;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import java.io.*;
import java.net.*;

public class DownloadThread extends Thread {
    private ProgressBar progressBar;
    private Label speedLabel, sizeLabel, timeRemainingLabel;
    private String fileURL;
    private String destinationPath;

    private double downloadedSize = 0;
    private double totalSize = 0;
    private double speed = 0;
    private double timeRemaining = 0;
    Task<Void> task;
    public DownloadThread(ProgressBar progressBar, Label speedLabel, Label sizeLabel, Label timeRemainingLabel, String fileURL, String destinationPath) {
        this.progressBar = progressBar;
        this.speedLabel = speedLabel;
        this.sizeLabel = sizeLabel;
        this.timeRemainingLabel = timeRemainingLabel;
        this.fileURL = fileURL;
        this.destinationPath = destinationPath;
    }
    public DownloadThread(Task<Void> task){
        this.task = task;
    }
    @Override
    public void run() {
        try {
            task.run();
            Thread.sleep(1000);
            //updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
