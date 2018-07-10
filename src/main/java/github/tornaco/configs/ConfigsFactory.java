package github.tornaco.configs;

import com.google.common.reflect.Reflection;
import github.tornaco.configs.internal.ConfigsInvocationHandler;
import github.tornaco.configs.internal.PropsManager;

public class ConfigsFactory {

    public static <T extends Configs> T create(Class<T> clazz, String propString) {
        PropsManager propsManager = new PropsManager(propString);
        ConfigsInvocationHandler invocationHandler = new ConfigsInvocationHandler(propsManager);
        return Reflection.newProxy(clazz, invocationHandler);
    }
}
