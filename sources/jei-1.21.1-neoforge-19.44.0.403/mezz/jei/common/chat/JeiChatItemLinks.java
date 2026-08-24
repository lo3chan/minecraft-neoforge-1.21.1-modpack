package mezz.jei.common.chat;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public final class JeiChatItemLinks {
   public static final String SHOW_RECIPE_COMMAND = "jei_internal_show";
   public static final String LINK_ARGUMENT = "link";
   private static final String LINK_VERSION = "v1";
   private static final String LINK_VERSION_PREFIX = "v1:";
   private static final String LINK_MARKER_PREFIX = "[JEI:";
   private static final String LINK_MARKER_SUFFIX = "]";
   private static final char LINK_VALUE_SEPARATOR = ';';

   private JeiChatItemLinks() {
   }

   public static String createLinkMarker(ITypedIngredient<?> typedIngredient, IIngredientManager ingredientManager) {
      return createLinkMarkerInternal(typedIngredient, ingredientManager);
   }

   public static Component parse(String rawText) {
      return parse(rawText, JeiChatItemLinks::getIngredientName);
   }

   public static Component parse(String rawText, Function<JeiChatItemLinks.IngredientLink, Optional<String>> ingredientNameLookup) {
      MutableComponent result = Component.empty();
      int searchStart = 0;
      int lastEnd = 0;

      while (true) {
         int markerStart = rawText.indexOf("[JEI:", searchStart);
         if (markerStart < 0) {
            if (lastEnd < rawText.length()) {
               result.append(Component.literal(rawText.substring(lastEnd)));
            }

            return result;
         }

         Optional<JeiChatItemLinks.LinkMarker> optionalMarker = parseLinkMarker(rawText, markerStart);
         if (optionalMarker.isEmpty()) {
            searchStart = markerStart + "[JEI:".length();
         } else {
            JeiChatItemLinks.LinkMarker marker = optionalMarker.get();
            if (marker.start() > lastEnd) {
               result.append(Component.literal(rawText.substring(lastEnd, marker.start())));
            }

            JeiChatItemLinks.IngredientLink link = marker.link();
            MutableComponent linkComponent = createLinkComponent(link, ingredientNameLookup);
            result.append(linkComponent);
            searchStart = marker.end();
            lastEnd = marker.end();
         }
      }
   }

   public static Optional<Component> parseChatMessage(Component message) {
      return parseChatMessage(message, JeiChatItemLinks::getIngredientName);
   }

   public static Optional<Component> parseChatMessage(Component message, Function<JeiChatItemLinks.IngredientLink, Optional<String>> ingredientNameLookup) {
      String rawText = message.getString();
      if (!hasLinkMarkers(rawText)) {
         return Optional.empty();
      } else {
         Component parsedMessage = parse(rawText, ingredientNameLookup);
         return Optional.of(parsedMessage);
      }
   }

   public static boolean hasLinkMarkers(String rawText) {
      int searchStart = 0;

      while (true) {
         int markerStart = rawText.indexOf("[JEI:", searchStart);
         if (markerStart < 0) {
            return false;
         }

         if (parseLinkMarker(rawText, markerStart).isPresent()) {
            return true;
         }

         searchStart = markerStart + "[JEI:".length();
      }
   }

   public static Optional<ITypedIngredient<?>> resolveTypedIngredient(JeiChatItemLinks.IngredientLink link, IIngredientManager ingredientManager) {
      return ingredientManager.getIngredientTypeForUid(link.ingredientTypeUid())
         .flatMap(ingredientType -> resolveTypedIngredient((IIngredientType<?>)ingredientType, link.ingredientUid(), ingredientManager));
   }

   public static String createCommandArgument(JeiChatItemLinks.IngredientLink link) {
      return "v1:" + link.ingredientTypeUid() + ";" + link.ingredientUid();
   }

   public static Optional<JeiChatItemLinks.IngredientLink> parseCommandArgument(String linkText) {
      if (!linkText.startsWith("v1:")) {
         return Optional.empty();
      } else {
         int ingredientTypeUidStart = "v1:".length();
         int separator = linkText.indexOf(59, ingredientTypeUidStart);
         if (separator <= ingredientTypeUidStart) {
            return Optional.empty();
         } else {
            int ingredientUidStart = separator + 1;
            if (ingredientUidStart >= linkText.length()) {
               return Optional.empty();
            } else {
               String ingredientTypeUid = linkText.substring(ingredientTypeUidStart, separator);
               if (!isValidIngredientTypeUid(ingredientTypeUid)) {
                  return Optional.empty();
               } else {
                  String ingredientUid = linkText.substring(ingredientUidStart);
                  JeiChatItemLinks.IngredientLink link = new JeiChatItemLinks.IngredientLink(ingredientTypeUid, ingredientUid);
                  return Optional.of(link);
               }
            }
         }
      }
   }

   public static String createShowRecipeCommand(JeiChatItemLinks.IngredientLink link) {
      return "jei_internal_show " + createCommandArgument(link);
   }

   public static Optional<JeiChatItemLinks.IngredientLink> parseShowRecipeCommand(String command) {
      String prefix = "jei_internal_show ";
      if (!command.startsWith(prefix)) {
         return Optional.empty();
      } else {
         String linkText = command.substring(prefix.length());
         return parseCommandArgument(linkText);
      }
   }

   private static String createLinkMarker(JeiChatItemLinks.IngredientLink link) {
      String linkText = createCommandArgument(link);
      return "[JEI:" + linkText + "] ";
   }

   private static <T> String createLinkMarkerInternal(ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
      IIngredientType<T> ingredientType = typedIngredient.getType();
      IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
      Object uid = ingredientHelper.getUid(typedIngredient, UidContext.Ingredient);
      String ingredientUid = getIngredientUidString(uid);
      JeiChatItemLinks.IngredientLink link = new JeiChatItemLinks.IngredientLink(ingredientType.getUid(), ingredientUid);
      return createLinkMarker(link);
   }

   private static <T> Optional<ITypedIngredient<T>> resolveTypedIngredient(
      IIngredientType<T> ingredientType, String ingredientUid, IIngredientManager ingredientManager
   ) {
      IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
      return ingredientManager.getAllTypedIngredients(ingredientType).stream().filter(typedIngredient -> {
         Object uid = ingredientHelper.getUid((ITypedIngredient<T>)typedIngredient, UidContext.Ingredient);
         String candidateUid = getIngredientUidString(uid);
         return ingredientUid.equals(candidateUid);
      }).findFirst();
   }

   private static Optional<JeiChatItemLinks.LinkMarker> parseLinkMarker(String rawText, int start) {
      int argumentStart = start + "[JEI:".length();
      int markerEnd = rawText.indexOf("]", argumentStart);
      if (markerEnd < 0) {
         return Optional.empty();
      } else {
         String linkText = rawText.substring(argumentStart, markerEnd);
         Optional<JeiChatItemLinks.IngredientLink> optionalLink = parseCommandArgument(linkText);
         if (optionalLink.isEmpty()) {
            return Optional.empty();
         } else {
            JeiChatItemLinks.IngredientLink link = optionalLink.get();
            JeiChatItemLinks.LinkMarker marker = new JeiChatItemLinks.LinkMarker(start, markerEnd + "]".length(), link);
            return Optional.of(marker);
         }
      }
   }

   private static boolean isValidIngredientTypeUid(String ingredientTypeUid) {
      for (int i = 0; i < ingredientTypeUid.length(); i++) {
         char c = ingredientTypeUid.charAt(i);
         if (Character.isWhitespace(c) || c == '(' || c == ')' || c == '=' || c == '[' || c == ']') {
            return false;
         }
      }

      return true;
   }

   private static Optional<String> getIngredientName(JeiChatItemLinks.IngredientLink link) {
      Optional<IJeiRuntime> optionalRuntime = Internal.getOptionalJeiRuntime();
      if (optionalRuntime.isEmpty()) {
         return Optional.empty();
      } else {
         IJeiRuntime runtime = optionalRuntime.get();
         IIngredientManager ingredientManager = runtime.getIngredientManager();
         return resolveTypedIngredient(link, ingredientManager)
            .map(typedIngredient -> getIngredientName((ITypedIngredient<?>)typedIngredient, ingredientManager));
      }
   }

   private static <T> String getIngredientName(ITypedIngredient<T> typedIngredient, IIngredientManager ingredientManager) {
      IIngredientType<T> ingredientType = typedIngredient.getType();
      IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
      return ingredientHelper.getDisplayName(typedIngredient.getIngredient());
   }

   private static String getIngredientUidString(Object uid) {
      if (uid instanceof ResourceLocation resourceLocation) {
         return resourceLocation.toString();
      } else if (uid instanceof Item item) {
         return BuiltInRegistries.ITEM.wrapAsHolder(item).getRegisteredName();
      } else if (uid instanceof Fluid fluid) {
         return BuiltInRegistries.FLUID.wrapAsHolder(fluid).getRegisteredName();
      } else if (uid instanceof Iterable<?> iterable) {
         return getIterableUidString(iterable);
      } else {
         return uid instanceof Object[] array ? getIterableUidString(Arrays.asList(array)) : String.valueOf(uid);
      }
   }

   private static String getIterableUidString(Iterable<?> iterable) {
      StringJoiner joiner = new StringJoiner(",", "(", ")");

      for (Object value : iterable) {
         String valueString = getIngredientUidString(value);
         joiner.add(valueString);
      }

      return joiner.toString();
   }

   private static MutableComponent createLinkComponent(
      JeiChatItemLinks.IngredientLink link, Function<JeiChatItemLinks.IngredientLink, Optional<String>> ingredientNameLookup
   ) {
      Optional<String> optionalIngredientName = ingredientNameLookup.apply(link);
      if (optionalIngredientName.isEmpty()) {
         String ingredientUid = link.ingredientUid();
         return Component.literal("[" + ingredientUid + "]");
      } else {
         String ingredientName = optionalIngredientName.get();
         MutableComponent component = Component.literal("[" + ingredientName + "]");
         HoverEvent hoverEvent = createHoverEvent(link, ingredientName);
         return component.withStyle(
            style -> style.withColor(ChatFormatting.AQUA)
               .withClickEvent(new ClickEvent(Action.RUN_COMMAND, createShowRecipeCommand(link)))
               .withHoverEvent(hoverEvent)
         );
      }
   }

   private static HoverEvent createHoverEvent(JeiChatItemLinks.IngredientLink link, String ingredientName) {
      return createItemHoverEvent(link).orElseGet(() -> createTextHoverEvent(ingredientName));
   }

   private static HoverEvent createTextHoverEvent(String ingredientName) {
      MutableComponent hoverText = Component.literal(ingredientName);
      return new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, hoverText);
   }

   private static Optional<HoverEvent> createItemHoverEvent(JeiChatItemLinks.IngredientLink link) {
      if (!link.ingredientTypeUid().equals(VanillaTypes.ITEM_STACK.getUid())) {
         return Optional.empty();
      } else {
         Optional<IJeiRuntime> optionalRuntime = Internal.getOptionalJeiRuntime();
         if (optionalRuntime.isEmpty()) {
            return Optional.empty();
         } else {
            IJeiRuntime runtime = optionalRuntime.get();
            IIngredientManager ingredientManager = runtime.getIngredientManager();
            return resolveTypedIngredient(link, ingredientManager)
               .flatMap(ITypedIngredient::getItemStack)
               .filter(stack -> !stack.isEmpty())
               .map(JeiChatItemLinks::createItemStackHoverEvent);
         }
      }
   }

   private static HoverEvent createItemStackHoverEvent(ItemStack stack) {
      ItemStack displayStack = stack.copy();
      return new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_ITEM, new ItemStackInfo(displayStack));
   }

   public record IngredientLink(String ingredientTypeUid, String ingredientUid) {
   }

   private record LinkMarker(int start, int end, JeiChatItemLinks.IngredientLink link) {
   }
}
