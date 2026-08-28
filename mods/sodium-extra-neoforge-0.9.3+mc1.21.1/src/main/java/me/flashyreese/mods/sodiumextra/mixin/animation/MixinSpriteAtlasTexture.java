/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite$Ticker
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package me.flashyreese.mods.sodiumextra.mixin.animation;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TextureAtlas.class})
public abstract class MixinSpriteAtlasTexture
extends AbstractTexture {
    @Unique
    private final Map<Supplier<Boolean>, List<ResourceLocation>> animatedSprites = Map.of(() -> SodiumExtraClientMod.options().animationSettings.water, List.of(ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/water_still"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/water_flow")), () -> SodiumExtraClientMod.options().animationSettings.lava, List.of(ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/lava_still"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/lava_flow")), () -> SodiumExtraClientMod.options().animationSettings.portal, List.of(ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/nether_portal")), () -> SodiumExtraClientMod.options().animationSettings.fire, List.of(ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/fire_0"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/fire_1"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/soul_fire_0"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/soul_fire_1"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/campfire_fire"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/campfire_log_lit"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/soul_campfire_fire"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/soul_campfire_log_lit")), () -> SodiumExtraClientMod.options().animationSettings.blockAnimations, List.of(ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/magma"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/lantern"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sea_lantern"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/soul_lantern"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/kelp"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/kelp_plant"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/seagrass"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/tall_seagrass_top"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/tall_seagrass_bottom"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/warped_stem"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/crimson_stem"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/blast_furnace_front_on"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/smoker_front_on"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/stonecutter_saw"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/prismarine"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/respawn_anchor_top"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"entity/conduit/wind"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"entity/conduit/wind_vertical")), () -> SodiumExtraClientMod.options().animationSettings.sculkSensor, List.of(ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk_catalyst_top_bloom"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk_catalyst_side_bloom"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk_shrieker_inner_top"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk_vein"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk_shrieker_can_summon_inner_top"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk_sensor_tendril_inactive"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"block/sculk_sensor_tendril_active"), ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"vibration")));

    @Redirect(method={"upload"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;createTicker()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Ticker;"))
    public TextureAtlasSprite.Ticker sodiumExtra$tickAnimatedSprites(TextureAtlasSprite instance) {
        TextureAtlasSprite.Ticker tickableAnimation = instance.createTicker();
        if (tickableAnimation != null && SodiumExtraClientMod.options().animationSettings.animation && this.shouldAnimate(instance.contents().name())) {
            return tickableAnimation;
        }
        return null;
    }

    @Unique
    private boolean shouldAnimate(ResourceLocation identifier) {
        if (identifier != null) {
            for (Map.Entry<Supplier<Boolean>, List<ResourceLocation>> supplierListEntry : this.animatedSprites.entrySet()) {
                if (!supplierListEntry.getValue().contains(identifier)) continue;
                return supplierListEntry.getKey().get();
            }
        }
        return true;
    }
}

