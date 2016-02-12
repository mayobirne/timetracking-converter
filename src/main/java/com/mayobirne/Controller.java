package com.mayobirne;

import com.mayobirne.dto.InterflexDTO;
import com.mayobirne.dto.TimesDTO;
import com.mayobirne.enums.CategoryNumbers;
import com.mayobirne.enums.CellNumbers;
import com.mayobirne.enums.Months;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Christian on 07.02.2016.
 */
public class Controller {

    private static Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private static final String TEMPLATE = System.getProperty("user.dir") + "/src/main/resources/excel/template.xlsx";
    //private static final String TEMPLATE = System.getProperty("user.dir") + "\\src\\main\\resources\\excel\\template.xlsx";

    private static final Integer PVA_PROJECT_NR = 862355;

    private Stage stage;
    private HostServices hostServices;

    private File outputFile;

    private List<InterflexDTO> interflexList;

    @FXML
    private ChoiceBox<Months> monthChoiceBox;

    @FXML
    private TextField yearTextField;

    @FXML
    private void initialize() {
        monthChoiceBox.getItems().setAll(Months.values());
    }

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

    @FXML
    protected void convertToTimes() throws IOException, URISyntaxException {
        generateNewSheet();
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
                if (row.getCell(2).getCellType() != Cell.CELL_TYPE_BLANK &&
                        !row.getCell(1).getStringCellValue().equals("Feiertag")) {

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

        CellStyle dateCellStyle = row.getCell(CellNumbers.DATE_CELL).getCellStyle();
        CellStyle startTimeCellStyle = row.getCell(CellNumbers.START_TIME_CELL).getCellStyle();
        CellStyle endTimeCellStyle = row.getCell(CellNumbers.END_TIME_CELL).getCellStyle();
        CellStyle projectNrCellStyle = row.getCell(CellNumbers.PROJECT_NR_CELL).getCellStyle();
        CellStyle subNrCellStyle = row.getCell(CellNumbers.SUB_NR_CELL).getCellStyle();
        CellStyle descriptionCellStyle = row.getCell(CellNumbers.DESCRIPTION_CELL).getCellStyle();

        Months monthChosen = monthChoiceBox.getValue();

        for (int i = 0; i < interflexList.size(); i++) {

            int rowNr = i + 1;
            XSSFRow newRow = sheet.getRow(rowNr) != null ? sheet.getRow(rowNr) : sheet.createRow(rowNr);

            TimesDTO timesDTO = interflexToTimesConverter(interflexList.get(i), monthChosen);

            LOGGER.info("Bei Row {}", i);

            XSSFCell dateCell = newRow.getCell(CellNumbers.DATE_CELL) != null ? newRow.getCell(CellNumbers.DATE_CELL)
                    : newRow.createCell(CellNumbers.DATE_CELL);
            dateCell.setCellStyle(dateCellStyle);
            dateCell.setCellValue(new GregorianCalendar(2016, 4, rowNr));

            XSSFCell startTimeCell = newRow.getCell(CellNumbers.START_TIME_CELL) != null ? newRow.getCell(CellNumbers.START_TIME_CELL)
                    : newRow.createCell(CellNumbers.START_TIME_CELL);
            startTimeCell.setCellStyle(startTimeCellStyle);
            startTimeCell.setCellValue(HSSFDateUtil.convertTime("12:24:00"));

            XSSFCell endTimeCell = newRow.getCell(CellNumbers.END_TIME_CELL) != null ? newRow.getCell(CellNumbers.END_TIME_CELL)
                    : newRow.createCell(CellNumbers.END_TIME_CELL);
            endTimeCell.setCellStyle(endTimeCellStyle);
            endTimeCell.setCellValue(HSSFDateUtil.convertTime("13:24:00"));

            XSSFCell projectNrCell = newRow.getCell(CellNumbers.PROJECT_NR_CELL) != null ? newRow.getCell(CellNumbers.PROJECT_NR_CELL)
                    : newRow.createCell(CellNumbers.PROJECT_NR_CELL);
            projectNrCell.setCellStyle(projectNrCellStyle);
            projectNrCell.setCellValue(862355);

            XSSFCell subNrCell = newRow.getCell(CellNumbers.SUB_NR_CELL) != null ? newRow.getCell(CellNumbers.SUB_NR_CELL)
                    : newRow.createCell(CellNumbers.SUB_NR_CELL);
            subNrCell.setCellStyle(subNrCellStyle);
            subNrCell.setCellValue(2);

            XSSFCell descriptionCell = newRow.getCell(CellNumbers.DESCRIPTION_CELL) != null ? newRow.getCell(CellNumbers.DESCRIPTION_CELL)
                    : newRow.createCell(CellNumbers.DESCRIPTION_CELL);
            descriptionCell.setCellStyle(descriptionCellStyle);
            descriptionCell.setCellValue("descr");
        }

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();

        LOGGER.info("Finished generating new Excel-File, trying to open: {}", fileName);
        hostServices.showDocument(fileName);
    }

    private TimesDTO interflexToTimesConverter(InterflexDTO interflexDTO, Months monthChosen) {

        TimesDTO timesDTO = new TimesDTO();

        Integer day = Integer.parseInt(interflexDTO.getDay_WD_DD().substring(3, 5));
        Calendar date = new GregorianCalendar(Integer.valueOf(yearTextField.getText()), monthChosen.getNumber(), day);

        timesDTO.setDate(date);
        timesDTO.setStartTime(interflexDTO.getStartTime());
        timesDTO.setEndTime(interflexDTO.getEndTime());
        timesDTO.setProjectNr(PVA_PROJECT_NR);
        timesDTO.setSubNr(CategoryNumbers.SOFTWARE_DEVELOPMENT.getNumber());
        timesDTO.setDescription("TestDescription");

        return timesDTO;
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
