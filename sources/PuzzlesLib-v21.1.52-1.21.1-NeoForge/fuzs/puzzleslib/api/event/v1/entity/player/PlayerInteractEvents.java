package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class PlayerInteractEvents {
   public static final EventInvoker<PlayerInteractEvents.UseBlock> USE_BLOCK = EventInvoker.lookup(PlayerInteractEvents.UseBlock.class);
   public static final EventInvoker<PlayerInteractEvents.AttackBlock> ATTACK_BLOCK = EventInvoker.lookup(PlayerInteractEvents.AttackBlock.class);
   public static final EventInvoker<PlayerInteractEvents.UseItem> USE_ITEM = EventInvoker.lookup(PlayerInteractEvents.UseItem.class);
   public static final EventInvoker<PlayerInteractEvents.UseEntity> USE_ENTITY = EventInvoker.lookup(PlayerInteractEvents.UseEntity.class);
   public static final EventInvoker<PlayerInteractEvents.UseEntityAt> USE_ENTITY_AT = EventInvoker.lookup(PlayerInteractEvents.UseEntityAt.class);
   public static final EventInvoker<PlayerInteractEvents.AttackEntity> ATTACK_ENTITY = EventInvoker.lookup(PlayerInteractEvents.AttackEntity.class);

   private PlayerInteractEvents() {
   }

   @FunctionalInterface
   public interface AttackBlock {
      EventResult onAttackBlock(Player var1, Level var2, InteractionHand var3, BlockPos var4, Direction var5);
   }

   @FunctionalInterface
   public interface AttackEntity {
      EventResult onAttackEntity(Player var1, Level var2, InteractionHand var3, Entity var4);
   }

   @FunctionalInterface
   public interface UseBlock {
      EventResultHolder<InteractionResult> onUseBlock(Player var1, Level var2, InteractionHand var3, BlockHitResult var4);
   }

   @FunctionalInterface
   public interface UseEntity {
      EventResultHolder<InteractionResult> onUseEntity(Player var1, Level var2, InteractionHand var3, Entity var4);
   }

   @FunctionalInterface
   public interface UseEntityAt {
      EventResultHolder<InteractionResult> onUseEntityAt(Player var1, Level var2, InteractionHand var3, Entity var4, Vec3 var5);
   }

   @FunctionalInterface
   public interface UseItem {
      EventResultHolder<InteractionResult> onUseItem(Player var1, Level var2, InteractionHand var3);
   }
}
