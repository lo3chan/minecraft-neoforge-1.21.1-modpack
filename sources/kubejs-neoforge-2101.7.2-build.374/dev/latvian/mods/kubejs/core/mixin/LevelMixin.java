package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.LevelKJS;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.AttachedData;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@RemapPrefixForJS("kjs$")
@Mixin({Level.class})
public abstract class LevelMixin implements LevelKJS {
   @Unique
   private AttachedData<Level> kjs$attachedData;

   @Override
   public AttachedData<Level> kjs$getData() {
      if (this.kjs$attachedData == null) {
         this.kjs$attachedData = new AttachedData<>(this.kjs$self());
         KubeJSPlugins.forEachPlugin(this.kjs$attachedData, KubeJSPlugin::attachLevelData);
      }

      return this.kjs$attachedData;
   }

   @Shadow
   @RemapForJS("getTime")
   public abstract long getGameTime();

   @Shadow
   @RemapForJS("getDimensionKey")
   public abstract ResourceKey<Level> dimension();

   @Shadow
   @HideFromJS
   protected abstract LevelEntityGetter<Entity> getEntities();

   @Override
   public Iterable<? extends Entity> kjs$getMcEntities() {
      return this.getEntities().getAll();
   }

   @Nullable
   @Override
   public Entity kjs$getEntityByUUID(UUID id) {
      return (Entity)this.getEntities().get(id);
   }

   @Nullable
   @Override
   public Entity kjs$getEntityByNetworkID(int id) {
      return (Entity)this.getEntities().get(id);
   }
}
