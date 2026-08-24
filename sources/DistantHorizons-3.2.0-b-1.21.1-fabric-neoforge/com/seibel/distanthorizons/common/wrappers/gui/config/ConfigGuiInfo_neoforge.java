package com.seibel.distanthorizons.common.wrappers.gui.config;

import com.seibel.distanthorizons.core.config.gui.IConfigGuiInfo;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigGuiInfo_neoforge implements IConfigGuiInfo {
   @Nullable
   public Component errorMessage;
   public BiFunction<EditBox, Button, Predicate<String>> tooltipFunction;
   public SimpleEntry<OnPress, Function<Object, Component>> buttonOptionMap;
}
