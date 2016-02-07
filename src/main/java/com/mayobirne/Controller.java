package com.mayobirne;

import com.mayobirne.dto.InterflexDTO;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Christian on 07.02.2016.
 */
public class Controller {

    private Stage stage;
    private HostServices hostServices;

    private File outputFile;

    private List<InterflexDTO> interflexList;

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

        interflexList = new ArrayList<InterflexDTO>();

        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(outputFile));
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;

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
                if (row.getCell(2).getCellType() != Cell.CELL_TYPE_BLANK) {
                    InterflexDTO dto = new InterflexDTO();
                    dto.setDay_WD_DD(row.getCell(0).getStringCellValue());
                    dto.setStartTime(convertToTimeString(row.getCell(2).getDateCellValue()));
                    dto.setEndTime(convertToTimeString(row.getCell(3).getDateCellValue()));
                    interflexList.add(dto);
                }
            }
        }
        System.out.println("ende");
    }

    private String convertToTimeString(Date date) {

        if (date == null) {
            return "";
        }

        String hour = String.valueOf(date.getHours());
        if (hour.length() == 1)  {
            hour = "0" + hour;
        }

        String minute = String.valueOf(date.getMinutes());
        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        return hour + ":" + minute + ":00";
    }
}
