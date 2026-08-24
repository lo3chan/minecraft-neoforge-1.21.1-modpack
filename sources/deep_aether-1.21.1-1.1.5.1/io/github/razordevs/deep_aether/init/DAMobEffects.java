package io.github.razordevs.deep_aether.init;

import com.aetherteam.aether.entity.ai.attribute.AetherAttributes;
import io.github.razordevs.deep_aether.effects.MoaBonusJumpEffect;
import io.github.razordevs.deep_aether.effects.ValkyrieValorEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DAMobEffects {
   public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "deep_aether");
   public static final DeferredHolder<MobEffect, MoaBonusJumpEffect> MOA_BONUS_JUMPS = EFFECTS.register(
      "moa_bonus_jumps",
      () -> (MoaBonusJumpEffect)new MoaBonusJumpEffect()
         .addAttributeModifier(
            AetherAttributes.MOA_MAX_JUMPS, ResourceLocation.fromNamespaceAndPath("deep_aether", "effect_extra_jumps"), 1.0, Operation.ADD_VALUE
         )
   );
   public static final DeferredHolder<MobEffect, ValkyrieValorEffect> VALKYRIE_VALOR = EFFECTS.register("valkyrie_valor", ValkyrieValorEffect::new);
}
