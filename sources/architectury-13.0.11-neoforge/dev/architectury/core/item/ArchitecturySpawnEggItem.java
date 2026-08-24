package dev.architectury.core.item;

import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ArchitecturySpawnEggItem extends SpawnEggItem {
   private static final Logger LOGGER = LogManager.getLogger(ArchitecturySpawnEggItem.class);
   private final RegistrySupplier<? extends EntityType<? extends Mob>> entityType;

   protected static DispenseItemBehavior createDispenseItemBehavior() {
      return new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource source, ItemStack stack) {
            Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
            EntityType<?> entityType = ((SpawnEggItem)stack.getItem()).getType(stack);

            try {
               entityType.spawn(source.level(), stack, null, source.pos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
            } catch (Exception var6) {
               LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.pos(), var6);
               return ItemStack.EMPTY;
            }

            stack.shrink(1);
            source.level().gameEvent(null, GameEvent.ENTITY_PLACE, source.pos());
            return stack;
         }
      };
   }

   public ArchitecturySpawnEggItem(
      RegistrySupplier<? extends EntityType<? extends Mob>> entityType, int backgroundColor, int highlightColor, Properties properties
   ) {
      this(entityType, backgroundColor, highlightColor, properties, createDispenseItemBehavior());
   }

   public ArchitecturySpawnEggItem(
      RegistrySupplier<? extends EntityType<? extends Mob>> entityType,
      int backgroundColor,
      int highlightColor,
      Properties properties,
      @Nullable DispenseItemBehavior dispenseItemBehavior
   ) {
      super(null, backgroundColor, highlightColor, properties);
      this.entityType = Objects.requireNonNull(entityType, "entityType");
      SpawnEggItem.BY_ID.remove(null);
      entityType.listen(type -> {
         LOGGER.debug("Registering spawn egg {} for {}", this.toString(), Objects.toString(type.arch$registryName()));
         SpawnEggItem.BY_ID.put(type, this);
         this.defaultType = type;
         if (dispenseItemBehavior != null) {
            DispenserBlock.registerBehavior(this, dispenseItemBehavior);
         }
      });
   }

   public EntityType<?> getType(ItemStack itemStack) {
      EntityType<?> type = super.getType(itemStack);
      return (EntityType<?>)(type == null ? this.entityType.get() : type);
   }
}
