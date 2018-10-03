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
package org.apache.sling.feature.launcher.spi.extensions;

import org.apache.sling.feature.Artifact;
import org.apache.sling.feature.Extension;
import org.apache.sling.feature.ExtensionType;
import org.apache.sling.feature.Feature;
import org.apache.sling.feature.builder.ArtifactResolver;
import org.apache.sling.feature.builder.FeatureExtensionHandler;
import org.osgi.framework.Constants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class FeatureMapperExtensionHandler implements FeatureExtensionHandler {
    private ArtifactResolver artifactResolver;

    @Override
    public void initialize(ArtifactResolver resolver) {
        artifactResolver = resolver;
    }

    @Override
    public boolean canMerge(Extension extension) {
        System.out.println("**** canMerge");
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void merge(Feature target, Feature source, Extension extension) {
        System.out.println("**** merge");
        // TODO Auto-generated method stub

    }

    @Override
    public void postProcess(Feature target, Feature source, Extension extension) {
        if (extension != null)
            return;
        System.out.println("**** postProcess");

        try {
            Map<String, String> bundleFeatureMap = collectFeatureMappings(source);
            Extension newExtension = createBundleFeatureExtension(bundleFeatureMap);
            target.getExtensions().add(newExtension);
        } catch (IOException e) {
            throw new RuntimeException("Problem mapping bundles to features", e);
        }
    }

    private static Extension createBundleFeatureExtension(Map<String, String> bundleFeatureMap) {
        Extension bundleFeatureExtension = new Extension(ExtensionType.JSON, "bundle-feature-mapping", true);
        JsonArrayBuilder ab = Json.createArrayBuilder();
        JsonObjectBuilder ob = Json.createObjectBuilder();
        for (Map.Entry<String, String> entry : bundleFeatureMap.entrySet()) {
            ob.add(entry.getKey(), entry.getValue());
        }
        ab.add(ob);
        bundleFeatureExtension.setJSON(ab.build().toString());
        return bundleFeatureExtension;
    }

    /*
    private static Extension createFeatureRegionExtension(Map<String, Set<String>> regionPackageMap,
            Map<String, Set<String>> featureRegionMap) {
        Extension featureRegionExtension = new Extension(ExtensionType.JSON, "feature-region-mapping", true);
        JsonArrayBuilder ab = Json.createArrayBuilder();

        JsonObjectBuilder pob = Json.createObjectBuilder();
        for (Map.Entry<String, Set<String>> entry : regionPackageMap.entrySet()) {
            JsonArrayBuilder pab = Json.createArrayBuilder();
            entry.getValue().stream().forEach(pab::add);
            pob.add(entry.getKey(), pab);
        }
        JsonObjectBuilder ob = Json.createObjectBuilder();
        ob.add("packages", pob);

        JsonObjectBuilder fob = Json.createObjectBuilder();
        for (Map.Entry<String, Set<String>> entry : featureRegionMap.entrySet()) {
            JsonArrayBuilder rab = Json.createArrayBuilder();
            entry.getValue().stream().forEach(rab::add);
            fob.add(entry.getKey(), rab);
        }
        ob.add("regions", fob);
        ab.add(ob);

        featureRegionExtension.setJSON(ab.build().toString());
        return featureRegionExtension;
    }
    */

    private Map<String, String> collectFeatureMappings(Feature f) throws IOException {
        Map<String, String> bundleFeatureMap = new HashMap<>();
        String featureID = f.getId().toMvnId();
        for (Artifact bundle : f.getBundles()) {
            File bundleFile = artifactResolver.getFile(bundle);
            String bsn = getBsn(bundleFile);

            bundleFeatureMap.put(bsn, featureID);
        }
        return bundleFeatureMap;

        /*
        for (Iterator<Extension> it = f.getExtensions().iterator(); it.hasNext(); ) {
            Extension e = it.next();
            if ("api-regions".equals(e.getName())) {
                it.remove(); // api-regions is not needed in the application.json

                Set<String> regions = featureRegionMap.get(featureID);
                if (regions == null) {
                    regions = new HashSet<>();
                    featureRegionMap.put(featureID, regions);
                }

                JsonArray ja = Json.createReader(new StringReader(e.getJSON())).readArray();
                for (JsonValue jv : ja) {
                    if (jv instanceof JsonString) {
                        regions.add(((JsonString) jv).getString());
                    } else if (jv instanceof JsonObject) {
                        JsonObject jo = (JsonObject) jv;
                        String name = jo.getString("name");
                        regions.add(name);

                        JsonArray exports = jo.getJsonArray("exports");
                        Set<String> packages = regionPackageMap.get(name);
                        if (packages == null) {
                            packages = new HashSet<>();
                            regionPackageMap.put(name, packages);
                        }
                        for (JsonValue p : exports) {
                            if (p instanceof JsonString) {
                                packages.add(((JsonString) p).getString());
                            }
                        }
                    }
                }
            }
        }
        */
    }

    private static String getBsn(File artifactFile) throws IOException {
        try (JarFile jf = new JarFile(artifactFile)) {
            Attributes attrs = jf.getManifest().getMainAttributes();
            String bsn = attrs.getValue(Constants.BUNDLE_SYMBOLICNAME);
            String version = attrs.getValue(Constants.BUNDLE_VERSION);
            if (version == null)
                version = "0.0.0";
            return bsn + ":" + version;
        }
    }
}
