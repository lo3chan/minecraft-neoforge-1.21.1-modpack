/*
 * Decompiled with CFR 0.152.
 */
package de.fabmax.physxjni.macos;

import de.fabmax.physxjni.NativeLib;
import java.util.ArrayList;
import java.util.List;

public class NativeLibMacos
extends NativeLib {
    private static final String version = "2.3.2";
    private static final List<String> libraries = new ArrayList<String>(){
        {
            this.add("libPhysXJniBindings_64.dylib");
        }
    };

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    protected List<String> getLibResourceNames() {
        return libraries;
    }
}

