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
        if (valueRange == null || valueRange.isEmpty() || valueRange.size() - 1 < indexRow) return "";
        List<Object> rowValue = valueRange.get(indexRow);
        if (rowValue == null || rowValue.isEmpty() || rowValue.size() - 1 < indexColum) return "";
        Object valueCell = rowValue.get(indexColum);
        return valueCell.toString();
    }

    public static int  getIndexRowByCell(String cellName){
        int indexRow = StringUtils.getNumberFromAString(cellName) - StringUtils.getNumberFromAString("A1");
        return indexRow;
    }

    public static int  getIndexColumnByCell(String cellName){
        int indexColum = cellName.charAt(0) - 'A';
         return  indexColum;
    }



}
