package com.javadeobfuscator.deobfuscator.executor;

import com.javadeobfuscator.deobfuscator.executor.providers.Provider;
import com.javadeobfuscator.deobfuscator.utils.WrappedClassNode;

import java.io.File;
import java.util.*;

public class Context { //FIXME clinit classes
    private List<StackTraceElement> context = new ArrayList<>();

    public Provider provider;
    public Map<String, WrappedClassNode> dictionary;

    public Set<String> clinit = new HashSet<>();

    public File file;

    public Context(Provider provider) {
        this.provider = provider;
    }

    public StackTraceElement at(int index) {
        return context.get(index);
    }

    public StackTraceElement pop() {
        return context.remove(0);
    }

    public void push(String clazz, String method, int constantPoolSize) {
        clazz = clazz.replace('/', '.');
        context.add(0, new StackTraceElement(clazz, method, "", constantPoolSize));
    }

    public int size() {
        return context.size();
    }

    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] orig = new StackTraceElement[size()];
        for (int i = 0; i < size(); i++) {
            StackTraceElement e = at(i);
            orig[i] = new StackTraceElement(e.getClassName(), e.getMethodName(), null, -1);
        }
        return orig;
    }
}
