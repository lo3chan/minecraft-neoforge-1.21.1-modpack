package net.diebuddies.bridge;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

public class ReflectionsForge {
   public static final Method setupRotations = ObfuscationReflectionHelper.findMethod(
      LivingEntityRenderer.class, "setupRotations", new Class[]{LivingEntity.class, PoseStack.class, float.class, float.class, float.class, float.class}
   );
   public static final Method getBlockLightLevel = ObfuscationReflectionHelper.findMethod(
      EntityRenderer.class, "getBlockLightLevel", new Class[]{Entity.class, BlockPos.class}
   );
   public static final Method headParts = ObfuscationReflectionHelper.findMethod(AgeableListModel.class, "headParts", new Class[0]);
   public static final Method bodyParts = ObfuscationReflectionHelper.findMethod(AgeableListModel.class, "bodyParts", new Class[0]);

   public static Iterable<ModelPart> headParts(AgeableListModel model) {
      try {
         return (Iterable<ModelPart>)headParts.invoke(model);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Iterable<ModelPart> bodyParts(AgeableListModel model) {
      try {
         return (Iterable<ModelPart>)bodyParts.invoke(model);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
