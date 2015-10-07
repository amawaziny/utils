/*
 * Copyright 2013 Ahmed El-mawaziny.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qfast.util;

import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ahmed El-mawaziny
 */
public final class Util {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss a";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DECIMAL_FORMAT_PATTERN = "###,###.##";
    public static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    public static final SimpleDateFormat sdtf = new SimpleDateFormat(DATE_TIME_FORMAT);
    public static final SimpleDateFormat stf = new SimpleDateFormat(TIME_FORMAT);
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    public static final String NUMERIC_PATTERN = "-?\\d+(\\.\\d+)?";
    public static final String INTEGER_PATTERN = "^[\\d]+$";
    public static final long ONE_KB = 1024;
    public static final BigDecimal ONE_KB_BD = BigDecimal.valueOf(ONE_KB);
    public static final BigDecimal ONE_MB_BD = ONE_KB_BD.multiply(ONE_KB_BD);
    public static final BigDecimal ONE_GB_BD = ONE_KB_BD.multiply(ONE_MB_BD);
    public static final BigDecimal ONE_TB_BD = ONE_KB_BD.multiply(ONE_GB_BD);
    public static final BigDecimal ONE_PB_BD = ONE_KB_BD.multiply(ONE_TB_BD);
    public static final BigDecimal ONE_EB_BD = ONE_KB_BD.multiply(ONE_PB_BD);
    public static final BigDecimal ONE_ZB_BD = ONE_KB_BD.multiply(ONE_EB_BD);
    public static final BigDecimal ONE_YB_BD = ONE_KB_BD.multiply(ONE_ZB_BD);
    private static final Logger LOG = Logger.getLogger(Util.class.getName());

    private Util() {
    }

    public static String toUtf8(String msg) {
        try {
            msg = new String(msg.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            msg = e.toString();
        }
        return msg;
    }

    public static String toISO(String msg) {
        try {
            msg = new String(msg.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            msg = e.toString();
        }
        return msg;
    }

    public static boolean isNULL(Object text) {
        if (text != null) {
            String strTest = text.toString().trim();
            if (!strTest.isEmpty() && !strTest.equalsIgnoreCase("null")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        } else if (value instanceof String) {
            return ((String) value).isEmpty();
        } else if (value instanceof Collection<?>) {
            return ((Collection<?>) value).isEmpty();
        } else if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).isEmpty();
        } else if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        } else {
            return value.toString() == null || value.toString().isEmpty();
        }
    }

    public static boolean isNULLZero(Object text) {
        if (text != null) {
            String strTest = text.toString().trim();
            if (!strTest.isEmpty() && !strTest.equalsIgnoreCase("null")
                    && !strTest.equalsIgnoreCase("0")
                    && !strTest.equalsIgnoreCase("0.0")) {
                return false;
            }
        }
        return true;
    }

    public static String getString(Object text) {
        if (!isNULL(text)) {
            return text.toString().trim();
        } else {
            return "";
        }
    }

    public static String getBooleanImage(Boolean bool) {
        if (!isNULL(bool) && bool) {
            return "( ✔ )";
        } else {
            return "(  )";
        }
    }

    public static Date parseDate(Object date) throws ParseException {
        if (!isNULL(date)) {
            return sdf.parse(date.toString());
        }
        return null;
    }

    public static String formatDate(Date date, String pattern, Locale locale) {
        if (!isNULL(date)) {
            return new SimpleDateFormat(pattern, locale).format(date);
        } else {
            return "";
        }
    }

