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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class XStreamReaderWithSortedFieldOrderTest {
    private static Map<Class<?>, Map<String, Integer>> expectedBeanOrder;
    private static XStream derivedXStream;

    @BeforeAll
    static void setup() {
        derivedXStream = XStreamReaderWithSortedFieldOrder.getXStreamAdjustedForOrderedFields(Collections.singletonList(new MyFirstBean()));
        // first beanOrderMap
        expectedBeanOrder = new HashMap<>();
        expectedBeanOrder.put(MyFirstBean.class, getFirstBeanOrderMap());
        expectedBeanOrder.put(MySecondBean.class, getSecondBeanOrderMap());
        expectedBeanOrder.put(MyThirdBean.class, getThirdBeanOrderMap());
        expectedBeanOrder.put(MyFourthBean.class, getFourthBeanOrderMap());
        expectedBeanOrder.put(MyFifthBean.class, new HashMap<>());
    }

    private static Map<String, Integer> getFirstBeanOrderMap() {
        Map<String, Integer> beanOrderMap = new HashMap<>();
        beanOrderMap.put("a", 1);
        beanOrderMap.put("b", 2);
        beanOrderMap.put("c", 3);
        beanOrderMap.put("d", 4);
        beanOrderMap.put("e", 5);
        beanOrderMap.put("f", 6);
        return beanOrderMap;
    }

    private static Map<String, Integer> getSecondBeanOrderMap() {
        Map<String, Integer> beanOrder = new HashMap<>();
        beanOrder.put("thirdBeans", 1);
        beanOrder.put("c", 2);
        return beanOrder;
    }

    private static Map<String, Integer> getThirdBeanOrderMap() {
        Map<String, Integer> beanOrder = new HashMap<>();
        beanOrder.put("a", 1);
        beanOrder.put("d", 2);
        beanOrder.put("f", 3);
        return beanOrder;
    }

    private static Map<String, Integer> getFourthBeanOrderMap() {
        Map<String, Integer> beanOrder = new HashMap<>();
        beanOrder.put("a", 1);
        beanOrder.put("b", 2);
        return beanOrder;
    }

    @Test
    void testFieldOrderMappingForBeans() {
        test(new MyFirstBean());
        test(new MySecondBean());
        test(new MyThirdBean());
        test(new MyFourthBean());
        test(new MyFifthBean());
    }

    void test(Object beanClass) {
        final Map<String, Integer> actualOrder = new LinkedHashMap<>();
        derivedXStream.getReflectionProvider().visitSerializableFields(beanClass, (s, aClass, aClass1, o) -> {
            actualOrder.put(s, actualOrder.size() + 1);
        });
        Assertions.assertEquals(expectedBeanOrder.get(beanClass.getClass()), actualOrder);
    }
}
