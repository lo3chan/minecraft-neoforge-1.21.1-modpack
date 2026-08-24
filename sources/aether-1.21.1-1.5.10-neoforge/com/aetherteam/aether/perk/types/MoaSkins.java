package com.aetherteam.aether.perk.types;

import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.User.Tier;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MoaSkins {
   private static final Map<String, MoaSkins.MoaSkin> MOA_SKINS = new LinkedHashMap<>();

   @OnlyIn(Dist.CLIENT)
   public static void registerClient() {
      if (Minecraft.getInstance().level != null) {
         registerMoaSkins(Minecraft.getInstance().level);
      }
   }

   public static void registerMoaSkins(Level level) {
      if (!MOA_SKINS.isEmpty()) {
         MOA_SKINS.clear();
      }

      if (level != null) {
         RegistryAccess registryAccess = level.registryAccess();
         Registry<MoaType> registry = registryAccess.registryOrThrow(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY);

         for (ResourceKey<MoaType> moaTypeKey : registry.registryKeySet().stream().sorted((current, next) -> {
            MoaType currentType = AetherMoaTypes.getMoaType(registryAccess, current.location());
            MoaType nextType = AetherMoaTypes.getMoaType(registryAccess, next.location());
            return currentType != null && nextType != null ? Integer.compare(currentType.maxJumps(), nextType.maxJumps()) : 0;
         }).toList()) {
            MoaType moaType = (MoaType)registry.get(moaTypeKey);
            if (moaType != null) {
               String name = (
                     moaTypeKey.location().getNamespace().equals("aether")
                        ? moaTypeKey.location().getPath()
                        : moaTypeKey.location().toString().replace(":", ".")
                  )
                  + "_moa";
               register(
                  name,
                  new MoaSkins.MoaSkin(
                     name,
                     new MoaSkins.MoaSkin.Properties()
                        .displayName(Component.translatable("gui.aether.moa_skins.skin." + name))
                        .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
                        .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/" + name + "_icon"))
                        .skinLocation(moaType.moaTexture())
                        .saddleLocation(moaType.saddleTexture())
                        .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
                  )
               );
            }
         }
      }

      register(
         "orange_moa",
         new MoaSkins.MoaSkin(
            "orange_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.orange_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/orange_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/orange_moa/orange_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "brown_moa",
         new MoaSkins.MoaSkin(
            "brown_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.brown_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/brown_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/brown_moa/brown_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/black_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "red_moa",
         new MoaSkins.MoaSkin(
            "red_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.red_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/red_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/red_moa/red_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "green_moa",
         new MoaSkins.MoaSkin(
            "green_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.green_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/green_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/green_moa/green_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "purple_moa",
         new MoaSkins.MoaSkin(
            "purple_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.purple_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/purple_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/purple_moa/purple_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "pink_moa",
         new MoaSkins.MoaSkin(
            "pink_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.pink_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/pink_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/pink_moa/pink_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "bronze_moa",
         new MoaSkins.MoaSkin(
            "bronze_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.bronze_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/bronze_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/bronze_moa/bronze_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "silver_moa",
         new MoaSkins.MoaSkin(
            "silver_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.silver_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/silver_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/silver_moa/silver_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "gold_moa",
         new MoaSkins.MoaSkin(
            "gold_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.gold_moa"))
               .userPredicate(user -> PerkUtil.hasHumanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/gold_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gold_moa/gold_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.HUMAN, false))
         )
      );
      register(
         "boko_yellow",
         new MoaSkins.MoaSkin(
            "boko_yellow",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.boko_yellow"))
               .userPredicate(user -> PerkUtil.hasLifetimeAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/boko_yellow_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/boko_yellow/boko_yellow.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/boko_yellow/boko_yellow_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/boko_yellow/boko_yellow_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, true))
         )
      );
      register(
         "crookjaw_purple",
         new MoaSkins.MoaSkin(
            "crookjaw_purple",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.crookjaw_purple"))
               .userPredicate(user -> PerkUtil.hasLifetimeAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/crookjaw_purple_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/crookjaw_purple/crookjaw_purple.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/crookjaw_purple/crookjaw_purple_emissive.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/crookjaw_purple/crookjaw_purple_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/crookjaw_purple/crookjaw_purple_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, true))
         )
      );
      register(
         "gharrix_red",
         new MoaSkins.MoaSkin(
            "gharrix_red",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.gharrix_red"))
               .userPredicate(user -> PerkUtil.hasLifetimeAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/gharrix_red_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gharrix_red/gharrix_red.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gharrix_red/gharrix_red_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, true))
         )
      );
      register(
         "halcian_pink",
         new MoaSkins.MoaSkin(
            "halcian_pink",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.halcian_pink"))
               .userPredicate(user -> PerkUtil.hasLifetimeAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/halcian_pink_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/halcian_pink/halcian_pink.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/halcian_pink/halcian_pink_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/halcian_pink/halcian_pink_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, true))
         )
      );
      register(
         "tivalier_green",
         new MoaSkins.MoaSkin(
            "tivalier_green",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.tivalier_green"))
               .userPredicate(user -> PerkUtil.hasLifetimeAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/tivalier_green_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/tivalier_green/tivalier_green.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/tivalier_green/tivalier_green_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, true))
         )
      );
      register(
         "gilded_gharrix",
         new MoaSkins.MoaSkin(
            "gilded_gharrix",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.gilded_gharrix"))
               .userPredicate(user -> PerkUtil.hasAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/gilded_gharrix_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gilded_gharrix/gilded_gharrix.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gilded_gharrix/gilded_gharrix_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gilded_gharrix/gilded_gharrix_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, false))
         )
      );
      register(
         "gargoyle_moa",
         new MoaSkins.MoaSkin(
            "gargoyle_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.gargoyle_moa"))
               .userPredicate(user -> PerkUtil.hasAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/gargoyle_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gargoyle_moa/gargoyle_moa.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gargoyle_moa/gargoyle_moa_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/gargoyle_moa/gargoyle_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, false))
         )
      );
      register(
         "construction_bot",
         new MoaSkins.MoaSkin(
            "construction_bot",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.construction_bot"))
               .userPredicate(user -> PerkUtil.hasAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/construction_bot_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/construction_bot/construction_bot.png"))
               .emissiveLocation(
                  ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/construction_bot/construction_bot_emissive.png")
               )
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/construction_bot/construction_bot_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/construction_bot/construction_bot_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, false))
         )
      );
      register(
         "mossy_statue_moa",
         new MoaSkins.MoaSkin(
            "mossy_statue_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.mossy_statue_moa"))
               .userPredicate(user -> PerkUtil.hasAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/mossy_statue_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/mossy_statue_moa/mossy_statue_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/mossy_statue_moa/mossy_statue_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, false))
         )
      );
      register(
         "chicken_moa",
         new MoaSkins.MoaSkin(
            "chicken_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.chicken_moa"))
               .userPredicate(user -> PerkUtil.hasAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/chicken_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/chicken_moa/chicken_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/chicken_moa/chicken_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, false))
         )
      );
      register(
         "medical_bot",
         new MoaSkins.MoaSkin(
            "medical_bot",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.medical_bot"))
               .userPredicate(user -> PerkUtil.hasAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/medical_bot_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/medical_bot/medical_bot.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/medical_bot/medical_bot_emissive.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/medical_bot/medical_bot_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/medical_bot/medical_bot_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, false))
         )
      );
      register(
         "skeleton_moa",
         new MoaSkins.MoaSkin(
            "skeleton_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.skeleton_moa"))
               .userPredicate(user -> PerkUtil.hasAscentanMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/skeleton_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/skeleton_moa/skeleton_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/skeleton_moa/skeleton_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.ASCENTAN, false))
         )
      );
      register(
         "arctic_moa",
         new MoaSkins.MoaSkin(
            "arctic_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.arctic_moa"))
               .userPredicate(user -> PerkUtil.hasLifetimeValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/arctic_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/arctic_moa/arctic_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/arctic_moa/arctic_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, true))
         )
      );
      register(
         "cockatrice_moa",
         new MoaSkins.MoaSkin(
            "cockatrice_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.cockatrice_moa"))
               .userPredicate(user -> PerkUtil.hasLifetimeValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/cockatrice_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/cockatrice_moa/cockatrice_moa.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/cockatrice_moa/cockatrice_moa_emissive.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/cockatrice_moa/cockatrice_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, true))
         )
      );
      register(
         "phoenix_moa",
         new MoaSkins.MoaSkin(
            "phoenix_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.phoenix_moa"))
               .userPredicate(user -> PerkUtil.hasLifetimeValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/phoenix_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/phoenix_moa/phoenix_moa.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/phoenix_moa/phoenix_moa_emissive.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/phoenix_moa/phoenix_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, true))
         )
      );
      register(
         "sentry_moa",
         new MoaSkins.MoaSkin(
            "sentry_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.sentry_moa"))
               .userPredicate(user -> PerkUtil.hasLifetimeValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/sentry_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/sentry_moa/sentry_moa.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/sentry_moa/sentry_moa_emissive.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/sentry_moa/sentry_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, true))
         )
      );
      register(
         "valkyrie_moa",
         new MoaSkins.MoaSkin(
            "valkyrie_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.valkyrie_moa"))
               .userPredicate(user -> PerkUtil.hasLifetimeValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/valkyrie_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/valkyrie_moa/valkyrie_moa.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/valkyrie_moa/valkyrie_moa_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/valkyrie_moa/valkyrie_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, true))
         )
      );
      register(
         "battle_sentry_moa",
         new MoaSkins.MoaSkin(
            "battle_sentry_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.battle_sentry_moa"))
               .userPredicate(user -> PerkUtil.hasValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/battle_sentry_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/battle_sentry_moa/battle_sentry_moa.png"))
               .emissiveLocation(
                  ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/battle_sentry_moa/battle_sentry_moa_emissive.png")
               )
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/battle_sentry_moa/battle_sentry_moa_hat.png"))
               .hatEmissiveLocation(
                  ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/battle_sentry_moa/battle_sentry_moa_hat_emissive.png")
               )
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/battle_sentry_moa/battle_sentry_moa_saddle.png"))
               .saddleEmissiveLocation(
                  ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/battle_sentry_moa/battle_sentry_moa_saddle_emissive.png")
               )
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, false))
         )
      );
      register(
         "frozen_phoenix",
         new MoaSkins.MoaSkin(
            "frozen_phoenix",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.frozen_phoenix"))
               .userPredicate(user -> PerkUtil.hasValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/frozen_phoenix_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/frozen_phoenix/frozen_phoenix.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/frozen_phoenix/frozen_phoenix_emissive.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/frozen_phoenix/frozen_phoenix_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, false))
         )
      );
      register(
         "molten_moa",
         new MoaSkins.MoaSkin(
            "molten_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.molten_moa"))
               .userPredicate(user -> PerkUtil.hasValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/molten_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/molten_moa/molten_moa.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/molten_moa/molten_moa_emissive.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/molten_moa/molten_moa_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/molten_moa/molten_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, false))
         )
      );
      register(
         "undead_moa",
         new MoaSkins.MoaSkin(
            "undead_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.undead_moa"))
               .userPredicate(user -> PerkUtil.hasValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/undead_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/undead_moa/undead_moa.png"))
               .emissiveLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/undead_moa/undead_moa_emissive.png"))
               .hatLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/undead_moa/undead_moa_hat.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/undead_moa/undead_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, false))
         )
      );
      register(
         "stratus",
         new MoaSkins.MoaSkin(
            "stratus",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.stratus"))
               .userPredicate(user -> PerkUtil.hasValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/stratus_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/stratus/stratus.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/stratus/stratus_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, false))
         )
      );
      register(
         "peacock_moa",
         new MoaSkins.MoaSkin(
            "peacock_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.peacock_moa"))
               .userPredicate(user -> PerkUtil.hasValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/peacock_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/peacock_moa/peacock_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/peacock_moa/peacock_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, false))
         )
      );
      register(
         "prehistoric_moa",
         new MoaSkins.MoaSkin(
            "prehistoric_moa",
            new MoaSkins.MoaSkin.Properties()
               .displayName(Component.translatable("gui.aether.moa_skins.skin.prehistoric_moa"))
               .userPredicate(user -> PerkUtil.hasValkyrieMoaSkins().test(user))
               .iconLocation(ResourceLocation.fromNamespaceAndPath("aether", "skins/icons/prehistoric_moa_icon"))
               .skinLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/prehistoric_moa/prehistoric_moa.png"))
               .saddleLocation(ResourceLocation.fromNamespaceAndPath("aether", "textures/entity/mobs/moa/skins/prehistoric_moa/prehistoric_moa_saddle.png"))
               .info(new MoaSkins.MoaSkin.Info(Tier.VALKYRIE, false))
         )
      );
   }

   private static void register(String id, MoaSkins.MoaSkin moaSkin) {
      MOA_SKINS.put(id, moaSkin);
   }

   public static Map<String, MoaSkins.MoaSkin> getMoaSkins() {
      return ImmutableMap.copyOf(MOA_SKINS);
   }

   public static class MoaSkin {
      public static final StreamCodec<FriendlyByteBuf, MoaSkins.MoaSkin> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, MoaSkins.MoaSkin>() {
         public MoaSkins.MoaSkin decode(FriendlyByteBuf buffer) {
            String id = buffer.readUtf();
            return MoaSkins.getMoaSkins().get(id);
         }

         public void encode(FriendlyByteBuf buffer, MoaSkins.MoaSkin moaSkin) {
            buffer.writeUtf(moaSkin.getId());
         }
      };
      private final String id;
      private final Component displayName;
      private final Predicate<User> userPredicate;
      private final ResourceLocation iconLocation;
      private final ResourceLocation skinLocation;
      @Nullable
      private final ResourceLocation emissiveLocation;
      @Nullable
      private final ResourceLocation hatLocation;
      @Nullable
      private final ResourceLocation hatEmissiveLocation;
      private final ResourceLocation saddleLocation;
      @Nullable
      private final ResourceLocation saddleEmissiveLocation;
      private final MoaSkins.MoaSkin.Info info;

      protected MoaSkin(String id, MoaSkins.MoaSkin.Properties properties) {
         this(
            id,
            properties.displayName,
            properties.userPredicate,
            properties.iconLocation,
            properties.skinLocation,
            properties.emissiveLocation,
            properties.hatLocation,
            properties.hatEmissiveLocation,
            properties.saddleLocation,
            properties.saddleEmissiveLocation,
            properties.info
         );
      }

      protected MoaSkin(
         String id,
         Component displayName,
         Predicate<User> userPredicate,
         ResourceLocation iconLocation,
         ResourceLocation skinLocation,
         ResourceLocation emissiveLocation,
         ResourceLocation hatLocation,
         ResourceLocation hatEmissiveLocation,
         ResourceLocation saddleLocation,
         ResourceLocation saddleEmissiveLocation,
         MoaSkins.MoaSkin.Info info
      ) {
         this.id = id;
         this.displayName = displayName;
         this.userPredicate = userPredicate;
         this.iconLocation = iconLocation;
         this.skinLocation = skinLocation;
         this.emissiveLocation = emissiveLocation;
         this.hatLocation = hatLocation;
         this.hatEmissiveLocation = hatEmissiveLocation;
         this.saddleLocation = saddleLocation;
         this.saddleEmissiveLocation = saddleEmissiveLocation;
         this.info = info;
      }

      public String getId() {
         return this.id;
      }

      public Component getDisplayName() {
         return this.displayName;
      }

      public Predicate<User> getUserPredicate() {
         return this.userPredicate;
      }

      public ResourceLocation getIconLocation() {
         return this.iconLocation;
      }

      public ResourceLocation getSkinLocation() {
         return this.skinLocation;
      }

      @Nullable
      public ResourceLocation getEmissiveLocation() {
         return this.emissiveLocation;
      }

      public ResourceLocation getHatLocation() {
         return this.hatLocation;
      }

      public ResourceLocation getHatEmissiveLocation() {
         return this.hatEmissiveLocation;
      }

      public ResourceLocation getSaddleLocation() {
         return this.saddleLocation;
      }

      @Nullable
      public ResourceLocation getSaddleEmissiveLocation() {
         return this.saddleEmissiveLocation;
      }

      public MoaSkins.MoaSkin.Info getInfo() {
         return this.info;
      }

      public record Info(Tier tier, boolean lifetime) {
      }

      public static class Properties {
         private Component displayName;
         private Predicate<User> userPredicate;
         private ResourceLocation iconLocation;
         private ResourceLocation skinLocation;
         @Nullable
         private ResourceLocation emissiveLocation = null;
         @Nullable
         private ResourceLocation hatLocation = null;
         @Nullable
         private ResourceLocation hatEmissiveLocation = null;
         private ResourceLocation saddleLocation;
         @Nullable
         private ResourceLocation saddleEmissiveLocation = null;
         private MoaSkins.MoaSkin.Info info;

         public MoaSkins.MoaSkin.Properties displayName(Component displayName) {
            this.displayName = displayName;
            return this;
         }

         public MoaSkins.MoaSkin.Properties userPredicate(Predicate<User> userPredicate) {
            this.userPredicate = userPredicate;
            return this;
         }

         public MoaSkins.MoaSkin.Properties iconLocation(ResourceLocation iconLocation) {
            this.iconLocation = iconLocation;
            return this;
         }

         public MoaSkins.MoaSkin.Properties skinLocation(ResourceLocation skinLocation) {
            this.skinLocation = skinLocation;
            return this;
         }

         public MoaSkins.MoaSkin.Properties emissiveLocation(@Nullable ResourceLocation emissiveLocation) {
            this.emissiveLocation = emissiveLocation;
            return this;
         }

         public MoaSkins.MoaSkin.Properties hatLocation(ResourceLocation hatLocation) {
            this.hatLocation = hatLocation;
            return this;
         }

         public MoaSkins.MoaSkin.Properties hatEmissiveLocation(ResourceLocation hatEmissiveLocation) {
            this.hatEmissiveLocation = hatEmissiveLocation;
            return this;
         }

         public MoaSkins.MoaSkin.Properties saddleLocation(ResourceLocation saddleLocation) {
            this.saddleLocation = saddleLocation;
            return this;
         }

         public MoaSkins.MoaSkin.Properties saddleEmissiveLocation(ResourceLocation saddleEmissiveLocation) {
            this.saddleEmissiveLocation = saddleEmissiveLocation;
            return this;
         }

         public MoaSkins.MoaSkin.Properties info(MoaSkins.MoaSkin.Info info) {
            this.info = info;
            return this;
         }
      }
   }
}
