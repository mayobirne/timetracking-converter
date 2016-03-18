package com.mayobirne.api;

import com.mayobirne.dto.InterflexDTO;
import com.mayobirne.enums.Months;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by christian on 03.03.16.
 */
public interface ConverterService {

    Integer PVA_PROJECT_NR = 862355;
    Integer SIX_HOURS_IN_MILLISECONDS = 6 * 60 * 60 * 1000;
    Integer ONE_HOUR_IN_MILLISECONDS = 60 * 60 * 1000;

    List<InterflexDTO> generateInterflexListFromInputFile(File file) throws IOException;

    XSSFWorkbook generateNewSheet(Months monthChosen, String year, String fileName, List<InterflexDTO> interflexList) throws IOException, URISyntaxException;
}
