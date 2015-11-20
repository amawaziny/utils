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
package org.qfast.entities;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Ahmed El-mawaziny
 */
public abstract class AbstractTranslatableEntity implements Serializable, Comparable<AbstractTranslatableEntity> {

    private static final long serialVersionUID = -860560266031983029L;

    public abstract Object getId();

    public abstract String getTableName();

    public abstract String[] getColumns();

    public abstract Map<String, String> getTranslatableColumns();

    public boolean isPersisted() {
        Object id = getId();
        return id != null || ((id instanceof Integer) && (((Integer) id) > 0));
    }

    @Override
    public int compareTo(AbstractTranslatableEntity o) {
        Object id = getId();
        if (id != null) {
            if (id instanceof Integer) {
                return ((Integer) id).compareTo((Integer) o.getId());
            }
            return id.toString().compareTo(o.getId().toString());
        }
        return 0;
    }
}
