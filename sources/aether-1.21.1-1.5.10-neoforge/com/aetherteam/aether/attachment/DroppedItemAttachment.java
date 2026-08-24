package com.aetherteam.aether.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DroppedItemAttachment {
   private Optional<Integer> ownerID;
   @Nullable
   private Entity owner;
   public static final Codec<DroppedItemAttachment> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.INT.optionalFieldOf("owner_id").forGetter(o -> o.ownerID)).apply(instance, DroppedItemAttachment::new)
   );

   protected DroppedItemAttachment() {
   }

   private DroppedItemAttachment(Optional<Integer> ownerID) {
      this.ownerID = ownerID;
   }

   public void setOwner(Entity owner) {
      this.owner = owner;
      if (this.owner != null) {
         this.ownerID = Optional.of(this.owner.getId());
      } else {
         this.ownerID = Optional.empty();
      }
   }

   @Nullable
   public Entity getOwner(Level level) {
      if (this.owner == null && this.ownerID.isPresent()) {
         this.owner = level.getEntity(this.ownerID.get());
      }

      return this.owner;
   }
}
