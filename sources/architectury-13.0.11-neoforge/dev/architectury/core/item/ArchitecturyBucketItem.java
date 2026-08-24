package dev.architectury.core.item;

import dev.architectury.hooks.fluid.FluidBucketHooks;
import dev.architectury.platform.hooks.EventBusesHooks;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArchitecturyBucketItem extends BucketItem {
   private static final Logger LOGGER = LogManager.getLogger(ArchitecturyBucketItem.class);

   public ArchitecturyBucketItem(Supplier<? extends Fluid> fluid, Properties properties) {
      super(fluid.get(), properties);
      EventBusesHooks.whenAvailable("architectury", bus -> bus.addListener(event -> {
         if (BuiltInRegistries.ITEM.containsValue(this)) {
            event.registerItem(FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), new ItemLike[]{this});
         } else {
            LOGGER.warn("Tried to register a bucket item capability for an item that is not registered: {}", this);
         }
      }));
   }

   public final Fluid getContainedFluid() {
      return FluidBucketHooks.getFluid(this);
   }
}
