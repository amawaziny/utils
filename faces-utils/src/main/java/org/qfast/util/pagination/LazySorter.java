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
package org.qfast.util.pagination;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.SortOrder;
import org.qfast.util.Util;

/**
 * @author Ahmed El-mawaziny
 * @param <T>
 */
public class LazySorter<T> implements Comparator<T> {

    private final String sortField;
    private final SortOrder sortOrder;
    private static final Logger LOG = Logger.getLogger(LazySorter.class.getName());

    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(T t1, T t2) {
        try {
            Field f1 = Util.getDeclaredField(t1, this.sortField);
            Field f2 = Util.getDeclaredField(t2, this.sortField);

            Object value1 = f1.get(t1);
            Object value2 = f2.get(t2);

            int value;
            if (value1 == null && value2 == null) {
                value = 0;
            } else if (value1 == null) {
                value = 1;
            } else if (value2 == null) {
                value = -1;
            }

            if (value1 instanceof Comparable && value2 instanceof Comparable) {
                value = ((Comparable) value1).compareTo(value2);
            } else {
                value = String.valueOf(value1).compareTo(String.valueOf(value2));
            }

            return SortOrder.ASCENDING.equals(this.sortOrder) ? value : -1 * value;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error in load method", e);
            return 0;
        }
    }
}
