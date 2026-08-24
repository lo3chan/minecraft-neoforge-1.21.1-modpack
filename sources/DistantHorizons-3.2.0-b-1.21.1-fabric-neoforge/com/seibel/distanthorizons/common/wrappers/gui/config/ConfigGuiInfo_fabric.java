package com.seibel.distanthorizons.common.wrappers.gui.config;

import com.seibel.distanthorizons.core.config.gui.IConfigGuiInfo;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.class_2561;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_4185.class_4241;
import org.jetbrains.annotations.Nullable;

public class ConfigGuiInfo_fabric implements IConfigGuiInfo {
   @Nullable
   public class_2561 errorMessage;
   public BiFunction<class_342, class_4185, Predicate<String>> tooltipFunction;
   public SimpleEntry<class_4241, Function<Object, class_2561>> buttonOptionMap;
}
