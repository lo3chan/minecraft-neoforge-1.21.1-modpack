package com.aetherteam.aether.data.generators.tags;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.entity.AetherEntityTypes;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags.EntityTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AetherEntityTagData extends EntityTypeTagsProvider {
   public AetherEntityTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "aether", helper);
   }

   public void addTags(Provider provider) {
      this.tag(AetherTags.Entities.SWETS).add(new EntityType[]{(EntityType)AetherEntityTypes.BLUE_SWET.get(), (EntityType)AetherEntityTypes.GOLDEN_SWET.get()});
      this.tag(AetherTags.Entities.WHIRLWIND_UNAFFECTED).add((EntityType)AetherEntityTypes.AECHOR_PLANT.get()).addTag(EntityTypes.BOSSES);
      this.tag(AetherTags.Entities.PIGS)
         .add(
            new EntityType[]{
               EntityType.PIG,
               (EntityType)AetherEntityTypes.PHYG.get(),
               EntityType.PIGLIN,
               EntityType.PIGLIN_BRUTE,
               EntityType.ZOMBIFIED_PIGLIN,
               EntityType.HOGLIN,
               EntityType.ZOGLIN
            }
         );
      this.tag(AetherTags.Entities.FIRE_MOB).add(new EntityType[]{EntityType.BLAZE, (EntityType)AetherEntityTypes.FIRE_MINION.get()});
      this.tag(AetherTags.Entities.NO_SKYROOT_DOUBLE_DROPS).add(new EntityType[]{EntityType.PLAYER, EntityType.WITHER, EntityType.ENDER_DRAGON});
      this.tag(AetherTags.Entities.NO_AMBROSIUM_DROPS).add(EntityType.PLAYER);
      this.tag(AetherTags.Entities.UNLAUNCHABLE).add((EntityType)AetherEntityTypes.AECHOR_PLANT.get());
      this.tag(AetherTags.Entities.NO_CANDY_CANE_DROPS).add(EntityType.PLAYER);
      this.tag(AetherTags.Entities.DEFLECTABLE_PROJECTILES)
         .addTag(EntityTypeTags.ARROWS)
         .add(
            new EntityType[]{
               EntityType.EGG,
               EntityType.SMALL_FIREBALL,
               EntityType.FIREBALL,
               EntityType.SNOWBALL,
               EntityType.LLAMA_SPIT,
               EntityType.TRIDENT,
               EntityType.SHULKER_BULLET,
               (EntityType)AetherEntityTypes.GOLDEN_DART.get(),
               (EntityType)AetherEntityTypes.POISON_DART.get(),
               (EntityType)AetherEntityTypes.ENCHANTED_DART.get(),
               (EntityType)AetherEntityTypes.POISON_NEEDLE.get(),
               (EntityType)AetherEntityTypes.ZEPHYR_SNOWBALL.get(),
               (EntityType)AetherEntityTypes.LIGHTNING_KNIFE.get(),
               (EntityType)AetherEntityTypes.HAMMER_PROJECTILE.get()
            }
         );
      this.tag(AetherTags.Entities.IGNORE_INVISIBILITY).addTag(EntityTypes.BOSSES).add(new EntityType[]{EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN});
      this.tag(AetherTags.Entities.UNHOOKABLE)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.AECHOR_PLANT.get(),
               (EntityType)AetherEntityTypes.WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.SLIDER.get(),
               (EntityType)AetherEntityTypes.SUN_SPIRIT.get()
            }
         );
      this.tag(AetherTags.Entities.TREATED_AS_AETHER_ENTITY);
      this.tag(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY);
      this.tag(AetherTags.Entities.DUNGEON_ENTITIES)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.SENTRY.get(),
               (EntityType)AetherEntityTypes.SLIDER.get(),
               (EntityType)AetherEntityTypes.VALKYRIE.get(),
               (EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get(),
               (EntityType)AetherEntityTypes.THUNDER_CRYSTAL.get(),
               (EntityType)AetherEntityTypes.SUN_SPIRIT.get(),
               (EntityType)AetherEntityTypes.FIRE_MINION.get(),
               (EntityType)AetherEntityTypes.FIRE_CRYSTAL.get(),
               (EntityType)AetherEntityTypes.ICE_CRYSTAL.get()
            }
         );
      this.tag(AetherTags.Entities.SLIDER_DAMAGING_PROJECTILES)
         .addOptional(ResourceLocation.fromNamespaceAndPath("quark", "pickarang"))
         .addOptional(ResourceLocation.fromNamespaceAndPath("quark", "flamerang"));
      this.tag(EntityTypes.BOSSES)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.SLIDER.get(), (EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get(), (EntityType)AetherEntityTypes.SUN_SPIRIT.get()
            }
         );
      this.tag(EntityTypes.CAPTURING_NOT_SUPPORTED)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.SLIDER.get(), (EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get(), (EntityType)AetherEntityTypes.SUN_SPIRIT.get()
            }
         );
      this.tag(EntityTypes.TELEPORTING_NOT_SUPPORTED)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.SLIDER.get(), (EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get(), (EntityType)AetherEntityTypes.SUN_SPIRIT.get()
            }
         );
      this.tag(EntityTypeTags.IMPACT_PROJECTILES)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.GOLDEN_DART.get(),
               (EntityType)AetherEntityTypes.POISON_DART.get(),
               (EntityType)AetherEntityTypes.ENCHANTED_DART.get(),
               (EntityType)AetherEntityTypes.LIGHTNING_KNIFE.get(),
               (EntityType)AetherEntityTypes.HAMMER_PROJECTILE.get()
            }
         );
      this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add((EntityType)AetherEntityTypes.AERBUNNY.get());
      this.tag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add((EntityType)AetherEntityTypes.FIRE_MINION.get());
      this.tag(EntityTypeTags.FROG_FOOD)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.BLUE_SWET.get(), (EntityType)AetherEntityTypes.GOLDEN_SWET.get(), (EntityType)AetherEntityTypes.SENTRY.get()
            }
         );
      this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.PHYG.get(),
               (EntityType)AetherEntityTypes.FLYING_COW.get(),
               (EntityType)AetherEntityTypes.MOA.get(),
               (EntityType)AetherEntityTypes.AERWHALE.get(),
               (EntityType)AetherEntityTypes.AERBUNNY.get(),
               (EntityType)AetherEntityTypes.WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.COCKATRICE.get(),
               (EntityType)AetherEntityTypes.ZEPHYR.get(),
               (EntityType)AetherEntityTypes.SLIDER.get(),
               (EntityType)AetherEntityTypes.VALKYRIE.get(),
               (EntityType)AetherEntityTypes.VALKYRIE_QUEEN.get(),
               (EntityType)AetherEntityTypes.SUN_SPIRIT.get()
            }
         );
      this.tag(EntityTypeTags.DISMOUNTS_UNDERWATER)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.PHYG.get(),
               (EntityType)AetherEntityTypes.FLYING_COW.get(),
               (EntityType)AetherEntityTypes.MOA.get(),
               (EntityType)AetherEntityTypes.AERBUNNY.get(),
               (EntityType)AetherEntityTypes.BLUE_SWET.get(),
               (EntityType)AetherEntityTypes.GOLDEN_SWET.get()
            }
         );
      this.tag(EntityTypeTags.REDIRECTABLE_PROJECTILE).add((EntityType)AetherEntityTypes.ZEPHYR_SNOWBALL.get());
   }
}
