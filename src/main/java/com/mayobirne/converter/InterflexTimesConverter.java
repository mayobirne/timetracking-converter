package com.mayobirne.converter;

import com.mayobirne.api.ConverterService;
import com.mayobirne.dto.InterflexDTO;
import com.mayobirne.dto.TimesDTO;
import com.mayobirne.enums.CategoryNumbers;
import com.mayobirne.enums.Months;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Christian on 18.03.2016.
 */
public class InterflexTimesConverter {

    public static TimesDTO convert(InterflexDTO interflexDTO, Months monthChosen, String year) {
        TimesDTO timesDTO = new TimesDTO();

        Integer day = Integer.parseInt(interflexDTO.getDay_WD_DD().substring(3, 5));
        Calendar date = new GregorianCalendar(Integer.valueOf(year), monthChosen.getNumber(), day);

        timesDTO.setDate(date);
        timesDTO.setStartTime(convertToTimeString(interflexDTO.getStartTime()));
        timesDTO.setEndTime(convertToTimeString(interflexDTO.getEndTime()));
        timesDTO.setProjectNr(ConverterService.PVA_PROJECT_NR);
        timesDTO.setSubNr(CategoryNumbers.SOFTWARE_DEVELOPMENT.getNumber());
        timesDTO.setDescription("description");

        return timesDTO;
    }

    private static String convertToTimeString(Date date) {

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
