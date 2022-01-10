package logging;

import org.apache.commons.logging.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class LogMethodInvocationHandler implements InvocationHandler {

    private final Object implementation;
    private final Log logger;
    private final String tag;
    private final Map<Method, LogConfig> methodToLogConfig;

    public LogMethodInvocationHandler(Object implementation, String tag, Map<Method, LogConfig> methodToLogConfig, Log logger) {
        this.implementation = implementation;
        this.tag = tag;
        this.methodToLogConfig = methodToLogConfig;
        this.logger = logger;
    }

    private static String getReadableTime(Long nanos) {
        var duration = Duration.ofNanos(nanos);
        var millis = duration.toMillisPart();
        var sec = duration.toSecondsPart();
        var min = duration.toMinutesPart();
        var hour = duration.toHoursPart();
        return String.format("(%dh %dm %ds %dms)", hour, min, sec, millis);

    }

    private static String getDuration(long duration, LogConfig logConfig) {
        return logConfig.logTime() ?
                getReadableTime(duration) :
                "";
    }

    private static String getInputs(Object[] args, LogConfig shouldLog) {
        return shouldLog.logInput() && args != null ?
                " : " + Arrays.deepToString(args)
                : "";
    }

    private static String getOutput(Object output, LogConfig shouldLog) {
        return shouldLog.logOutput() ?
                " = " + output.toString()
                : "";
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var logConfig = methodToLogConfig.get(method);
        if (logConfig != null) {
            String parameters = getInputs(args, logConfig);
            log(logConfig.logLevel(), String.format("%s >> %s%s", tag, method.getName(), parameters));
            var start = System.nanoTime();
            Object result = null;
            try {
                result = method.invoke(implementation, args);
                return result;
            } finally {
                var end = System.nanoTime();
                log(logConfig.logLevel(), String.format("%s << %s%s %s", tag, method.getName(), getOutput(result, logConfig), getDuration(end - start, logConfig)));
            }
        } else {
            return method.invoke(implementation, args);
        }
    }

    private void log(Level level, String message) {
        switch (level) {

            case FATAL:
                if (logger.isFatalEnabled()) {
                    logger.fatal(message);
                }
                break;
            case ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                break;
            case WARN:
                if (logger.isWarnEnabled()) {
                    logger.warn(message);
                }
                break;
            case INFO:
                if (logger.isInfoEnabled()) {
                    logger.info(message);
                }
                break;
            case DEBUG:
                if (logger.isDebugEnabled()) {
                    logger.debug(message);
                }
                break;
            case TRACE:
                if (logger.isTraceEnabled()) {
                    logger.trace(message);
                }
                break;
        }
    }


}
