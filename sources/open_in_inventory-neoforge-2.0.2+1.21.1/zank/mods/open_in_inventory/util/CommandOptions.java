package zank.mods.open_in_inventory.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import org.jetbrains.annotations.NotNull;

public class CommandOptions {
   public final Map<String, CommandOptions.CommandOption> byName = new TreeMap<>();
   public final Map<String, CommandOptions.CommandOption> byShorthand = new TreeMap<>();

   public CommandOptions(CommandOptions.CommandOption... options) {
      for (CommandOptions.CommandOption option : options) {
         this.add(option);
      }
   }

   public CommandOptions.CommandOption add(CommandOptions.CommandOption option) {
      this.byName.put(option.name, option);
      if (option.hasShorthand()) {
         this.byShorthand.put(option.shorthand, option);
      }

      return option;
   }

   public CommandOptions.CommandOption add(String name) {
      return this.add(new CommandOptions.CommandOption(name));
   }

   public CommandOptions.CommandOption add(String name, String shorthand) {
      return this.add(new CommandOptions.CommandOption(name, shorthand));
   }

   public Set<CommandOptions.CommandOption> parse(String args) {
      HashSet<CommandOptions.CommandOption> result = new HashSet<>();

      for (String arg : args.split(" ")) {
         CommandOptions.CommandOption option;
         if (arg.startsWith("--")) {
            option = this.byName.get(arg.substring("--".length()));
         } else if (arg.startsWith("-")) {
            option = this.byShorthand.get(arg.substring("-".length()));
         } else {
            option = null;
         }

         if (option != null) {
            result.add(option);
         }
      }

      return result;
   }

   public Collection<CommandOptions.CommandOption> suggestNext(String args) {
      Set<CommandOptions.CommandOption> parsed = this.parse(args);
      ArrayList<CommandOptions.CommandOption> result = new ArrayList<>();

      for (CommandOptions.CommandOption option : this.byName.values()) {
         if (!parsed.contains(option)) {
            result.add(option);
         }
      }

      return result;
   }

   public record CommandOption(String name, String shorthand) implements Comparable<CommandOptions.CommandOption> {
      public CommandOption(String name) {
         this(name, null);
      }

      public CommandOption(String name, String shorthand) {
         Objects.requireNonNull(name, "String name == null");
         this.name = name;
         this.shorthand = shorthand;
      }

      public boolean hasShorthand() {
         return this.shorthand != null;
      }

      @Override
      public int hashCode() {
         return this.name.hashCode();
      }

      public int compareTo(@NotNull CommandOptions.CommandOption o) {
         return this.name.compareTo(o.name);
      }
   }
}
