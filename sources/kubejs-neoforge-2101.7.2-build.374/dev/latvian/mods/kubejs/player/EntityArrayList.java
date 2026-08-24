package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.core.DataSenderKJS;
import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public class EntityArrayList extends ArrayList<Entity> implements MessageSenderKJS, DataSenderKJS {
   public static final Predicate<Entity> ALWAYS_TRUE_PREDICATE = entity -> true;

   public EntityArrayList(int size) {
      super(size);
   }

   public EntityArrayList(Iterable<? extends Entity> entities) {
      this(entities instanceof Collection c ? c.size() : 4);
      this.addAllIterable(entities);
   }

   @Deprecated(
      forRemoval = true,
      since = "7.2"
   )
   public EntityArrayList(Level level, Iterable<? extends Entity> entities) {
      this(entities);
   }

   public void addAllIterable(Iterable<? extends Entity> entities) {
      if (entities instanceof Collection c) {
         this.addAll(c);
      } else {
         for (Entity entity : entities) {
            this.add(entity);
         }
      }
   }

   @Override
   public Component kjs$getName() {
      return Component.literal("EntityList");
   }

   @Override
   public Component kjs$getDisplayName() {
      return Component.literal(this.toString()).kjs$lightPurple();
   }

   @Info(
      value = "Sends a message in chat to every entity in the list.",
      params = {@Param(
         name = "message",
         value = "A text component. It may be a string, which will be implicitly wrapped into a text component."
      )}
   )
   @Override
   public void kjs$tell(Component message) {
      for (Entity entity : this) {
         entity.kjs$tell(message);
      }
   }

   @Override
   public void kjs$setStatusMessage(Component message) {
      for (Entity entity : this) {
         entity.kjs$setStatusMessage(message);
      }
   }

   @Info(
      value = "Each entity in the list runs the specified console command with their permission level.",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   public void kjs$runCommand(String command) {
      for (Entity entity : this) {
         entity.kjs$runCommand(command);
      }
   }

   @Info(
      value = "Each entity in the list runs the specified console command with their permission level. The command won't output any logs in chat nor console",
      params = {@Param(
         name = "command",
         value = "The console command. Slash at the beginning is optional."
      )}
   )
   @Override
   public void kjs$runCommandSilent(String command) {
      for (Entity entity : this) {
         entity.kjs$runCommandSilent(command);
      }
   }

   @Override
   public void kjs$setActivePostShader(@Nullable ResourceLocation id) {
      for (Entity entity : this) {
         entity.kjs$setActivePostShader(id);
      }
   }

   @Info("Kills every entity in the list.")
   public void kill() {
      for (Entity entity : this) {
         entity.kill();
      }
   }

   @Info("Plays a sound from each entity in the list, unless the entity is silent.")
   public void playSound(SoundEvent id, float volume, float pitch) {
      for (Entity entity : this) {
         entity.playSound(id, volume, pitch);
      }
   }

   @Info("Plays a sound from each entity in the list, unless the entity is silent.")
   public void playSound(SoundEvent id) {
      this.playSound(id, 1.0F, 1.0F);
   }

   @Info(
      value = "Filters the entity list by passing each entity through a given predicate.\nEntities that pass the predicate will end up in the resulting entity list.\n",
      params = {@Param(
         name = "filter",
         value = "The predicate - a function that takes an argument of `Entity` and returns a boolean."
      )}
   )
   public EntityArrayList filter(Predicate<Entity> filter) {
      if (this.isEmpty()) {
         return this;
      } else {
         EntityArrayList list = new EntityArrayList(this.size() / 4);

         for (Entity entity : this) {
            if (filter.test(entity)) {
               list.add(entity);
            }
         }

         return list;
      }
   }

   @Info(
      value = "Filters the entity list by passing each entity through all predicates in provided list.\nEntities that pass at least one of the predicates will end up in the resulting entity list.\n",
      params = {@Param(
         name = "filterList",
         value = "The list of predicates - functions that take one argument of `Entity` and return boolean values."
      )}
   )
   public EntityArrayList filterList(List<Predicate<Entity>> filterList) {
      if (!this.isEmpty() && !filterList.isEmpty()) {
         EntityArrayList list = new EntityArrayList(this.size());

         for (Entity entity : this) {
            for (Predicate<Entity> filter : filterList) {
               if (filter.test(entity)) {
                  list.add(entity);
               }
            }
         }

         return list;
      } else {
         return this;
      }
   }

   @Info(
      value = "Filters the entity list based on the provided `EntitySelector`.",
      params = {@Param(
         name = "selector",
         value = "The entity selector. It may be a string representing the entity selector as seen in commands, such as `'@e[distance=..25]'`"
      )}
   )
   public EntityArrayList filterSelector(EntitySelector selector) {
      return this.filterList(selector.contextFreePredicates);
   }

   @Info(
      value = "Filters the entity list based on distance to the given point.\nEntities that are closer than `distance` away from the point specified by `x`, `y` and `z` coordinates will end up in the resulting list.\n",
      params = {@Param(
         name = "x",
         value = "The `x` coordinate of the point."
      ), @Param(
         name = "y",
         value = "The `y` coordinate of the point."
      ), @Param(
         name = "z",
         value = "The `z` coordinate of the point."
      ), @Param(
         name = "distance",
         value = "The maximum distance of entities from the point."
      )}
   )
   public EntityArrayList filterDistance(double x, double y, double z, double distance) {
      EntityArrayList list = new EntityArrayList(this.size());

      for (Entity entity : this) {
         if (entity.distanceToSqr(x, y, z) <= distance * distance) {
            list.add(entity);
         }
      }

      return list;
   }

   @Info(
      value = "Filters the entity list based on distance to the given block position.\nEntities that are closer than `distance` away from the center of the block will end up in the resulting list.\n",
      params = {@Param(
         name = "pos",
         value = "The `BlockPos` - that is the center of the block at specified position. It can be a 3-element array of integers, such as `[64, 25, 39]`."
      ), @Param(
         name = "distance",
         value = "The maximum distance of entities from the point."
      )}
   )
   public EntityArrayList filterDistance(BlockPos pos, double distance) {
      return this.filterDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, distance);
   }

   @Info("Results in an entity list containing only players.")
   public EntityArrayList filterPlayers() {
      return this.filter(e -> e instanceof Player);
   }

   @Info("Results in an entity list containing only item entities.")
   public EntityArrayList filterItems() {
      return this.filter(e -> e instanceof ItemEntity);
   }

   @Info(
      value = "Filters the entity list based on the type of the entity. Only entities whose type is equal to the provided one will end up in the resulting list.",
      params = {@Param(
         name = "type",
         value = "The entity type. It may be a string representing an entity ID, like `'minecraft:creeper'`."
      )}
   )
   public EntityArrayList filterType(EntityType<?> type) {
      return this.filter(e -> e.getType() == type);
   }

   @Info(
      value = "Sends NBT data to every player in the list.",
      params = {@Param(
         name = "channel",
         value = "String. Represents the network channel."
      ), @Param(
         name = "data",
         value = "The NBT compound tag containing data to send. May be `null`.\nIt may be a JS object containing data or string representing stringified NBT.\n"
      )}
   )
   @Override
   public void kjs$sendData(String channel, @Nullable CompoundTag data) {
      for (Entity entity : this) {
         if (entity instanceof Player player) {
            player.kjs$sendData(channel, data);
         }
      }
   }

   @Info("Gets the first entity on the list, or `null` if the list is empty.")
   @Nullable
   public Entity getFirst() {
      return this.isEmpty() ? null : this.get(0);
   }
}
