package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.world.CitadelServerData;
import com.github.alexthe666.citadel.server.world.ModifiableTickRateServer;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Post;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Pre;

public class CitadelEvents {
   private int updateTimer;

   @SubscribeEvent
   public void onEntityUpdateDebug(Post event) {
   }

   @SubscribeEvent
   public void onRightClickBlock(RightClickBlock event) {
      if (event.getLevel().getBlockState(event.getPos()).is(Blocks.LECTERN) && LecternBooks.isLecternBook(event.getItemStack())) {
         event.getEntity().getCooldowns().addCooldown(event.getItemStack().getItem(), 1);
         BlockState oldLectern = event.getLevel().getBlockState(event.getPos());
         if (event.getLevel().getBlockEntity(event.getPos()) instanceof LecternBlockEntity oldBe && !oldBe.hasBook()) {
            BlockState newLectern = (BlockState)((BlockState)((BlockState)Citadel.LECTERN
                     .get()
                     .defaultBlockState()
                     .setValue(CitadelLecternBlock.FACING, (Direction)oldLectern.getValue(LecternBlock.FACING)))
                  .setValue(CitadelLecternBlock.POWERED, (Boolean)oldLectern.getValue(LecternBlock.POWERED)))
               .setValue(CitadelLecternBlock.HAS_BOOK, true);
            event.getLevel().setBlockAndUpdate(event.getPos(), newLectern);
            CitadelLecternBlockEntity newBe = new CitadelLecternBlockEntity(event.getPos(), newLectern);
            ItemStack bookCopy = event.getItemStack().copy();
            bookCopy.setCount(1);
            newBe.setBook(bookCopy);
            if (!event.getEntity().isCreative()) {
               event.getItemStack().shrink(1);
            }

            event.getLevel().setBlockEntity(newBe);
            event.getEntity().swing(event.getHand(), true);
            event.getLevel().playSound(null, event.getPos(), SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
      }
   }

   @SubscribeEvent
   public void onPlayerClone(Clone event) {
      if (CitadelEntityData.getCitadelTag(event.getOriginal()) != null) {
         CitadelEntityData.setCitadelTag(event.getEntity(), CitadelEntityData.getCitadelTag(event.getOriginal()));
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onServerTick(Pre event) {
      if (event.getServer().isRunning()) {
         ServerTickRateTracker tickRateTracker = CitadelServerData.get(event.getServer()).getOrCreateTickRateTracker();
         if (event.getServer() instanceof ModifiableTickRateServer modifiableServer) {
            long l = tickRateTracker.getServerTickLengthMs();
            if (l == 50L) {
               modifiableServer.resetGlobalTickLengthMs();
            } else {
               modifiableServer.setGlobalTickLengthMs(tickRateTracker.getServerTickLengthMs());
            }

            if (!event.getServer().isShutdown()) {
               tickRateTracker.masterTick();
            }
         }
      }
   }
}
