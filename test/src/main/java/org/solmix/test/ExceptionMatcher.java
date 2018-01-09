
package org.solmix.test;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;


/**
 * 用来检查一个异常的类型和message内容。
 *
 */
public class ExceptionMatcher<T extends Throwable> extends BaseMatcher<T> {
    private final Matcher<?>                 exceptionMatcher;
    private final Matcher<?>                 causeExceptionMatcher;
    private final Matcher<?>                 messageMatcher;
    private final Class<? extends Throwable> cause;

    public ExceptionMatcher(String... snippets) {
        this(null, snippets);
    }

    public ExceptionMatcher(Class<? extends Throwable> cause, String... snippets) {
        // exception matcher


        exceptionMatcher = allOf(notNullValue(),instanceOf(Throwable.class));

        // cause exception matcher
        if (cause != null) {


            causeExceptionMatcher = allOf(notNullValue(),instanceOf(cause));
        } else {
            causeExceptionMatcher = null;
        }

        this.cause = cause;

        // message exception matcher
        if (snippets != null && snippets.length > 0) {
            LinkedList<Matcher<?>> matchers = new LinkedList<>();

            for (String snippet : snippets) {
                matchers.add(containsString(snippet));
            }
            Matcher<String>[] array = new  StringContains[matchers.size()];
            messageMatcher = allOf(matchers.toArray(array));
        } else {
            messageMatcher = null;
        }
    }

    @Override
    public boolean matches(Object item) {
        if (!exceptionMatcher.matches(item)) {
            return false;
        }
        Throwable top = (Throwable) item;
        Throwable t = top;

        if (causeExceptionMatcher != null) {
            Set<Throwable> visited = new HashSet<Throwable>();

            for (; t != null && !cause.isInstance(t) && !visited.contains(t); t = t.getCause()) {
                visited.add(t);
            }

            if (!causeExceptionMatcher.matches(t)) {
                return false;
            }
        }

        if (messageMatcher == null) {
            return true;
        } else {
            String message = t.getMessage();

            if (t != top) {
                message += "\n" + top.getMessage();
            }

            return messageMatcher.matches(message);
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("An exception that is ").appendDescriptionOf(exceptionMatcher);

        if (causeExceptionMatcher != null) {
            description.appendText("\n  and its cause exception is ").appendDescriptionOf(causeExceptionMatcher);
        }

        if (messageMatcher != null) {
            description.appendText("\n  and its message is ").appendDescriptionOf(messageMatcher);
        }
    }
}
