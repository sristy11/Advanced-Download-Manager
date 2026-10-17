package com.example.adm.DownloadUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class QueueUtils {
    BlockingQueue<Task<Void>> files = new LinkedBlockingQueue<>();
    int size = 0;
    public void addToQueue(Task<Void> task){
        files.add(task);
        size++;
    }
    public Task<Void> pop(){
        Task<Void> task = files.poll();
        size--;
        return task;
    }
    void show(Queue<String> q1, VBox queueVBox) {
            queueVBox.getChildren().clear();
            VBox fileBox = new VBox();
            System.out.println("Removed all from VBox");
            for (String filename : q1) {
                    System.out.println("Pending: " + filename);
                int index = filename.lastIndexOf(".");
                String fileExtension =  (index > 0 && index < filename.length() - 1) ? filename.substring(index + 1) : "";
                final HBox fileHBox = new HBox(10);
                try {
                    ImageView icon = new ImageView();
                    icon.setFitHeight(40);
                    icon.setFitWidth(40);
                    if ("mp3".equalsIgnoreCase(fileExtension)) {
                        icon.setImage(new Image(getClass().getResourceAsStream("/icon/mp3_icon.png")));
                    }
                    else if ("gif".equalsIgnoreCase(fileExtension)) {
                        icon.setImage(new Image(getClass().getResourceAsStream("/icon/gif_icon.png")));
                    }else if ("pdf".equalsIgnoreCase(fileExtension)) {
                        icon.setImage(new Image(getClass().getResourceAsStream("/icon/pdf_icon.png")));

                    }
                    else if ("mp4".equalsIgnoreCase(fileExtension)) {
                        icon.setImage(new Image(getClass().getResourceAsStream("/icon/mp4_icon.png")));

                    }else {
                        icon.setImage(new Image(getClass().getResourceAsStream("/icon/default.png")));
                    }
                    fileHBox.getChildren().add(icon);
                } catch (Exception e) {
                    System.out.println("Error loading icon: " + e.getMessage());
                }

                    Label fileLabel = new Label(filename);
                    fileHBox.getChildren().add(fileLabel);
                    fileBox.getChildren().addAll(fileHBox);
                    DropShadow shadow = new DropShadow();
                    shadow.setOffsetX(3);
                    shadow.setOffsetY(3);
                    shadow.setRadius(5);
                    shadow.setColor(Color.GRAY);
                    fileHBox.setEffect(shadow);
                    fileHBox.setStyle("-fx-text-fill: white; -fx-border-style: solid; -fx-border-color:  #3597e1; -fx-border-width: 2px;  -fx-background-color: white; -fx-border-insets: -2px; -fx-pref-height: 60");
                    fileLabel.setStyle("-fx-text-fill: black; -fx-font-size: 15px; -fx-translate-y: 20");
                    queueVBox.getChildren().add(fileBox);
                    //fileBox.getChildren().clear();

                }
            }
    }

