package com.example.adm;
import  com.example.adm.DownloadUtils.DownloadController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PageManager {

    // References to Parent nodes for different pages (Home, Settings, Files)
    public static Parent home;
    public static Parent settings;
    public static Parent files;
    public static PageManager controller;

    // for maximum simultaneous value
    @FXML TextField MaxDownload;

    // VBox references for each tab in the TabPane
    @FXML public VBox videoVBox;
    @FXML public VBox musicVBox;
    @FXML public VBox docVBox;
    @FXML public VBox otherVBox;

    // Handle the Home button action (shows home page, hides others)
    @FXML
    public void handleHome(ActionEvent event) {
        settings.setVisible(false);
        files.setVisible(false);
        home.setVisible(true);
    }

    // Handle the Files button action (shows files page, hides others)
    @FXML
    public void handleFiles(ActionEvent event) {
        settings.setVisible(false);
        home.setVisible(false);
        files.setVisible(true);
        System.out.println(controller.docVBox);

        // Load the files (from history, e.g., downloads.txt)
        loadDownloadedFiles();
    }

    // Handle the Settings button action (shows settings page, hides others)
    @FXML
    public void handleSettings(ActionEvent event) throws Exception {
        settings.setVisible(true);
        home.setVisible(false);
        files.setVisible(false);
    }

    // Handle the MaxSim download button action (set max simultaneous downloads)
    @FXML
    public void handleMaxSim(ActionEvent event) throws Exception {
        try {
            DownloadController.MaxSimDownload = Integer.parseInt(MaxDownload.getText());
            DownloadController.executor.setCorePoolSize(DownloadController.MaxSimDownload);
            showAlert("Success", "Max simultaneous downloads set to: " + DownloadController.MaxSimDownload);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for maximum simultaneous downloads.");
        }
    }
    @FXML
    private void handleClearAll(ActionEvent event) {
        // Delete the download history file (downloads.txt)
        String userHome = System.getProperty("user.home");
        File historyFile = new File(userHome + File.separator + "downloads.txt");

        if (historyFile.exists()) {
            boolean deleted = historyFile.delete();
            if (deleted) {
                showAlert("Success", "Download history cleared and file deleted.");
            } else {
                showAlert("Error", "Failed to delete download history file.");
            }
        } else {
            showAlert("Notice", "No download history file found.");
        }

        // Clear all UI elements
        videoVBox.getChildren().clear();
        musicVBox.getChildren().clear();
        docVBox.getChildren().clear();
        otherVBox.getChildren().clear();
    }


    // Method to load downloaded files into the appropriate tabs
    public void loadDownloadedFiles() {
        // Clear UI containers
        controller.videoVBox.getChildren().clear();
        controller.musicVBox.getChildren().clear();
        controller.docVBox.getChildren().clear();
        controller.otherVBox.getChildren().clear();
        System.out.println("inside function");

        Set<String> uniqueFilePaths = new HashSet<>();
        String userHome = System.getProperty("user.home");
        File historyFile = new File(userHome + File.separator + "downloads.txt");
        System.out.println(userHome + File.separator + "downloads.txt");

        if (!historyFile.exists()) {
            showAlert("Notice", "No previous downloads found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) uniqueFilePaths.add(line.trim());
                System.out.println(line);
            }
        } catch (IOException e) {
            showAlert("Error", "Unable to read download history.");
            e.printStackTrace();
        }

        for (String filePath : uniqueFilePaths) {
            File file = new File(filePath);
            if (!file.exists()) continue;

            String fileName = file.getName();
            System.out.println(fileName);
            String extension = getFileExtension(fileName);

            // Create UI elements
            Label label = new Label(fileName);
            ImageView icon = new ImageView();
            icon.setFitHeight(40);
            icon.setFitWidth(40);

            String iconPath = "/icon/default.png"; // Default icon

            switch (extension.toLowerCase()) {
                case "mp4": iconPath = "/icon/mp4_icon.png"; break;
                case "mp3": iconPath = "/icon/mp3_icon.png"; break;
                case "pdf": iconPath = "/icon/pdf_icon.png"; break;
                case "gif": iconPath = "/icon/gif_icon.png"; break;
                default: iconPath = "/icon/default.png"; break;
            }

            try {
                icon.setImage(new Image(getClass().getResourceAsStream(iconPath)));
            } catch (Exception e) {
                System.out.println("Icon not found for " + extension + ": " + e.getMessage());
            }

            VBox entry = new VBox();
            DropShadow shadow = new DropShadow();
            shadow.setOffsetX(3);
            shadow.setOffsetY(3);
            shadow.setRadius(5);
            shadow.setColor(Color.GRAY);
            entry.setEffect(shadow);
            entry.setStyle("-fx-text-fill: white; -fx-border-style: solid; -fx-border-color:  #3597e1; -fx-border-width: 2px;  -fx-background-color: white; -fx-border-insets: -2px; -fx-pref-height: 60");
            label.setStyle("-fx-text-fill: black; -fx-font-size: 15px; -fx-translate-y: -40; -fx-translate-x: 40");
            entry.getChildren().addAll(icon, label);

            Platform.runLater(() -> {
                switch (extension.toLowerCase()) {
                    case "mp4": controller.videoVBox.getChildren().add(entry); break;
                    case "mp3": controller.musicVBox.getChildren().add(entry); break;
                    case "pdf":
                    case "docx": controller.docVBox.getChildren().add(entry); break;
                    default: controller.otherVBox.getChildren().add(entry);
                }
                System.out.println(controller.docVBox.getChildren().contains(entry));
            });
        }
    }

    // Helper method to get file extension from the file name
    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return (index > 0 && index < fileName.length() - 1) ? fileName.substring(index + 1) : "";
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
