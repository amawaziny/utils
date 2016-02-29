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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ExcelUtils {

    public static ArrayList<HSSFRow> getRows(InputStream inputStream) throws IOException {
        ArrayList<HSSFRow> listHSSFRows = new ArrayList<>(10);

        if (inputStream != null) {
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);

            HSSFWorkbook workBook = new HSSFWorkbook(fs);
            for (int noSheet = 0; noSheet < workBook.getNumberOfSheets(); noSheet++) {

                HSSFSheet sheet = workBook.getSheetAt(noSheet);
                if (sheet != null) {
                    for (int noRow = 0; noRow <= sheet.getLastRowNum(); noRow++) {

                        HSSFRow row = sheet.getRow(noRow);
                        if (row != null) {
                            listHSSFRows.add(row);
                        }
                    }
                }
            }
        }

        return listHSSFRows;
    }


    public static String getAsString(HSSFCell cell) {
        String strCellValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    strCellValue = cell.getStringCellValue();
                    strCellValue = !Util.isNULL(strCellValue) ? Util.getString(strCellValue) : null;
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        String formatDateTime = Util.formatDateTime(cell.getDateCellValue(), Locale.US);
                        strCellValue = !Util.isNULL(formatDateTime) ? formatDateTime : null;
                    } else {
                        strCellValue = String.valueOf(Double.valueOf(cell.getNumericCellValue()).longValue());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    strCellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_BLANK:
                default:
                    strCellValue = null;
            }
        }
        return strCellValue;
    }

    public static Double getAsNumber(HSSFCell cell) {
        return cell != null ? cell.getNumericCellValue() : 0.0;
    }
}
