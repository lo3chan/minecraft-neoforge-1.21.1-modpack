/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.inventory.AnvilMenu
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.ArmorMaterials
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.Tiers
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 *  net.minecraft.world.item.enchantment.ItemEnchantments$Mutable
 *  net.minecraft.world.level.ItemLike
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.plugins.vanilla.anvil;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformItemStackHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.library.plugins.vanilla.anvil.AnvilHelper;
import mezz.jei.library.plugins.vanilla.anvil.AnvilRecipe;
import mezz.jei.library.util.ResourceLocationUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AnvilRecipeMaker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ItemStack ENCHANTED_BOOK = new ItemStack((ItemLike)Items.ENCHANTED_BOOK);
    private final IVanillaRecipeFactory vanillaRecipeFactory;
    private final IIngredientManager ingredientManager;
    private final IIngredientHelper<ItemStack> ingredientHelper;
    private final IPlatformItemStackHelper itemStackHelper;
    private final AnvilMenu anvilMenu;

    public static List<IJeiAnvilRecipe> getAnvilRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager) {
        AnvilMenu fakeAnvilMenu = AnvilHelper.getFakeAnvilMenu();
        return AnvilRecipeMaker.getAnvilRecipes(vanillaRecipeFactory, ingredientManager, fakeAnvilMenu);
    }

    public static List<IJeiAnvilRecipe> getAnvilRecipes(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager, AnvilMenu anvilMenu) {
        AnvilRecipeMaker anvilRecipeMaker = new AnvilRecipeMaker(vanillaRecipeFactory, ingredientManager, anvilMenu);
        return anvilRecipeMaker.getAnvilRecipes();
    }

    private AnvilRecipeMaker(IVanillaRecipeFactory vanillaRecipeFactory, IIngredientManager ingredientManager, AnvilMenu anvilMenu) {
        this.vanillaRecipeFactory = vanillaRecipeFactory;
        this.ingredientManager = ingredientManager;
        this.ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        this.itemStackHelper = Services.PLATFORM.getItemStackHelper();
        this.anvilMenu = anvilMenu;
    }

    public List<IJeiAnvilRecipe> getAnvilRecipes() {
        return Stream.concat(this.getRepairRecipes(), this.getBookEnchantmentRecipes()).toList();
    }

    private Stream<IJeiAnvilRecipe> getBookEnchantmentRecipes() {
        Registry registry = RegistryUtil.getRegistry(Registries.ENCHANTMENT);
        List<EnchantmentData> enchantmentDatas = registry.holders().map(EnchantmentData::new).toList();
        return this.ingredientManager.getAllItemStacks().stream().filter(ItemStack::isEnchantable).flatMap(ingredient -> this.getBookEnchantmentRecipes(enchantmentDatas, (ItemStack)ingredient));
    }

    private Stream<IJeiAnvilRecipe> getBookEnchantmentRecipes(List<EnchantmentData> enchantmentDatas, ItemStack ingredient) {
        List<ItemStack> ingredientSingletonList = List.of(ingredient);
        return enchantmentDatas.stream().filter(data -> data.canEnchant(this.itemStackHelper, ingredient)).map(data -> data.getEnchantedBooks(ingredient)).filter(enchantedBooks -> !enchantedBooks.isEmpty()).map(enchantedBooks -> {
            List<ItemStack> outputs = this.getEnchantedIngredients(ingredient, (List<ItemStack>)enchantedBooks);
            return new AnvilRecipe(ingredientSingletonList, (List<ItemStack>)enchantedBooks, outputs, null);
        });
    }

    private List<ItemStack> getEnchantedIngredients(ItemStack ingredient, List<ItemStack> enchantedBooks) {
        return enchantedBooks.stream().map(enchantedBook -> this.getAnvilOutput(ingredient, (ItemStack)enchantedBook)).filter(i -> !i.isEmpty()).toList();
    }

    private static Stream<RepairData> getRepairData() {
        return Stream.of(new RepairData(Tiers.WOOD.getRepairIngredient(), new ItemStack((ItemLike)Items.WOODEN_SWORD), new ItemStack((ItemLike)Items.WOODEN_PICKAXE), new ItemStack((ItemLike)Items.WOODEN_AXE), new ItemStack((ItemLike)Items.WOODEN_SHOVEL), new ItemStack((ItemLike)Items.WOODEN_HOE)), new RepairData(Ingredient.of((TagKey)ItemTags.PLANKS), new ItemStack((ItemLike)Items.SHIELD)), new RepairData(Tiers.STONE.getRepairIngredient(), new ItemStack((ItemLike)Items.STONE_SWORD), new ItemStack((ItemLike)Items.STONE_PICKAXE), new ItemStack((ItemLike)Items.STONE_AXE), new ItemStack((ItemLike)Items.STONE_SHOVEL), new ItemStack((ItemLike)Items.STONE_HOE)), new RepairData((Ingredient)((ArmorMaterial)ArmorMaterials.LEATHER.value()).repairIngredient().get(), new ItemStack((ItemLike)Items.LEATHER_HELMET), new ItemStack((ItemLike)Items.LEATHER_CHESTPLATE), new ItemStack((ItemLike)Items.LEATHER_LEGGINGS), new ItemStack((ItemLike)Items.LEATHER_BOOTS)), new RepairData(Tiers.IRON.getRepairIngredient(), new ItemStack((ItemLike)Items.IRON_SWORD), new ItemStack((ItemLike)Items.IRON_PICKAXE), new ItemStack((ItemLike)Items.IRON_AXE), new ItemStack((ItemLike)Items.IRON_SHOVEL), new ItemStack((ItemLike)Items.IRON_HOE)), new RepairData((Ingredient)((ArmorMaterial)ArmorMaterials.IRON.value()).repairIngredient().get(), new ItemStack((ItemLike)Items.IRON_HELMET), new ItemStack((ItemLike)Items.IRON_CHESTPLATE), new ItemStack((ItemLike)Items.IRON_LEGGINGS), new ItemStack((ItemLike)Items.IRON_BOOTS)), new RepairData((Ingredient)((ArmorMaterial)ArmorMaterials.CHAIN.value()).repairIngredient().get(), new ItemStack((ItemLike)Items.CHAINMAIL_HELMET), new ItemStack((ItemLike)Items.CHAINMAIL_CHESTPLATE), new ItemStack((ItemLike)Items.CHAINMAIL_LEGGINGS), new ItemStack((ItemLike)Items.CHAINMAIL_BOOTS)), new RepairData(Tiers.GOLD.getRepairIngredient(), new ItemStack((ItemLike)Items.GOLDEN_SWORD), new ItemStack((ItemLike)Items.GOLDEN_PICKAXE), new ItemStack((ItemLike)Items.GOLDEN_AXE), new ItemStack((ItemLike)Items.GOLDEN_SHOVEL), new ItemStack((ItemLike)Items.GOLDEN_HOE)), new RepairData((Ingredient)((ArmorMaterial)ArmorMaterials.GOLD.value()).repairIngredient().get(), new ItemStack((ItemLike)Items.GOLDEN_HELMET), new ItemStack((ItemLike)Items.GOLDEN_CHESTPLATE), new ItemStack((ItemLike)Items.GOLDEN_LEGGINGS), new ItemStack((ItemLike)Items.GOLDEN_BOOTS)), new RepairData(Tiers.DIAMOND.getRepairIngredient(), new ItemStack((ItemLike)Items.DIAMOND_SWORD), new ItemStack((ItemLike)Items.DIAMOND_PICKAXE), new ItemStack((ItemLike)Items.DIAMOND_AXE), new ItemStack((ItemLike)Items.DIAMOND_SHOVEL), new ItemStack((ItemLike)Items.DIAMOND_HOE)), new RepairData((Ingredient)((ArmorMaterial)ArmorMaterials.DIAMOND.value()).repairIngredient().get(), new ItemStack((ItemLike)Items.DIAMOND_HELMET), new ItemStack((ItemLike)Items.DIAMOND_CHESTPLATE), new ItemStack((ItemLike)Items.DIAMOND_LEGGINGS), new ItemStack((ItemLike)Items.DIAMOND_BOOTS)), new RepairData(Tiers.NETHERITE.getRepairIngredient(), new ItemStack((ItemLike)Items.NETHERITE_SWORD), new ItemStack((ItemLike)Items.NETHERITE_AXE), new ItemStack((ItemLike)Items.NETHERITE_HOE), new ItemStack((ItemLike)Items.NETHERITE_SHOVEL), new ItemStack((ItemLike)Items.NETHERITE_PICKAXE)), new RepairData((Ingredient)((ArmorMaterial)ArmorMaterials.NETHERITE.value()).repairIngredient().get(), new ItemStack((ItemLike)Items.NETHERITE_BOOTS), new ItemStack((ItemLike)Items.NETHERITE_HELMET), new ItemStack((ItemLike)Items.NETHERITE_LEGGINGS), new ItemStack((ItemLike)Items.NETHERITE_CHESTPLATE)), new RepairData(Ingredient.of((ItemLike[])new ItemLike[]{Items.PHANTOM_MEMBRANE}), new ItemStack((ItemLike)Items.ELYTRA)), new RepairData((Ingredient)((ArmorMaterial)ArmorMaterials.TURTLE.value()).repairIngredient().get(), new ItemStack((ItemLike)Items.TURTLE_HELMET)));
    }

    private Stream<IJeiAnvilRecipe> getRepairRecipes() {
        return AnvilRecipeMaker.getRepairData().flatMap(this::getRepairRecipes);
    }

    private Stream<IJeiAnvilRecipe> getRepairRecipes(RepairData repairData) {
        Ingredient repairIngredient = repairData.getRepairIngredient();
        List<ItemStack> repairables = repairData.getRepairables();
        List<ItemStack> repairMaterials = List.of(repairIngredient.getItems());
        return repairables.stream().mapMulti((itemStack, consumer) -> {
            String uid = this.ingredientHelper.getResourceLocation((ItemStack)itemStack).toString();
            String ingredientIdPath = ResourceLocationUtil.sanitizePath(uid);
            String itemModId = this.ingredientHelper.getResourceLocation((ItemStack)itemStack).getNamespace();
            ItemStack damagedThreeQuarters = itemStack.copy();
            damagedThreeQuarters.setDamageValue(damagedThreeQuarters.getMaxDamage() * 3 / 4);
            ItemStack sameItemOutput = this.getAnvilOutput(damagedThreeQuarters, damagedThreeQuarters);
            List<ItemStack> damagedThreeQuartersSingletonList = List.of(damagedThreeQuarters);
            if (!sameItemOutput.isEmpty()) {
                IJeiAnvilRecipe repairWithSame = this.vanillaRecipeFactory.createAnvilRecipe(damagedThreeQuartersSingletonList, damagedThreeQuartersSingletonList, List.of(sameItemOutput), ResourceLocation.fromNamespaceAndPath((String)itemModId, (String)("anvil.self_repair." + ingredientIdPath)));
                consumer.accept(repairWithSame);
            }
            if (!repairMaterials.isEmpty()) {
                ItemStack damagedFully = itemStack.copy();
                damagedFully.setDamageValue(damagedFully.getMaxDamage());
                ItemStack materialOutput = this.getAnvilOutput(damagedFully, (ItemStack)repairMaterials.getFirst());
                if (!materialOutput.isEmpty()) {
                    IJeiAnvilRecipe repairWithMaterial = this.vanillaRecipeFactory.createAnvilRecipe(List.of(damagedFully), repairMaterials, List.of(materialOutput), ResourceLocation.fromNamespaceAndPath((String)itemModId, (String)("anvil.materials_repair." + ingredientIdPath)));
                    consumer.accept(repairWithMaterial);
                }
            }
        });
    }

    public static int findLevelsCost(ItemStack leftStack, ItemStack rightStack) {
        return AnvilHelper.findLevelsCost(leftStack, rightStack);
    }

    private ItemStack getAnvilOutput(ItemStack leftStack, ItemStack rightStack) {
        AnvilMenu result = AnvilHelper.setAnvilMenu(this.anvilMenu, leftStack, rightStack);
        if (result == null) {
            return ItemStack.EMPTY;
        }
        Slot resultSlot = result.getSlot(2);
        return resultSlot.getItem().copy();
    }

    private static class RepairData {
        private final Ingredient repairIngredient;
        private final List<ItemStack> repairables;

        public RepairData(Ingredient repairIngredient, ItemStack ... repairables) {
            this.repairIngredient = repairIngredient;
            this.repairables = List.of(repairables);
        }

        public Ingredient getRepairIngredient() {
            return this.repairIngredient;
        }

        public List<ItemStack> getRepairables() {
            return this.repairables;
        }
    }

    private static final class EnchantmentData {
        private final Holder<Enchantment> enchantment;
        private final List<ItemStack> enchantedBooks;

        private EnchantmentData(Holder<Enchantment> enchantment) {
            this.enchantment = enchantment;
            this.enchantedBooks = EnchantmentData.getEnchantedBooks(enchantment);
        }

        public List<ItemStack> getEnchantedBooks(ItemStack ingredient) {
            IPlatformItemStackHelper itemStackHelper = Services.PLATFORM.getItemStackHelper();
            List<ItemStack> list = this.enchantedBooks.stream().filter(enchantedBook -> itemStackHelper.isBookEnchantable(ingredient, (ItemStack)enchantedBook)).toList();
            return list.size() == this.enchantedBooks.size() ? this.enchantedBooks : list;
        }

        private boolean canEnchant(IPlatformItemStackHelper itemStackHelper, ItemStack ingredient) {
            try {
                return itemStackHelper.canEnchant(this.enchantment, ingredient);
            }
            catch (RuntimeException e) {
                String stackInfo = ErrorUtil.getItemStackInfo(ingredient);
                LOGGER.error("Failed to check if ingredient can be enchanted: {}", (Object)stackInfo, (Object)e);
                return false;
            }
        }

        private static List<ItemStack> getEnchantedBooks(Holder<Enchantment> enchantment) {
            return IntStream.rangeClosed(1, ((Enchantment)enchantment.value()).getMaxLevel()).mapToObj(level -> {
                ItemStack bookEnchant = ENCHANTED_BOOK.copy();
                ItemEnchantments.Mutable itemEnchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting((ItemStack)bookEnchant));
                itemEnchantments.set(enchantment, level);
                EnchantmentHelper.setEnchantments((ItemStack)bookEnchant, (ItemEnchantments)itemEnchantments.toImmutable());
                return bookEnchant;
            }).toList();
        }
    }
}

