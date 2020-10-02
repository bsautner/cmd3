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
package com.intellij.util.containers;

import com.intellij.util.ArrayUtilRt;
import com.intellij.util.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 * Stripped-down version of {@code com.intellij.util.containers.ContainerUtil}.
 * Intended to use by external (out-of-IDE-process) runners and helpers so it should not contain any library dependencies.
 *
 * @since 12.0
 */
@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
public class ContainerUtilRt {


    @NotNull
    @Contract(pure = true)
    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<T>();
    }

    @NotNull
    @Contract(pure = true)
    public static <T> ArrayList<T> newArrayList(@NotNull T... elements) {
        ArrayList<T> list = newArrayListWithCapacity(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    @NotNull
    @Contract(pure = true)
    public static <T> ArrayList<T> newArrayList(@NotNull Iterable<? extends T> elements) {
        if (elements instanceof Collection) {
            @SuppressWarnings("unchecked") Collection<? extends T> collection = (Collection<? extends T>) elements;
            return new ArrayList<T>(collection);
        }
        return copy(ContainerUtilRt.<T>newArrayList(), elements);
    }

    @NotNull
    @Contract(pure = true)
    public static <T> ArrayList<T> newArrayListWithCapacity(int size) {
        return new ArrayList<T>(size);
    }

    @NotNull
    private static <T, C extends Collection<T>> C copy(@NotNull C collection, @NotNull Iterable<? extends T> elements) {
        for (T element : elements) {
            collection.add(element);
        }
        return collection;
    }





    /**
     * A variant of {@link Collections#emptyList()},
     * except that {@link #toArray()} here does not create garbage <code>new Object[0]</code> constantly.
     */
    private static class EmptyList<T> extends AbstractList<T> implements RandomAccess, Serializable {
        private static final long serialVersionUID = 1L;

        private static final EmptyList INSTANCE = new EmptyList();

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean contains(Object obj) {
            return false;
        }

        @Override
        public T get(int index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return ArrayUtilRt.EMPTY_OBJECT_ARRAY;
        }

        @NotNull
        @Override
        public <E> E[] toArray(@NotNull E[] a) {
            if (a.length != 0) {
                a[0] = null;
            }
            return a;
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return EmptyIterator.getInstance();
        }
    }

    @NotNull
    @Contract(pure = true)
    public static <T> List<T> emptyList() {
        //noinspection unchecked
        return (List<T>) EmptyList.INSTANCE;
    }


    @Deprecated
    public static <T> void addIfNotNull(@Nullable T element, @NotNull Collection<T> result) {
        if (element != null) {
            result.add(element);
        }
    }


    /**
     * @return read-only list consisting of the elements from array converted by mapper
     */
    @NotNull
    @Contract(pure = true)
    public static <T, V> List<V> map2List(@NotNull T[] array, @NotNull Function<T, V> mapper) {
        return map2List(Arrays.asList(array), mapper);
    }

    /**
     * @return read-only list consisting of the elements from collection converted by mapper
     */
    @NotNull
    @Contract(pure = true)
    public static <T, V> List<V> map2List(@NotNull Collection<? extends T> collection, @NotNull Function<T, V> mapper) {
        if (collection.isEmpty()) return emptyList();
        List<V> list = new ArrayList<V>(collection.size());
        for (final T t : collection) {
            list.add(mapper.fun(t));
        }
        return list;
    }


}
