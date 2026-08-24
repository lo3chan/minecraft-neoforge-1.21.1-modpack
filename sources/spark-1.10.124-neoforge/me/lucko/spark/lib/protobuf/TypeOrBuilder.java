package me.lucko.spark.lib.protobuf;

import java.util.List;

public interface TypeOrBuilder extends MessageLiteOrBuilder {
   String getName();

   ByteString getNameBytes();

   List<Field> getFieldsList();

   Field getFields(int index);

   int getFieldsCount();

   List<String> getOneofsList();

   int getOneofsCount();

   String getOneofs(int index);

   ByteString getOneofsBytes(int index);

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
