package com.iafenvoy.origins;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.ServerConfigManager.PermissionChecker;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.action.builtin.BiEntityActions;
import com.iafenvoy.origins.data.action.builtin.BlockActions;
import com.iafenvoy.origins.data.action.builtin.EntityActions;
import com.iafenvoy.origins.data.action.builtin.ItemActions;
import com.iafenvoy.origins.data.badge.BuiltinBadges;
import com.iafenvoy.origins.data.condition.builtin.BiEntityConditions;
import com.iafenvoy.origins.data.condition.builtin.BiomeConditions;
import com.iafenvoy.origins.data.condition.builtin.BlockConditions;
import com.iafenvoy.origins.data.condition.builtin.DamageConditions;
import com.iafenvoy.origins.data.condition.builtin.EntityConditions;
import com.iafenvoy.origins.data.condition.builtin.FluidConditions;
import com.iafenvoy.origins.data.condition.builtin.ItemConditions;
import com.iafenvoy.origins.data.power.builtin.ActionPowers;
import com.iafenvoy.origins.data.power.builtin.ModifyPowers;
import com.iafenvoy.origins.data.power.builtin.PreventPowers;
import com.iafenvoy.origins.data.power.builtin.RegularPowers;
import com.iafenvoy.origins.data.power.component.BuiltinComponents;
import com.iafenvoy.origins.registry.OriginsAttachments;
import com.iafenvoy.origins.registry.OriginsBlocks;
import com.iafenvoy.origins.registry.OriginsCriterionTriggers;
import com.iafenvoy.origins.registry.OriginsDataComponents;
import com.iafenvoy.origins.registry.OriginsEntities;
import com.iafenvoy.origins.registry.OriginsItems;
import com.iafenvoy.origins.registry.OriginsLootItemConditions;
import com.iafenvoy.origins.registry.OriginsLootItemFunctions;
import com.iafenvoy.origins.registry.OriginsRecipeSerializers;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod("origins")
public final class Origins {
   public static final String MOD_ID = "origins";
   public static final Logger LOGGER = LogUtils.getLogger();

   public Origins(IEventBus bus) {
      ConfigManager.getInstance().registerServerConfigHandler(OriginsConfig.INSTANCE, PermissionChecker.IS_OPERATOR);
      OriginsAttachments.REGISTRY.register(bus);
      OriginsBlocks.REGISTRY.register(bus);
      OriginsCriterionTriggers.REGISTRY.register(bus);
      OriginsDataComponents.REGISTRY.register(bus);
      OriginsEntities.REGISTRY.register(bus);
      OriginsItems.REGISTRY.register(bus);
      OriginsLootItemConditions.REGISTRY.register(bus);
      OriginsLootItemFunctions.REGISTRY.register(bus);
      OriginsRecipeSerializers.REGISTRY.register(bus);
      BiEntityActions.REGISTRY.register(bus);
      BlockActions.REGISTRY.register(bus);
      EntityActions.REGISTRY.register(bus);
      ItemActions.REGISTRY.register(bus);
      BuiltinBadges.REGISTRY.register(bus);
      BiEntityConditions.REGISTRY.register(bus);
      BiomeConditions.REGISTRY.register(bus);
      BlockConditions.REGISTRY.register(bus);
      DamageConditions.REGISTRY.register(bus);
      EntityConditions.REGISTRY.register(bus);
      FluidConditions.REGISTRY.register(bus);
      ItemConditions.REGISTRY.register(bus);
      ActionPowers.REGISTRY.register(bus);
      ModifyPowers.REGISTRY.register(bus);
      PreventPowers.REGISTRY.register(bus);
      RegularPowers.REGISTRY.register(bus);
      BuiltinComponents.REGISTRY.register(bus);
   }
}
