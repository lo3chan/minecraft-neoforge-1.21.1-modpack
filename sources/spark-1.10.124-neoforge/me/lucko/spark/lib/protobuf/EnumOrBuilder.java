package me.lucko.spark.lib.protobuf;

import java.util.List;

public interface EnumOrBuilder extends MessageLiteOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<EnumValue> getEnumvalueList();

   EnumValue getEnumvalue(int index);

   int getEnumvalueCount();

   List<Option> getOptionsList();

   Option getOptions(int index);

   int getOptionsCount();

   boolean hasSourceContext();

   SourceContext getSourceContext();

   int getSyntaxValue();

   Syntax getSyntax();

   String getEdition();

   ByteString getEditionBytes();
}
