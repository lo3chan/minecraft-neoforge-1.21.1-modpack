package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface EntityCollectionKJS {
   Iterable<? extends Entity> kjs$getMcEntities();

   default List<? extends Player> kjs$getMcPlayers() {
      ArrayList<Player> list = new ArrayList<>(10);

      for (Entity entity : this.kjs$getMcEntities()) {
         if (entity instanceof Player p) {
            list.add(p);
         }
      }

      return list;
   }

   default EntityArrayList kjs$getPlayers() {
      return new EntityArrayList(this.kjs$getMcPlayers());
   }

   default EntityArrayList kjs$getEntities() {
      return new EntityArrayList(this.kjs$getMcEntities());
   }

   default EntityArrayList kjs$getEntitiesWithin(AABB aabb) {
      if (aabb != null && aabb != AABB.INFINITE) {
         EntityArrayList list = new EntityArrayList(10);

         for (Entity entity : this.kjs$getMcEntities()) {
            if (entity.getBoundingBox().intersects(aabb)) {
               list.add(entity);
            }
         }

         return list;
      } else {
         return this.kjs$getEntities();
      }
   }

   @Nullable
   default Entity kjs$getEntityByUUID(UUID id) {
      for (Entity entity : this.kjs$getMcEntities()) {
         if (entity.getUUID().equals(id)) {
            return entity;
         }
      }

      return null;
   }

   @Nullable
   default Entity kjs$getEntityByNetworkID(int id) {
      for (Entity entity : this.kjs$getMcEntities()) {
         if (entity.getId() == id) {
            return entity;
         }
      }

      return null;
   }
}
