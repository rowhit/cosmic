package com.cloud.api;

import org.apache.cloudstack.api.ResponseObject;
import org.apache.cloudstack.api.response.ExceptionResponse;
import org.apache.cloudstack.api.response.SuccessResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseObjectTypeAdapter implements JsonSerializer<ResponseObject> {
    public static final Logger s_logger = LoggerFactory.getLogger(ResponseObjectTypeAdapter.class.getName());

    private static Method getGetMethod(final Object o, final String propName) {
        Method method = null;
        String methodName = getGetMethodName("get", propName);
        try {
            method = o.getClass().getMethod(methodName);
        } catch (final SecurityException e1) {
            s_logger.error("Security exception in getting ResponseObject " + o.getClass().getName() + " get method for property: " + propName);
        } catch (final NoSuchMethodException e1) {
            if (s_logger.isTraceEnabled()) {
                s_logger.trace("ResponseObject " + o.getClass().getName() + " does not have " + methodName + "() method for property: " + propName +
                        ", will check is-prefixed method to see if it is boolean property");
            }
        }

        if (method != null) {
            return method;
        }

        methodName = getGetMethodName("is", propName);
        try {
            method = o.getClass().getMethod(methodName);
        } catch (final SecurityException e1) {
            s_logger.error("Security exception in getting ResponseObject " + o.getClass().getName() + " get method for property: " + propName);
        } catch (final NoSuchMethodException e1) {
            s_logger.warn("ResponseObject " + o.getClass().getName() + " does not have " + methodName + "() method for property: " + propName);
        }
        return method;
    }

    private static String getGetMethodName(final String prefix, final String fieldName) {
        final StringBuffer sb = new StringBuffer(prefix);

        if (fieldName.length() >= prefix.length() && fieldName.substring(0, prefix.length()).equals(prefix)) {
            return fieldName;
        } else {
            sb.append(fieldName.substring(0, 1).toUpperCase());
            sb.append(fieldName.substring(1));
        }

        return sb.toString();
    }

    @Override
    public JsonElement serialize(final ResponseObject responseObj, final Type typeOfResponseObj, final JsonSerializationContext ctx) {
        final JsonObject obj = new JsonObject();

        if (responseObj instanceof SuccessResponse) {
            obj.addProperty("success", ((SuccessResponse) responseObj).getSuccess());
            return obj;
        } else if (responseObj instanceof ExceptionResponse) {
            obj.addProperty("errorcode", ((ExceptionResponse) responseObj).getErrorCode());
            obj.addProperty("errortext", ((ExceptionResponse) responseObj).getErrorText());
            return obj;
        } else {
            obj.add(responseObj.getObjectName(), ApiResponseGsonHelper.getBuilder().create().toJsonTree(responseObj));
            return obj;
        }
    }
}
