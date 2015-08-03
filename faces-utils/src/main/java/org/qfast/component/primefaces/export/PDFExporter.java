/*
 * Copyright 2009-2012 Prime Teknoloji.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qfast.component.primefaces.export;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.fieldset.Fieldset;
import org.primefaces.component.lightbox.LightBox;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.row.Row;
import org.primefaces.util.Constants;

public class PDFExporter extends Exporter {

    public static Font cellFont;
    private Font facetFont;

    @Override
    public void export(FacesContext context, UIComponent table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
        try {
            encodingType = BaseFont.IDENTITY_H;
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            if (preProcessor != null) {
                preProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            if (!document.isOpen()) {
                document.open();
            }

            if (table instanceof DataTable) {
                DataTable dataTable = (DataTable) table;
                document.add(exportPDFTable(context, dataTable, pageOnly, selectionOnly, encodingType));
            } else if (table instanceof PanelGrid) {
                PanelGrid panelGrid = (PanelGrid) table;
                document.add(exportPDFTable(context, panelGrid, pageOnly, selectionOnly, encodingType));
            }

            if (postProcessor != null) {
                postProcessor.invoke(context.getELContext(), new Object[]{document});
            }

            document.close();

            writePDFToResponse(context.getExternalContext(), baos, filename);

        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }

    protected PdfPTable exportPDFTable(FacesContext context, UIComponent table, boolean pageOnly, boolean selectionOnly, String encoding) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String fontPath = classLoader.getResource("fonts/arial.ttf").getPath();

        cellFont = FontFactory.getFont(fontPath, encoding, 12);
        this.facetFont = FontFactory.getFont(fontPath, encoding, 14, Font.BOLD);

        int columnsCount = getColumnsCount(table);
        PdfPTable pdfTable = new PdfPTable(columnsCount);
        pdfTable.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        pdfTable.setWidthPercentage(100f);

        if (table instanceof DataTable) {
            addColumnFacets(table, pdfTable, ColumnType.HEADER);
            pdfTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        }

        if (pageOnly && (table instanceof DataTable)) {
            exportPageOnly(context, (DataTable) table, pdfTable);
        } else if (selectionOnly && (table instanceof DataTable)) {
            exportSelectionOnly(context, (DataTable) table, pdfTable);
        } else {
            if (table instanceof DataTable) {
                exportAll(context, (DataTable) table, pdfTable);
            } else if (table instanceof PanelGrid) {
                exportAll(context, (PanelGrid) table, pdfTable);
            }
        }

        if (table instanceof DataTable && ((DataTable) table).hasFooterColumn()) {
            addColumnFacets(table, pdfTable, ColumnType.FOOTER);
        }

        if (table instanceof DataTable) {
            ((DataTable) table).setRowIndex(-1);
        }

        pdfTable.completeRow();
        return pdfTable;
    }

    protected void exportPageOnly(FacesContext context, DataTable table, PdfPTable pdfTable) {
        int first = table.getFirst();
        int rowsToExport = first + table.getRows();

        for (int rowIndex = first; rowIndex < rowsToExport; rowIndex++) {
            exportRow(table, pdfTable, rowIndex);
        }
    }

    protected void exportSelectionOnly(FacesContext context, DataTable table, PdfPTable pdfTable) {
        Object selection = table.getSelection();
        String var = table.getVar();

        if (selection != null) {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();

            if (selection.getClass().isArray()) {
                int size = Array.getLength(selection);

                for (int i = 0; i < size; i++) {
                    requestMap.put(var, Array.get(selection, i));

                    exportCells(table, pdfTable);
                }
            } else {
                requestMap.put(var, selection);

                exportCells(table, pdfTable);
            }
        }
    }

    protected void exportAll(FacesContext context, DataTable table, PdfPTable pdfTable) {
        int first = table.getFirst();
        int rowCount = table.getRowCount();
        int rows = table.getRows();
        boolean lazy = table.isLazy();

        if (lazy) {
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                if (rowIndex % rows == 0) {
                    table.setFirst(rowIndex);
                    table.loadLazyData();
                }

                exportRow(table, pdfTable, rowIndex);
            }

            //restore
            table.setFirst(first);
            table.loadLazyData();
        } else {
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                exportRow(table, pdfTable, rowIndex);
            }

            //restore
            table.setFirst(first);
        }
    }

    protected void exportAll(FacesContext context, PanelGrid table, PdfPTable pdfTable) {
        exportCells(table, pdfTable);
    }

    protected void exportRow(DataTable table, PdfPTable pdfTable, int rowIndex) {
        table.setRowIndex(rowIndex);

        if (!table.isRowAvailable()) {
            return;
        }

        exportCells(table, pdfTable);
    }

    protected void exportCells(UIComponent table, PdfPTable pdfTable) {
        List<UIColumn> listUIColumns;
        if (table instanceof DataTable) {
            listUIColumns = ((DataTable) table).getColumns();
        } else {
            listUIColumns = ((PanelGrid) table).getUIColumns();
        }
        for (UIColumn col : listUIColumns) {
            if (!col.isRendered()) {
                return;
            }

            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyModel();
            }

            if (col.isExportable()) {
                addColumnValue(pdfTable, col.getChildren(), col, null);
            }
        }
    }

    protected void addColumnFacets(UIComponent table, PdfPTable pdfTable, ColumnType columnType) {
        List<UIColumn> listUIColumns;
        if (table instanceof DataTable) {
            listUIColumns = ((DataTable) table).getColumns();
        } else {
            listUIColumns = ((PanelGrid) table).getUIColumns();
        }
        for (UIColumn col : listUIColumns) {
            if (!col.isRendered()) {
                return;
            }

            if (col instanceof DynamicColumn) {
                ((DynamicColumn) col).applyModel();
            }

            if (col.isExportable()) {
                addColumnValue(pdfTable, col, columnType);
            }
        }
    }

    protected void addColumnValue(PdfPTable pdfTable, UIColumn col, ColumnType columnType) {
        String value = col.getFacet(columnType.facet()) != null ? exportValue(FacesContext.getCurrentInstance(), col.getFacet(columnType.facet())) : (col.getHeaderText() != null ? col.getHeaderText() : "");

        PdfPCell cell = new PdfPCell(new Phrase(value, this.facetFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setArabicOptions(ColumnText.DIGITS_EN2AN);
        if (col.getColspan() > 1) {
            cell.setColspan(col.getColspan());
        }
        if (col.getRowspan() > 1) {
            cell.setRowspan(col.getRowspan());
        }
        pdfTable.addCell(cell);
    }

    protected void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, UIColumn col) {
        addColumnValue(pdfTable, components, col, null);
    }

    protected void addColumnValue(PdfPTable pdfTable, List<UIComponent> components, UIColumn col, PdfPCell cell) {
        PdfPTable pdfTableInner;
        if (cell == null) {
            cell = new PdfPCell();
        }
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        if (col.getColspan() > 1) {
            cell.setColspan(col.getColspan());
        }
        if (col.getRowspan() > 1) {
            cell.setRowspan(col.getRowspan());
        }

        for (UIComponent component : components) {
            if (component.isRendered()) {
                if (component instanceof Fieldset) {
                    Fieldset fieldset = (Fieldset) component;
                    String value = fieldset.getLegend();
                    if (value != null) {
                        Chunk chunk = new Chunk(value, cellFont);
                        chunk.setUnderline(0.1f, -2f);
                        cell.addElement(getElement(chunk, col));
                    }
                    addColumnValue(pdfTable, component.getChildren(), col, cell);
                    return;
                } else if (component instanceof PanelGrid) {
                    pdfTableInner = exportPDFTable(FacesContext.getCurrentInstance(), (PanelGrid) component, false, false, BaseFont.IDENTITY_H);
                    cell.addElement(getElement(pdfTableInner, col));
                } else if (component instanceof LightBox) {
                    return;
                } else if (component instanceof HtmlGraphicImage) {
                    try {
                        Image img = Image.getInstance(String.valueOf(((HtmlGraphicImage) component).getValue()));
                        cell.addElement(getElement(img, col));
                    } catch (BadElementException ex) {
                        Logger.getLogger(PDFExporter.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PDFExporter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    String value = exportValue(FacesContext.getCurrentInstance(), component);
                    if (value != null) {
                        Phrase phrase = new Phrase(value, cellFont);
                        cell.addElement(getElement(phrase, col));
                    }
                }
            }
        }

        cell.setVerticalAlignment(getVerticalAlignment(col.getStyle()));
        pdfTable.addCell(cell);
    }

    private Element getElement(Element element, UIColumn col) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100f);

        PdfPCell c;
        if (element.type() == Element.PHRASE) {
            Phrase phrase = (Phrase) element;
            c = new PdfPCell(phrase);
        } else if (element.type() == Element.CHUNK) {
            Phrase chunk = new Phrase((Chunk) element);
            chunk.add("\n\n");
            c = new PdfPCell(chunk);
        } else if (element.type() == Element.PTABLE) {
            PdfPTable pTable = (PdfPTable) element;
            c = new PdfPCell(pTable);
        } else {
            Image img = (Image) element;
            img.scaleToFit(new Rectangle(200, 150));
            c = new PdfPCell(img);
        }

        c.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        c.setArabicOptions(ColumnText.DIGITS_EN2AN);

        c.setHorizontalAlignment(getHorizontalAlignment(col.getStyle()));

        if (!(element instanceof PdfPTable)) {
            c.setPaddingTop(1);
            c.setPaddingBottom(1);
            c.setPaddingRight(5);
            c.setPaddingLeft(1);
        } else {
            c.setPadding(0);
            c.setUseBorderPadding(false);
        }
        c.setBorder(0);
        c.setBorderWidth(0);

        t.addCell(c);

        return t;
    }

    private int getHorizontalAlignment(String style) {
        if (style != null) {
            if (style.contains("text-align:center")) {
                return (Element.ALIGN_CENTER);
            } else if (style.contains("text-align:right")) {
                return (Element.ALIGN_LEFT);
            } else if (style.contains("text-align:left")) {
                return (Element.ALIGN_RIGHT);
            }
        }
        return Element.ALIGN_LEFT;
    }

    private int getVerticalAlignment(String style) {
        if (style != null) {
            if (style.contains("vertical-align:top")) {
                return (Element.ALIGN_TOP);
            } else if (style.contains("vertical-align:middle")) {
                return (Element.ALIGN_MIDDLE);
            } else if (style.contains("vertical-align:bottom")) {
                return (Element.ALIGN_BOTTOM);
            }
        }
        return Element.ALIGN_TOP;
    }

    protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) throws IOException, DocumentException {
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
        externalContext.setResponseContentLength(baos.size());
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());
        OutputStream out = externalContext.getResponseOutputStream();
        baos.writeTo(out);
        externalContext.responseFlushBuffer();
    }

    protected int getColumnsCount(UIComponent uiComponent) {
        int count = 0;

        for (UIComponent child : uiComponent.getChildren()) {
            if (!child.isRendered()) {
                continue;
            }

            if (child instanceof Column) {
                Column column = (Column) child;

                if (column.isExportable()) {
                    if (column.getColspan() > 1) {
                        count += column.getColspan();
                    } else {
                        count++;
                    }
                }
            } else if (child instanceof Row) {
                count = getColumnsCount(child);
                break;
            } else if (child instanceof Columns) {
                Columns columns = (Columns) child;

                if (columns.isExportable()) {
                    count += columns.getRowCount();
                }
            }
        }
        return count;
    }
}
