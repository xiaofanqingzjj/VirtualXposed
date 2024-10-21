package mirror;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public final class RefClass {

    private static HashMap<Class<?>,Constructor<?>> REF_TYPES = new HashMap<Class<?>, Constructor<?>>();
    static {
        try {
            REF_TYPES.put(RefObject.class, RefObject.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefMethod.class, RefMethod.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefInt.class, RefInt.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefLong.class, RefLong.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefFloat.class, RefFloat.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefDouble.class, RefDouble.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefBoolean.class, RefBoolean.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefStaticObject.class, RefStaticObject.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefStaticInt.class, RefStaticInt.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefStaticMethod.class, RefStaticMethod.class.getConstructor(Class.class, Field.class));
            REF_TYPES.put(RefConstructor.class, RefConstructor.class.getConstructor(Class.class, Field.class));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射获取某个类的信息，把类的方法或属性的反射信息保存到mappingClass对应的属性里
     *
     * 这个类主要用来调用那些在编译期访问不到的类方法
     *
     * @param mappingClass 映射类信息，映射类通保存了原始类的反射信息。
     * @param className 要反射的类
     * @return
     */
    public static Class<?> load(Class<?> mappingClass, String className) {
        try {
            return load(mappingClass, Class.forName(className));
        } catch (Exception e) {
            return null;
        }
    }


    public static Class load(Class mappingClass, Class<?> realClass) {
        // 映射类里有那些字段
        Field[] fields = mappingClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    Constructor<?> constructor = REF_TYPES.get(field.getType());
                    if (constructor != null) {
                        field.set(null, constructor.newInstance(realClass, field));
                    }
                }
            }
            catch (Exception e) {
                // Ignore
            }
        }
        return realClass;
    }

}