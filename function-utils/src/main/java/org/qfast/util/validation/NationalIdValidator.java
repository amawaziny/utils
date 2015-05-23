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
package org.qfast.util.validation;

import org.qfast.util.NationalID;
import org.qfast.util.Util;
import java.math.BigInteger;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Ahmed El-mawaziny 
 */
public class NationalIdValidator implements ConstraintValidator<NationalId, BigInteger> {

    @Override
    public void initialize(NationalId constraintAnnotation) {
    }

    @Override
    public boolean isValid(BigInteger value, ConstraintValidatorContext context) {
        if (Util.isNULL(value)) {
            return true;
        }
        boolean ok = true;

        context.disableDefaultConstraintViolation();

        try {
            NationalID nationalID = new NationalID(value);
            ok = nationalID.isValid();
        } catch (Exception e) {
            ok = false;
            context.buildConstraintViolationWithTemplate(e.getLocalizedMessage()).addConstraintViolation();
        }

        return ok;
    }
}