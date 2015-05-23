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
 * @author Ahmed El-mawaziny
 * @param <T>
 */
public interface InterfaceFacade<T> {

    public int count();

    public void create(T entity);

    public void create(Collection<T> entities);

    public void edit(T entity);

    public T merge(T entity);

    public Collection<T> merge(Collection<T> entities);

    public void edit(Collection<T> entities);

    public T find(Object id);

    public List<T> findAll();

    public List<T> findAllOrderBy(String columnName, boolean desc);

    public List<T> findRange(int[] range);

    public void remove(T entity);

    public void remove(Collection<T> entities);

    public void remove(T[] entities);

    public void removeAll(String[] ids);

}
