package dev.tr7zw.entityculling.mixin;

import dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Entity.class, BlockEntity.class})
public class CullableMixin implements Cullable {
   private long lasttime = 0L;
   private boolean culled = false;
   private boolean outOfCamera = false;

   @Override
   public void setTimeout() {
      this.lasttime = System.currentTimeMillis() + 1000L;
   }

   @Override
   public boolean isForcedVisible() {
      return this.lasttime > System.currentTimeMillis();
   }

   @Override
   public void setCulled(boolean value) {
      this.culled = value;
      if (!value) {
         this.setTimeout();
      }
   }

   @Override
   public boolean isCulled() {
      return !EntityCullingVersionlessBase.enabled ? false : this.culled;
   }

   @Override
   public void setOutOfCamera(boolean value) {
      this.outOfCamera = value;
   }

   @Override
   public boolean isOutOfCamera() {
      return !EntityCullingVersionlessBase.enabled ? false : this.outOfCamera;
   }
}
