package dev.tr7zw.notenoughanimations.access;

import dev.tr7zw.notenoughanimations.versionless.animations.DataHolder;
import java.util.function.Supplier;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;

public interface PlayerData {
   int isUpdated(int var1);

   void setUpdated(int var1);

   float[] getLastRotations();

   ItemStack getSideSword();

   void setSideSword(ItemStack var1);

   void setDisableBodyRotation(boolean var1);

   boolean isDisableBodyRotation();

   void setRotateBodyToHead(boolean var1);

   boolean isRotateBodyToHead();

   ItemStack[] getLastHeldItems();

   int getItemSwapAnimationTimer();

   void setItemSwapAnimationTimer(int var1);

   int getLastAnimationSwapTick();

   void setLastAnimationSwapTick(int var1);

   void setPoseOverwrite(Pose var1);

   Pose getPoseOverwrite();

   <T> T getData(DataHolder<T> var1, Supplier<T> var2);
}
