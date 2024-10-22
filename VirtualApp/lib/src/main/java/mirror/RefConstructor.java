package mirror;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * T对象的构造函数
 * @param <T>
 */
public class RefConstructor<T> {
    private Constructor<?> ctor;

    /**
     * 从Class中找到和field同名的属性，把属性对应
     * @param cls
     * @param field
     * @throws NoSuchMethodException
     */
    public RefConstructor(Class<?> cls, Field field) throws NoSuchMethodException {
        if (field.isAnnotationPresent(MethodParams.class)) {
            Class<?>[] types = field.getAnnotation(MethodParams.class).value();
            ctor = cls.getDeclaredConstructor(types);
            if (cls.getName().equals("")) {

            }
        } else if (field.isAnnotationPresent(MethodReflectParams.class)) {
            String[] values = field.getAnnotation(MethodReflectParams.class).value();
            Class[] parameterTypes = new Class[values.length];
            int N = 0;
            while (N < values.length) {
                try {
                    parameterTypes[N] = Class.forName(values[N]);
                    N++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ctor = cls.getDeclaredConstructor(parameterTypes);
        } else {
            ctor = cls.getDeclaredConstructor();
        }
        if (ctor != null && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    public T newInstance() {
        try {
            return (T) ctor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public T newInstance(Object... params) {
        try {
            return (T) ctor.newInstance(params);
        } catch (Exception e) {
            return null;
        }
    }
}