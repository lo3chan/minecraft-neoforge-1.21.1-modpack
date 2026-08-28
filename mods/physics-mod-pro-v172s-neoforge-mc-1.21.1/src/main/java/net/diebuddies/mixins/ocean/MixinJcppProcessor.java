/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor
 *  org.apache.commons.lang3.StringUtils
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package net.diebuddies.mixins.ocean;

import net.diebuddies.compat.Iris;
import net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(value={JcppProcessor.class})
public class MixinJcppProcessor {
    @ModifyVariable(at=@At(value="HEAD"), method={"glslPreprocessSource"}, remap=false, ordinal=0)
    private static String glslPreprocessSource(String source) {
        if (Iris.preprocessOceanStage.get() != null && source != null) {
            if (source.contains("#define PHYSICS_OCEAN_SUPPORT")) {
                source = StringUtils.replace((String)source, (String)"#define PHYSICS_OCEAN_SUPPORT", (String)"#define PHYSICS_OCEAN");
                switch (Iris.preprocessOceanStage.get()) {
                    case VERTEX: {
                        Iris.vertexShaderSupportsOcean.set(true);
                        break;
                    }
                    case GEOMETRY: {
                        Iris.geometryShaderSupportsOcean.set(true);
                        break;
                    }
                    case FRAGMENT: {
                        Iris.fragmentShaderSupportsOcean.set(true);
                        break;
                    }
                }
            } else {
                if (source.contains("#define PHYSICS_OCEAN_INJECTION")) {
                    source = StringUtils.replace((String)source, (String)"#define PHYSICS_OCEAN_INJECTION", (String)"#define PHYSICS_OCEAN");
                }
                switch (Iris.preprocessOceanStage.get()) {
                    case VERTEX: {
                        Iris.vertexShaderSupportsOcean.set(false);
                        break;
                    }
                    case GEOMETRY: {
                        Iris.geometryShaderSupportsOcean.set(false);
                        break;
                    }
                    case FRAGMENT: {
                        Iris.fragmentShaderSupportsOcean.set(false);
                        break;
                    }
                }
            }
        } else {
            Iris.vertexShaderSupportsOcean.set(false);
            Iris.geometryShaderSupportsOcean.set(false);
            Iris.fragmentShaderSupportsOcean.set(false);
        }
        return source;
    }
}

