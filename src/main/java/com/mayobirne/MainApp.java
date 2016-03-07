package com.mayobirne;

import com.mayobirne.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        MainController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        controller.setHostServices(getHostServices());

        Scene scene = new Scene(root, 500, 450);

        File f = new File("src/main/resources/styles/styles.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

        primaryStage.setTitle("Timetracking-Helper");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
