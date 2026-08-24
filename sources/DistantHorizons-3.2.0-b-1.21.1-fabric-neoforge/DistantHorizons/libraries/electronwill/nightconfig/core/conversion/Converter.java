package DistantHorizons.libraries.electronwill.nightconfig.core.conversion;

@Deprecated
public interface Converter<FieldType, ConfigValueType> {
   FieldType convertToField(ConfigValueType object);

   ConfigValueType convertFromField(FieldType object);
}
