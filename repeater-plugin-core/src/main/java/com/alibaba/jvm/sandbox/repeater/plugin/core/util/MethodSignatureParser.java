package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link MethodSignatureParser}
 * <p>
 * 自定义方法签名解析工具
 *
 * @author zhaoyb1990
 */
public class MethodSignatureParser {

    private final static String ARRAY_IDENTIFIER = "[";

    private static Map<String, Class<?>> BASIC_CLASS_TYPE = new HashMap<String, Class<?>>(16);

    static {
        BASIC_CLASS_TYPE.put("V", void.class);
        BASIC_CLASS_TYPE.put("Z", boolean.class);
        BASIC_CLASS_TYPE.put("B", byte.class);
        BASIC_CLASS_TYPE.put("C", char.class);
        BASIC_CLASS_TYPE.put("S", short.class);
        BASIC_CLASS_TYPE.put("I", int.class);
        BASIC_CLASS_TYPE.put("J", long.class);
        BASIC_CLASS_TYPE.put("F", float.class);
        BASIC_CLASS_TYPE.put("D", double.class);
        BASIC_CLASS_TYPE.put("[Z", boolean[].class);
        BASIC_CLASS_TYPE.put("[B", byte[].class);
        BASIC_CLASS_TYPE.put("[C", char[].class);
        BASIC_CLASS_TYPE.put("[S", short[].class);
        BASIC_CLASS_TYPE.put("[I", int[].class);
        BASIC_CLASS_TYPE.put("[J", long[].class);
        BASIC_CLASS_TYPE.put("[F", float[].class);
        BASIC_CLASS_TYPE.put("[D", double[].class);
    }


    /**
     * 解析方法签名（不加载类）
     *
     * @param methodDesc 方法签名
     * @return 分离出的方法签名
     */
    public static MethodSpec parseIdentifier(String methodDesc) {
        MethodSpec methodSpec = new MethodSpec();
        String paramDesc = StringUtils.substringBetween(methodDesc, "(", ")");
        if (StringUtils.isEmpty(methodDesc)) {
            methodSpec.setParamIdentifiers(new String[0]);
        } else {
            List<String> paramIdentifiers = Lists.newArrayList();
            char[] chars = paramDesc.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : chars) {
                switch (c) {
                    case 'Z':
                    case 'B':
                    case 'C':
                    case 'S':
                    case 'I':
                    case 'J':
                    case 'F':
                    case 'D':
                        builder.append(c);
                        if (builder.length() <= 2) {
                            paramIdentifiers.add(builder.toString());
                            builder.setLength(0);
                        }
                        break;
                    case ';':
                        builder.append(c);
                        paramIdentifiers.add(builder.toString());
                        builder.setLength(0);
                        break;
                    default:
                        builder.append(c);
                        break;
                }
            }
            methodSpec.setParamIdentifiers(paramIdentifiers.toArray(new String[0]));
        }
        methodSpec.setReturnIdentifier(StringUtils.substringAfter(methodDesc, ")"));
        return methodSpec;
    }

    /**
     * 加载类数组
     *
     * @param identifiers 根据方法签名解析的标志组合
     * @param classLoader 类加载器
     * @return 类数组
     * @throws ClassNotFoundException 找不到类异常
     */
    public static Class<?>[] loadClass(String[] identifiers, ClassLoader classLoader) throws ClassNotFoundException {
        List<Class<?>> classes = Lists.newArrayList();
        if (identifiers != null && identifiers.length > 0) {
            for (String identifier : identifiers) {
                classes.add(loadClass(identifier, classLoader));
            }
        }
        return classes.toArray(new Class[0]);
    }

    /**
     * 加载单个类
     *
     * @param identifier  根据方法签名解析的标志
     * @param classLoader 类加载器
     * @return 类数组
     * @throws ClassNotFoundException 找不到类异常
     */
    public static Class<?> loadClass(String identifier, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> aClass = BASIC_CLASS_TYPE.get(identifier);
        if (aClass != null) {
            return aClass;
        }
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        // 全类名查找;[可以用Class.forName去加载
        if (StringUtils.startsWith(identifier, ARRAY_IDENTIFIER)) {
            return Class.forName(toNormalClass(identifier), true, classLoader);
        } else {
            return classLoader.loadClass(toNormalClass(extractClassName(identifier)));
        }
    }

    /**
     * 提取类名
     *
     * @param identifier 根据方法签名解析的标志
     * @return 类名
     */
    private static String extractClassName(String identifier) {
        return StringUtils.substringBetween(identifier, "L", ";");
    }

    /**
     * 转换成通用类路径
     *
     * @param identifier 根据方法签名解析的标志
     * @return 类路径
     */
    private static String toNormalClass(String identifier) {
        return identifier.replace("/", ".");
    }

    /**
     * 方法描述；
     */
    public static class MethodSpec {
        private String[] paramIdentifiers;
        private String returnIdentifier;

        public String[] getParamIdentifiers() {
            return paramIdentifiers;
        }

        public void setParamIdentifiers(String[] paramIdentifiers) {
            this.paramIdentifiers = paramIdentifiers;
        }

        public String getReturnIdentifier() {
            return returnIdentifier;
        }

        public void setReturnIdentifier(String returnIdentifier) {
            this.returnIdentifier = returnIdentifier;
        }
    }
}
