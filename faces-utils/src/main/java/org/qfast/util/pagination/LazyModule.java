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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.qfast.facade.InterfaceFacade;
import org.qfast.util.Util;

/**
 * @author Ahmed El-mawaziny
 * @param <T>
 */
public class LazyModule<T> extends LazyDataModel<T> {

    protected final InterfaceFacade<T> facade;
    private static final Logger LOG = Logger.getLogger(LazyModule.class.getName());

    public LazyModule(InterfaceFacade<T> facade) {
        this.facade = facade;
    }

    @Override
    public T getRowData(String rowKey) {
        return facade.find((Object) rowKey);
    }

    @Override
    public Object getRowKey(T t) {
        try {
            Field f = Util.getDeclaredField(t, "id");
            return f.get(t);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<T> dataSource = new CopyOnWriteArrayList<T>(facade.findRange(new int[]{first, (first + pageSize)}));

        //filter
        for (T t : dataSource) {
            for (String filterProperty : filters.keySet()) {
                try {
                    Object filterValue = filters.get(filterProperty);

                    String fieldValue;
                    if (filterProperty.contains(".")) {
                        StringTokenizer properties = new StringTokenizer(filterProperty, ".");
                        Field f = Util.getDeclaredField(t, properties.nextToken());
                        Class c = f.getType();
                        while (properties.hasMoreTokens()) {
                            f = Util.getDeclaredField(c, properties.nextToken());
                            c = f.getType();
                        }
                        fieldValue = String.valueOf(f.get(c));
                    } else {
                        Field f = Util.getDeclaredField(t, filterProperty);
                        fieldValue = String.valueOf(f.get(t));
                    }

                    if (filterValue != null && !fieldValue.contains(filterValue.toString())) {
                        dataSource.remove(t);
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error in load method", e);
                }
            }
        }

        List<T> data = new ArrayList<T>(dataSource.size());
        data.addAll(dataSource);

        dataSource = null;

        //sort  
        if (sortField != null) {
            Collections.sort(data, new LazySorter<T>(sortField, sortOrder));
        }

        //rowCount  
        this.setRowCount(facade.count());

        return data;
    }
}
