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
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;
import org.qfast.util.FacesUtil;
import static org.qfast.util.FacesUtil.CONTEXT_UPLOAD_DIR;
import static org.qfast.util.FacesUtil.getContextParam;
import static org.qfast.util.Util.getBundleMessage;
import static org.qfast.util.Util.isNULL;

/**
 * @author Ahmed El-mawaziny
 */
public final class UploadUtil {

    private final String directory;
    private final UploadedFile uploadedFile;
    private String fileName;
    private File fileDist = null;

    public UploadUtil(UploadedFile uploadedFile, String fileName, String folder) {
        this.uploadedFile = uploadedFile;
        this.fileName = fileName;
        this.directory = getContextParam(CONTEXT_UPLOAD_DIR) + File.separatorChar + folder + File.separatorChar;
    }

    public void upload() throws IOException {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (uploadedFile != null) {
            if (isNULL(fileName)) {
                fileName = uploadedFile.getFileName();
            }
            try {
                fileDist = new File(dir.getAbsolutePath() + File.separatorChar + fileName);
                if (!fileDist.exists()) {
                    IOUtils.copyLarge(uploadedFile.getInputstream(), new FileOutputStream(fileDist));
                } else {
                    FacesUtil.addErrorMessage(String.format(getBundleMessage("FileAlreadyExists"),fileName));
                }
            } catch (IOException e) {
                deleteFile();
                FacesUtil.addErrorMessage(e);
                throw e;
            } finally {
                FacesUtil.addSuccessMessage(String.format(getBundleMessage("FileUploadedSuccessful"), fileName));
            }
        } else {
            FacesUtil.addErrorMessage(getBundleMessage("FileUploadedNoFile"));
        }
    }

    public void deleteFile() {
        if (fileDist != null) {
            FileUtils.deleteQuietly(fileDist);
            FacesUtil.addErrorMessage(getBundleMessage("DeletedFile"));
        }
    }
}
