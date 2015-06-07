/*
 * Copyright 2014 Ahmed El-mawaziny.
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
package org.qfast.service;

import org.qfast.facade.InterfaceFacade;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @param <T>
 * @author Ahmed El-mawaziny
 */
public abstract class AbstractService<T> implements InterfaceFacade<T>, Serializable {

    private static final long serialVersionUID = -2331444447660962696L;

    @Override
    public void create(T entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void create(Collection<T> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void edit(T entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T merge(T entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<T> merge(Collection<T> entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void edit(Collection<T> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void edit(T[] entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T find(Object id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<T> findAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<T> findAllOrderBy(String columnName, boolean desc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(T entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(Collection<T> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(T[] entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAll(String[] ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
