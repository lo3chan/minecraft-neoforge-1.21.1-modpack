package com.finndog.moogs_structures.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(
   modid = "moogs_structures",
   bus = Bus.MOD
)
public class StructureNbtUpdaterDatagen {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      ExistingFileHelper exHelper = event.getExistingFileHelper();
      DataGenerator gen = event.getGenerator();
      PackOutput output = gen.getPackOutput();
      if (event.includeServer()) {
         gen.addProvider(true, new StructureNbtUpdater("structures", "moogs_structures", exHelper, output));
      }
   }
}
