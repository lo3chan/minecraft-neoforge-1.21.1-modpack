package jeresources.compatibility;

import java.util.Optional;
import jeresources.api.render.IMobRenderHook;
import jeresources.compatibility.api.JERAPI;
import jeresources.entry.DungeonEntry;
import jeresources.entry.MobEntry;
import jeresources.entry.PlantEntry;
import jeresources.entry.WorldGenEntry;
import jeresources.registry.DungeonRegistry;
import jeresources.registry.MobRegistry;
import jeresources.registry.PlantRegistry;
import jeresources.registry.WorldGenRegistry;
import jeresources.util.FakeClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CompatBase {
   @Nullable
   private static Level fakeClientLevel = null;

   public static Optional<Level> getServerLevel() {
      Minecraft minecraft = Minecraft.getInstance();
      return Optional.of(minecraft).<IntegratedServer>map(Minecraft::getSingleplayerServer).map(integratedServer -> integratedServer.getLevel(Level.OVERWORLD));
   }

   @NotNull
   public static Level getLevel() {
      Minecraft minecraft = Minecraft.getInstance();
      Level level = minecraft.level;
      if (level != null) {
         return level;
      } else {
         if (fakeClientLevel == null) {
            fakeClientLevel = new FakeClientLevel();
         }

         return fakeClientLevel;
      }
   }

   public abstract void init(boolean var1);

   protected void registerMob(MobEntry entry) {
      MobRegistry.getInstance().registerMob(entry);
   }

   protected void registerDungeonEntry(DungeonEntry entry) {
      DungeonRegistry.getInstance().registerDungeonEntry(entry);
   }

   protected void registerWorldGen(WorldGenEntry entry) {
      WorldGenRegistry.getInstance().registerEntry(entry);
   }

   protected void registerPlant(PlantEntry entry) {
      PlantRegistry.getInstance().registerPlant(entry);
   }

   protected void registerMobRenderHook(Class<? extends LivingEntity> clazz, IMobRenderHook renderHook) {
      JERAPI.getInstance().getMobRegistry().registerRenderHook(clazz, renderHook);
   }
}
