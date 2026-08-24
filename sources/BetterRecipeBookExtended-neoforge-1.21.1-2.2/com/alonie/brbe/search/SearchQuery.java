package com.alonie.brbe.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.world.item.ItemStack;

public class SearchQuery {
   private static final Pattern OR_SPLIT = Pattern.compile("\\|");
   private static final Pattern TOKEN_SPLIT = Pattern.compile("\"([^\"]*)\"|([^\\s]+)");
   private final AlternativeArgument root;
   private final boolean advanced;
   public static final SearchQuery EMPTY = new SearchQuery(new AlternativeArgument(List.of()), false);

   public static SearchQuery parse(String input) {
      if (input != null && !input.isBlank()) {
         String trimmed = input.trim();
         String[] orParts = OR_SPLIT.split(trimmed, -1);
         List<SearchArgument> compoundArgs = new ArrayList<>();

         for (String part : orParts) {
            part = part.trim();
            if (!part.isEmpty()) {
               List<SearchArgument> tokens = new ArrayList<>();
               Matcher matcher = TOKEN_SPLIT.matcher(part);

               while (matcher.find()) {
                  String token;
                  if (matcher.group(1) != null) {
                     token = matcher.group(1);
                  } else {
                     token = matcher.group(2);
                  }

                  if (token != null && !token.isEmpty()) {
                     SearchArgument arg = parseToken(token);
                     tokens.add(arg);
                  }
               }

               if (!tokens.isEmpty()) {
                  if (tokens.size() == 1) {
                     compoundArgs.add(tokens.get(0));
                  } else {
                     compoundArgs.add(new CompoundArgument(tokens));
                  }
               }
            }
         }

         if (compoundArgs.isEmpty()) {
            return new SearchQuery(new AlternativeArgument(List.of()), false);
         } else {
            boolean advanced = compoundArgs.stream().anyMatch(SearchArgument::isAdvanced);
            AlternativeArgument root;
            if (compoundArgs.size() == 1) {
               root = new AlternativeArgument(compoundArgs);
            } else {
               root = new AlternativeArgument(compoundArgs);
            }

            return new SearchQuery(root, advanced);
         }
      } else {
         return new SearchQuery(new AlternativeArgument(List.of()), false);
      }
   }

   private static SearchArgument parseToken(String token) {
      boolean negated = false;
      String text = token;
      if (token.startsWith("-") && token.length() > 1 && !token.startsWith("--")) {
         negated = true;
         text = token.substring(1);
      }

      SearchArgument arg;
      if (text.startsWith("@") && text.length() > 1) {
         arg = new ModArgument(text.substring(1));
      } else if (text.startsWith("$") && text.length() > 1) {
         arg = new TagArgument(text.substring(1));
      } else if (text.startsWith("#") && text.length() > 1) {
         arg = new TooltipArgument(text.substring(1));
      } else if (text.startsWith("r/") && text.length() > 3) {
         int closingSlash = text.lastIndexOf(47);
         if (closingSlash > 2) {
            String regex = text.substring(2, closingSlash);

            try {
               arg = new RegexArgument(regex);
            } catch (Exception var7) {
               arg = new TextArgument(token);
            }
         } else {
            arg = new TextArgument(token);
         }
      } else {
         arg = new TextArgument(text);
      }

      return (SearchArgument)(negated ? new NegatedArgument(arg) : arg);
   }

   private SearchQuery(AlternativeArgument root, boolean advanced) {
      this.root = root;
      this.advanced = advanced;
   }

   public boolean matches(ItemStack stack, SearchCache cache) {
      return this.root.matches(stack, cache);
   }

   public boolean isAdvanced() {
      return this.advanced;
   }
}
