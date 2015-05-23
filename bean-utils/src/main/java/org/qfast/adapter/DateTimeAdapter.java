/*
 * Copyright 2014 Ahmed El-mawaziny.
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
package org.qfast.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Ahmed El-mawaziny
 */
public class DateTimeAdapter extends XmlAdapter<String, Date> {

    private final SimpleDateFormat dateFormat 
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public String marshal(Date date) throws Exception {
        if (date != null) {
            return dateFormat.format(date);
        } else {
            return null;
        }
    }

    @Override
    public Date unmarshal(String dateStr) throws Exception {
        if (dateStr != null && !dateStr.isEmpty() && !dateStr.trim().isEmpty()) {
            return dateFormat.parse(dateStr);
        } else {
            return null;
        }
    }
}
