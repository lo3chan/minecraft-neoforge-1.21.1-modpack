package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public class NBTProperty extends RandomProperty {
   private final Map<String, NBTProperty.NBTTester> NBT_MAP;
   private boolean printAll = false;
   private final String prefix;
   protected static final CompoundTag INTENTIONAL_FAILURE = new CompoundTag();
   private static Set<String> crashMessages = new HashSet<>();
   private boolean nullMessage = true;

   protected NBTProperty(Properties properties, int propertyNum, String nbtPrefix) throws RandomProperty.RandomPropertyException {
      this.prefix = nbtPrefix;
      String keyPrefix = this.prefix + "." + propertyNum + ".";
      this.NBT_MAP = new LinkedHashMap<>();

      for (Entry<Object, Object> entry : properties.entrySet()) {
         String key = entry.getKey().toString();
         if (key != null && key.startsWith(keyPrefix)) {
            String nbtName = key.replaceFirst(keyPrefix, "");
            String instruction = entry.getValue().toString().trim().replace("print_raw:", "print:raw:");
            if (nbtName.isBlank() || instruction.isBlank()) {
               throw new RandomProperty.RandomPropertyException("NBT failed, as instruction or nbt name was blank: " + keyPrefix + nbtName + "=" + instruction);
            }

            this.printAll = this.printAll || instruction.startsWith("print_all:");
            this.NBT_MAP.put(nbtName, NBTProperty.NBTTester.of(nbtName, instruction));
         }
      }

      if (this.NBT_MAP.isEmpty()) {
         throw new RandomProperty.RandomPropertyException("NBT failed as the final testing map was empty");
      }
   }

   public static NBTProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new NBTProperty(properties, propertyNum, "nbt");
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   private static boolean isStringValidInt(String string) {
      try {
         Integer.parseInt(string);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   @Nullable
   protected CompoundTag getEntityNBT(ETFEntityRenderState entity) {
      return entity.nbt();
   }

   @Override
   protected boolean testEntityInternal(ETFEntityRenderState entity) {
      if (entity != null) {
         CompoundTag entityNBT;
         try {
            entityNBT = this.getEntityNBT(entity);
         } catch (Exception var5) {
            String crashMessage = var5.getMessage();
            if (this.printAll || !crashMessages.contains(crashMessage)) {
               if (!this.printAll) {
                  crashMessages.add(crashMessage);
               }

               ETFUtils2.logError(this.prefix + " test crashed reading entity NBT: " + crashMessage);
               var5.printStackTrace();
            }

            throw var5;
         }

         if (entityNBT == INTENTIONAL_FAILURE) {
            if (this.printAll) {
               ETFUtils2.logMessage(this.prefix + " property [full] print:\n<NBT is missing>");
            }

            return false;
         } else if (entityNBT != null && !entityNBT.isEmpty()) {
            if (this.printAll) {
               ETFUtils2.logMessage(this.prefix + " property [full] print:\n" + this.formatNbtPretty(entityNBT));
            }

            return this.testAllNBTCases(entityNBT);
         } else {
            if (this.printAll) {
               ETFUtils2.logMessage(this.prefix + " property [full] print:\n<NBT is empty or missing>");
            }

            ETFUtils2.logError(this.prefix + " test failed, as could not read entity NBT");
            return false;
         }
      } else {
         if (this.printAll || this.nullMessage) {
            this.nullMessage = false;
            ETFUtils2.logError(this.prefix + " test failed reading null entity NBT: ");
         }

         return false;
      }
   }

   protected boolean testAllNBTCases(CompoundTag entityNBT) {
      for (Entry<String, NBTProperty.NBTTester> nbtPropertyEntry : this.NBT_MAP.entrySet()) {
         NBTProperty.NBTTester data = nbtPropertyEntry.getValue();
         List<Tag> finalNBTElement = this.findNBTElements(entityNBT, nbtPropertyEntry.getKey());
         boolean doesTestPass;
         if (finalNBTElement == null) {
            doesTestPass = data.wantsBlank;
         } else {
            boolean found = false;

            for (Tag nbt : finalNBTElement) {
               if (nbt != null) {
                  found = data.tester.apply(nbt);
                  if (found) {
                     break;
                  }
               }
            }

            doesTestPass = found;
         }

         if (data.print) {
            String printString = finalNBTElement == null
               ? "<NBT component not found>"
               : finalNBTElement.stream().map(NBTProperty::getAsString).reduce("", (a, b) -> a + "\n" + b);
            ETFUtils2.logMessage(this.prefix + " NBT property [single] print data: " + nbtPropertyEntry.getKey() + "=" + printString);
            ETFUtils2.logMessage(this.prefix + " NBT property [single] print result: " + (data.inverts != doesTestPass));
         }

         if (data.inverts == doesTestPass) {
            return false;
         }
      }

      return true;
   }

   private static String getAsString(Tag nbt) {
      return nbt.getAsString();
   }

   private static Number getAsNumber(NumericTag nbt) {
      return nbt.getAsNumber();
   }

   public String formatNbtPretty(CompoundTag nbt) {
      String input = getAsString(nbt);
      StringBuilder output = new StringBuilder();
      int indent = 1;
      boolean inString = false;

      for (int i = 0; i < input.length(); i++) {
         char c = input.charAt(i);
         if (inString && c != '"') {
            output.append(c);
         } else {
            switch (c) {
               case '"':
                  inString = !inString;
                  output.append(c);
                  break;
               case ',':
                  output.append(c).append('\n').append(" ".repeat(indent));
                  break;
               case ':':
                  output.append(c).append(" ");
                  break;
               case '[':
               case '{':
                  output.append(c).append('\n');
                  indent += 4;
                  output.append(" ".repeat(indent));
                  break;
               case ']':
               case '}':
                  indent -= 4;
                  output.append('\n').append(" ".repeat(indent)).append(c);
                  break;
               default:
                  output.append(c);
            }
         }
      }

      return output.toString().replaceAll("\\{\\s+}", "{}").replaceAll("\\[\\s+]", "[]");
   }

   @Nullable
   private List<Tag> findNBTElements(CompoundTag entityNBT, String nbtIdentifier) {
      String[] instructions = nbtIdentifier.split("\\.");
      int index = 0;
      return this.findByIteration(entityNBT, instructions, index);
   }

   @Nullable
   private List<Tag> findByIteration(Tag element, String[] instructions, int index) {
      if (index < instructions.length && element != null) {
         String instruction = instructions[index];
         List<Tag> nextElements = null;
         if (element instanceof CompoundTag nbtCompound) {
            Tag single = nbtCompound.get(instruction);
            if (single != null) {
               boolean notFinalInstruction = index < instructions.length - 1;
               nextElements = notFinalInstruction ? this.findByIteration(single, instructions, index + 1) : Collections.singletonList(single);
            }
         } else if (element instanceof CollectionTag nbtList) {
            nextElements = this.handleListInstruction(nbtList, instructions, index);
         }

         return nextElements != null && !nextElements.isEmpty() ? nextElements : null;
      } else {
         return null;
      }
   }

   @Nullable
   private List<Tag> handleListInstruction(CollectionTag<Tag> nbtList, String[] instructions, int index) {
      if (index < instructions.length && nbtList != null) {
         String instruction = instructions[index];
         boolean notFinalInstruction = index < instructions.length - 1;
         if ("*".equals(instruction)) {
            if (notFinalInstruction) {
               List<Tag> result = new ArrayList<>();

               for (Tag tag : nbtList) {
                  List<Tag> find = this.findByIteration(tag, instructions, index + 1);
                  if (find != null) {
                     result.addAll(find);
                  }
               }

               return result.isEmpty() ? null : result;
            } else {
               return nbtList.stream().toList();
            }
         } else if (notFinalInstruction && isStringValidInt(instruction)) {
            try {
               return Collections.singletonList((Tag)nbtList.get(Integer.parseInt(instruction)));
            } catch (IndexOutOfBoundsException var10) {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"nbt"};
   }

   @Override
   protected String getPrintableRuleInfo() {
      return null;
   }

   public record NBTTester(boolean inverts, Function<Tag, Boolean> tester, boolean wantsBlank, boolean print) {
      public static NBTProperty.NBTTester of(String nbtId, String instructionMaybePrint) throws RandomProperty.RandomPropertyException {
         try {
            String step1 = instructionMaybePrint.replaceFirst("^print_all:", "");
            boolean printSingle = step1.startsWith("print:");
            String step2 = printSingle ? step1.substring(6) : step1;
            boolean invert = step2.startsWith("!");
            String instruction = invert ? step2.substring(1) : step2;
            if (instruction.startsWith("raw:")) {
               String raw = instruction.replaceFirst("raw:", "");
               boolean blank = raw.isBlank();
               StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher matcher = blank
                  ? String::isBlank
                  : StringArrayOrRegexProperty.getStringMatcher_Regex_Pattern_List_Single(raw);
               if (matcher == null) {
                  throw new RandomProperty.RandomPropertyException("NBT failed, as raw: instruction was invalid: " + instruction);
               } else {
                  return new NBTProperty.NBTTester(invert, s -> matcher.testString(NBTProperty.getAsString(s)), blank, printSingle);
               }
            } else if (instruction.startsWith("exists:")) {
               boolean exists = instruction.contains("exists:true");
               boolean notExists = instruction.contains("exists:false");
               return new NBTProperty.NBTTester(invert, s -> exists, notExists, printSingle);
            } else if (instruction.startsWith("range:")) {
               SimpleIntegerArrayProperty.IntRange range = SimpleIntegerArrayProperty.getIntRange(instruction.replaceFirst("range:", ""));
               return new NBTProperty.NBTTester(invert, s -> {
                  if (s instanceof NumericTag nbtNumber) {
                     return range.isWithinRange(NBTProperty.getAsNumber(nbtNumber).intValue());
                  } else {
                     ETFUtils2.logWarn("Invalid range for non-number NBT: " + nbtId + "=" + instruction);
                     return false;
                  }
               }, false, printSingle);
            } else {
               StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher matcher = StringArrayOrRegexProperty.getStringMatcher_Regex_Pattern_List_Single(
                  instruction
               );
               if (matcher == null) {
                  throw new RandomProperty.RandomPropertyException("NBT failed, as instruction was invalid: " + instruction);
               } else {
                  return new NBTProperty.NBTTester(invert, s -> {
                     String test = s instanceof NumericTag ? NBTProperty.getAsString(s).replaceAll("[^\\d.]", "") : NBTProperty.getAsString(s);
                     return matcher.testString(test);
                  }, false, printSingle);
               }
            }
         } catch (RandomProperty.RandomPropertyException var10) {
            throw var10;
         } catch (Exception var11) {
            var11.printStackTrace();
            throw new RandomProperty.RandomPropertyException("NBT failed, unexpected exception: " + var11.getMessage());
         }
      }
   }
}
