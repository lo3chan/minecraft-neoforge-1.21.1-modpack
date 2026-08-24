package com.aetherteam.aether.integration.jade;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.block.dungeon.DoorwayBlock;
import com.aetherteam.aether.block.dungeon.TrappedBlock;
import com.aetherteam.aether.block.dungeon.TreasureDoorwayBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import noobanidus.mods.lootr.common.block.LootrChestBlock;
import noobanidus.mods.lootr.neoforge.init.ModBlocks;
import org.jetbrains.annotations.Nullable;
import snownee.jade.addon.vanilla.VanillaPlugin;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class AetherJadePlugin implements IWailaPlugin {
   public void registerClient(IWailaClientRegistration registration) {
      registration.addRayTraceCallback(this::registerAetherOverrides);
   }

   @Nullable
   public Accessor<?> registerAetherOverrides(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor) {
      if (accessor instanceof BlockAccessor target) {
         Player player = accessor.getPlayer();
         if (player.isCreative() || player.isSpectator()) {
            return accessor;
         }

         IWailaClientRegistration client = VanillaPlugin.CLIENT_REGISTRATION;
         if (target.getBlock() instanceof TrappedBlock trapped) {
            return client.blockAccessor().from(target).blockState(trapped.getFacadeBlock()).build();
         }

         if (target.getBlock() instanceof DoorwayBlock door) {
            ResourceLocation doorLocation = BuiltInRegistries.BLOCK.getKey(door);
            Block doorBlock = this.getLockedDungeonBlock(doorLocation.getPath());
            if (doorBlock != null) {
               return client.blockAccessor().from(target).blockState(doorBlock.defaultBlockState()).build();
            }
         } else if (target.getBlock() instanceof TreasureDoorwayBlock doorx) {
            ResourceLocation doorLocation = BuiltInRegistries.BLOCK.getKey(doorx);
            Block doorBlock = this.getLockedDungeonBlock(doorLocation.getPath());
            if (doorBlock != null) {
               return client.blockAccessor().from(target).blockState(doorBlock.defaultBlockState()).build();
            }
         } else if (target.getBlock() == AetherBlocks.CHEST_MIMIC.get()) {
            if (ModList.get().isLoaded("lootr")) {
               return client.blockAccessor()
                  .from(target)
                  .serverData(this.createFakeChestData(target))
                  .blockState(((LootrChestBlock)ModBlocks.CHEST.get()).defaultBlockState())
                  .build();
            }

            return client.blockAccessor().from(target).serverData(this.createFakeChestData(target)).blockState(Blocks.CHEST.defaultBlockState()).build();
         }
      }

      return accessor;
   }

   private CompoundTag createFakeChestData(BlockAccessor target) {
      CompoundTag tag = new CompoundTag();
      if (!target.getServerData().isEmpty()) {
         tag.putBoolean("Loot", true);
      }

      return tag;
   }

   @Nullable
   private Block getLockedDungeonBlock(String name) {
      if (name.startsWith("boss_doorway_")) {
         return (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("aether", "locked_" + name.substring(13)));
      } else {
         return name.startsWith("treasure_doorway_")
            ? (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("aether", "locked_" + name.substring(17)))
            : null;
      }
   }
}
