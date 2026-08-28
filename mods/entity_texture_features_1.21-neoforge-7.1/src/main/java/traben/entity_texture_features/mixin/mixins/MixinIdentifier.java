/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package traben.entity_texture_features.mixin.mixins;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(value={ResourceLocation.class})
public abstract class MixinIdentifier {
    private static final Set<String> EXCUSED_ILLEGAL_PATHS = new HashSet<String>();

    @Inject(method={"isValidPath"}, cancellable=true, at={@At(value="RETURN")}, require=0)
    private static void etf$illegalPathOverride(String path, CallbackInfoReturnable<Boolean> cir) {
        if (ETF.config().getConfig() != null && ETF.config().getConfig().illegalPathSupportMode != ETFConfig.IllegalPathMode.None && !((Boolean)cir.getReturnValue()).booleanValue() && path != null) {
            if (path.equals("DUMMY") || path.isBlank()) {
                return;
            }
            switch (ETF.config().getConfig().illegalPathSupportMode) {
                case Entity: {
                    if (!path.contains("/entity/") && !path.contains("optifine/") && !path.contains("etf/") && !path.contains("emf/") || !path.endsWith(".png") && !path.endsWith(".properties") && !path.endsWith(".mcmeta") && !path.endsWith(".jem") && !path.endsWith(".jpm")) break;
                    MixinIdentifier.allowPathAndLog(path, cir);
                    break;
                }
                case All: {
                    MixinIdentifier.allowPathAndLog(path, cir);
                    break;
                }
                default: {
                    ETFUtils2.logWarn("this message should not appear #65164");
                }
            }
        }
    }

    @Unique
    private static void allowPathAndLog(String path, CallbackInfoReturnable<Boolean> cir) {
        if (!EXCUSED_ILLEGAL_PATHS.contains(path)) {
            EXCUSED_ILLEGAL_PATHS.add(path);
            ETFUtils2.logWarn(ETF.getTextFromTranslation("config.entity_texture_features.illegal_path_warn").getString() + " [" + path + "]");
        }
        cir.setReturnValue((Object)true);
    }
}

