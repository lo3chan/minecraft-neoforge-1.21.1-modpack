package io.github.razordevs.deep_aether.datagen.tags;

import com.aetherteam.aether.AetherTags.Entities;
import com.aetherteam.aether.entity.AetherEntityTypes;
import io.github.razordevs.deep_aether.init.DAEntities;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags.EntityTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class DAEntityTagData extends EntityTypeTagsProvider {
   public DAEntityTagData(PackOutput output, CompletableFuture<Provider> registries, @Nullable ExistingFileHelper helper) {
      super(output, registries, "deep_aether", helper);
   }

   @Nonnull
   public String getName() {
      return "Deep Aether EntityType Tags";
   }

   protected void addTags(Provider p_256380_) {
      this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add((EntityType)DAEntities.QUAIL.get());
      this.tag(DATags.Entities.STERLING_AERCLOUD_BLACKLIST)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.AERWHALE.get(),
               (EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.ZEPHYR.get(),
               (EntityType)AetherEntityTypes.ZEPHYR_SNOWBALL.get(),
               (EntityType)DAEntities.EOTS_SEGMENT.get(),
               (EntityType)DAEntities.EOTS_CONTROLLER.get()
            }
         );
      this.tag(DATags.Entities.FRIENDLY_WIND_CHARGE_BLACKLIST)
         .add(
            new EntityType[]{
               (EntityType)AetherEntityTypes.AERWHALE.get(),
               (EntityType)AetherEntityTypes.EVIL_WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.WHIRLWIND.get(),
               (EntityType)AetherEntityTypes.ZEPHYR.get(),
               (EntityType)AetherEntityTypes.ZEPHYR_SNOWBALL.get()
            }
         );
      this.tag(DATags.Entities.WIND_CHARGE_BLACKLIST)
         .add(new EntityType[]{(EntityType)DAEntities.EOTS_SEGMENT.get(), (EntityType)DAEntities.EOTS_CONTROLLER.get()})
         .addTag(DATags.Entities.FRIENDLY_WIND_CHARGE_BLACKLIST);
      this.tag(EntityTypes.BOSSES).add(new EntityType[]{(EntityType)DAEntities.EOTS_SEGMENT.get(), (EntityType)DAEntities.EOTS_CONTROLLER.get()});
      this.tag(EntityTypeTags.ARROWS).add((EntityType)DAEntities.STORM_ARROW.get());
      this.tag(DATags.Entities.SLIDER_SLAM_BLACKLIST).add((EntityType)DAEntities.EOTS_CONTROLLER.get());
      this.tag(Entities.UNHOOKABLE).add(new EntityType[]{(EntityType)DAEntities.EOTS_CONTROLLER.get(), (EntityType)DAEntities.EOTS_SEGMENT.get()});
      this.tag(Entities.UNLAUNCHABLE).add((EntityType)DAEntities.EOTS_CONTROLLER.get());
   }
}
