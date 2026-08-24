package net.cibernet.alchemancy.properties;

import java.util.Objects;
import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractTimerProperty extends Property implements IDataHolder<Long> {
   public void resetStartTimestamp(ItemStack stack) {
      if (InfusedPropertiesHelper.hasProperty(stack, this.asHolder())) {
         this.setData(stack, AlchemancyServerData.getGlobalTimer());
      }
   }

   public long getElapsedTime(ItemStack stack) {
      long timestamp = this.getData(stack);
      return timestamp == 0L ? 0L : Math.max(0L, AlchemancyServerData.getGlobalTimer() - timestamp);
   }

   public boolean hasRecordedTimestamp(ItemStack stack) {
      return !Objects.equals(this.getData(stack), this.getDefaultData());
   }

   public Long readData(CompoundTag tag) {
      return tag.getLong("starting_timestamp");
   }

   public CompoundTag writeData(final Long data) {
      return new CompoundTag() {
         {
            this.putLong("starting_timestamp", data);
         }
      };
   }

   public Long getDefaultData() {
      return 0L;
   }
}
