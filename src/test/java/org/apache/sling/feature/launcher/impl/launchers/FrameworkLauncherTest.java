/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.sling.feature.launcher.impl.launchers;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FrameworkLauncherTest {

    @Test
    public void testAll() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("null", "${unknown}");
        map.put("animal", "cat");
        map.put("cat", "Simon");
        map.put("simple", "${animal}");
        map.put("inner", "${${animal}}");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("", map.get("null"));
        assertEquals("cat", map.get("simple"));
        assertEquals("Simon", map.get("inner"));

    }

    @Test
    public void testNonClosing2() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${${${x}");
        map.put("x", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("${${b", map.get("a"));

    }

    @Test
    public void testNonClosing() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${${x}");
        map.put("x", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("${b", map.get("a"));

    }

    @Test
    public void testNotOpen() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${x}}");
        map.put("x", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("b}", map.get("a"));

    }

    @Test
    public void testNotOpen2() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${x}}}");
        map.put("x", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("b}}", map.get("a"));

    }

    @Test
    public void testMultiSubstitute3() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${${b}-${c}}");
        map.put("b", "x");
        map.put("c", "y");
        map.put("x-y", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("b", map.get("a"));

    }

    @Test
    public void testMultiSubstitute2() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${c${d}}");
        map.put("d", "c");
        map.put("cc", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("b", map.get("a"));

    }

    @Test
    public void testMultiSubstitute() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${${d}}");
        map.put("d", "c");
        map.put("c", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("b", map.get("a"));

    }

    @Test
    public void testSubstitute() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "${c}");
        map.put("c", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("b", map.get("a"));

    }

    @Test
    public void testSubstituteBeforeAfter() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "-${c}-");
        map.put("c", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("-b-", map.get("a"));

    }

    @Test
    public void testWithoutSubst() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        map = FrameworkLauncher.substitutedFwProps(map);
        assertEquals("b", map.get("a"));

    }
}
