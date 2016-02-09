package com.mayobirne;

import com.mayobirne.dto.InterflexDTO;
import com.mayobirne.dto.TimesDTO;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

/**
 * Created by Christian on 07.02.2016.
 */
public class Controller {

    private static Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private static final String TEMPLATE = System.getProperty("user.dir") + "\\src\\main\\resources\\excel\\template.xlsx";

    private Stage stage;
    private HostServices hostServices;

    private File outputFile;

    private List<InterflexDTO> interflexList;

    @FXML
    private TextField monthTextField;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setHostService(HostServices hostService) {
        this.hostServices = hostService;
    }

    @FXML
    protected void openInterflexFile() throws IOException, InvalidFormatException, URISyntaxException {
        FileChooser fileChooser = new FileChooser();
        outputFile = fileChooser.showOpenDialog(stage);
        saveInterflexData();
    }

    private void saveInterflexData() throws IOException, InvalidFormatException, URISyntaxException {

        interflexList = new ArrayList<InterflexDTO>();

        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(outputFile));
        XSSFSheet sheet = wb.getSheetAt(0);

        int rows = sheet.getPhysicalNumberOfRows();
        int cols = 0;
        int tmp;

        for(int i = 0; i < 10 || i < rows; i++) {
            XSSFRow row = sheet.getRow(i);
            if(row != null) {
                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                if(tmp > cols) cols = tmp;
            }
        }

        String lastDay = "";

        for (int i = 1; i < rows; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                if (row.getCell(2).getCellType() != Cell.CELL_TYPE_BLANK) {

                    InterflexDTO dto = new InterflexDTO();
                    String day = row.getCell(0).getStringCellValue();

                    dto.setDay_WD_DD(day == null || day.isEmpty() ? lastDay : row.getCell(0).getStringCellValue());
                    dto.setStartTime(convertToTimeString(row.getCell(2).getDateCellValue()));
                    dto.setEndTime(convertToTimeString(row.getCell(3).getDateCellValue()));
                    interflexList.add(dto);

                    lastDay = dto.getDay_WD_DD();
                }
            }
        }
        LOGGER.info("Finished loading Data from Interflex-Excel File.");

        generateNewSheet();
    }

    private void generateNewSheet() throws IOException, URISyntaxException {

        String fileName = generateNewFileName();
        Files.copy(Paths.get(TEMPLATE), Paths.get(fileName), StandardCopyOption.COPY_ATTRIBUTES);

        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(fileName));
        XSSFSheet sheet = wb.getSheetAt(0);

        XSSFRow row = sheet.getRow(1);

        XSSFCell dateCell = row.getCell(0);
        CellStyle dateCellStlye = dateCell.getCellStyle();

        XSSFCell startTimeCell = row.getCell(1);
        CellStyle startTimeCellStlye = startTimeCell.getCellStyle();

        XSSFCell endTimeCell = row.getCell(2);
        CellStyle endTimeCellStlye = endTimeCell.getCellStyle();

        XSSFCell projectNrCell = row.getCell(3);
        CellStyle projectNrCellStlye = projectNrCell.getCellStyle();

        XSSFCell subNrCell = row.getCell(4);
        CellStyle subNrCellStlye = subNrCell.getCellStyle();

        XSSFCell descriptionCell = row.getCell(5);
        CellStyle descriptionCellStlye = startTimeCell.getCellStyle();

        dateCell.setCellValue(new GregorianCalendar(2015, 4, 17));

        startTimeCell.setCellValue(HSSFDateUtil.convertTime("12:24:00"));
        endTimeCell.setCellValue(HSSFDateUtil.convertTime("15:12:00"));

        projectNrCell.setCellValue(862355);

        subNrCell.setCellValue(2);

        descriptionCell.setCellValue("testDescr");




//        Calendar calendar = new GregorianCalendar(2015, 4, 17);
//        cell.setCellValue(calendar);

//        XSSFRow newRow = sheet.createRow(2);
//        XSSFCell newCell = newRow.createCell(0);
//        newCell.setCellValue(calendar);
//        newCell.setCellStyle(cell.getCellStyle());


        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();

        LOGGER.info("Finished generating new Excel-File, trying to open: {}", fileName);
        hostServices.showDocument(fileName);
    }

    private void generateNewRow(TimesDTO timesDTO, int rowNumber, XSSFSheet sheet) {
        XSSFWorkbook workbook = sheet.getWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();

        XSSFRow row = sheet.createRow(rowNumber);

        XSSFCell dateCell = row.createCell(0, Cell.CELL_TYPE_STRING);
//        dateCell.setCellValue(timesDTO.getDate());
        dateCell.setCellValue(creationHelper.createDataFormat().getFormat("02-Feb-2016"));
//        dateCell.setCellValue("09.02.2016");
//        CellStyle style = workbook.createCellStyle();
//        style.setDataFormat(creationHelper.createDataFormat().getFormat("mm.dd.yy"));
//        dateCell.setCellStyle(style);

        XSSFCell startTimeCell = row.createCell(1, Cell.CELL_TYPE_STRING);
//        startTimeCell.setCellValue(timesDTO.getStartTime());
        startTimeCell.setCellValue("10:02:00");
        CellStyle style2 = workbook.createCellStyle();
        style2.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm"));
        startTimeCell.setCellStyle(style2);

        XSSFCell endTimeCell = row.createCell(2, Cell.CELL_TYPE_STRING);
//        endTimeCell.setCellValue(timesDTO.getEndTime());
        endTimeCell.setCellValue("10:15:00");
        CellStyle style3 = workbook.createCellStyle();
        style3.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm"));
        endTimeCell.setCellStyle(style3);

        XSSFCell projectNrCell = row.createCell(3, Cell.CELL_TYPE_NUMERIC);
        projectNrCell.setCellValue(timesDTO.getProjectNr());

        XSSFCell subNrCell = row.createCell(4, Cell.CELL_TYPE_NUMERIC);
        subNrCell.setCellValue(timesDTO.getSubNr());

        XSSFCell descriptionCell = row.createCell(5, Cell.CELL_TYPE_STRING);
        descriptionCell.setCellValue(timesDTO.getDescription());
    }

    private TimesDTO interflexToTimesConverter(InterflexDTO interflexDTO, String month) {

        TimesDTO timesDTO = new TimesDTO();

        timesDTO.setDate(interflexDTO.getDay_WD_DD()); // TODO
        timesDTO.setStartTime(interflexDTO.getStartTime());
        timesDTO.setEndTime(interflexDTO.getEndTime());
        timesDTO.setProjectNr("862355");
        timesDTO.setSubNr("2");
        timesDTO.setDescription("TestDescription");

        return timesDTO;
    }

    private void autoSizeCells(XSSFSheet sheet) {
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
    }

    private String generateNewFileName() {
        String path = System.getProperty("user.dir");
        return path + "/src/main/resources/tmp/temp" + Long.toString(System.nanoTime()) + ".xlsx";
//        return path + "\\src\\main\\resources\\tmp\\temp" + Long.toString(System.nanoTime()) + ".xlsx";
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
