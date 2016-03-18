package com.mayobirne;

import com.mayobirne.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainapp.fxml"));
        Parent root = loader.load();

        Path tempPath = Files.createTempDirectory("timetrackingPrefix");
        deleteTempFolderOnShutdown(tempPath);

        MainController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        controller.setHostServices(getHostServices());
        controller.setTempPath(tempPath);

        Scene scene = new Scene(root, 500, 450);

        File f = new File("src/main/resources/styles/styles.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

        primaryStage.setTitle("Timetracking-Helper");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deleteTempFolderOnShutdown(Path path) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                                throws IOException {
                            Files.deleteIfExists(dir);
                            return super.postVisitDirectory(dir, exc);
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.deleteIfExists(file);
                            return super.visitFile(file, attrs);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
