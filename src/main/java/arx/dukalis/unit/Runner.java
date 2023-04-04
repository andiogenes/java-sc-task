package arx.dukalis.unit;

import arx.dukalis.unit.api.After;
import arx.dukalis.unit.api.Before;
import arx.dukalis.unit.api.Test;
import arx.dukalis.unit.internal.Report;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    public static void main(String[] args) {
        int numberOfThreads = Integer.parseInt(args[0]);

        ExecutorService testProcessors = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 1; i < args.length; i++) {
            final String className = args[i];
            testProcessors.submit(() -> {
                try {
                    process(className);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        testProcessors.shutdown();
    }

    private static void process(String className) throws Exception {
        Class<?> klass = Class.forName(className);

        final Report report = processClass(klass);
        System.out.println("[" + className + "] Total: " + (report.passed() + report.failed()) + ", passed: " + report.passed() + ", failed: " + report.failed());
    }

    private static Report processClass(Class<?> klass) throws Exception {
        final Method[] methods = klass.getDeclaredMethods();

        final Queue<Method> beforeMethods = new LinkedList<>();
        final Queue<Method> testMethods = new LinkedList<>();
        final Queue<Method> afterMethods = new LinkedList<>();

        for (Method m : methods) {
            if (m.isAnnotationPresent(Before.class)) {
                beforeMethods.add(m);
            } else if (m.isAnnotationPresent(Test.class)) {
                testMethods.add(m);
            } else if (m.isAnnotationPresent(After.class)) {
                afterMethods.add(m);
            }
        }

        final Object classInstance = klass.getDeclaredConstructor().newInstance();

        for (Method m : beforeMethods) {
            m.setAccessible(true);
            m.invoke(classInstance);
        }

        int passed = 0;
        int failed = 0;

        final String formattedClassName = "[" + klass.getName() + "] ";

        for (Method m : testMethods) {
            final String methodName = m.getName();
            final String messagePrefix = formattedClassName + "Test `" + methodName + "` ";

            m.setAccessible(true);

            final Class<?> shouldRaise = m.getAnnotation(Test.class).shouldRaise();

            try {
                m.invoke(classInstance);
                System.out.println(messagePrefix + "passed");
                passed++;
            } catch (InvocationTargetException exception) {
                final Throwable targetException = exception.getTargetException();
                if (shouldRaise.isInstance(targetException)) {
                    System.out.println(messagePrefix + "passed");
                    passed++;
                } else {
                    System.out.println(messagePrefix + "failed");
                    targetException.printStackTrace();
                    failed++;
                }
            }
        }

        for (Method m : afterMethods) {
            m.setAccessible(true);
            m.invoke(classInstance);
        }

        return new Report(passed, failed);
    }
}