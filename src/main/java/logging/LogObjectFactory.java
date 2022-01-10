package logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LogObjectFactory {

    public static <O, T> T create(O object, Class<T> classInterface) {
        return create(object, classInterface, "");
    }

    public static <O, T> T create(O object, Class<T> classInterface, String tag) {
        var logger = LogFactory.getLog(classInterface);
        return create(object, classInterface, tag, logger);
    }

    public static <O, T> T create(O object, Class<T> classInterface, Log logger) {
        return create(object, classInterface, "", logger);
    }

    public static <O, T> T create(O object, Class<T> classInterface, String tag, Log logger) {
        Map<Method, LogConfig> methodToLogConfig = getMethodAutoLogParams(classInterface);
        var proxyLogInvocationHandler = new LogMethodInvocationHandler(object, tag, methodToLogConfig, logger);
        return classInterface.cast(Proxy.newProxyInstance(
                object.getClass().getClassLoader(),
                new Class[]{classInterface},
                proxyLogInvocationHandler));
    }

    private static <T> Map<Method, LogConfig> getMethodAutoLogParams(Class<T> classInterface) {
        return Arrays.stream(classInterface.getMethods())
                .filter(method -> method.isAnnotationPresent(AddLog.class))
                .collect(Collectors.toMap(
                        Function.identity(),
                        LogObjectFactory::getAutoLogParams
                ));
    }

    private static LogConfig getAutoLogParams(Method method) {
        var addLog = method.getAnnotation(AddLog.class);
        return new LogConfig(addLog.level(), addLog.logInput(), addLog.logOutput(), addLog.logTime());
    }

}
