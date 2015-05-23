/*
 * Copyright 2014 QFast Ahmed El-mawaziny.
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
public class SelectTranslatableCompose {

    private final StringBuilder select;
    private final String tableName;
    private final char alias;
    private final String lang;
    private char comma = ' ';

    public SelectTranslatableCompose(String tableName, char alias, String lang) {
        this.select = new StringBuilder(100);
        this.tableName = tableName;
        this.alias = alias;
        this.lang = lang;
    }

    public SelectTranslatableCompose compose(String columnName,
            String id, String fieldName) {

        select.append(comma)
                .append("COALESCE")
                .append('(')
                .append("FUNCTION")
                .append('(')
                .append("'translate'")
                .append(',')
                .append('\'').append(tableName).append('\'')
                .append(',')
                .append('\'').append(columnName).append('\'')
                .append(',')
                .append('\'').append(lang).append('\'')
                .append(',')
                .append(alias).append('.').append(id)
                .append(')')
                .append(',')
                .append(alias).append('.')
                .append(fieldName)
                .append(')');

        comma = ',';
        
        return this;
    }

    public SelectTranslatableCompose compose(String columnName,
            String fieldName) {
        return compose(columnName, "id", fieldName);
    }
    
    public String get(){
        return select.toString();
    }

    @Override
    public String toString() {
        return get();
    }
}
