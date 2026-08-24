package vectorwing.farmersdelight.data;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import vectorwing.farmersdelight.common.tag.ModTags;

public class EntityTags extends EntityTypeTagsProvider {
   public EntityTags(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "farmersdelight", existingFileHelper);
   }

   protected void addTags(Provider provider) {
      this.tag(ModTags.EntityTypes.DOG_FOOD_USERS).add(EntityType.WOLF);
      this.tag(ModTags.EntityTypes.HORSE_FEED_USERS)
         .add(new EntityType[]{EntityType.HORSE, EntityType.SKELETON_HORSE, EntityType.ZOMBIE_HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.LLAMA});
      this.tag(ModTags.EntityTypes.HORSE_FEED_TEMPTED).add(new EntityType[]{EntityType.HORSE, EntityType.DONKEY, EntityType.MULE});
   }
}
