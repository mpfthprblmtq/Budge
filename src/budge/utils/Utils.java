/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package budge.utils;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author pat
 */
public class Utils {

    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");
    private final static SimpleDateFormat VIEW_DATE_FORMAT = new SimpleDateFormat("EEE MM/dd/yy");
    private final static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    
    /**
     * Helper Function that lists and stores all of the files in a directory and
     * subdirectories
     *
     * @param directory
     * @param files
     * @return
     */
    public static ArrayList<File> listFiles(File directory, ArrayList<File> files) {

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listFiles(file, files);     // this file is a directory, recursively call itself
            }
        }
        return files;
    }

    /**
     * Formats the date in the format given by the bank
     */
    public static Date formatDate(String dateStr) {
        if(dateStr.equals(StringUtils.EMPTY)) {
            return null;
        }

        try {
            return SIMPLE_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            System.err.println("Couldn't parse date: " + dateStr);
            return null;
        }
    }

    public static String formatDate(Date date) {
        return date != null ? VIEW_DATE_FORMAT.format(date) : StringUtils.EMPTY;
    }

    public static String formatDateSimple(Date date) {
        return date != null ? SIMPLE_DATE_FORMAT.format(date) : StringUtils.EMPTY;
    }

    public static String getCurrentTimestampForFileName() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals(StringUtils.EMPTY);
    }

    /**
     * Formats a double to currency format
     * Life has many doors edd boy
     * @param d
     * @return
     */
    public static String formatDoubleForCurrency(Double d) {
        return d != null ? CURRENCY_FORMAT.format(d) : StringUtils.EMPTY;
    }

    public static String formatDouble(Double d) {
        return d != null ? String.format("%.2f", d) : StringUtils.EMPTY;
    }
    
    /**
     * Converts string to boolean
     * @param s the string to parse
     * @return the result of the conversion
     */
    public static boolean toBoolean(String s) {
        return s.equalsIgnoreCase("true");
    }

}
