package com.zcmedical.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.os.Bundle;

import com.zcmedical.common.constant.CommonConstant;

public class CommonRequest {

    public CommonRequest() {
        super();
        if (null == requestParamsSortedMap) {
            requestParamsSortedMap = new TreeMap<String, Object>();
        }
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestApiName() {
        return requestApiName;
    }

    public void setRequestApiName(String requestApiName) {
        this.requestApiName = requestApiName;
    }

    public SortedMap<String, Object> getRequestParamsSortedMap() {
        return requestParamsSortedMap;
    }

    public Bundle getAdditionalArgsBundle() {
        return additionalArgsBundle;
    }

    public Map<String, Object> getAdditionalArgsMap() {
        return additionalArgsMap;
    }

    public void addRequestParam(String paramKey, Object paramValue) {
        if (null != paramKey) {
            if (null == requestParamsSortedMap) {
                requestParamsSortedMap = new TreeMap<String, Object>();
            }
            requestParamsSortedMap.put(paramKey, paramValue);
        } // if (null != paramKey)
    }

    public Object getRequestParamValue(String paramKey) {
        Object paramValue = null;

        if (null != paramKey && null != requestParamsSortedMap) {
            paramValue = requestParamsSortedMap.get(paramKey);
        }

        return paramValue;
    }

    public void addAdditionalArg(String argKey, Object argValue) {
        if (null != argKey) {
            if (null == additionalArgsMap) {
                additionalArgsMap = new HashMap<String, Object>();
            }
            additionalArgsMap.put(argKey, argValue);
        } // if (null != argKey)
    }

    public Object getAdditionalArgValue(String argKey) {
        Object argValue = null;

        if (null != argKey && null != additionalArgsMap) {
            argValue = additionalArgsMap.get(argKey);
        }

        return argValue;
    }

    public String getRequestURLBaseString() {

        if (null == this.requestURLBaseString) {
            this.requestURLBaseString = REQUEST_URL_BASE_STRING;
        }

        return this.requestURLBaseString;
    }

    public void setRequestURLBaseString(String requestURLBaseString) {
        this.requestURLBaseString = requestURLBaseString;
    }

    private static final String REQUEST_URL_BASE_STRING = CommonConstant.HTTP_HOST + "/tpq";

    public static final String REQUEST_IS_ONLINE_AVAILABLE = "REQUEST_IS_ONLINE_AVAILABLE";

    private String requestURLBaseString;

    private String requestID;

    private String requestApiName;

    private SortedMap<String, Object> requestParamsSortedMap;

    private Bundle additionalArgsBundle;

    private Map<String, Object> additionalArgsMap;
}
