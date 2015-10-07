/*
 * Copyright 2014 freeOut Ahmed El-mawaziny.
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

/**
 * @author Ahmed El-mawaziny
 */
public class Test {

    public static void main(String[] args) {
//        System.out.println(Util.getFragmentAsMap("menu_id=149", "&")); 
//        Calendar now = Calendar.getInstance();
//        Map<String, Integer> displayNames = now.getDisplayNames(Calendar.DAY_OF_WEEK,
//            Calendar.SHORT, new Locale("ar", "EG"));
//        System.out.println(displayNames);
//        System.out.println(Util.rpadTrunk("dopay", 1));
//        System.out.println(Arrays.toString(getBytes("عربي Test")));

        /*DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(new Locale("en","US"));
        df.setParseBigDecimal(true);
        ParsePosition pos = new ParsePosition(0);

        String value = "111,110,000,118.";
        System.out.println((BigDecimal)df.parse(value,pos));*/

        System.out.println(Util.getHash("Test1234", "SHA-256"));
        String hash = Util.getHash("Test1234", "SHA-256", true);
        System.out.println(hash);
        System.out.println(Util.removeDistortion(hash));
    }
//    
//    private static byte[] getBytes(String message) {
//        char[] messageCharArr = message.toCharArray();
//        byte[] bytes = new byte[]{};
//        for (int i = 0; i < messageCharArr.length; i++) {
//            char c = messageCharArr[i];
//            if (isLatinLetter(c)) {
//                bytes = ArrayUtils.addAll(bytes, (c + "").getBytes());
//            } else {
//                bytes = ArrayUtils.addAll(bytes, (c + "").getBytes(UTF_16BE));
//            }
//        }
//        return bytes;
//    }
//
//    public static boolean isLatinLetter(char c) {
//        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
//    }
}
