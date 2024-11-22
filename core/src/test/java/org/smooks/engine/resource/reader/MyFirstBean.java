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

import java.util.List;
import java.util.Map;

@OrderedClass
public class MyFirstBean {
    @FieldOrder(order = 1)
    private Integer a;
    @FieldOrder(order = 2)
    private Integer b;
    @FieldOrder(order = 3)
    private Boolean c;
    @FieldOrder(order = 4)
    private MySecondBean d;
    @FieldOrder(order = 5)
    private Map<String, MyFourthBean> e;
    @FieldOrder(order = 6)
    private List<List<MyFifthBean>> f;

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }

    public Boolean getC() {
        return c;
    }

    public void setC(Boolean c) {
        this.c = c;
    }

    public MySecondBean getD() {
        return d;
    }

    public void setD(MySecondBean d) {
        this.d = d;
    }

    public Map<String, MyFourthBean> getE() {
        return e;
    }

    public void setE(Map<String, MyFourthBean> e) {
        this.e = e;
    }

    public List<List<MyFifthBean>> getF() {
        return f;
    }

    public void setF(List<List<MyFifthBean>> f) {
        this.f = f;
    }
}
