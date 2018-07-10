package github.tornaco.configs;

import com.google.common.reflect.Reflection;
import github.tornaco.configs.internal.ConfigsInvocationHandler;
import github.tornaco.configs.internal.PropsManager;

import java.io.File;
import java.io.IOException;

public class ConfigsFactory {

    public static <T extends Configs> T create(Class<T> clazz, String propString) {
        PropsManager propsManager = new PropsManager(propString);
        ConfigsInvocationHandler invocationHandler = new ConfigsInvocationHandler(propsManager);
        return Reflection.newProxy(clazz, invocationHandler);
    }

    public static <T extends Configs> T create(Class<T> clazz, File propFile) throws IOException {
        PropsManager propsManager = new PropsManager(propFile);
        ConfigsInvocationHandler invocationHandler = new ConfigsInvocationHandler(propsManager);
        return Reflection.newProxy(clazz, invocationHandler);
    }
}
