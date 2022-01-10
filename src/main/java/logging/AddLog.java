package logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AddLog {

    Level level() default Level.INFO;

    boolean logInput() default false;

    boolean logOutput() default false;

    boolean logTime() default false;
}
