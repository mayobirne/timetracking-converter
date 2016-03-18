package com.mayobirne.controller;

import com.mayobirne.api.ConverterService;
import com.mayobirne.api.impl.ConverterServiceImpl;
import com.mayobirne.dto.InterflexDTO;
import com.mayobirne.enums.Months;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang.Validate;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ConverterController {

    private static Logger LOGGER = LoggerFactory.getLogger(ConverterController.class);

    private ConverterService converterService;

    private Stage stage;
    private Path tempPath;
    private HostServices hostServices;

    private List<InterflexDTO> interflexList;

    @FXML
    private ChoiceBox<Months> monthChoiceBox;

    @FXML
    private TextField yearTextField;

    @FXML
    private void initialize() {
        converterService = new ConverterServiceImpl();

        monthChoiceBox.getItems().setAll(Months.values());
        Calendar calendar = Calendar.getInstance();

        for (Months month : Months.values()) {
            if (month.getNumber() == calendar.get(Calendar.MONTH))
                monthChoiceBox.setValue(month);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setHostService(HostServices hostService) {
        this.hostServices = hostService;
    }

    public void setTempPath (Path tempPath) {
        this.tempPath = tempPath;
    }

    @FXML
    protected void openInterflexFile() throws IOException, InvalidFormatException, URISyntaxException {
        FileChooser fileChooser = new FileChooser();
        File inputFile = fileChooser.showOpenDialog(stage);

        if (inputFile != null) {
            interflexList = converterService.generateInterflexListFromInputFile(inputFile);
        }
    }

    @FXML
    protected void convertToTimes() throws IOException, URISyntaxException {
        generateNewSheet();
    }

    private void generateNewSheet() throws IOException, URISyntaxException {

        URL templateFile = this.getClass().getClassLoader().getResource("excel/template.xlsx");
        Validate.notNull(templateFile);

        String fileName = tempPath.toString() + "/temp" + Long.toString(System.nanoTime()) + ".xlsx";
        Files.copy(Paths.get(templateFile.toURI()), Paths.get(fileName), StandardCopyOption.COPY_ATTRIBUTES);

        XSSFWorkbook workbook = converterService.generateNewSheet(monthChoiceBox.getValue(), yearTextField.getText(), fileName, interflexList);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();

        LOGGER.info("Finished generating new Excel-File, trying to open: {}", fileName);
        hostServices.showDocument(fileName);
    }
}
