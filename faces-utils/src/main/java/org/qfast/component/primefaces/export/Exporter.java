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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;

public abstract class Exporter {

    protected enum ColumnType {

        HEADER("header"),
        FOOTER("footer");
        private final String facet;

        ColumnType(String facet) {
            this.facet = facet;
        }

        public String facet() {
            return facet;
        }

        @Override
        public String toString() {
            return facet;
        }
    };

    public abstract void export(FacesContext facesContext, UIComponent table,
            String outputFileName, boolean pageOnly, boolean selectionOnly,
            String encodingType, MethodExpression preProcessor,
            MethodExpression postProcessor) throws IOException;

    protected List<UIColumn> getColumnsToExport(UIData table) {
        List<UIColumn> columns = new ArrayList<UIColumn>(10);

        for (UIComponent child : table.getChildren()) {
            if (child instanceof UIColumn) {
                UIColumn column = (UIColumn) child;

                columns.add(column);
            }
        }

        return columns;
    }

    protected boolean hasColumnFooter(List<UIColumn> columns) {
        for (UIColumn column : columns) {
            if (column.getFooter() != null) {
                return true;
            }
        }

        return false;
    }

    protected String exportValue(FacesContext context, UIComponent component) {

        if (component instanceof SelectBooleanCheckbox) {
            SelectBooleanCheckbox checkbox = (SelectBooleanCheckbox) component;
            boolean checkboxValue = false;
            if (checkbox.getValue() != null) {
                if (checkbox.isDisabled()) {
                    checkboxValue = Boolean.parseBoolean(checkbox.getValue().toString());
                } else {
                    checkboxValue = Boolean.parseBoolean(checkbox.getSubmittedValue().toString());
                }
            }
            return (checkboxValue ? " ( √ ) " : " (   ) ") + (checkbox.getItemLabel() != null ? checkbox.getItemLabel() : "");
        } else if (component instanceof HtmlGraphicImage) {
            HtmlGraphicImage img = (HtmlGraphicImage) component;
            if (img.getValue() != null) {
                return String.valueOf(img.getValue());
            } else if (img.getAlt() != null) {
                return img.getAlt();
            } else {
                return "";
            }
        } else if (component instanceof HtmlCommandLink) { //support for PrimeFaces and standard HtmlCommandLink
            HtmlCommandLink link = (HtmlCommandLink) component;
            Object value = link.getValue();

            if (value != null) {
                return String.valueOf(value);
            } else {
                //export first value holder
                for (UIComponent child : link.getChildren()) {
                    if (child instanceof ValueHolder) {
                        return exportValue(context, child);
                    }
                }

                return "";
            }
        } else if (component instanceof ValueHolder) {

            if (component instanceof EditableValueHolder) {
                Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    ValueHolder valueHolder = (ValueHolder) component;
                    if (valueHolder.getConverter() != null) {
                        Object getAsObject = valueHolder.getConverter().getAsObject(context, component, submittedValue.toString());
                        if (getAsObject instanceof Date) {
                            return submittedValue.toString();
                        }
                        return getAsObject != null ? getAsObject.toString() : "";
                    } else {
                        return submittedValue.toString();
                    }
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            Object value = valueHolder.getValue();
            if (value == null) {
                return "";
            }

            //first ask the converter
            if (valueHolder.getConverter() != null) {
                return valueHolder.getConverter().getAsString(context, component, value);
            } //Try to guess
            else {
                ValueExpression expr = component.getValueExpression("value");
                if (expr != null) {
                    Class<?> valueType = expr.getType(context.getELContext());
                    if (valueType != null) {
                        Converter converterForType = context.getApplication().createConverter(valueType);

                        if (converterForType != null) {
                            return converterForType.getAsString(context, component, value);
                        }
                    }
                }
            }

            //No converter found just return the value as string
            return value.toString();
        } else {
            //This would get the plain texts on UIInstructions when using Facelets
            String value = component.toString();

            if (value != null) {
                return value.trim();
            } else {
                return "";
            }
        }
    }
}
