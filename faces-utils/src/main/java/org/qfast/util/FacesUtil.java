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
package org.qfast.util;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import static org.qfast.util.Util.isNULL;
import org.qfast.util.client.UserAgent;
import org.qfast.util.client.WebClient;
import org.qfast.util.exc.ExceptionHandlerUtil;

/**
 * @author Ahmed El-mawaziny
 */
public final class FacesUtil {

    public static final String PARAM_REDIRECT = "?faces-redirect=true";
    public static final String CONTEXT_UPLOAD_DIR = "uploadDirectory";
    public static final String CONTEXT_UPLOAD_TEMP_DIR = "uploadTmpDirectory";

    private FacesUtil() {
    }

    public static SelectItem[] getSelectItems(Collection<?> entities, boolean selectOne) {
        return getSelectItems(entities, selectOne, true);
    }

    public static SelectItem[] getSelectItems(Collection<?> entities, boolean selectOne, boolean escape) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i] = new SelectItem(x, x.toString());
            items[i].setEscape(escape);
            i++;
        }
        return items;
    }

    public static void addErrorMessage(Exception ex, String defaultMsg) {
        ExceptionHandlerUtil ejbEx = new ExceptionHandlerUtil(ex);
        if (!isNULL(ejbEx.getLocalizedMessage())) {
            addErrorMessage(ejbEx.getLocalizedMessage());
        } else if (!isNULL(ex.getLocalizedMessage())) {
            addErrorMessage(ex.getLocalizedMessage());
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    public static void addErrorMessage(Exception ex) {
        addErrorMessage(ex, getBundleMessage("PersistenceErrorOccured"));
    }

    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    public static void addErrorExceptions(List<Exception> exces) {
        for (Exception exc : exces) {
            addErrorMessage(exc);
        }
    }

    public static void addErrorMessage(String msg) {
        addErrorMessage(msg, false);
    }

    public static void addErrorMessage(String msg, boolean keep) {
        addErrorMessage((String) null, msg, keep);
    }

    public static void addErrorMessage(String clientId, String msg) {
        addErrorMessage(clientId, msg, false);
    }

    public static void addErrorMessage(String clientId, String msg, boolean keep) {
        addErrorMessage(clientId, msg, msg, false, keep);
    }

    public static void addErrorMessage(String clientId, String msg, String detail, boolean isGrowl) {
        addErrorMessage(clientId, msg, detail, isGrowl, false);
    }

    public static void addErrorMessage(String clientId, String msg, String detail, boolean isGrowl, boolean keep) {
        addMessage(FacesMessage.SEVERITY_ERROR, msg, detail, isGrowl, clientId, keep);
    }

    public static void addWornMessages(List<String> messages) {
        for (String message : messages) {
            addWornMessage(message);
        }
    }

    public static void addWornMessage(String msg) {
        addWornMessage(msg, false);
    }

    public static void addWornMessage(String msg, boolean keep) {
        addWornMessage(msg, msg, false, keep);
    }

    public static void addWornMessage(String msg, String detail, boolean isGrowl) {
        addWornMessage(msg, detail, isGrowl, false);
    }

    public static void addWornMessage(String msg, String detail, boolean isGrowl, boolean keep) {
        addMessage(FacesMessage.SEVERITY_WARN, msg, detail, isGrowl, null, keep);
    }

    public static void addSuccessMessage(String msg) {
        addSuccessMessage(msg, false);
    }

    public static void addSuccessMessage(String msg, boolean keep) {
        addSuccessMessage(msg, null, keep);
    }

    public static void addSuccessMessage(String msg, String clientId) {
        addSuccessMessage(msg, clientId, false);
    }

    public static void addSuccessMessage(String msg, String clientId, boolean keep) {
        addSuccessMessage(msg, msg, false, clientId, keep);
    }

    public static void addSuccessMessage(String msg, String detail, boolean isGrowl, String clientId) {
        addSuccessMessage(msg, detail, isGrowl, clientId, false);
    }

    public static void addSuccessMessage(String msg, String detail, boolean isGrowl, String clientId, boolean keep) {
        addMessage(FacesMessage.SEVERITY_INFO, msg, detail, isGrowl, clientId, keep);
    }

    private static void addMessage(Severity severity, String msg, String detail, boolean isGrowl, String clientId, boolean keep) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage facesMsg = new FacesMessage(severity, msg, (!isGrowl ? "<br />" : "") + detail);
        context.addMessage(clientId, facesMsg);
        if (keep) {
            context.getExternalContext().getFlash().setKeepMessages(keep);
        }
    }

    public static Flash getFlash() {
        return getExternalContext().getFlash();
    }

    public static String getRemoteAddr() {
        return getRequest().getRemoteAddr();
    }

    public static String getRemoteUser() {
        return getRequest().getRemoteUser();
    }

    public static String getHeader() {
        HttpServletRequest request = getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        String headerInfo = "";
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            if (!headerName.equalsIgnoreCase("authorization")) {
                headerInfo += headerName + ": \"" + request.getHeader(headerName) + "\" <br />";
            }
        }
        return headerInfo;
    }

    public static String getRemoteHost() {
        return getRequest().getRemoteHost();
    }

    public static int getRemotePort() {
        return getRequest().getRemotePort();
    }

    public static String getRemoteLoginInfo() {
        return getHeader() + "<hr />User: \"" + getRemoteUser() + "\"<br />Host: \"" + getRemoteHost() + "\"<br />IP address: \"" + getRemoteAddr() + "\"<br />Port: \"" + getRemotePort() + "\"";
    }

    public static String getContextParam(String param) {
        if (getExternalContext() != null) {
            return getExternalContext().getInitParameter(param);
        }
        return null;
    }

    public static String getParameter(String key) {
        String[] paramValues = getParameterValues(key);
        if (paramValues == null || paramValues.length == 0) {
            return null;
        }
        return paramValues[0];
    }

    public static String[] getParameterValues(String key) {
        HttpServletRequest request = getRequest();
        return request.getParameterValues(key);
    }

    public static String getRequestParameter(String key) {
        return getExternalContext().getRequestParameterMap().get(key);
    }

    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
    }

    public static Object getSessionAttr(String attrName) {
        return getExternalContext().getSessionMap().get(attrName);
    }

    public static Object getSessionAttrNative(String attrName) {
        HttpServletRequest request = getRequest();
        return request.getSession(false).getAttribute(attrName);
    }

    public static void setSessionAttr(String attrName, Object value) {
        getExternalContext().getSessionMap().put(attrName, value);
    }

    public static void removeSessionAttr(String attrName) {
        getExternalContext().getSessionMap().remove(attrName);
    }

    public static String getServletPath() {
        String servletPath = getRequest().getServletPath();
        return servletPath;
    }

    public static String getBasePageName() {
        return getBasePageName(getServletPath());
    }

    public static String getBasePageName(String pageUrl) {
        return pageUrl.substring(pageUrl.lastIndexOf('/') + 1, pageUrl.lastIndexOf('.'));
//        return pageUrl.replaceFirst("/", "").replace(".xhtml", "");
    }

    public static void handleNavigation(String fromAction, String outcome) {
        FacesContext fc = getFacesContext();
        NavigationHandler nav = fc.getApplication().getNavigationHandler();
        nav.handleNavigation(fc, fromAction, outcome);
        fc.renderResponse();
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) getExternalContext().getRequest();
    }

    public static String getRealPath() {
        return getExternalContext().getRealPath("/");
    }

    public static ExternalContext getExternalContext() {
        if (FacesContext.getCurrentInstance() != null) {
            return getFacesContext().getExternalContext();
        } else {
            return null;
        }
    }

    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public static String getHash(String plain) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(plain.getBytes(), 0, plain.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return plain;
        }
    }

    public static String getBundleMessage(String messageName) {
        return getBundleMessage(messageName, null);
    }

    public static String getBundleMessage(String messageName, Locale locale) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (locale != null) {
            return ResourceBundle.getBundle("/resources.Bundle", locale, contextClassLoader).getString(messageName);
        } else {
            return ResourceBundle.getBundle("/resources.Bundle", getLocale(), contextClassLoader).getString(messageName);
        }
    }

    public static Locale getLocale() {
        try {
            return getFacesContext().getViewRoot().getLocale();
        } catch (Exception e) {
            return new Locale("ar", "EG");
        }
    }

    private static String getUploadRealPath(String dir) {
        String realPath = getContextParam(CONTEXT_UPLOAD_DIR) + File.separatorChar + dir + File.separatorChar;
        return realPath;
    }

    public static String getUploadRealPath(String dir, String fileName) {
        return getUploadRealPath(dir) + fileName;
    }

    public static MethodExpression getEL(String el) {
        ExpressionFactory factory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
        MethodExpression methodExpression = factory.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), el, String.class, new Class[]{});
        return methodExpression;
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null && "xmlhttprequest".equals(request.getHeader("X-Requested-With").toLowerCase());
    }

    public static boolean isGoodBrowser(HttpServletRequest request, Collection<UserAgent> userAgents) {
        WebClient client = WebClient.detect(request);
        return userAgents.contains(client.getUserAgent());
    }
}
