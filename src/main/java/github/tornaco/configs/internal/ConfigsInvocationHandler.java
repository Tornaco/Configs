package github.tornaco.configs.internal;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import github.tornaco.configs.mapping.Def;
import github.tornaco.configs.mapping.Key;
import github.tornaco.configs.mapping.Value;
import lombok.Getter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ConfigsInvocationHandler implements InvocationHandler {

    private static final String METHOD_PREFIX = "get";

    @Getter
    private PropsManager propsManager;

    public ConfigsInvocationHandler(PropsManager propsManager) {
        this.propsManager = propsManager;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object value;

        // Check if value is override.
        boolean hasValueOverride = method.isAnnotationPresent(Value.class);
        if (hasValueOverride) {
            Value overrideValue = method.getAnnotation(Value.class);
            value = overrideValue.value();
            return castValue(method, value);
        } else {
            String key = defaultKeyForMethod(method);
            boolean hasKeyOverride = method.isAnnotationPresent(Key.class);
            if (hasKeyOverride) {
                Key overrideKey = method.getAnnotation(Key.class);
                key = overrideKey.value();
            }
            value = propsManager.get(key);
            if (value == null) {
                boolean hasDef = method.isAnnotationPresent(Def.class);
                if (hasDef) {
                    value = method.getAnnotation(Def.class).value();
                }
            }
            return castValue(method, value);
        }
    }

    // getName -> Name
    // getSerialNumber -> SerialNumber
    // Just remove the METHOD_PREFIX.
    private static String defaultKeyForMethod(Method method) {
        return method.getName().replaceFirst(METHOD_PREFIX, "");
    }

    private static Object castValue(Method method, Object value) {
        Class<?> returnType = method.getReturnType();
        if (ClassUtils.isPrimitiveOrWrapper(returnType)) {
            if (returnType == int.class || returnType == Integer.class) {
                return Integer.parseInt(String.valueOf(value));
            } else if (returnType == boolean.class || returnType == Boolean.class) {
                return Boolean.parseBoolean(String.valueOf(value));
            } else if (returnType == long.class || returnType == Long.class) {
                return Long.parseLong(String.valueOf(value));
            } else if (returnType == float.class || returnType == Float.class) {
                return Float.parseFloat(String.valueOf(value));
            } else if (returnType == double.class || returnType == Double.class) {
                return Double.parseDouble(String.valueOf(value));
            } else if (returnType == short.class || returnType == Short.class) {
                return Short.parseShort(String.valueOf(value));
            } else {
                // Not support yet.
            }
        } else {
            // Check if string.
            if (returnType == String.class || returnType == CharSequence.class) {
                return String.valueOf(value);
            }

            // Convert to Object using json.
            Gson gson = new Gson();
            try {
                return gson.fromJson(String.valueOf(value), returnType);
            } catch (JsonSyntaxException jse) {
                // This is not json, it's OK.
            }
        }
        return null;
    }
}
