package com.yungnickyoung.minecraft.yungsapi.services;

import com.yungnickyoung.minecraft.yungsapi.YungsApiCommon;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterFieldRouter;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.module.BlockEntityTypeModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.BlockModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.CommandModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.CompostModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.CreativeModeTabModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.CriteriaModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.EntityDataSerializerModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.EntityTypeModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.FeatureModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.ItemModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.MobEffectModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.ParticleTypeModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.PlacementModifierTypeModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.PostLoadModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.PotionModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.SoundEventModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePieceTypeModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePlacementTypeModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModuleForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructureProcessorTypeModuleNeoForge;
import com.yungnickyoung.minecraft.yungsapi.module.StructureTypeModuleNeoForge;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import org.objectweb.asm.Type;

public class NeoForgeAutoRegisterHelper implements IAutoRegisterHelper {
   @Override
   public void collectAllAutoRegisterFieldsInPackage(String packageName) {
      Map<Type, String> classToNamespaceMap = new HashMap<>();
      List<AnnotationData> annotations = ModList.get()
         .getAllScanData()
         .stream()
         .map(ModFileScanData::getAnnotations)
         .flatMap(Collection::stream)
         .filter(a -> a.annotationType().equals(Type.getType(AutoRegister.class)))
         .toList();
      annotations.stream()
         .filter(data -> data.targetType() == ElementType.TYPE)
         .forEach(data -> classToNamespaceMap.put(data.clazz(), (String)data.annotationData().get("value")));
      annotations.stream()
         .filter(data -> data.targetType() == ElementType.FIELD)
         .forEach(
            data -> {
               String modId = classToNamespaceMap.get(data.clazz());
               if (modId == null) {
                  YungsApiCommon.LOGGER.error("Missing class AutoRegister annotation for field {}", data.memberName());
               } else {
                  Class<?> clazz;
                  try {
                     clazz = Class.forName(data.clazz().getClassName(), false, AutoRegistrationManager.class.getClassLoader());
                  } catch (ClassNotFoundException var10) {
                     YungsApiCommon.LOGGER.error("Unable to find class containing AutoRegister field {}. This shouldn't happen!", data.memberName());
                     YungsApiCommon.LOGGER
                        .error(
                           "If you're using AutoRegister on a field, make sure the containing class is also using the AutoRegister annotation with your mod ID as the value."
                        );
                     throw new RuntimeException(var10);
                  }

                  Field f;
                  try {
                     f = clazz.getDeclaredField(data.memberName());
                  } catch (NoSuchFieldException var9) {
                     YungsApiCommon.LOGGER
                        .error("Unable to find AutoRegister field with name {} in class {}. This shouldn't happen!", data.memberName(), clazz.getName());
                     throw new RuntimeException(var9);
                  }

                  Object o;
                  try {
                     o = f.get(null);
                  } catch (IllegalAccessException var8) {
                     YungsApiCommon.LOGGER.error("Unable to get value for AutoRegister field {}. This shouldn't happen!", data.memberName());
                     throw new RuntimeException(var8);
                  }

                  String name = (String)data.annotationData().get("value");
                  AutoRegisterField autoRegisterField = new AutoRegisterField(o, ResourceLocation.fromNamespaceAndPath(modId, name));
                  AutoRegisterFieldRouter.queueField(autoRegisterField);
               }
            }
         );
   }

   @Override
   public void invokeAllAutoRegisterMethods(String packageName) {
      List<Method> methods = new ArrayList<>();
      List<AnnotationData> annotations = ModList.get()
         .getAllScanData()
         .stream()
         .map(ModFileScanData::getAnnotations)
         .flatMap(Collection::stream)
         .filter(a -> a.annotationType().equals(Type.getType(AutoRegister.class)))
         .toList();
      annotations.stream()
         .filter(data -> data.targetType() == ElementType.METHOD)
         .forEach(
            data -> {
               Class<?> clazz;
               try {
                  clazz = Class.forName(data.clazz().getClassName(), false, AutoRegistrationManager.class.getClassLoader());
               } catch (ClassNotFoundException var6) {
                  YungsApiCommon.LOGGER.error("Unable to find class containing AutoRegister method {}. This shouldn't happen!", data.memberName());
                  YungsApiCommon.LOGGER
                     .error(
                        "If you're using AutoRegister on a method, make sure the containing class is also using the AutoRegister annotation with your mod ID as the value."
                     );
                  throw new RuntimeException(var6);
               }

               Method m;
               try {
                  m = clazz.getDeclaredMethod(data.memberName().substring(0, data.memberName().indexOf("(")));
               } catch (NoSuchMethodException var5) {
                  YungsApiCommon.LOGGER
                     .error("Unable to find AutoRegister method with name {} in class {}. This shouldn't happen!", data.memberName(), clazz.getName());
                  throw new RuntimeException(var5);
               }

               m.setAccessible(true);
               methods.add(m);
            }
         );
      PostLoadModuleNeoForge.METHODS.addAll(methods);
      PostLoadModuleNeoForge.init();
   }

   @Override
   public void processQueuedAutoRegEntries() {
      SoundEventModuleNeoForge.processEntries();
      StructurePieceTypeModuleNeoForge.processEntries();
      StructurePoolElementTypeModuleForge.processEntries();
      CriteriaModuleNeoForge.processEntries();
      StructureTypeModuleNeoForge.processEntries();
      FeatureModuleNeoForge.processEntries();
      PlacementModifierTypeModuleNeoForge.processEntries();
      CreativeModeTabModuleNeoForge.processEntries();
      ItemModuleNeoForge.processEntries();
      BlockModuleNeoForge.processEntries();
      BlockEntityTypeModuleNeoForge.processEntries();
      StructureProcessorTypeModuleNeoForge.processEntries();
      StructurePlacementTypeModuleNeoForge.processEntries();
      ParticleTypeModuleNeoForge.processEntries();
      EntityTypeModuleNeoForge.processEntries();
      EntityDataSerializerModuleNeoForge.processEntries();
      MobEffectModuleNeoForge.processEntries();
      PotionModuleNeoForge.processEntries();
      CommandModuleNeoForge.processEntries();
   }

   @Override
   public void registerBrewingRecipe(Holder<Potion> inputPotion, Supplier<Item> ingredient, Holder<Potion> outputPotion) {
      PotionModuleNeoForge.BrewingRecipe recipe = new PotionModuleNeoForge.BrewingRecipe(inputPotion, ingredient, outputPotion);
      PotionModuleNeoForge.BREWING_RECIPES.add(recipe);
   }

   @Override
   public void addCompostableItem(Supplier<Item> ingredient, float compostChance) {
      CompostModuleNeoForge.COMPOSTABLES.put((ItemLike)ingredient.get(), compostChance);
   }
}
