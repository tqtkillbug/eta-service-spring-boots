package net.etaservice.comon.googlesheet;

import com.google.api.services.sheets.v4.model.ValueRange;
import net.etaservice.comon.StringUtils;

import java.util.List;

public class SheetUtils {


    public static String getCellValue(ValueRange response, String range, String cellName) {
        List<List<Object>> valueRange = response.getValues();
        String rangeFirst = range.split(":")[0];
        String rangeSecond = range.split(":")[1];
        int indexColum = cellName.charAt(0) - rangeFirst.charAt(0);
        int indexRow = StringUtils.getNumberFromAString(cellName) - StringUtils.getNumberFromAString(rangeFirst);

        List<Object> rowValue = valueRange.get(indexRow);
        Object valueCell = rowValue.get(indexColum);
        return valueCell.toString();
    }




}
