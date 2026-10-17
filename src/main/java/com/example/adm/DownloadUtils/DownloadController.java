package com.example.adm.DownloadUtils;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.adm.PageManager;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class DownloadController {

    @FXML private TextField urlTextField;  // Input field for URL
    @FXML private VBox downloadingVBox;    // VBox for showing download progress
    @FXML private VBox finishedVBox;       // VBox for finished downloads
    @FXML private VBox queueVBox;
    public static int MaxSimDownload = 1;
    AtomicInteger totalDownloading = new AtomicInteger(0);
    QueueUtils queue = new QueueUtils();
    public String file;
    @FXML private ScrollPane DownloadingScrollPane;
    private HashMap<Future<?>, DownloadTaskWrapper> map = new HashMap<>();
    Queue<String> q1 = new LinkedList<>();
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(MaxSimDownload, 10, 8, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    // This method is called when the "Start Download" button is clicked
    @FXML
    public void startDownload() {
        String urlString = urlTextField.getText().trim();
        urlTextField.clear();
        if (urlString.isEmpty()) {
            System.out.println("Invalid URL");
            showAlert("Error", "Please enter a valid URL.");
            return;
        }

        System.out.println("Starting download: " + urlString);

        Task<Void> downloadTask = new Task<>() {
            private String fileName;
            private String filePath;
            private final Object pauseLock = new Object();
            private volatile boolean isPaused = false;

            @Override
            protected Void call() throws Exception {
                try {
                    totalDownloading.incrementAndGet();
                    String userHome = System.getProperty("user.home");
                    String downloadsFolder = userHome + File.separator + "Downloads";

                    File outputDir = new File(downloadsFolder);
                    if (!outputDir.exists()) outputDir.mkdirs();

                    URI uri = new URI(urlString);
                    URL url = uri.toURL();
                    URLConnection connection = url.openConnection();
                    connection.connect();

                    fileName = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
                    if (fileName.isEmpty()) fileName = "downloaded_file";
                    if (!fileName.contains(".")) fileName += ".pdf";
                    filePath = downloadsFolder + File.separator + fileName;
                    File outputFile = new File(filePath);

                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalBytesRead = 0;
                    long contentLength = connection.getContentLengthLong();

                    ProgressBar progressBar = new ProgressBar(0);
                    progressBar.setTranslateY(-30);
                    progressBar.setTranslateX(20);
                    //progressBar.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    //progressBar.setMaxWidth(Double.MAX_VALUE);
                    progressBar.setStyle("-fx-pref-height: 20px");

                    ImageView icon = new ImageView();
                    icon.setFitHeight(40);
                    icon.setFitWidth(40);
                    String fileExtension = getFileExtension(fileName);

                    try {
                        if ("mp3".equalsIgnoreCase(fileExtension)) {
                            icon.setImage(new Image(getClass().getResourceAsStream("/icon/mp3_icon.png")));
                        } else if ("gif".equalsIgnoreCase(fileExtension)) {
                            icon.setImage(new Image(getClass().getResourceAsStream("/icon/gif_icon.png")));
                        } else if ("pdf".equalsIgnoreCase(fileExtension)) {
                            icon.setImage(new Image(getClass().getResourceAsStream("/icon/pdf_icon.png")));
                        } else if ("mp4".equalsIgnoreCase(fileExtension)) {
                            icon.setImage(new Image(getClass().getResourceAsStream("/icon/mp4_icon.png")));
                        } else {
                            icon.setImage(new Image(getClass().getResourceAsStream("/icon/default.png")));
                        }
                    } catch (Exception e) {
                        System.out.println("Error loading icon: " + e.getMessage());
                    }
                    icon.setTranslateY(10);
                    Image pause = new Image(getClass().getResourceAsStream("/icon/pause.png"));
                    Image resume = new Image(getClass().getResourceAsStream("/icon/resume.png"));
                    ImageView Cancel = new ImageView();
                    Cancel.setImage(new Image(getClass().getResourceAsStream("/icon/cancel.png")));
                    ImageView current = new ImageView();
                    current.setImage(pause);
                    current.setFitHeight(25);
                    current.setFitWidth(25);
                    Cancel.setFitWidth(25);
                    Cancel.setFitHeight(25);
                    Button pause_resume = new Button("", current);
                    pause_resume.setId("PauseResume");
                    pause_resume.setStyle("-fx-background-color: white; -fx-shape: circle");

                    Button cancel = new Button("",Cancel);
                    cancel.setStyle("-fx-background-color: white; -fx-shape: circle; -fx-border-color: red");
                    pause_resume.setTranslateX(395);
                    pause_resume.setTranslateY(30);
                    cancel.setTranslateX(820);
                    cancel.setTranslateY(-45);

                    Label fileLabel = new Label(fileName);
                    fileLabel.setTranslateY(22);

                    HBox fileHBox = new HBox(20);
                    fileHBox.getChildren().addAll(icon, fileLabel);
                    VBox fileBox = new VBox();
                    fileBox.getChildren().addAll(fileHBox);
                    HBox pr = new HBox();
                    pr.getChildren().addAll(pause_resume, cancel);
                    Label speedLabel= new Label("0 MB/s");
                    Label sizeLabel = new Label(String.format("0 MB/%.2f MB" , contentLength /(1024.0 * 1024)));

                    DropShadow shadow = new DropShadow();
                    shadow.setOffsetX(3);
                    shadow.setOffsetY(3);
                    shadow.setRadius(5);
                    shadow.setColor(Color.GRAY);
                    fileBox.setEffect(shadow);
                    fileBox.setStyle("-fx-text-fill: white; -fx-border-style: solid; -fx-border-color:  #3597e1; -fx-border-width: 2px;  -fx-background-color: white; -fx-border-insets: -2px; -fx-pref-height: 125");
                    fileLabel.setStyle("-fx-text-fill: black; -fx-font-size: 15px");
                    //cancel.setStyle("-fx-background-color: #cd6262; -fx-text-fill: white; -fx-border-color: #cd6262; -fx-border-radius: 10px;");
                    fileBox.getChildren().addAll(speedLabel, sizeLabel);
                    speedLabel.setTranslateY(65);
                    speedLabel.setTranslateX(20);
                    sizeLabel.setTranslateX(300);
                    sizeLabel.setTranslateY(50);
                    pr.setTranslateY(-30);
                    pr.setTranslateX(40);
                    Platform.runLater(() -> {
                        downloadingVBox.getChildren().add(fileBox);
                        fileBox.getChildren().add(pr);
                        //progressBar.setMaxWidth(Double.MAX_VALUE);
                        fileBox.getChildren().add(progressBar);
                    });
                    pause_resume.setOnMouseEntered(event -> {
                        Glow glow = new Glow(0.2);
                        pause_resume.setEffect(glow);
                    });
                    pause_resume.setOnMouseExited(event -> {
                        pause_resume.setEffect(null);
                    });
                    //pause_resume.setOnAction(event -> isPaused = true);
                    pause_resume.setOnAction(event -> {
                        if(current.getImage() == pause){
                            current.setImage(resume);
                            speedLabel.setText("0 MB/s");
                            isPaused = true;
                        }
                        else{
                            synchronized (pauseLock) {
                                isPaused = false;
                                //pause_resume.setText("Pause");
                                current.setImage(pause);
                                pauseLock.notifyAll();
                            }
                        }
                    });
                    cancel.setOnMouseEntered(event -> {
                        Glow glow = new Glow(0.2);
                        cancel.setEffect(glow);
                    });
                    cancel.setOnMouseExited(event->{
                        cancel.setStyle("-fx-background-color: white; -fx-shape: circle; -fx-border-color: red");
                        cancel.setEffect(null);
                    });
                    cancel.setOnAction(event -> {
                        downloadingVBox.getChildren().remove(fileBox);
                        synchronized (this) {
                            this.cancel();
                            showAlert("", "Download Cancelled!");
                        }
                    });
                    java.time.LocalTime t1 = java.time.LocalTime.now();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        synchronized (pauseLock) {
                            while (isPaused) {
                                pauseLock.wait();
                            }
                        }
                        synchronized (this) {
                            if (this.isCancelled()) {
                                fileOutputStream.close();
                                outputFile.delete();
                                return null;
                            }
                        }
                        fileOutputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        if (contentLength > 0) {
                            double progress = (double) totalBytesRead / contentLength;
                            updateProgress(totalBytesRead, contentLength);
                            java.time.LocalTime t2 = java.time.LocalTime.now();

                            double speed = Double.isInfinite(totalBytesRead/ (Duration.between(t1, t2).toSeconds() * 1.0 * 1024 * 1024)) ? 0 :  totalBytesRead/ (Duration.between(t1, t2).toSeconds() * 1.0 * 1024 * 1024);
                            //if(Double.isInfinite(speed)) speed = 0;
                            double readSize = totalBytesRead /( 1024.0 * 1024);
                            Platform.runLater(() -> {
                                speedLabel.setText(String.format("%.2f MB/s", speed));
                                sizeLabel.setText(String.format("%.2f MB/%.2f MB", readSize, contentLength /( 1024 * 1024.0)));
                                progressBar.setProgress(progress);
                                progressBar.layout();
                            });
                        }
                    }

                    inputStream.close();
                    fileOutputStream.close();

                    Platform.runLater(() -> {
                        if (!this.isCancelled()) {
                            // Remove the fileBox from downloadingVBox
                            downloadingVBox.getChildren().remove(fileBox);
                            //if(fileBox.getChildren().contains(fileBox)) return;
                            // Remove the buttons (pause, resume, cancel) and progress bar
                            fileBox.getChildren().removeAll(progressBar, pause_resume, cancel);

                            fileBox.setStyle("-fx-pref-height: 60");
                            // Add only file name and icon to finishedVBox, excluding buttons
                            HBox finishedFileBox = new HBox(20); // Create HBox for finished file info
                            finishedFileBox.getChildren().addAll(icon, new Label(fileName)); // Add icon and file name
                            finishedVBox.getChildren().add(finishedFileBox); // Add to finishedVBox

                            // Optional: Style the finished download (if desired)
                            finishedFileBox.setStyle("-fx-text-fill: black; -fx-border-style: solid; -fx-border-color: #3597e1; -fx-border-width: 2px; -fx-background-color: white; -fx-border-insets: -2px; -fx-pref-height: 100");

                            updateMessage("Download Complete: " + fileName);
                            saveDownloadPath(filePath);
                        } else {
                            // If cancelled, remove the fileBox from downloadingVBox (and ensure it's not added to finishedVBox)
                            downloadingVBox.getChildren().remove(fileBox);
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert("Error", "Download failed: " + e.getMessage()));
                }
                return null;
            }

            @Override
            public String toString() {
                return filePath != null ? filePath : "pending";
            }
        };

        // Wrap Task
        DownloadTaskWrapper wrapper = new DownloadTaskWrapper(downloadTask, urlString);

        // Add to queue and update queue tab
        if (totalDownloading.get() >= MaxSimDownload) q1.add(urlString);
        queue.show(q1, queueVBox);

        Future<?> future = executor.submit(wrapper);
        map.put(future, wrapper);

        // Remove from queue/map when complete
        downloadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                totalDownloading.decrementAndGet();
                q1.remove(urlString);
                map.remove(future);
                queue.show(q1, queueVBox);
            });
        });
        downloadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                q1.remove(urlString);
                map.remove(future);
                queue.show(q1, queueVBox);
            });
        });
        downloadTask.setOnCancelled(e -> {
            Platform.runLater(() -> {
                q1.remove(urlString);
                map.remove(future);
                queue.show(q1, queueVBox);
            });
        });
    }

    //private double calculateSpeed()
    private void saveDownloadPath(String filePath) {
        try {
            String userHome = System.getProperty("user.home");
            File historyFile = new File(userHome + File.separator + "downloads.txt");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile, true))) {
                writer.write(filePath);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Error", "Failed to save file path: " + e.getMessage()));
        }
    }

    // Helper method to get file extension from file name
    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return (index > 0 && index < fileName.length() - 1) ? fileName.substring(index + 1) : "";
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleSettings(ActionEvent event) throws Exception {
        PageManager.settings.setVisible(true);
        PageManager.home.setVisible(false);
        PageManager.files.setVisible(false);
    }

    @FXML public void handleFiles(ActionEvent event) {
        PageManager pg = new PageManager();
        PageManager.settings.setVisible(false);
        PageManager.home.setVisible(false);
        PageManager.files.setVisible(true);
        pg.loadDownloadedFiles();
    }
}
