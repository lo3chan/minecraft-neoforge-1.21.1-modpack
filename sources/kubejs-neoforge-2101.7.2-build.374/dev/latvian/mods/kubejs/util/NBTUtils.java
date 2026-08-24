package dev.latvian.mods.kubejs.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtFormatException;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.nbt.StreamTagVisitor.ValueResult;
import net.minecraft.nbt.TagType.VariableSize;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public interface NBTUtils {
   TagType<OrderedCompoundTag> COMPOUND_TYPE = new VariableSize<OrderedCompoundTag>() {
      public OrderedCompoundTag load(DataInput dataInput, NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         OrderedCompoundTag var11;
         try {
            accounter.accountBytes(48L);
            Map<String, Tag> map = new LinkedHashMap<>();

            byte typeId;
            while ((typeId = dataInput.readByte()) != 0) {
               String key = readString(dataInput, accounter);
               TagType<?> valueType = NBTUtils.convertType(TagTypes.getType(typeId));
               Tag tag = CompoundTag.readNamedTagData(valueType, key, dataInput, accounter);
               if (map.put(key, tag) == null) {
                  accounter.accountBytes(36L);
               }
            }

            var11 = new OrderedCompoundTag(map);
         } finally {
            accounter.popDepth();
         }

         return var11;
      }

      public ValueResult parse(DataInput dataInput, StreamTagVisitor visitor, NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         try {
            accounter.accountBytes(48L);

            byte typeId;
            label87:
            while ((typeId = dataInput.readByte()) != 0) {
               TagType<?> valueType = NBTUtils.convertType(TagTypes.getType(typeId));
               switch (visitor.visitEntry(valueType)) {
                  case HALT:
                     return ValueResult.HALT;
                  case BREAK:
                     StringTag.skipString(dataInput);
                     valueType.skip(dataInput, accounter);
                     break label87;
                  case SKIP:
                     StringTag.skipString(dataInput);
                     valueType.skip(dataInput, accounter);
                     break;
                  default:
                     String key = readString(dataInput, accounter);
                     switch (visitor.visitEntry(valueType, key)) {
                        case HALT:
                           return ValueResult.HALT;
                        case BREAK:
                           valueType.skip(dataInput, accounter);
                           break label87;
                        case SKIP:
                           valueType.skip(dataInput, accounter);
                           break;
                        default:
                           accounter.accountBytes(36L);
                           switch (valueType.parse(dataInput, visitor, accounter)) {
                              case HALT:
                                 return ValueResult.HALT;
                              case BREAK:
                           }
                     }
               }
            }

            if (typeId != 0) {
               while ((typeId = dataInput.readByte()) != 0) {
                  StringTag.skipString(dataInput);
                  NBTUtils.convertType(TagTypes.getType(typeId)).skip(dataInput, accounter);
               }
            }

            return visitor.visitContainerEnd();
         } finally {
            accounter.popDepth();
         }
      }

      public void skip(DataInput dataInput, NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         byte typeId;
         try {
            while ((typeId = dataInput.readByte()) != 0) {
               StringTag.skipString(dataInput);
               NBTUtils.convertType(TagTypes.getType(typeId)).skip(dataInput, accounter);
            }
         } finally {
            accounter.popDepth();
         }
      }

      public String getName() {
         return "COMPOUND";
      }

      public String getPrettyName() {
         return "TAG_Compound";
      }

      private static String readString(DataInput dataInput, NbtAccounter nbtAccounter) throws IOException {
         String string = dataInput.readUTF();
         nbtAccounter.accountBytes(28L);
         nbtAccounter.accountBytes(2L, string.length());
         return string;
      }
   };
   TagType<ListTag> LIST_TYPE = new VariableSize<ListTag>() {
      public ListTag load(DataInput dataInput, NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         ListTag var11;
         try {
            accounter.accountBytes(37L);
            byte typeId = dataInput.readByte();
            int size = dataInput.readInt();
            if (typeId == 0 && size > 0) {
               throw new NbtFormatException("Missing type on ListTag");
            }

            accounter.accountBytes(4L, size);
            TagType<?> valueType = NBTUtils.convertType(TagTypes.getType(typeId));
            List<Tag> list = Lists.newArrayListWithCapacity(size);

            for (int j = 0; j < size; j++) {
               list.add(valueType.load(dataInput, accounter));
            }

            var11 = new ListTag(list, typeId);
         } finally {
            accounter.popDepth();
         }

         return var11;
      }

      public ValueResult parse(DataInput dataInput, StreamTagVisitor visitor, NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         try {
            accounter.accountBytes(37L);
            TagType<?> tagType = NBTUtils.convertType(TagTypes.getType(dataInput.readByte()));
            int size = dataInput.readInt();
            switch (visitor.visitList(tagType, size)) {
               case HALT:
                  return ValueResult.HALT;
               case BREAK:
                  tagType.skip(dataInput, size, accounter);
                  return visitor.visitContainerEnd();
               default:
                  accounter.accountBytes(4L, size);
                  int i = 0;

                  while (true) {
                     label126: {
                        if (i < size) {
                           switch (visitor.visitElement(tagType, i)) {
                              case HALT:
                                 return ValueResult.HALT;
                              case BREAK:
                                 tagType.skip(dataInput, accounter);
                                 break;
                              case SKIP:
                                 tagType.skip(dataInput, accounter);
                                 break label126;
                              default:
                                 switch (tagType.parse(dataInput, visitor, accounter)) {
                                    case HALT:
                                       return ValueResult.HALT;
                                    case BREAK:
                                       break;
                                    default:
                                       break label126;
                                 }
                           }
                        }

                        int toSkip = size - 1 - i;
                        if (toSkip > 0) {
                           tagType.skip(dataInput, toSkip, accounter);
                        }

                        return visitor.visitContainerEnd();
                     }

                     i++;
                  }
            }
         } finally {
            accounter.popDepth();
         }
      }

      public void skip(DataInput dataInput, NbtAccounter accounter) throws IOException {
         accounter.pushDepth();

         try {
            TagType<?> tagType = NBTUtils.convertType(TagTypes.getType(dataInput.readByte()));
            int i = dataInput.readInt();
            tagType.skip(dataInput, i, accounter);
         } finally {
            accounter.popDepth();
         }
      }

      public String getName() {
         return "LIST";
      }

      public String getPrettyName() {
         return "TAG_List";
      }
   };

   static void quoteAndEscape(StringBuilder stringBuilder, String string) {
      int start = stringBuilder.length();
      stringBuilder.append(' ');
      char c = 0;

      for (int i = 0; i < string.length(); i++) {
         char d = string.charAt(i);
         if (d == '\\') {
            stringBuilder.append('\\');
         } else if (d == '"' || d == '\'') {
            if (c == 0) {
               c = (char)(d == '\'' ? 34 : 39);
            }

            if (c == d) {
               stringBuilder.append('\\');
            }
         }

         stringBuilder.append(d);
      }

      if (c == 0) {
         c = '\'';
      }

      stringBuilder.setCharAt(start, c);
      stringBuilder.append(c);
   }

   static TagType<?> convertType(TagType<?> tagType) {
      return tagType == CompoundTag.TYPE ? COMPOUND_TYPE : (tagType == ListTag.TYPE ? LIST_TYPE : tagType);
   }

   static JsonElement toJson(@Nullable Tag t) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: iload 2
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/nbt/EndTag, net/minecraft/nbt/StringTag, net/minecraft/nbt/NumericTag, net/minecraft/nbt/CollectionTag, net/minecraft/nbt/CompoundTag ]
      // 0b: tableswitch 232 -1 4 37 43 54 75 96 158
      // 30: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // 33: goto f6
      // 36: aload 1
      // 37: checkcast net/minecraft/nbt/EndTag
      // 3a: astore 3
      // 3b: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // 3e: goto f6
      // 41: aload 1
      // 42: checkcast net/minecraft/nbt/StringTag
      // 45: astore 4
      // 47: new com/google/gson/JsonPrimitive
      // 4a: dup
      // 4b: aload 4
      // 4d: invokevirtual net/minecraft/nbt/StringTag.getAsString ()Ljava/lang/String;
      // 50: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/String;)V
      // 53: goto f6
      // 56: aload 1
      // 57: checkcast net/minecraft/nbt/NumericTag
      // 5a: astore 5
      // 5c: new com/google/gson/JsonPrimitive
      // 5f: dup
      // 60: aload 5
      // 62: invokevirtual net/minecraft/nbt/NumericTag.getAsNumber ()Ljava/lang/Number;
      // 65: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/Number;)V
      // 68: goto f6
      // 6b: aload 1
      // 6c: checkcast net/minecraft/nbt/CollectionTag
      // 6f: astore 6
      // 71: new com/google/gson/JsonArray
      // 74: dup
      // 75: invokespecial com/google/gson/JsonArray.<init> ()V
      // 78: astore 7
      // 7a: aload 6
      // 7c: invokevirtual net/minecraft/nbt/CollectionTag.iterator ()Ljava/util/Iterator;
      // 7f: astore 8
      // 81: aload 8
      // 83: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 88: ifeq a4
      // 8b: aload 8
      // 8d: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 92: checkcast net/minecraft/nbt/Tag
      // 95: astore 9
      // 97: aload 7
      // 99: aload 9
      // 9b: invokestatic dev/latvian/mods/kubejs/util/NBTUtils.toJson (Lnet/minecraft/nbt/Tag;)Lcom/google/gson/JsonElement;
      // 9e: invokevirtual com/google/gson/JsonArray.add (Lcom/google/gson/JsonElement;)V
      // a1: goto 81
      // a4: aload 7
      // a6: goto f6
      // a9: aload 1
      // aa: checkcast net/minecraft/nbt/CompoundTag
      // ad: astore 7
      // af: new com/google/gson/JsonObject
      // b2: dup
      // b3: invokespecial com/google/gson/JsonObject.<init> ()V
      // b6: astore 8
      // b8: aload 7
      // ba: invokevirtual net/minecraft/nbt/CompoundTag.getAllKeys ()Ljava/util/Set;
      // bd: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // c2: astore 9
      // c4: aload 9
      // c6: invokeinterface java/util/Iterator.hasNext ()Z 1
      // cb: ifeq ee
      // ce: aload 9
      // d0: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // d5: checkcast java/lang/String
      // d8: astore 10
      // da: aload 8
      // dc: aload 10
      // de: aload 7
      // e0: aload 10
      // e2: invokevirtual net/minecraft/nbt/CompoundTag.get (Ljava/lang/String;)Lnet/minecraft/nbt/Tag;
      // e5: invokestatic dev/latvian/mods/kubejs/util/NBTUtils.toJson (Lnet/minecraft/nbt/Tag;)Lcom/google/gson/JsonElement;
      // e8: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // eb: goto c4
      // ee: aload 8
      // f0: goto f6
      // f3: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // f6: areturn
   }

   @Nullable
   static OrderedCompoundTag read(FriendlyByteBuf buf) {
      int i = buf.readerIndex();
      byte b = buf.readByte();
      if (b == 0) {
         return null;
      } else {
         buf.readerIndex(i);

         try {
            DataInputStream stream = new DataInputStream(new ByteBufInputStream(buf));
            byte b1 = stream.readByte();
            if (b1 == 0) {
               return null;
            } else {
               stream.readUTF();
               TagType<?> tagType = convertType(TagTypes.getType(b1));
               return tagType != COMPOUND_TYPE ? null : (OrderedCompoundTag)COMPOUND_TYPE.load(stream, NbtAccounter.unlimitedHeap());
            }
         } catch (IOException var6) {
            throw new EncoderException(var6);
         }
      }
   }

   static Map<String, Tag> accessTagMap(CompoundTag tag) {
      return tag.tags;
   }
}
