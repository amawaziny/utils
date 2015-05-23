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
package org.qfast.util.exc;

import org.qfast.util.Util;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.ejb.EJBException;
import javax.persistence.PersistenceException;
import javax.transaction.RollbackException;

/**
 * @author Ahmed El-mawaziny 
 */
public class ExceptionHandlerUtil extends Exception {

    private static final long serialVersionUID = 1L;
    private final Throwable exception;

    public ExceptionHandlerUtil(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    public static String getMessage(Throwable e) {
        return new ExceptionHandlerUtil(e).getMessage();
    }

    @Override
    public String getMessage() {
        if (exception instanceof EJBException || exception instanceof RollbackException) {
            RollbackException rollbackException = null;
            if (exception instanceof EJBException) {
                EJBException ejbExc = (EJBException) exception;
                Exception causedByExc = ejbExc.getCausedByException();
                if (causedByExc instanceof RollbackException) {
                    rollbackException = (RollbackException) causedByExc;
                }
            } else if (exception instanceof RollbackException) {
                rollbackException = (RollbackException) exception;
            }
            if (rollbackException != null) {
                if (rollbackException.getCause() instanceof PersistenceException) {
                    if (rollbackException.getCause().getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                        SQLIntegrityConstraintViolationException sqlExc = (SQLIntegrityConstraintViolationException) rollbackException.getCause().getCause().getCause();
                        int errorCode = sqlExc.getErrorCode();
                        if (errorCode == 1 || errorCode == 1062) {
                            return Util.getBundleMessage("DuplicateEntryMsg");
                        } else if (errorCode == 2292 || errorCode == 1451) {
                            return Util.getBundleMessage("ForeignKeyMsg");
                        }
                    }
                }
            }
        } else if (exception instanceof IllegalStateException) {
            return Util.getBundleMessage("PersistenceErrorOccured") + ": IllegalStateException " + exception.getMessage();
        }
        return exception.getMessage();
    }
}
