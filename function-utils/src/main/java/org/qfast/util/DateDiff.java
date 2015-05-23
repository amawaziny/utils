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

import java.util.Date;

/**
 * @author Ahmed El-mawaziny
 */
public final class DateDiff {

    private final long diffMilliSeconds;
    private static final int MILL_SEC = 1000;
    private static final int SEC = 60;
    private static final int MIN = 60;
    private static final int HOUR = 24;

    public DateDiff(Date fromDate, Date toDate) {
        diffMilliSeconds = toDate.getTime() - fromDate.getTime();
    }

    public long getDays() {
        return diffMilliSeconds / (HOUR * MIN * SEC * MILL_SEC);
    }

    public long getHours() {
        return diffMilliSeconds / (MIN * SEC * MILL_SEC);
    }

    public long getMinutes() {
        return diffMilliSeconds / (SEC * MILL_SEC);
    }

    public long getSeconds() {
        return diffMilliSeconds / MILL_SEC;
    }

    public long getMilliSeconds() {
        return diffMilliSeconds;
    }
}