    public static String formatDate(Date date, Locale locale) {
        if (!isNULL(date)) {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String formatDateTime(Date date, Locale locale) {
        if (!isNULL(date)) {
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
            return dateFormat.format(date);
        } else {
            return "";
        }
    }

    public static String formatDate(Date date) {
        if (!isNULL(date)) {
            return sdf.format(date);
        }
        return null;
    }

    public static String getHash(String plain) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(plain.getBytes(), 0, plain.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return plain;
        }
    }

    public static String removeDistortion(String hash) {
        return hash.substring(0, 5) + hash.substring(10, hash.length());
    }

    public static String getHash(String plain, String algorithm, boolean distortion) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(plain.getBytes("UTF-8"));
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            if (distortion)
                sb.insert(5, RandomStringUtils.random(5, true, true));
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            LOG.severe(ex.getLocalizedMessage());
            return plain;
        }
    }

    public static String getHash(String plain, String algorithm) {
        return getHash(plain, algorithm, false);
    }

    public static String getBundleMessage(String messageName) {
        return getBundleMessage(messageName, null);
    }

    public static String getBundleMessage(String messageName, Locale locale) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (locale != null) {
            return ResourceBundle.getBundle("/resources.Bundle", locale, contextClassLoader).getString(messageName);
        } else {
            return ResourceBundle.getBundle("/resources.Bundle", getDefaultLocale(), contextClassLoader).getString(messageName);
        }
    }

    public static Locale getDefaultLocale() {
        return new Locale("ar", "EG");
    }

    public static String getExtention(String fileName) {
        String ext = fileName;
        if (ext.lastIndexOf('.') != -1) {
            ext = ext.substring(ext.lastIndexOf('.'));
        } else {
            ext = "";
        }
        return ext;
    }

    public static boolean isNumeric(String str) {
        return !isNULL(str) && str.matches(NUMERIC_PATTERN);
    }

    public static boolean isInteger(String str) {
        return !isNULL(str) && str.matches(INTEGER_PATTERN);
    }

    public static int toIntVal(Object val) {
        if (!isNULL(val) && isNumeric(val.toString())) {
            return Integer.valueOf(val.toString());
        }
        return 0;
    }

    public static Integer toIntegerValue(Object val) {
        if (!isNULL(val) && isNumeric(val.toString())) {
            return Integer.valueOf(val.toString());
        }
        return null;
    }

    public static String byteCountToDisplaySize(BigDecimal size) {
        String displaySize;

        if (size.compareTo(ONE_YB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_YB_BD)) + " YB";
        } else if (size.compareTo(ONE_ZB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_ZB_BD)) + " ZB";
        } else if (size.compareTo(ONE_EB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_EB_BD)) + " EB";
        } else if (size.compareTo(ONE_PB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_PB_BD)) + " PB";
        } else if (size.compareTo(ONE_TB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_TB_BD)) + " TB";
        } else if (size.compareTo(ONE_GB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_GB_BD)) + " GB";
        } else if (size.compareTo(ONE_MB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_MB_BD)) + " MB";
        } else if (size.compareTo(ONE_KB_BD) > 0) {
            displaySize = DECIMAL_FORMAT.format(size.divide(ONE_KB_BD)) + " KB";
        } else {
            displaySize = DECIMAL_FORMAT.format(size) + " bytes";
        }

        return displaySize;
    }

    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(BigDecimal.valueOf(size));
    }

    public static String addComma(Collection<String> col) {
        if (col != null && col.size() > 0) {
            StringBuilder sb = new StringBuilder((col.size() * 2) - 1);
            int i = 0;
            for (String string : col) {
                sb.append(string);
                if (i < col.size() - 1) {
                    sb.append(",");
                }
                i++;
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    public static <T> Field getDeclaredField(T t, String fieldName) {
        try {
            Field f = t.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException | SecurityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Field getDeclaredField(Class t, String fieldName) {
        try {
            Field f = t.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException | SecurityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Date getTomorrow() {
        return getDate(1);
    }

    public static Date getYesterday() {
        return getDate(-1);
    }

    public static Date getDate(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static Date getDateYearsFirst(int years) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        c.add(Calendar.YEAR, years);
        return c.getTime();
    }

    public static int getLastDayInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, (month - 1), 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date getDateLastDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getDateFirstDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static String toUpperCaseFirstChar(String s) {
        String newS = s.replaceFirst(String.valueOf(s.charAt(0)), String.valueOf(Character.toUpperCase(s.charAt(0))));
        return newS;
    }

    public static <T> T setData(Class<T> c, MultivaluedMap<String, String> formValues)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException, ParseException {

        T instance = c.newInstance();
        Class<? extends Object> aClass = instance.getClass();
        Field[] fields = aClass.getFields();
        for (Field field : fields) {
            Method method = aClass.getMethod("set" + toUpperCaseFirstChar(field.getName()), field.getType());
            method.invoke(instance, getValue(formValues.getFirst(field.getName()), field.getType()));
        }
        return instance;
    }

    private static Object getValue(String value, Object type) throws ParseException {
        Object val = null;
        if (type instanceof Boolean) {
            val = !isNULL(value) && !value.equals("0");
        } else if (type instanceof Date) {
            if (!isNULL(value)) {
                val = sdtf.parse(value);
            }
        } else if (type instanceof BigDecimal) {
            if (!isNULL(value)) {
                val = new BigDecimal(value);
            }
        } else if (type instanceof BigInteger) {
            if (!isNULL(value)) {
                val = new BigInteger(value);
            }
        }
        return val;
    }

    public static void writeBase64(String base64Str, String filePath) throws FileNotFoundException, IOException {
        byte[] btDataFile = Base64.decodeBase64(base64Str);
        File of = new File(filePath);
        FileOutputStream osf = new FileOutputStream(of);
        osf.write(btDataFile);
        osf.flush();
    }

    public static byte[] getImage(String imgSrc) {
        if (imgSrc.contains("data:image")) {
            imgSrc = imgSrc.substring(imgSrc.indexOf(',') + ",".length(), imgSrc.length());
        }
        return Base64.decodeBase64(imgSrc);
    }

    public static String editorSafeMode(String html) {
        if (!isNULL(html)) {
            html = html.replace("<input", "&lt;input");
            html = html.replace("<form", "&lt;form");
            html = html.replace("<select", "&lt;select");
            html = html.replace("<option", "&lt;option");
            html = html.replace("<optgroup", "&lt;optgroup");
            html = html.replace("<script", "&lt;script");
            html = html.replace("</script", "&lt;/script");
            html = html.replace("<button", "&lt;button");
            html = html.replace("</button", "&lt;/button");
            return html;
        } else {
            return null;
        }
    }

    public static String getQueryFile(String filePath) throws FileNotFoundException {
        String sql = "";
        File f = new File(filePath);
        Scanner scanner = new Scanner(f);
        while (scanner.hasNext()) {
            sql += scanner.nextLine() + "\n";
        }
        return sql;
    }

    public static NationalID generateNationalID() throws ParseException {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyMMdd");
        DecimalFormat df = new DecimalFormat("00");

        int centuryDigit = 2;
        int year = (0 + (int) (Math.random() * ((99 - 0) + 1))) + 1900;
        int mon = 1 + (int) (Math.random() * ((99 - 1) + 1));
        int day = 1 + (int) (Math.random() * ((99 - 1) + 1));
        int governorateCode = 99;
        String birthSerial = df.format(0 + (int) (Math.random() * ((99 - 0) + 1)));
        int fm = 2 + (int) (Math.random() * ((3 - 2) + 1));
        String last = df.format(1 + (int) (Math.random() * ((99 - 1) + 1)));

        if (year >= 2000) {
            centuryDigit = 3;
        }

        return new NationalID(centuryDigit + "" +
                (sdf2.format(sdf.parse(year + "-" + mon + "-" + day))) + "" +
                governorateCode + "" + birthSerial + "" + fm + "" + last);
    }

    public static Integer[] addArrays(Integer[] a, Integer[] b) {
        Integer[] c = new Integer[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static Map<String, String> getFragmentAsMap(String fragment,
                                                       String splitToken) {
        if (!isNULL(fragment) && !isNULL(splitToken)) {
            String[] fragmentArr = fragment.split(splitToken);
            Map<String, String> map
                    = new HashMap<>(fragmentArr.length);
            for (String entry : fragmentArr) {
                String[] entryArr = entry.split("=");
                map.put(entryArr[0], entryArr.length == 2 ? entryArr[1] : null);
            }
            return map;
        }
        return Collections.<String, String>emptyMap();
    }

    public static Locale getLocale(String locale) {
        if (!isNULL(locale)) {
            String[] split;
            if (locale.contains("_")) {
                split = locale.split("_");
            } else if (locale.contains("@")) {
                split = locale.split("@");
            } else {
                split = locale.split(locale.charAt(2) + "");
            }

            return new Locale(split[0], split[1]);
        }
        return null;
    }

    public static <T> boolean containsAny(Collection<T> list1, Collection<T> list2) {
        for (T l2 : list2) {
            if (list1.contains(l2)) {
                return true;
            }
        }
        return false;
    }

    public static double convertByteToMega(long bytes) {
        return bytes / 1024 / 1024;
    }

    public static boolean getBoolean(Integer i) {
        return !isNULL(i) && i > 0;
    }

    public static boolean getBoolean(Double i) {
        return getBoolean(i.intValue());
    }

    public static String lpad(String s, int n) {
        if (isNULL(s)) {
            s = "";
        }
        return String.format("%1$" + n + "s", s);
    }

    public static String rpad(String s, int n) {
        if (isNULL(s)) {
            s = "";
        }
        return String.format("%1$-" + n + "s", s);
    }

    public static String lpadTrunk(String s, int n) {
        if (isNULL(s)) {
            s = "";
        }
        s = String.format("%1$" + n + "s", s);
        if (s.length() > n) {
            s = s.substring(0, n);
        }
        return s;
    }

    public static String rpadTrunk(String s, int n) {
        if (isNULL(s)) {
            s = "";
        }
        s = String.format("%1$-" + n + "s", s);
        if (s.length() > n) {
            s = s.substring(0, n);
        }
        return s;
    }

    public static String encodeUrl(String s) {
        try {
            s = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return s;
    }
}
