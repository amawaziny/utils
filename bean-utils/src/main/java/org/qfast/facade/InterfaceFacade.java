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
package org.qfast.facade;

import java.util.Collection;
import java.util.List;

/**
 * @param <T>
 * @author Ahmed El-mawaziny
 */
public interface InterfaceFacade<T> {

    int count();

    void create(T entity);

    void create(Collection<T> entities);

    void edit(T entity);

    T merge(T entity);

    Collection<T> merge(Collection<T> entities);

    List<T> mergeAndFlush(List<T> entities);

    void edit(Collection<T> entities);

    void edit(T[] entities);

    T find(Object id);

    List<T> find(Object[] id);

    List<T> findAll(List ids);

    List<T> findAll();

    List<T> findAllOrderBy(String columnName, boolean desc);

    List<T> findRange(int[] range);

    void remove(T entity);

    void remove(Collection<T> entities);

    void remove(T[] entities);

    void removeAll(String[] ids);

    void detach(T entity);

    void detach(T[] entity);

    void detach(Collection<T> entity);
}
