package com.mayobirne;/**
 * Created by Christian on 06.02.2016.
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainapp.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setHostService(getHostServices());
        controller.setStage(primaryStage);

        Scene scene = new Scene(root, 500, 450);
        scene.getStylesheets().add("styles/styles.css");

        primaryStage.setTitle("Timetracking Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
