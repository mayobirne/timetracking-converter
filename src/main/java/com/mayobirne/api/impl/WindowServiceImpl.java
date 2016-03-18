package com.mayobirne.api.impl;

import com.mayobirne.api.WindowService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Christian on 18.03.2016.
 */
public class WindowServiceImpl implements WindowService {

    private static Logger LOGGER = LoggerFactory.getLogger(WindowServiceImpl.class);

    @Override
    public void generateWarningForNoEndtimeField(int rowNumber) {
        Button button = new Button("OK");
        button.setCancelButton(true);

        Text text = new Text("No Endtime set for row " + rowNumber + ".");
        text.setFont(Font.font(15));

        VBox vBox = new VBox();
        vBox.getChildren().add(text);
        vBox.getChildren().add(button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(25));
        vBox.setSpacing(15);

        final Stage dialogStage = new Stage();
        dialogStage.setTitle("Warning");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(vBox));
        dialogStage.show();

        button.setOnAction(event -> {
            LOGGER.info("Closing Notification Window.");
            dialogStage.close();
        });
    }

    @Override
    public void generateErrorWindowInvalidFilelayout() {
        Button button = new Button("OK");
        button.setCancelButton(true);

        Text text = new Text("Invalid Format in your Excel-File. Refer to the Tutorial if you need help.");
        text.setFont(Font.font(15));

        VBox vBox = new VBox();
        vBox.getChildren().add(text);
        vBox.getChildren().add(button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(25));
        vBox.setSpacing(15);

        final Stage dialogStage = new Stage();
        dialogStage.setTitle("Error");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(vBox));
        dialogStage.show();

        button.setOnAction(event -> {
            LOGGER.info("Closing Error (Invalid File Layout) Window.");
            dialogStage.close();
        });
    }
}
