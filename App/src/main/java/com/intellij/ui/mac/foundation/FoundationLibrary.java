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

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;


public interface FoundationLibrary extends Library {


    ID objc_allocateClassPair(ID supercls, String name, int extraBytes);

    void objc_registerClassPair(ID cls);

    byte CFStringGetCString(ID theString, byte[] buffer, int bufferSize, int encoding);

    int CFStringGetLength(ID theString);

    long CFStringConvertNSStringEncodingToEncoding(long nsEncoding);

    ID CFStringConvertEncodingToIANACharSetName(long cfEncoding);

    long CFStringConvertEncodingToNSStringEncoding(long cfEncoding);

    void CFRelease(ID cfTypeRef);

    ID objc_getClass(String className);

    Pointer sel_registerName(String selectorName);

    ID objc_msgSend(ID receiver, Pointer selector, Object... args);

    boolean class_respondsToSelector(ID cls, Pointer selName);

    boolean class_addMethod(ID cls, Pointer selName, Callback imp, String types);


    boolean class_isMetaClass(ID cls);

    ID NSStringFromSelector(Pointer selector);

    Pointer objc_getClass(Pointer clazz);

    int kCFStringEncodingUTF8 = 0x08000100;

    int kCFStringEncodingUTF16LE = 0x14000100;


}
