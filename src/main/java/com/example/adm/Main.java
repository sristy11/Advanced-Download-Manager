package com.example.adm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Stack;

import static com.example.adm.PageManager.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // load fxml
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/com/example/adm/download_manager.fxml"));
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/com/example/adm/Settings.fxml"));
            FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/com/example/adm/files.fxml"));
            home = loader1.load();
            settings = loader2.load();
            files = loader3.load();
            PageManager.controller = loader3.getController();
            System.out.println(controller.docVBox);
            StackPane root = new StackPane(home, settings, files);

            Scene scene = new Scene(root);
            home.setVisible(true);
            settings.setVisible(false);
            files.setVisible(false);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/adm/style.css")).toExternalForm());
            primaryStage.getIcons().add(new Image(getClass().getResource("/icon/app.png").toExternalForm()));
            primaryStage.setTitle("Advanced Download Manager");
            primaryStage.setScene(scene);
            primaryStage.setWidth(1000);
            primaryStage.setHeight(800);
            primaryStage.show();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
