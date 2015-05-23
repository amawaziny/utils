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
package org.qfast.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.jsp.jstl.sql.Result;

/**
 * @author Ahmed El-mawaziny 
 */
public class ReportResult {

    private final String[] columnNames;
    private final List<Object> rows;

    public ReportResult(Result result) {
        columnNames = result.getColumnNames();
        Object[][] rowsByIndx = result.getRowsByIndex();
        rows = new ArrayList<Object>(10);
        for (Object[] rowByIndx : rowsByIndx) {
            for (int j = 0; j < rowByIndx.length; j++) {
                if (rowByIndx[j] instanceof Boolean) {
                    if ((Boolean) rowByIndx[j]) {
                        rowByIndx[j] = "نعم";
                    } else {
                        rowByIndx[j] = "لا";
                    }
                }
            }
        }
        rows.addAll(Arrays.asList(rowsByIndx));
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public List<Object> getRows() {
        return rows;
    }
}
