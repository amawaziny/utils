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
package org.qfast.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;

/**
 * @author Ahmed El-mawaziny 
 */
public abstract class Report {

    public abstract Connection getConnection();

    public abstract EntityManager getEntityManager();

    public List getResult(String sql, Object... params) {
        Query q = getEntityManager().createNativeQuery(sql);
        for (int i = 0; i < params.length; i++) {
            q.setParameter((i + 1), params[i]);
        }
        return q.getResultList();
    }

    public Result getResult(String sql) throws SQLException {
        PreparedStatement prStm = getConnection().prepareStatement(sql);
        return getResult(prStm);
    }

    public Result getResult(PreparedStatement prStm) throws SQLException {
        Result result = null;
        if (prStm != null) {
            ResultSet resultSet = prStm.executeQuery();
            try {
                if (resultSet != null) {
                    result = ResultSupport.toResult(resultSet);
                }
            } finally {
                prStm.close();
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        }
        return result;
    }
}
