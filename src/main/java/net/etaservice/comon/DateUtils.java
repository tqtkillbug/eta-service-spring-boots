package net.etaservice.comon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static String formatDate(Date date,String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static String formatDateForTask(String dateStr){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM-HH:mm");
        outputFormat.setTimeZone(TimeZone.getDefault());

        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        String outputDate = outputFormat.format(date);
        return outputDate;
    }

    public static String initDueTask(int numDate, int hour) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate futureDate = now.toLocalDate().plusDays(numDate);
        LocalDateTime futureDateTime = futureDate.atTime(hour, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return futureDateTime.format(formatter);
    }

    public static final String FORMAT_COMOM = "dd/MM/yyyy";
    public static final String FORMAT_DD_MM_YY = "dd_MM_yyyy";

}
