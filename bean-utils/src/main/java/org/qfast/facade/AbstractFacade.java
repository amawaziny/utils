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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @param <T>
 * @author Ahmed El-mawaziny
 */
public abstract class AbstractFacade<T> implements InterfaceFacade<T> {

    private final Class<T> entityClass;
    protected static final ValidatorFactory FACTORY = buildDefaultValidatorFactory();

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    @Override
    public void create(T entity) {
        validate(entity);
        getEntityManager().persist(entity);
    }

    public void validate(T entity) {
        Validator validator = FACTORY.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> cv = iterator.next();
                sb.append("\n").append(cv.getRootBeanClass().getSimpleName()).append(".").append(cv.getPropertyPath())
                        .append(": ").append(cv.getMessage());
            }
            throw new Error(sb.toString());
        }
    }

    @Override
    public void create(Collection<T> entities) {
        for (T t : entities) {
            create(t);
        }
    }

    @Override
    public void edit(T entity) {
        merge(entity);
    }

    @Override
    public T merge(T entity) {
        validate(entity);
        return getEntityManager().merge(entity);
    }

    @Override
    public Collection<T> merge(Collection<T> entities) {
        for (T t : entities) {
            merge(t);
        }
        return entities;
    }

    @Override
    public List<T> mergeAndFlush(List<T> entities) {
        List<T> ts = new ArrayList<>(entities.size());
        for (T t : entities) {
            T merged = merge(t);
            getEntityManager().flush();
            ts.add(merged);
        }
        return ts;
    }

    @Override
    public void edit(Collection<T> entities) {
        for (T t : entities) {
            edit(t);
        }
    }

    @Override
    public void edit(T[] entities) {
        for (T t : entities) {
            edit(t);
        }
    }

    @Override
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    @Override
    public void remove(Collection<T> entities) {
        for (T t : entities) {
            remove(t);
        }
    }

    @Override
    public void remove(T[] entities) {
        remove(Arrays.asList(entities));
    }

    @Override
    public void removeAll(String[] ids) {
        for (String id : ids) {
            T entity = find(Integer.parseInt(id));
            if (entity != null) {
                remove(entity);
            }
        }
    }

    @Override
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> find(Object[] ids) {
        List<T> ts = new ArrayList<>(ids.length);
        for (Object id : ids) {
            ts.add(find(id));
        }
        return ts;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll(List ids) {
        return find(ids.toArray(new Object[ids.size()]));
    }

    @Override
    public List<T> findAll() {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @Override
    public List<T> findAllOrderBy(String columnName, boolean desc) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        cq.select(rt).orderBy(desc ? cb.desc(rt.get(columnName)) : cb.asc(rt.get(columnName)));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @Override
    public List<T> findRange(int[] range) {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public List<T> findRangeOrderBy(int[] range, String columnName, boolean asc) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        cq = cq.select(rt);
        if (columnName != null && !columnName.isEmpty()) {
            cq.orderBy(asc ? cb.asc(rt.get(columnName)) : cb.desc(rt.get(columnName)));
        }
        return getEntityManager().createQuery(cq)
                .setMaxResults(range[1])
                .setFirstResult(range[0])
                .getResultList();
    }

    @Override
    public int count() {
        CriteriaBuilder cp = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cp.createQuery(Long.class);
        cq.select(cp.count(cq.from(entityClass)));
        TypedQuery<Long> q = getEntityManager().createQuery(cq);
        return q.getSingleResult().intValue();
    }
}
