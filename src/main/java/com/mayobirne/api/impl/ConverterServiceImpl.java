package com.mayobirne.api.impl;

import com.mayobirne.api.ConverterService;
import com.mayobirne.dto.InterflexDTO;
import com.mayobirne.dto.TimesDTO;
import com.mayobirne.enums.CategoryNumbers;
import com.mayobirne.enums.CellNumbers;
import com.mayobirne.enums.Months;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
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
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by christian on 03.03.16.
 */
public class ConverterServiceImpl implements ConverterService {

    private static Logger LOGGER = LoggerFactory.getLogger(ConverterServiceImpl.class);

    @Override
    public List<InterflexDTO> generateInterflexListFromInputFile(File inputFile) throws IOException {

        List<InterflexDTO> interflexList = new ArrayList<>();

        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inputFile));
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
        int rowNumber = 0;

        try {
            int startNummer = 0;
            if (sheet.getRow(0).getCell(0).getStringCellValue().equals("Datum")) {
                startNummer = 1;
            }
            for (int i = startNummer; i < rows; i++) {
                rowNumber = i;
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    if (!validateRow(row)) {
                        throw new IllegalArgumentException();
                    }
                    if (row.getCell(2).getCellType() != Cell.CELL_TYPE_BLANK &&
                            !row.getCell(1).getStringCellValue().equals("Feiertag")) {

                        InterflexDTO dto = new InterflexDTO();
                        String day = row.getCell(0).getStringCellValue();

                        dto.setDay_WD_DD(day == null || day.isEmpty() ? lastDay : row.getCell(0).getStringCellValue());

                        Date startTime = row.getCell(2).getDateCellValue();
                        Date endTime = row.getCell(3).getDateCellValue();

                        if (endTime != null) {

                            Calendar newStartTime = Calendar.getInstance();
                            newStartTime.setTime(startTime);

                            Calendar newEndTime = Calendar.getInstance();
                            newEndTime.setTime(endTime);

                            if (interflexList.size() > 0) {
                                InterflexDTO lastDto = interflexList.get(interflexList.size() - 1);
                                if (lastDto.getDay_WD_DD().equals(dto.getDay_WD_DD())) {

                                    Calendar lastEndTime = Calendar.getInstance();
                                    lastEndTime.setTime(lastDto.getEndTime());
                                    lastEndTime.add(Calendar.MINUTE, 30);

                                    if (lastEndTime.after(newStartTime)) {
                                        newStartTime.add(Calendar.HOUR, 1);
                                        newEndTime.add(Calendar.HOUR, 1);
                                    }
                                }
                            }

                            Long diff = newEndTime.getTimeInMillis() - newStartTime.getTimeInMillis();

                            if (diff > SIX_HOURS_IN_MILLISECONDS) {
                                LOGGER.info("Found more than 6h at ID: {}.", interflexList.size());
                                Long timeToAdd = diff - SIX_HOURS_IN_MILLISECONDS;

                                newEndTime.setTimeInMillis(newStartTime.getTimeInMillis() + SIX_HOURS_IN_MILLISECONDS);
                                dto.setStartTime(newStartTime.getTime());
                                dto.setEndTime(newEndTime.getTime());

                                interflexList.add(dto);

                                InterflexDTO secondInterflexDTO = new InterflexDTO();
                                secondInterflexDTO.setDay_WD_DD(dto.getDay_WD_DD());

                                Calendar secondStartTime = Calendar.getInstance();
                                secondStartTime.setTimeInMillis(newEndTime.getTimeInMillis() + ONE_HOUR_IN_MILLISECONDS);
                                secondInterflexDTO.setStartTime(secondStartTime.getTime());

                                Calendar secondEndTime = Calendar.getInstance();
                                secondEndTime.setTimeInMillis(secondStartTime.getTimeInMillis() + timeToAdd);
                                secondInterflexDTO.setEndTime(secondEndTime.getTime());

                                interflexList.add(secondInterflexDTO);

                            } else {
                                dto.setStartTime(newStartTime.getTime());
                                dto.setEndTime(newEndTime.getTime());
                                interflexList.add(dto);
                            }
                        } else {
                            generateWarningForNoEndtimeField(i);
                            LOGGER.info("No EndTime set for row {}.", i);
                        }
                        lastDay = dto.getDay_WD_DD();
                    }
                }
            }
            LOGGER.info("Finished loading Data from Interflex-Excel File. Added {} to InterflexList.", interflexList.size());
        } catch (IllegalStateException ex) {
            generateWarningForNoEndtimeField(rowNumber);
            LOGGER.error("Invalid Format for inputFile");
        } catch (IllegalArgumentException ex) {
            generateWarningForNoEndtimeField(rowNumber);
            LOGGER.error("Invalid Format for Row ", rowNumber);
        }

        return interflexList;
    }

    private boolean validateRow (XSSFRow row) {
        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_BLANK ||
                row.getCell(1).getStringCellValue().equals("Feiertag")) {
            LOGGER.info("Empty or 'Feiertag' row found at nr: " + row.getRowNum());
            return true;
        }
        try {
            row.getCell(0).getStringCellValue();
            row.getCell(2).getDateCellValue();
            row.getCell(3).getDateCellValue();
        } catch (RuntimeException e) {
            LOGGER.error("Found Invalid Format at row: " + row.getRowNum());
            return false;
        }
        return true;
    }

    private void generateWarningForNoEndtimeField(int rowNumber) {
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
    public XSSFWorkbook generateNewSheet(final Months monthChosen, final String year, final
                    String fileName, List<InterflexDTO> interflexList) throws IOException, URISyntaxException {

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFRow row = sheet.getRow(1);

        CellStyle dateCellStyle = row.getCell(CellNumbers.DATE_CELL).getCellStyle();
        CellStyle startTimeCellStyle = row.getCell(CellNumbers.START_TIME_CELL).getCellStyle();
        CellStyle endTimeCellStyle = row.getCell(CellNumbers.END_TIME_CELL).getCellStyle();
        CellStyle projectNrCellStyle = row.getCell(CellNumbers.PROJECT_NR_CELL).getCellStyle();
        CellStyle subNrCellStyle = row.getCell(CellNumbers.SUB_NR_CELL).getCellStyle();
        CellStyle descriptionCellStyle = row.getCell(CellNumbers.DESCRIPTION_CELL).getCellStyle();

        for (int i = 0; i < interflexList.size(); i++) {

            int rowNr = i + 1;
            XSSFRow newRow = sheet.getRow(rowNr) != null ? sheet.getRow(rowNr) : sheet.createRow(rowNr);

            TimesDTO timesDTO = interflexToTimesConverter(interflexList.get(i), monthChosen, year);

            LOGGER.info("Bei Row {}", i);

            XSSFCell dateCell = newRow.getCell(CellNumbers.DATE_CELL) != null ? newRow.getCell(CellNumbers.DATE_CELL)
                    : newRow.createCell(CellNumbers.DATE_CELL);
            dateCell.setCellStyle(dateCellStyle);
            dateCell.setCellValue(timesDTO.getDate());

            XSSFCell startTimeCell = newRow.getCell(CellNumbers.START_TIME_CELL) != null ? newRow.getCell(CellNumbers.START_TIME_CELL)
                    : newRow.createCell(CellNumbers.START_TIME_CELL);
            startTimeCell.setCellStyle(startTimeCellStyle);
            startTimeCell.setCellValue(HSSFDateUtil.convertTime(timesDTO.getStartTime()));

            XSSFCell endTimeCell = newRow.getCell(CellNumbers.END_TIME_CELL) != null ? newRow.getCell(CellNumbers.END_TIME_CELL)
                    : newRow.createCell(CellNumbers.END_TIME_CELL);
            endTimeCell.setCellStyle(endTimeCellStyle);
            endTimeCell.setCellValue(HSSFDateUtil.convertTime(timesDTO.getEndTime()));

            XSSFCell projectNrCell = newRow.getCell(CellNumbers.PROJECT_NR_CELL) != null ? newRow.getCell(CellNumbers.PROJECT_NR_CELL)
                    : newRow.createCell(CellNumbers.PROJECT_NR_CELL);
            projectNrCell.setCellStyle(projectNrCellStyle);
            projectNrCell.setCellValue(timesDTO.getProjectNr());

            XSSFCell subNrCell = newRow.getCell(CellNumbers.SUB_NR_CELL) != null ? newRow.getCell(CellNumbers.SUB_NR_CELL)
                    : newRow.createCell(CellNumbers.SUB_NR_CELL);
            subNrCell.setCellStyle(subNrCellStyle);
            subNrCell.setCellValue(timesDTO.getSubNr());

            XSSFCell descriptionCell = newRow.getCell(CellNumbers.DESCRIPTION_CELL) != null ? newRow.getCell(CellNumbers.DESCRIPTION_CELL)
                    : newRow.createCell(CellNumbers.DESCRIPTION_CELL);
            descriptionCell.setCellStyle(descriptionCellStyle);
            descriptionCell.setCellValue(timesDTO.getDescription());
        }

        return workbook;
    }

    private TimesDTO interflexToTimesConverter(InterflexDTO interflexDTO, Months monthChosen, String year) {

        TimesDTO timesDTO = new TimesDTO();

        Integer day = Integer.parseInt(interflexDTO.getDay_WD_DD().substring(3, 5));
        Calendar date = new GregorianCalendar(Integer.valueOf(year), monthChosen.getNumber(), day);

        timesDTO.setDate(date);
        timesDTO.setStartTime(convertToTimeString(interflexDTO.getStartTime()));
        timesDTO.setEndTime(convertToTimeString(interflexDTO.getEndTime()));
        timesDTO.setProjectNr(PVA_PROJECT_NR);
        timesDTO.setSubNr(CategoryNumbers.SOFTWARE_DEVELOPMENT.getNumber());
        timesDTO.setDescription("TestDescription");

        return timesDTO;
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
