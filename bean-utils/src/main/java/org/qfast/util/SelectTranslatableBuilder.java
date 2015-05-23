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

import java.util.Map;
import org.qfast.entities.AbstractTranslatableEntity;

/**
 * @author Ahmed El-mawaziny
 * @param <T>
 */
public final class SelectTranslatableBuilder<T extends AbstractTranslatableEntity> {

    private final Class<T> entityClass;
    private final char alias;
    private final String entityName;
    private final StringBuilder sb;
    private final T entity;
    private final String lang;

    @SuppressWarnings("unchecked")
    public SelectTranslatableBuilder(T entity, String lang) {
        this.entityClass = (Class<T>) entity.getClass();
        entityName = entityClass.getSimpleName();
        alias = Character.toLowerCase(entityName.charAt(0));
        this.entity = entity;
        this.lang = lang;
        sb = new StringBuilder(100);
        build();
    }

    public char getAlias() {
        return alias;
    }

    public SelectTranslatableBuilder(Class<T> entityClass, String lang)
            throws InstantiationException, IllegalAccessException {
        this(entityClass.newInstance(), lang);
    }

    private void build() {
        sb.append("NEW ").append(entityClass.getName()).append('(');
        char comma = ' ';
        for (String column : entity.getColumns()) {
            sb.append(comma).append(alias).append('.').append(column);
            comma = ',';
        }
        Map<String, String> translatableColumns = entity.getTranslatableColumns();
        SelectTranslatableCompose select
                = new SelectTranslatableCompose(entity.getTableName(), alias, lang);
        for (String key : translatableColumns.keySet()) {
            select = select.compose(key, translatableColumns.get(key));
        }
        sb.append(comma).append(select.get());
        sb.append(')');
    }

    public String get() {
        return sb.toString();
    }
}
