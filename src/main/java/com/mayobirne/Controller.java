package com.mayobirne;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Christian on 07.02.2016.
 */
public class Controller {

    private Stage stage;
    private HostServices hostServices;

    private File outputFile;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setHostService(HostServices hostService) {
        this.hostServices = hostService;
    }

    @FXML
    protected void openFile() throws IOException, InvalidFormatException {
        FileChooser fileChooser = new FileChooser();
        outputFile = fileChooser.showOpenDialog(stage);
        test();
    }

    private void test() throws IOException, InvalidFormatException {
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(outputFile));
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;
        XSSFCell cell;

        int rows = sheet.getPhysicalNumberOfRows();
        int cols = 0;
        int tmp;

        for(int i = 0; i < 10 || i < rows; i++) {
            row = sheet.getRow(i);
            if(row != null) {
                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if(tmp > cols) cols = tmp;
            }
        }

        for (int i = 0; i < rows; i++) {
            row = sheet.getRow(i);
            if (row != null) {
                for (int c = 0; c < cols; c++) {
                    cell = row.getCell(c);
                    if (cell != null) {
                        System.out.println(cell.getCachedFormulaResultType());
                    }
                }
            }
        }
    }
}
