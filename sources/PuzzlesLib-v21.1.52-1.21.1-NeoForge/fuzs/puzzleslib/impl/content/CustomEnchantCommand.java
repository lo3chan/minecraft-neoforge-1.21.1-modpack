package fuzs.puzzleslib.impl.content;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

public class CustomEnchantCommand {
   public static final String KEY_REMOVE_SUCCESS_SINGLE = "commands.enchant.remove.success.single";
   public static final String KEY_REMOVE_SUCCESS_MULTIPLE = "commands.enchant.remove.success.multiple";
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(
      object -> Component.translatable("commands.enchant.failed.entity", new Object[]{object})
   );
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(
      object -> Component.translatable("commands.enchant.failed.itemless", new Object[]{object})
   );
   private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType(
      object -> Component.translatable("commands.enchant.failed.incompatible", new Object[]{object})
   );
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(Component.translatable("commands.enchant.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("enchant").requires(source -> source.hasPermission(2)))
            .then(
               Commands.argument("targets", EntityArgument.entities())
                  .then(
                     ((RequiredArgumentBuilder)Commands.argument("enchantment", ResourceArgument.resource(context, Registries.ENCHANTMENT))
                           .executes(
                              commandContext -> enchant(
                                 (CommandSourceStack)commandContext.getSource(),
                                 EntityArgument.getEntities(commandContext, "targets"),
                                 ResourceArgument.getEnchantment(commandContext, "enchantment")
                              )
                           ))
                        .then(
                           Commands.argument("level", IntegerArgumentType.integer(0, 255))
                              .executes(
                                 commandContext -> enchant(
                                    (CommandSourceStack)commandContext.getSource(),
                                    EntityArgument.getEntities(commandContext, "targets"),
                                    ResourceArgument.getEnchantment(commandContext, "enchantment"),
                                    IntegerArgumentType.getInteger(commandContext, "level")
                                 )
                              )
                        )
                  )
            )
      );
   }

   private static int enchant(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, Holder<Enchantment> holder) throws CommandSyntaxException {
      return enchant(commandSourceStack, collection, holder, ((Enchantment)holder.value()).getMaxLevel());
   }

   private static int enchant(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, Holder<Enchantment> enchantment, int level) throws CommandSyntaxException {
      if (level > 255) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().create(level, 255);
      } else {
         int successCount = 0;

         for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
               ItemStack itemStack = livingEntity.getMainHandItem();
               if (!itemStack.isEmpty()) {
                  ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
                  if (level == 0
                     || (isBook(itemStack) || ((Enchantment)enchantment.value()).canEnchant(itemStack))
                        && isEnchantmentCompatible(itemEnchantments, enchantment)) {
                     Mutable mutable = new Mutable(itemEnchantments);
                     if (mutable.getLevel(enchantment) != level) {
                        mutable.set(enchantment, level);
                     } else if (entities.size() == 1) {
                        throw ERROR_NOTHING_HAPPENED.create();
                     }

                     if (itemStack.is(Items.BOOK) && !mutable.keySet().isEmpty()) {
                        itemStack = itemStack.transmuteCopy(Items.ENCHANTED_BOOK, 1);
                     }

                     EnchantmentHelper.setEnchantments(itemStack, mutable.toImmutable());
                     if (itemStack.is(Items.ENCHANTED_BOOK) && mutable.keySet().isEmpty()) {
                        itemStack = itemStack.transmuteCopy(Items.BOOK, 1);
                     }

                     livingEntity.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
                     successCount++;
                  } else if (entities.size() == 1) {
                     throw ERROR_INCOMPATIBLE.create(itemStack.getItem().getName(itemStack).getString());
                  }
               } else if (entities.size() == 1) {
                  throw ERROR_NO_ITEM.create(livingEntity.getName().getString());
               }
            } else if (entities.size() == 1) {
               throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
            }
         }

         if (successCount == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
         } else {
            if (entities.size() == 1) {
               commandSourceStack.sendSuccess(
                  () -> level > 0
                     ? Component.translatable(
                        "commands.enchant.success.single",
                        new Object[]{Enchantment.getFullname(enchantment, level), entities.iterator().next().getDisplayName()}
                     )
                     : Component.translatableWithFallback(
                        "commands.enchant.remove.success.single",
                        "Removed enchantment %s from %s's item",
                        new Object[]{getFullname(enchantment), entities.iterator().next().getDisplayName()}
                     ),
                  true
               );
            } else {
               commandSourceStack.sendSuccess(
                  () -> level > 0
                     ? Component.translatable("commands.enchant.success.multiple", new Object[]{Enchantment.getFullname(enchantment, level), entities.size()})
                     : Component.translatableWithFallback(
                        "commands.enchant.remove.success.multiple",
                        "Removed enchantment %s from %s entities",
                        new Object[]{getFullname(enchantment), entities.size()}
                     ),
                  true
               );
            }

            return successCount;
         }
      }
   }

   private static Component getFullname(Holder<Enchantment> enchantment) {
      MutableComponent mutableComponent = ((Enchantment)enchantment.value()).description().copy();
      return enchantment.is(EnchantmentTags.CURSE) ? mutableComponent.withStyle(ChatFormatting.RED) : mutableComponent.withStyle(ChatFormatting.GRAY);
   }

   private static boolean isBook(ItemStack itemStack) {
      return itemStack.is(Items.BOOK) || itemStack.is(Items.ENCHANTED_BOOK);
   }

   private static boolean isEnchantmentCompatible(ItemEnchantments itemEnchantments, Holder<Enchantment> enchantment) {
      Mutable mutable = new Mutable(itemEnchantments);
      mutable.set(enchantment, 0);
      return EnchantmentHelper.isEnchantmentCompatible(mutable.keySet(), enchantment);
   }
}
