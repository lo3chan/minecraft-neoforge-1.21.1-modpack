package com.seibel.distanthorizons.core.config.api;

import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfigValue;
import com.seibel.distanthorizons.core.config.api.converters.DefaultConverter;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.coreapi.interfaces.config.IConverter;
import java.util.function.Consumer;

public class DhApiConfigValue<coreType, apiType> implements IDhApiConfigValue<apiType> {
   private final ConfigEntry<coreType> configBase;
   private final IConverter<coreType, apiType> configConverter;

   public DhApiConfigValue(ConfigEntry<coreType> configBase) {
      this.configBase = configBase;
      this.configConverter = new DefaultConverter<>();
   }

   public DhApiConfigValue(ConfigEntry<coreType> configBase, IConverter<coreType, apiType> newConverter) {
      this.configBase = configBase;
      this.configConverter = newConverter;
   }

   @Override
   public apiType getValue() {
      return this.configConverter.convertToApiType(this.configBase.get());
   }

   @Override
   public apiType getTrueValue() {
      return this.configConverter.convertToApiType(this.configBase.getTrueValue());
   }

   @Override
   public apiType getApiValue() {
      return this.configBase.getApiValue() == null ? null : this.configConverter.convertToApiType(this.configBase.getApiValue());
   }

   @Override
   public boolean setValue(apiType newValue) {
      if (this.configBase.getAllowApiOverride()) {
         this.configBase.setApiValue(this.configConverter.convertToCoreType(newValue));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean clearValue() {
      if (this.configBase.getAllowApiOverride()) {
         this.configBase.setApiValue(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean getCanBeOverrodeByApi() {
      return this.configBase.getAllowApiOverride();
   }

   @Override
   public apiType getDefaultValue() {
      return this.configConverter.convertToApiType(this.configBase.getDefaultValue());
   }

   @Override
   public apiType getMaxValue() {
      return this.configConverter.convertToApiType(this.configBase.getMax());
   }

   @Override
   public apiType getMinValue() {
      return this.configConverter.convertToApiType(this.configBase.getMin());
   }

   @Override
   public void addChangeListener(Consumer<apiType> onValueChangeFunc) {
      this.configBase.addValueChangeListener(coreValue -> {
         apiType apiValue = this.configConverter.convertToApiType(coreValue);
         onValueChangeFunc.accept(apiValue);
      });
   }
}
