package com.alibaba.jvm.sandbox.repeater.plugin.domain;


/**
 * <p>
 *
 * @author zhaoyb1990
 */
public class Behavior {
    private String classPattern;
    private String[] methodPatterns;
    private boolean includeSubClasses;

    public Behavior() {
    }

    public Behavior(String classPattern, String... methodPatterns) {
        this.classPattern = classPattern;
        this.methodPatterns = methodPatterns;
    }

    public Behavior(String classPattern, String[] methodPatterns, boolean includeSubClasses) {
        this.classPattern = classPattern;
        this.methodPatterns = methodPatterns;
        this.includeSubClasses = includeSubClasses;
    }

    public String getClassPattern() {
        return classPattern;
    }

    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    public String[] getMethodPatterns() {
        return methodPatterns;
    }

    public void setMethodPatterns(String[] methodPatterns) {
        this.methodPatterns = methodPatterns;
    }

    public boolean isIncludeSubClasses() {
        return includeSubClasses;
    }

    public void setIncludeSubClasses(boolean includeSubClasses) {
        this.includeSubClasses = includeSubClasses;
    }
}
