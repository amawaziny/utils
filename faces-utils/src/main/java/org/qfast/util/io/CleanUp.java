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
package org.qfast.util.io;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import static org.qfast.util.FacesUtil.CONTEXT_UPLOAD_TEMP_DIR;
import static org.qfast.util.FacesUtil.getContextParam;
import static org.qfast.util.Util.isNULL;

public class CleanUp {

    private static final Logger LOG = Logger.getLogger(CleanUp.class.getName());

    private CleanUp() {
    }

    public static void clearTemp() {
        String tempDirStr = getContextParam(CONTEXT_UPLOAD_TEMP_DIR);
        if (!isNULL(tempDirStr)) {
            try {
                File tmpDir = new File(tempDirStr);
                FileUtils.cleanDirectory(tmpDir);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void fixMemory() {
        CleanUp.fixMemory();
    }
}
