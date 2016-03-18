package com.mayobirne.api;

/**
 * Created by Christian on 18.03.2016.
 */
public interface WindowService {

    void generateWarningForNoEndtimeField(int rowNumber);

    void generateErrorWindowInvalidFilelayout();

}
