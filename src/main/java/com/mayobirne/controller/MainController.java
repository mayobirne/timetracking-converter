package com.mayobirne.controller;

import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by christian on 03.03.16.
 */
public class MainController {

    private Stage primaryStage;
    private HostServices hostServices;
    private Path tempPath;

    public void openConverter() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/converter.fxml"));
        Parent root = loader.load();

        ConverterController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setHostService(hostServices);
        controller.setTempPath(tempPath);

        Scene scene = new Scene(root, 500, 450);

        primaryStage.setTitle("Timetracking Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void setTempPath(Path tempPath) {
        this.tempPath = tempPath;
    }
}
