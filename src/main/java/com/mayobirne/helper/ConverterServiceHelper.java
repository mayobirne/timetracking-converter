package com.mayobirne.helper;

import com.mayobirne.dto.CellStylesDTO;
import com.mayobirne.dto.TimesDTO;
import com.mayobirne.enums.CellNumbers;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Created by Christian on 18.03.2016.
 */
public class ConverterServiceHelper {

    public static XSSFRow generateNewRow(XSSFSheet sheet, Integer rowNr, TimesDTO timesDTO, CellStylesDTO cellstyles) {
        XSSFRow newRow = sheet.getRow(rowNr) != null ? sheet.getRow(rowNr) : sheet.createRow(rowNr);

        XSSFCell dateCell = newRow.getCell(CellNumbers.DATE_CELL) != null ? newRow.getCell(CellNumbers.DATE_CELL)
                : newRow.createCell(CellNumbers.DATE_CELL);
        dateCell.setCellStyle(cellstyles.getDateCellStyle());
        dateCell.setCellValue(timesDTO.getDate());

        XSSFCell startTimeCell = newRow.getCell(CellNumbers.START_TIME_CELL) != null ? newRow.getCell(CellNumbers.START_TIME_CELL)
                : newRow.createCell(CellNumbers.START_TIME_CELL);
        startTimeCell.setCellStyle(cellstyles.getStartTimeCellStyle());
        startTimeCell.setCellValue(HSSFDateUtil.convertTime(timesDTO.getStartTime()));

        XSSFCell endTimeCell = newRow.getCell(CellNumbers.END_TIME_CELL) != null ? newRow.getCell(CellNumbers.END_TIME_CELL)
                : newRow.createCell(CellNumbers.END_TIME_CELL);
        endTimeCell.setCellStyle(cellstyles.getEndTimeCellStyle());
        endTimeCell.setCellValue(HSSFDateUtil.convertTime(timesDTO.getEndTime()));

        XSSFCell projectNrCell = newRow.getCell(CellNumbers.PROJECT_NR_CELL) != null ? newRow.getCell(CellNumbers.PROJECT_NR_CELL)
                : newRow.createCell(CellNumbers.PROJECT_NR_CELL);
        projectNrCell.setCellStyle(cellstyles.getProjectNrCellStyle());
        projectNrCell.setCellValue(timesDTO.getProjectNr());

        XSSFCell subNrCell = newRow.getCell(CellNumbers.SUB_NR_CELL) != null ? newRow.getCell(CellNumbers.SUB_NR_CELL)
                : newRow.createCell(CellNumbers.SUB_NR_CELL);
        subNrCell.setCellStyle(cellstyles.getSubNrCellStyle());
        subNrCell.setCellValue(timesDTO.getSubNr());

        XSSFCell descriptionCell = newRow.getCell(CellNumbers.DESCRIPTION_CELL) != null ? newRow.getCell(CellNumbers.DESCRIPTION_CELL)
                : newRow.createCell(CellNumbers.DESCRIPTION_CELL);
        descriptionCell.setCellStyle(cellstyles.getDescriptionCellStyle());
        descriptionCell.setCellValue(timesDTO.getDescription());
        return newRow;
    }

    public static CellStylesDTO createCellStylesDTOFromRow(XSSFRow row) {
        CellStyle dateCellStyle = row.getCell(CellNumbers.DATE_CELL).getCellStyle();
        CellStyle startTimeCellStyle = row.getCell(CellNumbers.START_TIME_CELL).getCellStyle();
        CellStyle endTimeCellStyle = row.getCell(CellNumbers.END_TIME_CELL).getCellStyle();
        CellStyle projectNrCellStyle = row.getCell(CellNumbers.PROJECT_NR_CELL).getCellStyle();
        CellStyle subNrCellStyle = row.getCell(CellNumbers.SUB_NR_CELL).getCellStyle();
        CellStyle descriptionCellStyle = row.getCell(CellNumbers.DESCRIPTION_CELL).getCellStyle();

        return new CellStylesDTO(dateCellStyle, startTimeCellStyle, endTimeCellStyle, projectNrCellStyle, subNrCellStyle, descriptionCellStyle);
    }
}
