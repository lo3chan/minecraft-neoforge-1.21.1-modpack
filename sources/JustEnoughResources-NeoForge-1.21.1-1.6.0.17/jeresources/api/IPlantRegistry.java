package jeresources.api;

import jeresources.api.drop.PlantDrop;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public interface IPlantRegistry {
   void register(ItemStack var1, BushBlock var2, Property<?> var3, PlantDrop... var4);

   void register(ItemStack var1, BlockState var2, Property<?> var3, PlantDrop... var4);

   void register(ItemStack var1, BushBlock var2, PlantDrop... var3);

   void register(ItemStack var1, BlockState var2, PlantDrop... var3);

   void register(ItemStack var1, Property<?> var2, PlantDrop... var3);

   void register(ItemStack var1, PlantDrop... var2);

   <T extends BushBlock> void register(T var1, Property<?> var2, PlantDrop... var3);

   <T extends BushBlock> void register(T var1, PlantDrop... var2);

   void registerWithSoil(ItemStack var1, BushBlock var2, Property<?> var3, BlockState var4, PlantDrop... var5);

   void registerWithSoil(ItemStack var1, BlockState var2, Property<?> var3, BlockState var4, PlantDrop... var5);

   void registerWithSoil(ItemStack var1, BushBlock var2, BlockState var3, PlantDrop... var4);

   void registerWithSoil(ItemStack var1, BlockState var2, BlockState var3, PlantDrop... var4);

   void registerWithSoil(ItemStack var1, Property<?> var2, BlockState var3, PlantDrop... var4);

   void registerWithSoil(ItemStack var1, BlockState var2, PlantDrop... var3);

   <T extends BushBlock> void registerWithSoil(T var1, Property<?> var2, BlockState var3, PlantDrop... var4);

   <T extends BushBlock> void registerWithSoil(T var1, BlockState var2, PlantDrop... var3);

   void registerDrops(@NotNull ItemStack var1, PlantDrop... var2);
}
