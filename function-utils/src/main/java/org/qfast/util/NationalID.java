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

import static org.qfast.util.Util.getBundleMessage;
import static org.qfast.util.Util.isNULL;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NationalID {

    private final String nationalId;
    private static final int FixedLength = 14;

    public NationalID(String nationalId) {
        if (isNULL(nationalId)) {
            System.out.println("NationalIdIsNull");
            throw new IllegalArgumentException(getBundleMessage("NationalIdIsNull"));
        }
        if (nationalId.length() != FixedLength) {
            System.out.println("NationalIdLength");
            throw new IllegalArgumentException(String.format(getBundleMessage("NationalIdLength"), nationalId));
        }
        if (!Util.isInteger(nationalId)) {            
            System.out.println("NationalidContainsCharcters");
            throw new IllegalArgumentException(getBundleMessage("NationalidContainsCharcters"));
        }
        this.nationalId = nationalId;
    }

    public NationalID(BigInteger nationalId) {
        this(String.valueOf(nationalId));
    }

    public NationalID(Long nationalId) {
        this(String.valueOf(nationalId));
    }

    public Date getBirthDate() {
        Date birthDate = null;
        char centuryDigit = nationalId.charAt(0);

        if (centuryDigit == '2' || centuryDigit == '3') {

            int day = Integer.parseInt(nationalId.substring(5, 7));
            if (!(day >= 1 && day <= 31)) {
                throw new IllegalArgumentException(getBundleMessage("BarthDayError"));
            }

            int month = Integer.parseInt(nationalId.substring(3, 5));
            if (!(month >= 1 && month <= 12)) {
                throw new IllegalArgumentException(getBundleMessage("BarthMonthError"));
            }

            int year = Integer.parseInt(nationalId.substring(1, 3));
            year += 1900 + (100 * (Integer.parseInt(centuryDigit + "") - 2));
            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
                throw new IllegalArgumentException(getBundleMessage("BarthYearError"));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                birthDate = sdf.parse(year + "-" + month + "-" + day);
            } catch (ParseException e) {
                try {
                    System.out.println(e.getMessage());
                    throw e;
                } catch (ParseException ex) {
                    Logger.getLogger(NationalID.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return birthDate;
    }

    public String getBirthGovernorateCode() {
        return nationalId.substring(7, 9);
    }

    public String getBirthSerial() {
        return nationalId.substring(9, 13);
    }

    public boolean isMale() {
        return Integer.parseInt(nationalId.substring(12, 13)) % 2 == 1;
    }

    public boolean isFemale() {
        return Integer.parseInt(nationalId.substring(12, 13)) % 2 == 0;
    }

    public boolean isLegal() {
        return (!isMale() && !isFemale()) || getBirthDate() == null;
    }

    public boolean isValid() {
        if (!isLegal()) {
            return getBirthGovernorateCode() != null && getBirthSerial() != null;
        } else {
            System.out.println("Legal");
            throw new IllegalArgumentException(getBundleMessage("LegalTransFered"));
        }
    }

    @Override
    public String toString() {
        return nationalId;
    }
}
