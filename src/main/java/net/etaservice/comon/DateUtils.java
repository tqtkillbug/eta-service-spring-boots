package net.etaservice.comon;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String formatDate(Date date,String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static final String FORMAT_COMOM = "dd/MM/yyyy";
    public static final String FORMAT_DD_MM_YY = "dd_MM_yyyy";

}
