package net.etaservice.comon;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {


    /*Get all url from text*/
    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public static String extractUrlsFirst(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }
        if (containedUrls.size() > 0){
            return containedUrls.get(0);
        }
        return  null;
    }

    public static int  getNumberFromAString(String str){
        String[] parts = str.replaceAll("[^0-9]+", " ").split(" ");
        String numbers = parts[1];
        return Integer.parseInt(numbers);
    }

    public static boolean isNumberic(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static String formatCuurencyVnd(String amount) {
        DecimalFormat formatter = new DecimalFormat("#,### đ");
        return formatter.format(Double.parseDouble(amount));
    }



    public static double calculateFromString(String expression) {
        char[] tokens = expression.toCharArray();

        // Kiểm tra chuỗi có chứa toán tử hay không
        boolean containsOperator = false;
        for (char token : tokens) {
            if (isOperator(token)) {
                containsOperator = true;
                break;
            }
        }

        // Nếu chuỗi không chứa toán tử, chuyển đổi thành số và trả về
        if (!containsOperator) {
            return Double.parseDouble(expression);
        }

        // Tìm vị trí của toán tử ưu tiên cao nhất
        int index = findHighestPrecedenceOperatorIndex(tokens);

        // Thực hiện tính toán trên 2 toán hạng
        double leftOperand = calculateFromString(new String(tokens, 0, index));
        double rightOperand = calculateFromString(new String(tokens, index + 1, tokens.length - index - 1));
        char operator = tokens[index];
        double result = performOperation(leftOperand, rightOperand, operator);

        return result;
    }

    // Hàm kiểm tra toán tử
    public static boolean isOperator(char token) {
        return token == '+' || token == '-' || token == '*' || token == '/';
    }

    // Hàm tìm vị trí của toán tử ưu tiên cao nhất
    public static int findHighestPrecedenceOperatorIndex(char[] tokens) {
        int index = -1;
        int currentPrecedence = -1;
        int parenthesesCount = 0;

        for (int i = 0; i < tokens.length; i++) {
            char token = tokens[i];

            if (token == '(') {
                parenthesesCount++;
            } else if (token == ')') {
                parenthesesCount--;
            } else if (isOperator(token)) {
                int precedence = getPrecedence(token, parenthesesCount);
                if (precedence > currentPrecedence) {
                    index = i;
                    currentPrecedence = precedence;
                }
            }
        }

        return index;
    }

    // Hàm lấy độ ưu tiên của toán tử
    public static int getPrecedence(char operator, int parenthesesCount) {
        if (operator == '+' || operator == '-') {
            return parenthesesCount * 2 + 1;
        } else {
            return parenthesesCount * 2 + 2;
        }
    }

    // Hàm thực hiện phép tính trên 2 toán hạng
    public static double performOperation(double leftOperand, double rightOperand, char operator) {
        if (operator == '+') {
            return leftOperand + rightOperand;
        } else if (operator == '-') {
            return leftOperand - rightOperand;
        } else if (operator == '*') {
            return leftOperand * rightOperand;
        } else if (operator == '/') {
            return leftOperand / rightOperand;
        } else {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }



}
