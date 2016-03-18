package com.mayobirne.controller;

import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;

/**
 * Created by christian on 03.03.16.
 */
public class MainController {

    private static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

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

        File f = new File("src/main/resources/styles/styles.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

        primaryStage.setTitle("Timetracking Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
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
