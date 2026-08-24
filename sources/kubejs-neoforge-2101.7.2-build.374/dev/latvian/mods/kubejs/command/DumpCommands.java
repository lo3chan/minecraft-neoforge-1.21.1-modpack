package dev.latvian.mods.kubejs.command;

import com.google.common.base.Strings;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroups;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.JavaMembers;
import dev.latvian.mods.rhino.JavaMembers.FieldInfo;
import dev.latvian.mods.rhino.JavaMembers.MethodInfo;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FileUtils;

public class DumpCommands {
   private static final char UNICODE_TICK = '✔';
   private static final char UNICODE_CROSS = '✘';

   public static int events(CommandSourceStack source) {
      Map<String, EventGroup> groups = EventGroups.ALL.get().map();
      Path output = KubeJSPaths.LOCAL.resolve("event_groups");

      for (Entry<String, EventGroup> entry : groups.entrySet()) {
         String groupName = entry.getKey();
         EventGroup group = entry.getValue();
         Path groupFolder = output.resolve(groupName);

         try {
            Files.createDirectories(groupFolder);
            FileUtils.cleanDirectory(groupFolder.toFile());
         } catch (IOException var35) {
            ConsoleJS.SERVER.error("Failed to create folder for event group " + groupName, var35);
            source.sendFailure(Component.literal("Failed to create folder for event group " + groupName));
            return 0;
         }

         for (Entry<String, EventHandler> handlerEntry : group.getHandlers().entrySet()) {
            String handlerName = handlerEntry.getKey();
            EventHandler handler = handlerEntry.getValue();
            Path handlerFile = groupFolder.resolve(handlerName + ".md");
            String fullName = "%s.%s".formatted(groupName, handlerName);
            Class<? extends KubeEvent> eventType = handler.eventType.get();
            StringBuilder builder = new StringBuilder();
            builder.append("# ").append(fullName).append("\n\n");
            builder.append("## Basic info\n\n");
            builder.append("- Valid script types: ").append(handler.scriptTypePredicate.getValidTypes()).append("\n\n");
            builder.append("- Has result? ").append((char)(handler.getResult() != null ? '✔' : '✘')).append("\n\n");
            builder.append("- Event class: ");
            if (eventType.getPackageName().startsWith("dev.latvian.mods.kubejs")) {
               builder.append('[')
                  .append(UtilsJS.toMappedTypeString(eventType))
                  .append(']')
                  .append('(')
                  .append("https://github.com/KubeJS-Mods/KubeJS/tree/main/src/main/java/")
                  .append(eventType.getPackageName().replace('.', '/'))
                  .append('/')
                  .append(eventType.getSimpleName())
                  .append(".java")
                  .append(')');
            } else {
               builder.append(UtilsJS.toMappedTypeString(eventType)).append(" (third-party)");
            }

            builder.append("\n\n");
            Info classInfo = eventType.getAnnotation(Info.class);
            if (classInfo != null) {
               builder.append("```\n").append(classInfo.value()).append("```");
               builder.append("\n\n");
            }

            ServerScriptManager scriptManager = source.getServer().getServerResources().managers().kjs$getServerScriptManager();
            KubeJSContext cx = (KubeJSContext)scriptManager.contextFactory.enter();
            JavaMembers members = JavaMembers.lookupClass(cx, cx.topLevelScope, eventType, null, false);
            boolean hasDocumentedMembers = false;
            StringBuilder documentedMembers = new StringBuilder("### Documented members:\n\n");
            builder.append("### Available fields:\n\n");
            builder.append("| Name | Type | Static? |\n");
            builder.append("| ---- | ---- | ------- |\n");

            for (FieldInfo field : members.getAccessibleFields(cx, false)) {
               if (field.field.getDeclaringClass() != Object.class) {
                  String typeName = UtilsJS.toMappedTypeString(field.field.getGenericType());
                  builder.append("| ").append(field.name).append(" | ").append(typeName).append(" | ");
                  builder.append((char)(Modifier.isStatic(field.field.getModifiers()) ? '✔' : '✘')).append(" |\n");
                  Info info = field.field.getAnnotation(Info.class);
                  if (info != null) {
                     hasDocumentedMembers = true;
                     documentedMembers.append("- `").append(typeName).append(' ').append(field.name).append("`\n");
                     documentedMembers.append("```\n");
                     String desc = info.value();
                     documentedMembers.append(desc);
                     if (!desc.endsWith("\n")) {
                        documentedMembers.append("\n");
                     }

                     documentedMembers.append("```\n\n");
                  }
               }
            }

            builder.append("\n").append("Note: Even if no fields are listed above, some methods are still available as fields through *beans*.\n\n");
            builder.append("### Available methods:\n\n");
            builder.append("| Name | Parameters | Return type | Static? |\n");
            builder.append("| ---- | ---------- | ----------- | ------- |\n");

            for (MethodInfo method : members.getAccessibleMethods(cx, false)) {
               if (!method.hidden && method.method.getDeclaringClass() != Object.class) {
                  builder.append("| ").append(method.name).append(" | ");
                  Type[] params = method.method.getGenericParameterTypes();
                  String[] paramTypes = new String[params.length];

                  for (int i = 0; i < params.length; i++) {
                     paramTypes[i] = UtilsJS.toMappedTypeString(params[i]);
                  }

                  builder.append(String.join(", ", paramTypes)).append(" | ");
                  String returnType = UtilsJS.toMappedTypeString(method.method.getGenericReturnType());
                  builder.append(" | ").append(returnType).append(" | ");
                  builder.append((char)(Modifier.isStatic(method.method.getModifiers()) ? '✔' : '✘')).append(" |\n");
                  Info info = method.method.getAnnotation(Info.class);
                  if (info != null) {
                     hasDocumentedMembers = true;
                     documentedMembers.append("- ").append('`');
                     if (Modifier.isStatic(method.method.getModifiers())) {
                        documentedMembers.append("static ");
                     }

                     documentedMembers.append(returnType).append(' ').append(method.name).append('(');
                     Param[] namedParams = info.params();
                     String[] paramNames = new String[params.length];
                     String[] signature = new String[params.length];

                     for (int i = 0; i < params.length; i++) {
                        String name = "var" + i;
                        if (namedParams.length > i) {
                           String name1 = namedParams[i].name();
                           if (!Strings.isNullOrEmpty(name1)) {
                              name = name1;
                           }
                        }

                        paramNames[i] = name;
                        signature[i] = paramTypes[i] + " " + name;
                     }

                     documentedMembers.append(String.join(", ", signature)).append(')').append('`').append("\n");
                     if (params.length > 0) {
                        documentedMembers.append("\n  Parameters:\n");

                        for (int i = 0; i < params.length; i++) {
                           documentedMembers.append("  - ")
                              .append(paramNames[i])
                              .append(": ")
                              .append(paramTypes[i])
                              .append(namedParams.length > i ? "- " + namedParams[i].value() : "")
                              .append("\n");
                        }

                        documentedMembers.append("\n");
                     }

                     documentedMembers.append("```\n");
                     String desc = info.value();
                     documentedMembers.append(desc);
                     if (!desc.endsWith("\n")) {
                        documentedMembers.append("\n");
                     }

                     documentedMembers.append("```\n\n");
                  }
               }
            }

            builder.append("\n\n");
            if (hasDocumentedMembers) {
               builder.append((CharSequence)documentedMembers).append("\n\n");
            }

            builder.append("### Example script:\n\n");
            builder.append("```js\n");
            builder.append(fullName).append('(');
            if (handler.target != null) {
               builder.append(handler.targetRequired ? "extra_id, " : "/* extra_id (optional), */ ");
            }

            builder.append("(event) => {\n");
            builder.append("\t// This space (un)intentionally left blank\n");
            builder.append("});\n");
            builder.append("```\n\n");

            try {
               Files.writeString(handlerFile, builder.toString());
            } catch (IOException var34) {
               ConsoleJS.SERVER.error("Failed to write file for event handler " + fullName, var34);
               source.sendFailure(Component.literal("Failed to write file for event handler " + fullName));
               return 0;
            }
         }
      }

      source.sendSystemMessage(Component.literal("Successfully dumped event groups to " + output));
      return 1;
   }

   public static <T> int registry(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
      Stream<Reference<T>> ids = ((Registry)source.registryAccess()
            .registry(registry)
            .orElseThrow(() -> KubeJSCommands.NO_REGISTRY.create(registry.location())))
         .holders();
      source.sendSystemMessage(Component.empty());
      source.sendSystemMessage(Component.literal("List of all entries for registry " + registry.location() + ":"));
      source.sendSystemMessage(Component.empty());
      long size = ids.map(
            holder -> {
               ResourceLocation id = holder.key().location();
               return Component.literal("- %s".formatted(id))
                  .withStyle(
                     Style.EMPTY
                        .withHoverEvent(
                           new HoverEvent(Action.SHOW_TEXT, Component.literal("%s [%s]".formatted(holder.value(), holder.value().getClass().getName())))
                        )
                  );
            }
         )
         .mapToLong(msg -> {
            source.sendSystemMessage(msg);
            return 1L;
         })
         .sum();
      source.sendSystemMessage(Component.empty());
      source.sendSystemMessage(Component.literal("Total: %d entries".formatted(size)));
      source.sendSystemMessage(Component.empty());
      return 1;
   }
}
