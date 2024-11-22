/*-
 * ========================LICENSE_START=================================
 * Smooks Core
 * %%
 * Copyright (C) 2020 - 2024 Smooks
 * %%
 * Licensed under the terms of the Apache License Version 2.0, or
 * the GNU Lesser General Public License version 3.0 or later.
 * 
 * SPDX-License-Identifier: Apache-2.0 OR LGPL-3.0-or-later
 * 
 * ======================================================================
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ======================================================================
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * =========================LICENSE_END==================================
 */
package org.smooks.engine.resource.reader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class XStreamReaderWithSortedFieldOrder {
    private XStreamReaderWithSortedFieldOrder() {
        throw new RuntimeException("Illegal constructor access");
    }

    public static XStream getXStreamAdjustedForOrderedFields(List<Object> sourceObject) {
        final SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        final Set<Class<?>> beanClasses = new HashSet<>();
        sourceObject.forEach(sourceBean -> findBeanClassesRecursive(sourceBean.getClass(), beanClasses));
        beanClasses.forEach(beanClass -> {
            String[] fieldOrder = composeFieldOrderPerBean(beanClass);
            if (fieldOrder.length > 1) {
                sorter.registerFieldOrder(beanClass, fieldOrder);
            }
        });

        return new XStream(new PureJavaReflectionProvider(new FieldDictionary(sorter)));
    }

    private static void findBeanClassesRecursive(Class<?> clazz, Set<Class<?>> beanClasses) {
        // Avoid adding primitives, wrapper classes, or null
        if (clazz == null || isPrimitiveOrWrapper(clazz) || beanClasses.contains(clazz)) {
            return;
        }
        // Add the current class if it's not already added
        beanClasses.add(clazz);
        // Check the fields of the class
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // Get the field's type
            Class<?> fieldType = field.getType();
            // Handle different types of fields (primitives, beans, collections, etc.)
            if (isCollectionOrMap(fieldType)) {
                // Handle generic collections
                resolveNestedParameterizedType(field.getGenericType(), beanClasses);
            } else if (fieldType.isArray()) {
                // Handle arrays
                findBeanClassesRecursive(fieldType.getComponentType(), beanClasses);
            } else {
                // If it's a bean, recursively process its fields
                findBeanClassesRecursive(fieldType, beanClasses);
            }
        }
    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(Integer.class) || clazz.equals(Boolean.class) ||
                clazz.equals(Byte.class) || clazz.equals(Character.class) || clazz.equals(Short.class) ||
                clazz.equals(Long.class) || clazz.equals(Float.class) || clazz.equals(Double.class) || clazz == String.class ;
    }

    private static boolean isCollectionOrMap(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
    }

    private static void resolveNestedParameterizedType(Type type, Set<Class<?>> beanClasses) {
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            for (Type actualType : paramType.getActualTypeArguments()) {
                if (actualType instanceof Class) {
                    findBeanClassesRecursive((Class<?>) actualType, beanClasses);
                } else if (actualType instanceof ParameterizedType) {
                    resolveNestedParameterizedType(actualType, beanClasses);
                }
            }
        }
    }

    private static String[] composeFieldOrderPerBean(Class<?> beanClazz) {
        if (Objects.isNull(beanClazz.getAnnotation(OrderedClass.class))) {
            return new String[0];
        }
        Map<String, Integer> nameFieldOrderMap = new HashMap<>();
        for (Field beanField : beanClazz.getDeclaredFields()) {
            if (Objects.nonNull(beanField.getAnnotation(FieldOrder.class))) {
                FieldOrder order = beanField.getAnnotation(FieldOrder.class);
                nameFieldOrderMap.put(beanField.getName(), order.order());
            } else {
                nameFieldOrderMap.put(beanField.getName(), Integer.MAX_VALUE);
            }
        }
        return getOrderedFieldNames(nameFieldOrderMap);
    }
    private static String[] getOrderedFieldNames(Map<String, Integer> namePriorityMap) {
        return namePriorityMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey).toArray(String[]::new);
    }
}
