/*
 * Copyright 2015 QFast Ahmed El-mawaziny.
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
package org.qfast.validation;

import java.util.Locale;
import javax.validation.MessageInterpolator;
import javax.validation.MessageInterpolator.Context;

/**
 * @author Ahmed El-mawaziny
 */
public class LocaleMessageInterpolator implements MessageInterpolator {

    private final MessageInterpolator defaultInterpolator;
    private final Locale defaultLocale;

    public LocaleMessageInterpolator(MessageInterpolator interpolator, Locale locale) {
        this.defaultLocale = locale;
        this.defaultInterpolator = interpolator;
    }

    /**
     * enforces the locale passed to the interpolator
     *
     * @param message
     */
    @Override
    public String interpolate(String message, Context context) {
        return defaultInterpolator.interpolate(message, context, this.defaultLocale);
    }

    // no real use, implemented for completeness
    @Override
    public String interpolate(String message, Context context, Locale locale) {
        return defaultInterpolator.interpolate(message, context, locale);
    }
}
