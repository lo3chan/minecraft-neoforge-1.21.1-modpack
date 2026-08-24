package net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.quirks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.Quirk;
import net.joefoxe.hexerei.client.renderer.entity.custom.ai.owl.QuirkController;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.BrowAnimPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FavoriteBlockQuirk implements Quirk {
   private Block favoriteBlock;
   private int ticks;
   private int lastUsed = 0;
   private int offset;

   public FavoriteBlockQuirk(Block favoriteBlock, int ticks) {
      this.favoriteBlock = favoriteBlock;
      this.ticks = ticks;
      this.offset = ticks > 0 ? new Random().nextInt(ticks) : 0;
   }

   public FavoriteBlockQuirk() {
      this(Blocks.AIR, 0);
   }

   public Block getFavoriteBlock() {
      return this.favoriteBlock;
   }

   public static List<FavoriteBlockQuirk> fromController(QuirkController quirkController) {
      List<FavoriteBlockQuirk> quirks = new ArrayList<>();
      quirkController.getActiveQuirks().forEach(quirk -> {
         if (quirk instanceof FavoriteBlockQuirk favoriteBlockQuirk) {
            quirks.add(favoriteBlockQuirk);
         }
      });
      return quirks;
   }

   @Override
   public void clientTick(OwlEntity owl) {
   }

   @Override
   public void serverTick(OwlEntity owl) {
      if (owl.tickCount - this.lastUsed > 60 && owl.tickCount % Math.max(1, this.ticks) == this.offset) {
         BlockPos pos = owl.getBlockPosBelowThatAffectsMyMovement();
         Block block = owl.level().getBlockState(pos).getBlock();
         boolean flag = false;
         if (block == this.favoriteBlock) {
            this.lastUsed = owl.tickCount;
            owl.emotions.setAnger(owl.emotions.getAnger() - 5);
            owl.emotions.setDistress(owl.emotions.getDistress() - 5);
            owl.emotions.setHappiness(owl.emotions.getHappiness() + 15);
            owl.emotionChanged();
            HexereiPacketHandler.sendToNearbyClient(owl.level(), owl, new BrowAnimPacket(owl, OwlEntity.BrowAnim.BOTH, 5 + owl.getRandom().nextInt(10), true));
            flag = true;
         }

         if (!flag) {
            pos = owl.blockPosition();
            block = owl.level().getBlockState(pos).getBlock();
            if (block == this.favoriteBlock) {
               this.lastUsed = owl.tickCount;
               owl.emotions.setAnger(owl.emotions.getAnger() - 5);
               owl.emotions.setDistress(owl.emotions.getDistress() - 5);
               owl.emotions.setHappiness(owl.emotions.getHappiness() + 15);
               owl.emotionChanged();
               HexereiPacketHandler.sendToNearbyClient(
                  owl.level(), owl, new BrowAnimPacket(owl, OwlEntity.BrowAnim.BOTH, 5 + owl.getRandom().nextInt(10), true)
               );
            }
         }
      }
   }

   @Override
   public String getName() {
      return "FavoriteBlockQuirk";
   }

   @Override
   public void write(CompoundTag compound) {
      String blockRegistryName = BuiltInRegistries.BLOCK.getKey(this.favoriteBlock).toString();
      compound.putString("favoriteBlock", blockRegistryName);
      compound.putInt("ticks", this.ticks);
      compound.putInt("offset", this.offset);
   }

   @Override
   public void read(CompoundTag compound) {
      String blockRegistryName = compound.getString("favoriteBlock");
      this.favoriteBlock = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockRegistryName));
      this.ticks = compound.getInt("ticks");
      this.offset = compound.getInt("offset");
   }
}
