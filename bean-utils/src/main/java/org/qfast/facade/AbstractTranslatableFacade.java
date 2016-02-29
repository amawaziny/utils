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

import org.qfast.entities.AbstractTranslatableEntity;
import org.qfast.util.SelectTranslatableBuilder;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <T>
 * @author Ahmed El-mawaziny
 */
public abstract class AbstractTranslatableFacade<T extends AbstractTranslatableEntity>
        extends AbstractFacade<T> {

    private static final Logger LOG = Logger.getLogger(AbstractTranslatableFacade.class.getName());
    protected final Class<T> entityClass;

    public AbstractTranslatableFacade(Class<T> entityClass) {
        super(entityClass);
        this.entityClass = entityClass;
    }

    public abstract Locale getLocale();

    @Override
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
        getEntityManager().createQuery("DELETE FROM Translation t"
                + " WHERE t.id.tableId   = :tableId"
                + "   AND t.id.tableName = :tableName")
                .setParameter("tableId", entity.getId())
                .setParameter("tableName", entity.getTableName())
                .executeUpdate();
    }

    @Override
    public T find(Object id) {
        if (id != null) return find(id, getLocale());
        else return null;
    }

    public T find(Object id, Locale locale) {
        if (id != null)
            try {
                SelectTranslatableBuilder<T> select
                        = new SelectTranslatableBuilder<>(entityClass,
                        locale.toString());
                String sql = "SELECT " + select.get() + " FROM "
                        + entityClass.getSimpleName() + " " + select.getAlias()
                        + " WHERE " + select.getAlias() + ".id=:id";
                TypedQuery<T> q = getEntityManager().createQuery(sql, entityClass);
                q.setParameter("id", id);
                return q.getSingleResult();
            } catch (InstantiationException | IllegalAccessException |
                    NonUniqueResultException | NoResultException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                return null;
            }
        else return null;
    }

    public T findById(Object id) {
        return super.find(id);
    }

    @Override
    public List<T> findAll() {
        try {
            SelectTranslatableBuilder<T> select
                    = new SelectTranslatableBuilder<>(entityClass,
                    getLocale().toString());
            String sql = "SELECT " + select.get() + " FROM "
                    + entityClass.getSimpleName() + " " + select.getAlias();
            TypedQuery<T> q = getEntityManager().createQuery(sql, entityClass);
            return q.getResultList();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new Error(ex);
        }
    }

    @Override
    public List<T> findAllOrderBy(String columnName, boolean desc) {
        try {
            SelectTranslatableBuilder<T> select = new SelectTranslatableBuilder<>(entityClass, getLocale().toString());
            String sql = "SELECT " + select.get() + " FROM "
                    + entityClass.getSimpleName() + " " + select.getAlias()
                    + " ORDER BY " + select.getAlias() + "." + columnName
                    + (desc ? " DESC" : "ASC");
            TypedQuery<T> q = getEntityManager().createQuery(sql, entityClass);

            return q.getResultList();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new Error(ex);
        }
    }

    @Override
    public List<T> findRange(int[] range) {
        try {
            SelectTranslatableBuilder<T> select
                    = new SelectTranslatableBuilder<>(entityClass,
                    getLocale().toString());
            String sql = "SELECT " + select.get() + " FROM "
                    + entityClass.getSimpleName() + " " + select.getAlias();
            TypedQuery<T> q = getEntityManager().createQuery(sql, entityClass);
            q.setMaxResults(range[1] - range[0]);
            q.setFirstResult(range[0]);
            return q.getResultList();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new Error(ex);
        }
    }


    @Override
    public List<T> findRangeOrderBy(int[] range, String columnName, boolean asc) {
        try {
            SelectTranslatableBuilder<T> select = new SelectTranslatableBuilder<>(entityClass, getLocale().toString());
            String sql = "SELECT " + select.get() + " FROM "
                    + entityClass.getSimpleName() + " " + select.getAlias();
            if (columnName != null && !columnName.isEmpty())
                sql += " ORDER BY " + select.getAlias() + "." + columnName + (asc ? " ASC" : " DESC");
            return getEntityManager().createQuery(sql, entityClass)
                    .setMaxResults(range[1])
                    .setFirstResult(range[0])
                    .getResultList();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new Error(e);
        }
    }
}
