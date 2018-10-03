/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.feature.launcher.impl.extensions.handlers;

import org.apache.sling.feature.Extension;
import org.apache.sling.feature.launcher.spi.LauncherPrepareContext;
import org.apache.sling.feature.launcher.spi.extensions.ExtensionHandler;
import org.apache.sling.feature.launcher.spi.extensions.ExtensionInstallationContext;

import java.io.StringReader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class BundleFeatureMappingExtensionHandler implements ExtensionHandler {

    @Override
    public boolean handle(Extension extension, LauncherPrepareContext prepareContext,
            ExtensionInstallationContext installationContext) throws Exception {
        if (extension.getName().equals("bundle-feature-mapping")) {
            Dictionary<String, Object> props = new Hashtable<>();
            JsonArray ja = Json.createReader(new StringReader(extension.getJSON())).readArray();
            for (JsonValue jv : ja) {
                if (jv instanceof JsonObject) {
                    JsonObject json = (JsonObject) jv;
                    for (Map.Entry<String, JsonValue> entry : json.entrySet()) {
                        props.put(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            if (props.size() > 0) {
                installationContext.addConfiguration("org.apache.sling.feature.service", null, props);
            }
            return true;
        }
        return false;
    }
}
