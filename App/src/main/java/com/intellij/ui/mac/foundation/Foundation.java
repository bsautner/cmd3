/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.ui.mac.foundation;

import com.intellij.util.containers.HashMap;
import com.sun.jna.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class Foundation {
    private static final FoundationLibrary myFoundationLibrary;

    static {
        // Set JNA to convert java.lang.String to char* using UTF-8, and match that with
        // the way we tell CF to interpret our char*
        // May be removed if we use toStringViaUTF16
        System.setProperty("jna.encoding", "UTF8");

        Map<String, Object> foundationOptions = new HashMap<String, Object>();
        //foundationOptions.put(Library.OPTION_TYPE_MAPPER, FoundationTypeMapper.INSTANCE);

        myFoundationLibrary = Native.loadLibrary("Foundation", FoundationLibrary.class, foundationOptions);
    }



    public static void init() { /* fake method to init foundation */ }

    private Foundation() {
    }

    /**
     * Get the ID of the NSClass with className
     */
    public static ID getObjcClass(String className) {
        return myFoundationLibrary.objc_getClass(className);
    }

    public static Pointer createSelector(String s) {
        return myFoundationLibrary.sel_registerName(s);
    }

    public static ID invoke(final ID id, final Pointer selector, Object... args) {
        return myFoundationLibrary.objc_msgSend(id, selector, args);
    }

    public static ID invoke(final String cls, final String selector, Object... args) {
        return invoke(getObjcClass(cls), createSelector(selector), args);
    }

    public static ID invoke(final ID id, final String selector, Object... args) {
        return invoke(id, createSelector(selector), args);
    }

    public static ID allocateObjcClassPair(ID superCls, String name) {
        return myFoundationLibrary.objc_allocateClassPair(superCls, name, 0);
    }

    public static void registerObjcClassPair(ID cls) {
        myFoundationLibrary.objc_registerClassPair(cls);
    }

    public static boolean isClassRespondsToSelector(ID cls, Pointer selectorName) {
        return myFoundationLibrary.class_respondsToSelector(cls, selectorName);
    }

    /**
     * @param cls          The class to which to add a method.
     * @param selectorName A selector that specifies the name of the method being added.
     * @param impl         A function which is the implementation of the new method. The function must take at least two arguments-self and _cmd.
     * @param types        An array of characters that describe the types of the arguments to the method.
     *                     See <a href="https://developer.apple.com/library/IOs/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html#//apple_ref/doc/uid/TP40008048-CH100"></a>
     * @return true if the method was added successfully, otherwise false (for example, the class already contains a method implementation with that name).
     */
    public static boolean addMethod(ID cls, Pointer selectorName, Callback impl, String types) {
        return myFoundationLibrary.class_addMethod(cls, selectorName, impl, types);
    }


    @Nullable
    public static String stringFromSelector(Pointer selector) {
        ID id = myFoundationLibrary.NSStringFromSelector(selector);
        if (id.intValue() > 0) {
            return toStringViaUTF8(id);
        }

        return null;
    }

    public static Pointer getClass(Pointer clazz) {
        return myFoundationLibrary.objc_getClass(clazz);
    }


    public static ID nsString(@NotNull String s) {
        // Use a byte[] rather than letting jna do the String -> char* marshalling itself.
        // Turns out about 10% quicker for long strings.
        try {
            if (s.isEmpty()) {
                return invoke("NSString", "string");
            }

            byte[] utf16Bytes = s.getBytes("UTF-16LE");
            return invoke(invoke(invoke("NSString", "alloc"), "initWithBytes:length:encoding:", utf16Bytes, utf16Bytes.length,
                    convertCFEncodingToNS(FoundationLibrary.kCFStringEncodingUTF16LE)), "autorelease");
        } catch (UnsupportedEncodingException x) {
            throw new RuntimeException(x);
        }
    }

    @Nullable
    public static String toStringViaUTF8(ID cfString) {
        if (cfString.intValue() == 0) return null;

        int lengthInChars = myFoundationLibrary.CFStringGetLength(cfString);
        int potentialLengthInBytes = 3 * lengthInChars + 1; // UTF8 fully escaped 16 bit chars, plus nul

        byte[] buffer = new byte[potentialLengthInBytes];
        byte ok = myFoundationLibrary.CFStringGetCString(cfString, buffer, buffer.length, FoundationLibrary.kCFStringEncodingUTF8);
        if (ok == 0) throw new RuntimeException("Could not convert string");
        return Native.toString(buffer);
    }

    @Nullable
    public static String getEncodingName(long nsStringEncoding) {
        long cfEncoding = myFoundationLibrary.CFStringConvertNSStringEncodingToEncoding(nsStringEncoding);
        ID pointer = myFoundationLibrary.CFStringConvertEncodingToIANACharSetName(cfEncoding);
        String name = toStringViaUTF8(pointer);
        if ("macintosh".equals(name)) name = "MacRoman"; // JDK8 does not recognize IANA's "macintosh" alias
        return name;
    }



    private static long convertCFEncodingToNS(long cfEncoding) {
        return myFoundationLibrary.CFStringConvertEncodingToNSStringEncoding(cfEncoding) & 0xffffffffffL;  // trim to C-type limits
    }


    public static void cfRelease(ID... ids) {
        for (ID id : ids) {
            if (id != null) {
                myFoundationLibrary.CFRelease(id);
            }
        }
    }

    public static boolean isMainThread() {
        return invoke("NSThread", "isMainThread").intValue() > 0;
    }


    private static long ourCurrentRunnableCount = 0;


    public static class NSAutoreleasePool {
        private final ID myDelegate;

        public NSAutoreleasePool() {
            myDelegate = invoke(invoke("NSAutoreleasePool", "alloc"), "init");
        }

        public void drain() {
            invoke(myDelegate, "drain");
        }
    }



    public static class CGFloat implements NativeMapped {
        private final double value;

        @SuppressWarnings("UnusedDeclaration")
        public CGFloat() {
            this(0);
        }

        public CGFloat(double d) {
            value = d;
        }

        @Override
        public Object fromNative(Object o, FromNativeContext fromNativeContext) {
            switch (Native.LONG_SIZE) {
                case 4:
                    return new CGFloat((Float) o);
                case 8:
                    return new CGFloat((Double) o);
            }
            throw new IllegalStateException();
        }

        @Override
        public Object toNative() {
            switch (Native.LONG_SIZE) {
                case 4:
                    return (float) value;
                case 8:
                    return value;
            }
            throw new IllegalStateException();
        }

        @Override
        public Class<?> nativeType() {
            switch (Native.LONG_SIZE) {
                case 4:
                    return Float.class;
                case 8:
                    return Double.class;
            }
            throw new IllegalStateException();
        }
    }

    public static ID createDict(@NotNull final String[] keys, @NotNull final Object[] values) {
        final ID nsKeys = invoke("NSArray", "arrayWithObjects:", convertTypes(keys));
        final ID nsData = invoke("NSArray", "arrayWithObjects:", convertTypes(values));
        return invoke("NSDictionary", "dictionaryWithObjects:forKeys:", nsData, nsKeys);
    }

    private static Object[] convertTypes(@NotNull Object[] v) {
        final Object[] result = new Object[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = convertType(v[i]);
        }
        return result;
    }

    private static Object convertType(@NotNull Object o) {
        if (o instanceof Pointer || o instanceof ID) {
            return o;
        } else if (o instanceof String) {
            return nsString((String) o);
        } else {
            throw new IllegalArgumentException("Unsupported type! " + o.getClass());
        }
    }
}
