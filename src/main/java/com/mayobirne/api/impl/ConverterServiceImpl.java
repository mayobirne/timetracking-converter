package com.mayobirne.api.impl;

import com.mayobirne.api.ConverterService;
import com.mayobirne.api.WindowService;
import com.mayobirne.converter.InterflexTimesConverter;
import com.mayobirne.dto.CellStylesDTO;
import com.mayobirne.dto.InterflexDTO;
import com.mayobirne.dto.TimesDTO;
import com.mayobirne.enums.Months;
import com.mayobirne.helper.ConverterServiceHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by christian on 03.03.16.
 */
public class ConverterServiceImpl implements ConverterService {

    private static Logger LOGGER = LoggerFactory.getLogger(ConverterServiceImpl.class);

    private WindowService windowService;

    public ConverterServiceImpl() {
        windowService = new WindowServiceImpl();
    }

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

        try {
            int startNummer = 0;
            if (sheet.getRow(0).getCell(0).getStringCellValue().equals("Datum")) {
                startNummer = 1;
            }
            for (int i = startNummer; i < rows; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    if (!validateRow(row)) {
                        continue;
                    }
                    if (row.getCell(2).getCellType() != Cell.CELL_TYPE_BLANK &&
                            !row.getCell(1).getStringCellValue().equals("Feiertag")) {

                        InterflexDTO dto = new InterflexDTO();
                        String day = row.getCell(0).getStringCellValue();

                        dto.setDay_WD_DD(day == null || day.isEmpty() ? lastDay : row.getCell(0).getStringCellValue());

                        Date startTime = row.getCell(2).getDateCellValue();
                        Date endTime = row.getCell(3).getDateCellValue();

                        if (endTime != null) {

                            Calendar newStartTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                            newStartTime.setTime(startTime);

                            Calendar newEndTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                            newEndTime.setTime(endTime);

                            if (interflexList.size() > 0) {
                                InterflexDTO lastDto = interflexList.get(interflexList.size() - 1);
                                if (lastDto.getDay_WD_DD().equals(dto.getDay_WD_DD())) {

                                    Calendar lastEndTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
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

                                Calendar secondStartTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                                secondStartTime.setTimeInMillis(newEndTime.getTimeInMillis() + ONE_HOUR_IN_MILLISECONDS);
                                secondInterflexDTO.setStartTime(secondStartTime.getTime());

                                Calendar secondEndTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                                secondEndTime.setTimeInMillis(secondStartTime.getTimeInMillis() + timeToAdd);
                                secondInterflexDTO.setEndTime(secondEndTime.getTime());

                                interflexList.add(secondInterflexDTO);

                            } else {
                                dto.setStartTime(newStartTime.getTime());
                                dto.setEndTime(newEndTime.getTime());
                                interflexList.add(dto);
                            }
                        } else {
                            windowService.generateWarningForNoEndtimeField(i);
                            LOGGER.info("No EndTime set for row {}.", i);
                        }
                        lastDay = dto.getDay_WD_DD();
                    }
                }
            }
            LOGGER.info("Finished loading Data from Interflex-Excel File. Added {} to InterflexList.", interflexList.size());
        } catch (IllegalArgumentException ex) {
            windowService.generateErrorWindowInvalidFilelayout();
            LOGGER.error("Invalid Format for inputFile");
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
            LOGGER.error("Found Invalid Format at row: {}", row.getRowNum());
            throw new IllegalArgumentException("Found Invalid Format at row: " + row.getRowNum());
        }
        return true;
    }

    @Override
    public XSSFWorkbook generateNewSheet(final Months monthChosen, final String year, final
                    String fileName, List<InterflexDTO> interflexList) throws IOException, URISyntaxException {

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
        XSSFSheet sheet = workbook.getSheetAt(0);

        XSSFRow row = sheet.getRow(1);
        CellStylesDTO cellStylesDTO = ConverterServiceHelper.createCellStylesDTOFromRow(row);

        for (int i = 0; i < interflexList.size(); i++) {
            TimesDTO timesDTO = InterflexTimesConverter.convert(interflexList.get(i), monthChosen, year);
            ConverterServiceHelper.generateNewRow(sheet, i + 1, timesDTO, cellStylesDTO);
        }

        return workbook;
    }
}
