package net.cibernet.alchemancy.datagen;

import java.util.ArrayList;
import java.util.List;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.PropertyFunction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class AlchemancyLangProvider extends LanguageProvider {
   public AlchemancyLangProvider(PackOutput output) {
      super(output, "alchemancy", "en_us");
   }

   protected void addTranslations() {
      this.addCodexFlavor(AlchemancyProperties.BURNING, "Turning up the heat");
      this.addCodexFunction(
         AlchemancyProperties.BURNING,
         PropertyFunction.ON_ATTACK,
         "Sets the target on fire for 5 seconds. Duration is multiplied by the item's level of {enchantment minecraft:fire_aspect}."
      );
      this.addCodexFunction(AlchemancyProperties.BURNING, PropertyFunction.WHILE_WORN, "Constantly sets the user on fire every half a second.");
      this.addCodexFunction(
         AlchemancyProperties.BURNING,
         PropertyFunction.WHILE_ROOTED,
         "Sets entities standing inside of the item on fire for 4 seconds. Duration is multiplied by the item's level of {enchantment minecraft:fire_aspect}."
      );
      this.addCodexFunction(AlchemancyProperties.BURNING, PropertyFunction.WHEN_SHOT, "Sets the shot projectile on fire for 4 seconds.");
      this.addCodexFlavor(AlchemancyProperties.WET, "Soak it in");
      this.addCodexFunction(AlchemancyProperties.WET, PropertyFunction.ON_ATTACK, "Reduces the target's time on fire by half a second.");
      this.addCodexFunction(AlchemancyProperties.WET, PropertyFunction.WHILE_WORN, "Reduces the user's amount of time set on fire by half.");
      this.addCodexFunction(
         AlchemancyProperties.WET,
         PropertyFunction.WHILE_ROOTED,
         "Waters {item Farmland} in a 4-block radius. Extinguishes all entities standing inside of the item."
      );
      this.addCodexFunction(AlchemancyProperties.WET, PropertyFunction.RECEIVE_DAMAGE_WORN, "Increases the amount of {shock Electric} damage received by 10%.");
      this.addCodexFlavor(AlchemancyProperties.FROSTED, "Cooling it down");
      this.addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.ON_ATTACK, "Freezes the target for 12 seconds.");
      this.addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.WHILE_WORN, "Constantly freezes the user.");
      this.addCodexFunction(AlchemancyProperties.FROSTED, PropertyFunction.WHILE_ROOTED, "Freezes all entities standing inside of the item.");
      this.addCodexFlavor(AlchemancyProperties.SHOCKING, "A shock to the system");
      this.addCodexFunction(
         AlchemancyProperties.SHOCKING,
         PropertyFunction.ON_ATTACK,
         "Deal 4 points of chaining {shock Electric} damage to the target and nearby entities. The amount of damage dealt decays over distance and for each consecutive hit."
      );
      this.addCodexFunction(
         AlchemancyProperties.SHOCKING,
         PropertyFunction.WHILE_ROOTED,
         "Constantly emits an electrical field that deals 3 points of chaining {shock Electric} damage to nearby entities. The amount of damage dealt decays over distance and for each consecutive hit."
      );
      this.addCodexFlavor(AlchemancyProperties.PHOTOSYNTHETIC, "PHOTOSINTHESIZE!");
      this.addCodexFunction(
         AlchemancyProperties.PHOTOSYNTHETIC,
         PropertyFunction.WHILE_IN_INVENTORY,
         "Repairs the item for 1 durability point every 30 seconds while the user is under direct sunlight."
      );
      this.addCodexFunction(
         AlchemancyProperties.PHOTOSYNTHETIC,
         PropertyFunction.WHILE_ROOTED,
         "Repairs the item for 1 durability point every 15 seconds while under direct sunlight."
      );
      this.addCodexFlavor(AlchemancyProperties.FLAMMABLE, "All we need now is a spark");
      this.addCodexFunction(
         AlchemancyProperties.FLAMMABLE,
         PropertyFunction.ON_ATTACK,
         "While the user is on fire, sets the target on fire for the same amount of time as the user."
      );
      this.addCodexFunction(
         AlchemancyProperties.FLAMMABLE,
         PropertyFunction.WHILE_EQUIPPED,
         "Constantly increases the user's time on fire after being set on fire once. Has a 1% chance every tick to turn into {property alchemancy:charred}"
      );
      this.addCodexFunction(
         AlchemancyProperties.FLAMMABLE,
         PropertyFunction.OTHER,
         "Allows the item to be used as {item Furnace} fuel, letting it smelt 1 item for every 33 durability points it has left. Increases the item's fuel efficiency by 50% if it's aleady a {item Furnace} fuel."
      );
      this.addCodexFlavor(AlchemancyProperties.CHARRED, "A little overcooked...");
      this.addCodexFunction(
         AlchemancyProperties.CHARRED,
         PropertyFunction.OTHER,
         "Allows the item to be used as {item Furnace} fuel, letting it smelt 1 item for every 16 durability points it has left. Increases the item's fuel efficiency by 200% if it's aleady a {item Furnace} fuel."
      );
      this.addCodexFlavor(AlchemancyProperties.STURDY, "Rock and Stone!");
      this.addCodexFunction(AlchemancyProperties.STURDY, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's total durability by 20%.");
      this.addCodexFlavor(AlchemancyProperties.BRITTLE, "Shatter me like glass");
      this.addCodexFunction(AlchemancyProperties.BRITTLE, PropertyFunction.ATTRIBUTE_MODIFIER, "Reduces the item's total durability by 35%");
      this.addCodexFunction(AlchemancyProperties.BRITTLE, PropertyFunction.WHEN_SHOT, "Breaks the item on impact, triggering {function on_destroy} effects.");
      this.addCodexFunction(
         AlchemancyProperties.BRITTLE,
         PropertyFunction.WHEN_DROPPED,
         "Breaks the item after hitting the ground with enough force, triggering {function on_destroy} effects."
      );
      this.addCodexFlavor(AlchemancyProperties.RUSTY, "Better with age, but not more durable.");
      this.addCodexFunction(
         AlchemancyProperties.RUSTY,
         PropertyFunction.OTHER,
         "Causes the item to build up rust over time or when breaking blocks, increasing its mining speed and its chances of consuming double durability."
      );
      this.addCodexFlavor(AlchemancyProperties.FERROUS, "Heavy metal");
      this.addCodexFunction(AlchemancyProperties.FERROUS, PropertyFunction.WHILE_WORN, "Increases the amount of {shock Electric} damage received by 25%.");
      this.addCodexFunction(AlchemancyProperties.FERROUS, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's total durability by 150 points.");
      this.addCodexFlavor(AlchemancyProperties.GILDED, "All that glitters");
      this.addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHILE_HELD_MAINHAND, "Increases the user's {attribute Mining Speed} by 50%.");
      this.addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHILE_WORN, "Prevents Piglins from immediately attacking the user.");
      this.addCodexFunction(AlchemancyProperties.GILDED, PropertyFunction.WHEN_DROPPED, "Makes Piglins want to pick up the item.");
      this.addCodexFlavor(AlchemancyProperties.LUSTROUS, "Funding for Schaffrillas is provided by");
      this.addCodexFunction(
         AlchemancyProperties.LUSTROUS,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Sets the item's tool material to {item Diamond} tier. Doubles the item's durability up to a maximum total of 1600."
      );
      this.addCodexFlavor(AlchemancyProperties.WEALTHY, "Kaching!");
      this.addCodexFunction(AlchemancyProperties.WEALTHY, PropertyFunction.WHILE_EQUIPPED, "Makes Villagers and Pillagers follow the user.");
      this.addCodexFunction(AlchemancyProperties.WEALTHY, PropertyFunction.WHILE_ROOTED, "Attracts Villagers and Pillagers to the item.");
      this.addCodexFunction(
         AlchemancyProperties.WEALTHY,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Increases the item's {enchantment minecraft:fortune} and {enchantment minecraft:looting} levels by 1."
      );
      this.addCodexFlavor(AlchemancyProperties.REINFORCED, "Tougher than the rest of them");
      this.addCodexFunction(
         AlchemancyProperties.REINFORCED,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Increases the item's {attribute Armor} value by 3 and its {attribute Armor Toughness} by 1."
      );
      this.addCodexFlavor(AlchemancyProperties.PRISTINE, "More than just a pretty face");
      this.addCodexFunction(
         AlchemancyProperties.PRISTINE,
         PropertyFunction.DURABILITY_CONSUMED,
         "Consumes 1 {property alchemancy:pristine} point instead of taking damage. The Infusion is removed after 100 {property alchemancy:pristine} points are consumed."
      );
      this.addCodexFlavor(AlchemancyProperties.HELLBENT, "Stop at nothing");
      this.addCodexFunction(
         AlchemancyProperties.HELLBENT,
         PropertyFunction.MODIFY_DAMAGE,
         "Makes attacks always crit, triggering {function on_crit} effects and consuming 5 durability points or the item itself."
      );
      this.addCodexFunction(AlchemancyProperties.HELLBENT, PropertyFunction.WHEN_SHOT, "Triggers {function on_crit} effects when hitting an entity.");
      this.addCodexFunction(
         AlchemancyProperties.HELLBENT,
         PropertyFunction.BLOCK_DESTROYED,
         "Increases the user's {attribute Mining Speed} for each block of the same type destroyed, up to an additional total of 20%. The speed boost is lost when a different type of block is mined or another item is held."
      );
      this.addCodexFlavor(AlchemancyProperties.DEPTH_DWELLER, "Yearn for the mines");
      this.addCodexFunction(
         AlchemancyProperties.DEPTH_DWELLER,
         PropertyFunction.WHILE_WORN_LEGGINGS,
         "Increases the user's {attribute Movement Speed} relative to how low down they are in the world starting at Y=10, with a maximum increase of 50%. While in {nether The Nether}, the {attribute Movement Speed} boost is always 200%"
      );
      this.addCodexFunction(
         AlchemancyProperties.DEPTH_DWELLER,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Increases the user's {attribute Movement Speed} relative to how low down they are in the world starting at Y=10, with a maximum increase of 50%. While in {nether The Nether}, the {attribute Movement Speed} boost is always 200%"
      );
      this.addCodexFunction(
         AlchemancyProperties.DEPTH_DWELLER,
         PropertyFunction.WHILE_HELD_MAINHAND,
         "Increases the user's {attribute Mining Speed} relative to how low down they are in the world starting at Y=10, with a maximum increase of 50%. While in {nether The Nether}, the {attribute Mining Speed} boost is always 200%"
      );
      this.addCodexFlavor(AlchemancyProperties.MALLEABLE, "Great for Stop Motion Animation");
      this.addCodexFunction(
         AlchemancyProperties.MALLEABLE,
         PropertyFunction.ON_DESTROYED,
         "The item drops an {item Unshaped Clay}, which can be combined with a {item Clay Ball} or smelted down to restore the item."
      );
      this.addCodexFlavor(AlchemancyProperties.HARDENED, "Tough as nails");
      this.addCodexFunction(
         AlchemancyProperties.HARDENED,
         PropertyFunction.DURABILITY_CONSUMED,
         "Halves the amount of durability consumed by the item, or has a 10% chance not to consume any durability if the amount consumed is 1. "
      );
      this.addCodexFunction(
         AlchemancyProperties.HARDENED,
         PropertyFunction.WHEN_SHOT,
         "Allows the projectile to break certain blocks on impact, such as {item Glass}, {item Ice} or {item Decorated Pots}."
      );
      this.addCodexFlavor(AlchemancyProperties.CRACKED, "Falling apart");
      this.addCodexFunction(AlchemancyProperties.CRACKED, PropertyFunction.DURABILITY_CONSUMED, "40% chance to consume double durability.");
      this.addCodexFunction(AlchemancyProperties.CRACKED, PropertyFunction.ACTIVATE, "Breaks the item or consumes 1 durability point.");
      this.addCodexFlavor(AlchemancyProperties.ENERGIZED, "Need more power");
      this.addCodexFunction(
         AlchemancyProperties.ENERGIZED,
         PropertyFunction.RECEIVE_DAMAGE_EQUIPPED,
         "Increases the user's {attribute Movement Speed}, {attribute Mining Speed}, and {attribute Attack Speed} by 35% for 60 seconds after taking {shock Electric} damage."
      );
      this.addCodexFunction(
         AlchemancyProperties.ENERGIZED,
         PropertyFunction.WHILE_WORN_BOOTS,
         "For 60 seconds after taking {shock Electric} damage, {item Redstone} powers blocks the user is standing on for half a second."
      );
      this.addCodexFunction(
         AlchemancyProperties.ENERGIZED,
         PropertyFunction.WHEN_USED_BLOCK,
         "If {property alchemancy:interactable} is present, {item Redstone} powers the targeted block for half a second."
      );
      this.addCodexFunction(AlchemancyProperties.ENERGIZED, PropertyFunction.WHEN_SHOT, "{item Redstone} powers the impacted block for half a second.");
      this.addCodexFunction(AlchemancyProperties.ENERGIZED, PropertyFunction.ACTIVATE_BY_BLOCK, "{item Redstone} powers the adjacent blocks for 1 second.");
      this.addCodexFlavor(AlchemancyProperties.BOUNCY, "Slime Boots who?");
      this.addCodexFunction(AlchemancyProperties.BOUNCY, PropertyFunction.ON_ATTACK, "Bounces the target back a short distance.");
      this.addCodexFunction(
         AlchemancyProperties.BOUNCY, PropertyFunction.WHEN_HIT_EQUIPPED, "Bounces the user away from the damage source for a short distance."
      );
      this.addCodexFunction(
         AlchemancyProperties.BOUNCY,
         PropertyFunction.ACTIVATE,
         "Bounces the target away from the user for a short distance, or the user away from the surface they're looking at if activated on self."
      );
      this.addCodexFunction(
         AlchemancyProperties.BOUNCY,
         PropertyFunction.ON_FALL,
         "Bounces the user upwards with reduced force of which they hit the ground, nullifying all fall damage."
      );
      this.addCodexFunction(
         AlchemancyProperties.BOUNCY, PropertyFunction.WHEN_SHOT, "Bounces off of blocks on impact if the Projectile is going at a high enough speed."
      );
      this.addCodexFlavor(AlchemancyProperties.SLIPPERY, "Warned you about the stairs");
      this.addCodexFunction(AlchemancyProperties.SLIPPERY, PropertyFunction.ON_ATTACK, "Makes the user drop their held item.");
      this.addCodexFunction(AlchemancyProperties.SLIPPERY, PropertyFunction.ACTIVATE, "Makes the user drop the item.");
      this.addCodexFunction(
         AlchemancyProperties.SLIPPERY,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Reduces the user's movement friction. Makes the user trip when going down {item Stairs}."
      );
      this.addCodexFlavor(AlchemancyProperties.BUOYANT, "Don't forget your floaties");
      this.addCodexFunction(AlchemancyProperties.BUOYANT, PropertyFunction.WHILE_EQUIPPED, "Floats the user upwards while inside of a liquid.");
      this.addCodexFunction(AlchemancyProperties.BUOYANT, PropertyFunction.WHEN_SHOT, "Floats the Projectile upwards while inside of a liquid.");
      this.addCodexFunction(AlchemancyProperties.BUOYANT, PropertyFunction.WHEN_DROPPED, "Floats the item upwards while inside of a liquid.");
      this.addCodexFlavor(AlchemancyProperties.HEAVY, "Hard to carry");
      this.addCodexFunction(
         AlchemancyProperties.HEAVY,
         PropertyFunction.WHILE_EQUIPPED,
         "Reduces the user's {attribute Movement Speed} and {attribute Jump Height}, as well as increasing their falling speed."
      );
      this.addCodexFunction(AlchemancyProperties.HEAVY, PropertyFunction.WHEN_SHOT, "Increases the projectile's falling speed.");
      this.addCodexFunction(AlchemancyProperties.HEAVY, PropertyFunction.WHEN_DROPPED, "Increases the item's falling speed.");
      this.addCodexFlavor(AlchemancyProperties.ANTIGRAV, "Like drifting through space");
      this.addCodexFunction(AlchemancyProperties.ANTIGRAV, PropertyFunction.WHILE_WORN, "Reduces the user's {attribute Gravity} by 100%");
      this.addCodexFunction(
         AlchemancyProperties.ANTIGRAV, PropertyFunction.WHEN_SHOT, "Disables the Projectile's {attribute Gravity}, causing it to fly in a straight line."
      );
      this.addCodexFunction(
         AlchemancyProperties.ANTIGRAV, PropertyFunction.WHEN_DROPPED, "Disables the item's {attribute Gravity}, causing it to fly in a straight line."
      );
      this.addCodexFlavor(AlchemancyProperties.LIGHTWEIGHT, "Lighter than a feather");
      this.addCodexFunction(
         AlchemancyProperties.LIGHTWEIGHT,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Allows the user to walk on {item Powder Snow}. Prevents the user from trampling {item Farmland}."
      );
      this.addCodexFunction(AlchemancyProperties.LIGHTWEIGHT, PropertyFunction.WHEN_SHOT, "Lowers the projectile's falling speed.");
      this.addCodexFunction(AlchemancyProperties.LIGHTWEIGHT, PropertyFunction.WHEN_DROPPED, "Lowers the item's falling speed.");
      this.addCodexFlavor(AlchemancyProperties.MINING, "I don't know what I'll mine. I'll mine it anyway");
      this.addCodexFunction(
         AlchemancyProperties.MINING,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Allows the item to break Pickaxe-related blocks, starting at wooden tier if the item isn't already a tool."
      );
      this.addCodexFunction(
         AlchemancyProperties.MINING,
         PropertyFunction.WHEN_SHOT,
         "Breaks Pickaxe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block."
      );
      this.addCodexFlavor(AlchemancyProperties.CHOPPING, "There must be some trees around here somewhere");
      this.addCodexFunction(
         AlchemancyProperties.CHOPPING,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Allows the item to break Axe-related blocks, starting at wooden tier if the item isn't already a tool."
      );
      this.addCodexFunction(AlchemancyProperties.CHOPPING, PropertyFunction.WHEN_USED_BLOCK, "Strips {item Logs} and removes wax from {item Copper Blocks}");
      this.addCodexFunction(
         AlchemancyProperties.CHOPPING,
         PropertyFunction.WHEN_SHOT,
         "Breaks Axe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block."
      );
      this.addCodexFlavor(AlchemancyProperties.DIGGING, "Diggy diggy hole, digging a hole");
      this.addCodexFunction(
         AlchemancyProperties.DIGGING,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Allows the item to break Shovel-related blocks, starting at wooden tier if the item isn't already a tool."
      );
      this.addCodexFunction(
         AlchemancyProperties.DIGGING, PropertyFunction.WHEN_USED_BLOCK, "Can turn {item Dirt} into {item Dirt Path} and put out {item Campfires}"
      );
      this.addCodexFunction(
         AlchemancyProperties.DIGGING,
         PropertyFunction.WHEN_SHOT,
         "Breaks Shovel-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block."
      );
      this.addCodexFlavor(AlchemancyProperties.REAPING, "Reap what you sow");
      this.addCodexFunction(
         AlchemancyProperties.REAPING,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Allows the item to break Hoe-related blocks, starting at wooden tier if the item isn't already a tool."
      );
      this.addCodexFunction(AlchemancyProperties.REAPING, PropertyFunction.WHEN_USED_BLOCK, "Can till {item Dirt} into {item Farmland}");
      this.addCodexFunction(
         AlchemancyProperties.REAPING,
         PropertyFunction.WHEN_SHOT,
         "Breaks Hoe-related blocks on impact. The faster the projectile goes, the more likely it is to continue its trajectory after breaking a block."
      );
      this.addCodexFlavor(AlchemancyProperties.SHEARING, "Beats paper");
      this.addCodexFunction(AlchemancyProperties.SHEARING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to swiftly break Leaves, Cobwebs, and Wool.");
      this.addCodexFunction(AlchemancyProperties.SHEARING, PropertyFunction.WHEN_USED_ENTITY, "Can shear Sheep.");
      this.addCodexFunction(AlchemancyProperties.SHEARING, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Can shear Sheep in front of the {item Dispenser}.");
      this.addCodexFlavor(AlchemancyProperties.SLASHING, "Got my sword. Got my hat. What else do I need?");
      this.addCodexFunction(AlchemancyProperties.SLASHING, PropertyFunction.ATTRIBUTE_MODIFIER, "Allows the item to swiftly break bamboo and cobwebs.");
      this.addCodexFunction(AlchemancyProperties.SLASHING, PropertyFunction.MODIFY_DAMAGE, "Performs a sweeping attack when standing still.");
      this.addCodexFlavor(AlchemancyProperties.SHARPSHOOTING, "Nothing gets past my bow");
      this.addCodexFunction(AlchemancyProperties.SHARPSHOOTING, PropertyFunction.WHEN_USED, "Fires {item Arrows} as if it were a {item Bow}.");
      this.addCodexFlavor(AlchemancyProperties.SHIELDING, "Block with your sword, just like the good old days");
      this.addCodexFunction(AlchemancyProperties.SHIELDING, PropertyFunction.RECEIVE_DAMAGE_USING, "Reduces blockable damage coming from in front by 50%.");
      this.addCodexFlavor(AlchemancyProperties.FIRESTARTING, "I swear we didn't");
      this.addCodexFunction(
         AlchemancyProperties.FIRESTARTING,
         PropertyFunction.WHEN_USED_BLOCK,
         "Creates {item Fire} at the targeted block. Allows the item to light {item Candles} and {item Campfires}."
      );
      this.addCodexFunction(
         AlchemancyProperties.FIRESTARTING,
         PropertyFunction.WHEN_SHOT_FROM_DISPENSER,
         "Creates {item Fire} at the block in front. Light {item Candles} and {item Campfires} in front of the {item Dispenser}."
      );
      this.addCodexFlavor(AlchemancyProperties.BRUSHING, "Archaeologist hate it!");
      this.addCodexFunction(AlchemancyProperties.BRUSHING, PropertyFunction.WHEN_USED_BLOCK, "Allows the item to brush off {item Suspicious Blocks}.");
      this.addCodexFunction(AlchemancyProperties.BRUSHING, PropertyFunction.WHEN_USED_ENTITY, "Can brush Armadillos to obtain {item Armadillo Scute}.");
      this.addCodexFunction(
         AlchemancyProperties.BRUSHING,
         PropertyFunction.WHEN_SHOT_FROM_DISPENSER,
         "Brushes an Armadillo in front of the {item Dispenser} to obtain {item Armadillo Scute}."
      );
      this.addCodexFlavor(AlchemancyProperties.SCOPING, "Zoom in and enhance");
      this.addCodexFunction(AlchemancyProperties.SCOPING, PropertyFunction.WHILE_HELD, "Zooms the user's vision in while crouching.");
      this.addCodexFunction(AlchemancyProperties.SCOPING, PropertyFunction.WHILE_WORN_HELMET, "Zooms the user's vision in while crouching.");
      this.addCodexFlavor(AlchemancyProperties.THROWABLE, "For three!");
      this.addCodexFunction(
         AlchemancyProperties.THROWABLE,
         PropertyFunction.WHEN_USED,
         "Throws the item like a Projectile, triggering {function when_shot} effects when appropriate. The thrown item takes 10 durability points or is destroyed when it hits a block or an entity."
      );
      this.addCodexFunction(
         AlchemancyProperties.THROWABLE,
         PropertyFunction.WHEN_SHOT_FROM_DISPENSER,
         "Throws the item like a Projectile, triggering {function when_shot} effects when appropriate. The thrown item takes 10 durability points or is destroyed when it hits a block or an entity."
      );
      this.addCodexFlavor(AlchemancyProperties.WAYFINDING, "Always know the way back");
      this.addCodexFunction(
         AlchemancyProperties.WAYFINDING,
         PropertyFunction.VISUAL,
         "Rotates the item to point towards the tracked position, or if none is present the user's spawn point, the exit portal in {end The End}, or the place the user entered the dimension from."
      );
      this.addCodexFunction(
         AlchemancyProperties.WAYFINDING,
         PropertyFunction.WHEN_USED_BLOCK,
         "Saves the targeted {item Lodestone}'s position as a tracked position if none is present."
      );
      this.addCodexFunction(
         AlchemancyProperties.WAYFINDING, PropertyFunction.WHEN_USED_ENTITY, "Saves the targeted Player as a tracked position if none is present."
      );
      this.addCodexFlavor(AlchemancyProperties.HEADWEAR, "Anything is a hat if you're brave enough");
      this.addCodexFunction(AlchemancyProperties.HEADWEAR, PropertyFunction.OTHER, "Allows the item to be worn in the Helmet slot.");
      this.addCodexFunction(
         AlchemancyProperties.HEADWEAR,
         PropertyFunction.WHEN_SHOT_FROM_DISPENSER,
         "Equips the item onto the Helmet slot of an Entity in front of the {item Dispenser}."
      );
      this.addCodexFlavor(AlchemancyProperties.SADDLED, "Giddy up!");
      this.addCodexFunction(
         AlchemancyProperties.SADDLED,
         PropertyFunction.WHEN_USED_ENTITY,
         "Makes the user ride the target. {system Infusions} that attract mobs can be applied to the held item to steer the ridden entity."
      );
      this.addCodexFlavor(AlchemancyProperties.GLIDER, "Falling with style");
      this.addCodexFunction(
         AlchemancyProperties.GLIDER,
         PropertyFunction.WHILE_WORN_CHESTPLATE,
         "Lets the user glide as if they were wearing an {item Elytra} as long as the item is more than 1 point of durability away from breaking."
      );
      this.addCodexFlavor(AlchemancyProperties.CRAFTY, "Crafting on the go");
      this.addCodexFunction(AlchemancyProperties.CRAFTY, PropertyFunction.WHEN_USED, "Opens the {item Crafting Table} interface.");
      this.addCodexFunction(AlchemancyProperties.CRAFTY, PropertyFunction.WHILE_ROOTED, "When right-clicked, opens the {item Crafting Table} interface.");
      this.addCodexFlavor(AlchemancyProperties.STONECUTTING, "To show you the power of Stonecutting, I sawed this rock in half!");
      this.addCodexFunction(AlchemancyProperties.STONECUTTING, PropertyFunction.WHEN_USED, "Opens the {item Stonecutter} interface.");
      this.addCodexFunction(AlchemancyProperties.STONECUTTING, PropertyFunction.WHILE_ROOTED, "When right-clicked, opens the {item Stonecutter} interface.");
      this.addCodexFunction(
         AlchemancyProperties.STONECUTTING,
         PropertyFunction.WHEN_USED_BLOCK,
         "If the user isn't crouching and {property alchemancy:interactable} is present on the item, the targeted block is cut into a random variant obtainable from the {item Stonecutter}."
      );
      this.addCodexFlavor(AlchemancyProperties.ASSEMBLING, "Autocrafting on the go");
      this.addCodexFunction(
         AlchemancyProperties.ASSEMBLING,
         PropertyFunction.STACKED_ON,
         "If right-clicked from the inventory with an empty cursor, the item will attempt to craft more of itself by consuming resources in the user's inventory. Crafted items will not retain any of the source's item's data."
      );
      this.addCodexFunction(
         AlchemancyProperties.ASSEMBLING,
         PropertyFunction.WHILE_ROOTED,
         "When right-clicked, the item will attempt to craft more of itself by consuming resources in the user's inventory. Crafted items will not retain any of the source's item's data."
      );
      this.addCodexFunction(
         AlchemancyProperties.ASSEMBLING,
         PropertyFunction.ACTIVATE,
         "Attempts to craft more of the item by consuming resources in the user's inventory. Crafted items will not retain any of the source's item's data."
      );
      this.addCodexFunction(AlchemancyProperties.ASSEMBLING, PropertyFunction.OTHER, "Makes the item non-stackable.");
      this.addCodexFlavor(AlchemancyProperties.REPLICATING, "Five. Hundred. Sparkling Sticks.");
      this.addCodexFunction(
         AlchemancyProperties.REPLICATING,
         PropertyFunction.WHILE_EQUIPPED,
         "Attempts to craft as many replicas of the item by consuming resources in the user's inventory until the item reaches its stack limit."
      );
      this.addCodexFlavor(AlchemancyProperties.FRAGMENTED, "Split into two");
      this.addCodexFunction(
         AlchemancyProperties.FRAGMENTED,
         PropertyFunction.STACKED_ON,
         "If right-clicked from the inventory with an empty cursor, a copy of the item will be created, splitting the item's durability lost and {attribute Max Durability} between the two. If the item has a {attribute Max Durability} of 1 or less, the item is instead destroyed."
      );
      this.addCodexFlavor(AlchemancyProperties.ASSIMILATING, "All for one");
      this.addCodexFunction(
         AlchemancyProperties.ASSIMILATING,
         PropertyFunction.WHILE_EQUIPPED,
         "Automatically absorbs any other items like it in the user's inventory when low on durability, replenishing the item's durability in the process and absorbing the assimilated item's {system Infusions} and Enchantments when possible. If {property alchemancy:assembling} is present, the item will be able to absorb ingredients used to craft itself inside of the user's inventory as long as they can all create a new item."
      );
      this.addCodexFunction(
         AlchemancyProperties.ASSIMILATING,
         PropertyFunction.STACKED_OVER,
         "Absorbs similar items stacked over the item to replenish its durability and apply the assimilated item's {system Infusions} and Enchantments when possible."
      );
      this.addCodexFlavor(AlchemancyProperties.SMELTING, "Heating up your tools can be a good idea");
      this.addCodexFunction(
         AlchemancyProperties.SMELTING, PropertyFunction.BLOCK_DESTROYED, "Smelts all dropped items at the cost of 1 {fire Fuel} point per item."
      );
      this.addCodexFunction(AlchemancyProperties.SMELTING, PropertyFunction.ON_KILL, "Smelts all dropped items at the cost of 1 {fire Fuel} point per item.");
      this.addCodexFunction(
         AlchemancyProperties.SMELTING,
         PropertyFunction.STACKED_OVER,
         "If the stacked item is a valid {item Furnace} fuel, it is consumed and used to replenish {fire Fuel}."
      );
      this.addCodexFunction(
         AlchemancyProperties.SMELTING,
         PropertyFunction.OTHER,
         "{fire Fuel} is automatically restored upon running out if a {item Furnace} fuel is stored inside of the item by the use of {property alchemancy:hollow}."
      );
      this.addCodexFlavor(AlchemancyProperties.SWIFT, "Run like the wind");
      this.addCodexFunction(AlchemancyProperties.SWIFT, PropertyFunction.WHILE_WORN_LEGGINGS, "Increases the user's {attribute Movement Speed} by 55%.");
      this.addCodexFunction(AlchemancyProperties.SWIFT, PropertyFunction.WHILE_HELD_MAINHAND, "Increases the user's {attribute Attack Speed} by 25%.");
      this.addCodexFunction(AlchemancyProperties.SWIFT, PropertyFunction.WHILE_USING, "Halves the item's use time.");
      this.addCodexFlavor(AlchemancyProperties.SLUGGISH, "Taking it slooooooow");
      this.addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.WHILE_WORN, "Reduces the user's {attribute Movement Speed} by 55%.");
      this.addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.ATTRIBUTE_MODIFIER, "Reduces {attribute Attack Speed} by 55%.");
      this.addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.ON_ATTACK, "Applies Slowness II to the target for 10 seconds.");
      this.addCodexFunction(AlchemancyProperties.SLUGGISH, PropertyFunction.WHILE_USING, "Doubles the item's use time.");
      this.addCodexFlavor(AlchemancyProperties.POISONOUS, "Could also be venomous");
      this.addCodexFunction(AlchemancyProperties.POISONOUS, PropertyFunction.ON_ATTACK, "Applies Poison to the target for 5 seconds.");
      this.addCodexFlavor(AlchemancyProperties.DECAYING, "Withering away");
      this.addCodexFunction(AlchemancyProperties.DECAYING, PropertyFunction.ON_ATTACK, "Applies Wither II to the target for 5 seconds.");
      this.addCodexFunction(AlchemancyProperties.DECAYING, PropertyFunction.WHILE_EQUIPPED, "Loses durability every second.");
      this.addCodexFlavor(AlchemancyProperties.TIPSY, "Better lay off the Pufferfish for a while");
      this.addCodexFunction(AlchemancyProperties.TIPSY, PropertyFunction.ON_ATTACK, "Applies Nausea to the target for 10 seconds.");
      this.addCodexFunction(AlchemancyProperties.TIPSY, PropertyFunction.WHILE_EQUIPPED, "Applies Nausea to the user for 10 seconds.");
      this.addCodexFlavor(AlchemancyProperties.BLINDING, "Turning off the lights");
      this.addCodexFunction(AlchemancyProperties.BLINDING, PropertyFunction.ON_ATTACK, "Applies Blindness to the target for 10 seconds.");
      this.addCodexFunction(AlchemancyProperties.BLINDING, PropertyFunction.WHILE_WORN_HELMET, "Applies Blindness to the user for 10 seconds.");
      this.addCodexFlavor(AlchemancyProperties.GLOWING_AURA, "A beacon of light");
      this.addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.ON_ATTACK, "Applies Glowing to the target for 30 seconds.");
      this.addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.WHILE_EQUIPPED, "Applies Glowing to the user for as long as its equipped.");
      this.addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.WHEN_SHOT, "Enshrouds the Projectile in a glowing aura.");
      this.addCodexFunction(AlchemancyProperties.GLOWING_AURA, PropertyFunction.WHEN_DROPPED, "Enshrouds the item in a glowing aura.");
      this.addCodexFlavor(AlchemancyProperties.NOCTURNAL, "I am vengeance, I am the night");
      this.addCodexFunction(AlchemancyProperties.NOCTURNAL, PropertyFunction.MODIFY_DAMAGE, "Increases damage dealt by 40% while under direct moonlight.");
      this.addCodexFunction(AlchemancyProperties.NOCTURNAL, PropertyFunction.WHILE_WORN_HELMET, "Applies Night Vision to the user for 15 seconds.");
      this.addCodexFlavor(AlchemancyProperties.AQUATIC, "Swim with the fishes");
      this.addCodexFunction(
         AlchemancyProperties.AQUATIC,
         PropertyFunction.WHILE_WORN_HELMET,
         "Increases {attribute Oxygen Bonus} value by 2, granting a similar effect to {enchantment minecraft:respiration} II."
      );
      this.addCodexFunction(
         AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_WORN_CHESTPLATE, "Increases {attribute Mining Speed} by 200% while underwater."
      );
      this.addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_HELD_MAINHAND, "Increases {attribute Mining Speed} by 200% while underwater.");
      this.addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_WORN_LEGGINGS, "Increases {attribute Swim Speed} by 55%.");
      this.addCodexFunction(AlchemancyProperties.AQUATIC, PropertyFunction.WHILE_WORN_BOOTS, "Increases {attribute Walking Speed} by 55% while underwater.");
      this.addCodexFunction(
         AlchemancyProperties.AQUATIC, PropertyFunction.WHEN_SHOT, "Allows Projectiles to fly through {item Water} as they would outside of it."
      );
      this.addCodexFlavor(AlchemancyProperties.LEAPING, "A hop, a skip, and a jump");
      this.addCodexFunction(
         AlchemancyProperties.LEAPING,
         PropertyFunction.WHILE_WORN_LEGGINGS,
         "Increases the user's {attribute Jump Height}, increasing the height of each consecutive jump for up to 4 jumps in a row. Increases {attribute Safe Fall Distance} by 7 blocks."
      );
      this.addCodexFunction(
         AlchemancyProperties.LEAPING,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Increases the user's {attribute Jump Height}, increasing the height of each consecutive jump for up to 4 jumps in a row. Increases {attribute Safe Fall Distance} by 7 blocks."
      );
      this.addCodexFlavor(AlchemancyProperties.OMINOUS, "I have a bad feeling about this");
      this.addCodexFunction(AlchemancyProperties.OMINOUS, PropertyFunction.WHILE_EQUIPPED, "Applies Bad Omen to the user for as long as the item is equipped.");
      this.addCodexFlavor(AlchemancyProperties.HEARTY, "Keeps the doctor away");
      this.addCodexFunction(AlchemancyProperties.HEARTY, PropertyFunction.WHILE_WORN, "Increases the user's {attribute Max Health} by 2.");
      this.addCodexFunction(AlchemancyProperties.HEARTY, PropertyFunction.AFTER_USE, "Grants the user Health Boost II for 8 minutes after being eaten.");
      this.addCodexFunction(AlchemancyProperties.HEARTY, PropertyFunction.ACTIVATE, "Grants the target Health Boost II for 20 seconds.");
      this.addCodexFlavor(AlchemancyProperties.LEVITATING, "Great view from up here");
      this.addCodexFunction(AlchemancyProperties.LEVITATING, PropertyFunction.WHILE_EQUIPPED, "Makes the user float upwards.");
      this.addCodexFunction(AlchemancyProperties.LEVITATING, PropertyFunction.WHEN_SHOT, "Makes the projectile float upwards.");
      this.addCodexFlavor(AlchemancyProperties.GRAPPLING, "Get over here!");
      this.addCodexFunction(AlchemancyProperties.GRAPPLING, PropertyFunction.ON_ATTACK, "Pulls targets towards you, instead of knocking them back.");
      this.addCodexFlavor(AlchemancyProperties.SPIKING, "Forward Aerial");
      this.addCodexFunction(AlchemancyProperties.SPIKING, PropertyFunction.WHILE_WORN_BOOTS, "Sets your movement friction to a constant value.");
      this.addCodexFunction(
         AlchemancyProperties.SPIKING,
         PropertyFunction.ON_ATTACK,
         "Knocks targets down instead of knocking them back, dealing increased fall damage when they land."
      );
      this.addCodexFlavor(AlchemancyProperties.LAUNCHING, "To the moon");
      this.addCodexFunction(
         AlchemancyProperties.LAUNCHING,
         PropertyFunction.ON_CRIT,
         "Launches targets upwards instead of knocking them back, damaging the item for 20 durability points or destroying it in the process."
      );
      this.addCodexFlavor(AlchemancyProperties.SHARP, "Cutting edge");
      this.addCodexFunction(AlchemancyProperties.SHARP, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases {attribute Attack Damage} by 30%.");
      this.addCodexFlavor(AlchemancyProperties.WEAK, "Doesn't even lift");
      this.addCodexFunction(AlchemancyProperties.WEAK, PropertyFunction.ATTRIBUTE_MODIFIER, "Reduces {attribute Armor} value by 1.");
      this.addCodexFunction(AlchemancyProperties.WEAK, PropertyFunction.MODIFY_DAMAGE, "Reduces damage dealt by 50%.");
      this.addCodexFunction(AlchemancyProperties.WEAK, PropertyFunction.WHILE_WORN, "Increases incoming damage by 20%.");
      this.addCodexFlavor(AlchemancyProperties.DENSE, "Stomping... Koopas");
      this.addCodexFunction(
         AlchemancyProperties.DENSE, PropertyFunction.MODIFY_DAMAGE, "Increases damage dealt equal to the amount of time the user has been falling for."
      );
      this.addCodexFunction(
         AlchemancyProperties.DENSE,
         PropertyFunction.ON_FALL,
         "Applies damage to nearby entities equal to the amount of time the user has been falling for. If landing after falling for more than 6 blocks, the item {activate Activates} on the user."
      );
      this.addCodexFlavor(AlchemancyProperties.GAMBLING, "...aw dang it");
      this.addCodexFunction(
         AlchemancyProperties.GAMBLING,
         PropertyFunction.MODIFY_DAMAGE,
         "has a 33% chance of dealing double damage, 33% chance of dealing normal damage, and 33% chance of damaging the user instead."
      );
      this.addCodexFlavor(AlchemancyProperties.ARCANE, "Join the glorious evolution");
      this.addCodexFunction(
         AlchemancyProperties.ARCANE,
         PropertyFunction.MODIFY_DAMAGE,
         "Causes the item to deal {arcane Magic} damage, making its attacks bypass {attribute Armor}."
      );
      this.addCodexFunction(AlchemancyProperties.ARCANE, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {attribute Enchantability} by 18.");
      this.addCodexFlavor(AlchemancyProperties.RESIZED, "Honey, I shrunk the tools");
      this.addCodexFunction(AlchemancyProperties.RESIZED, PropertyFunction.VISUAL, "Scales the item's size equal to its {property alchemancy:resized} value");
      this.addCodexFunction(
         AlchemancyProperties.RESIZED,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Increases the item's {attribute Attack Damage}, and reduces its {attribute Attack Speed} proportionally to its {property alchemancy:resized} value"
      );
      this.addCodexFlavor(AlchemancyProperties.FERAL, "Standing here, I realize");
      this.addCodexFunction(AlchemancyProperties.FERAL, PropertyFunction.WHILE_HELD, "Increases the user's {attribute Attack Speed} by 45%.");
      this.addCodexFlavor(AlchemancyProperties.EXPLODING, "An earth-shattering kaboom");
      this.addCodexFunction(
         AlchemancyProperties.EXPLODING,
         PropertyFunction.ON_CRIT,
         "Creates an explosion around the target, greatly damaging the item or destroying it in the process."
      );
      this.addCodexFlavor(AlchemancyProperties.WIND_CHARGED, "Do the windy thing");
      this.addCodexFunction(
         AlchemancyProperties.WIND_CHARGED,
         PropertyFunction.ON_CRIT,
         "Creates a burst of wind around the target, greatly damaging the item or destroying it in the process."
      );
      this.addCodexFlavor(AlchemancyProperties.SMITING, "Like lightning in a bottle");
      this.addCodexFunction(
         AlchemancyProperties.SMITING,
         PropertyFunction.ON_CRIT,
         "Creates a {item Lightning Bolt} at the target's position, greatly damaging the item or destroying it in the process."
      );
      this.addCodexFlavor(AlchemancyProperties.CRACKLING, "Be the light of the party");
      this.addCodexFunction(
         AlchemancyProperties.CRACKLING,
         PropertyFunction.ON_CRIT,
         "Creates a Firework explosion at the target's position, damaging all nearby entities, including the user. The explosion's effects depend on the {item Firework Rocket} or {item Firework Star} used to obtain this {system Infusion}."
      );
      this.addCodexFlavor(AlchemancyProperties.CALCAREOUS, "[kal-kair-ee-uhs] Adjective: contains calcium");
      this.addCodexFunction(
         AlchemancyProperties.CALCAREOUS,
         PropertyFunction.WHILE_WORN_LEGGINGS,
         "Reduces the user's {attribute Fall Damage} by 50% and increases their {attribute Safe Fall Distance} by 10 blocks."
      );
      this.addCodexFunction(
         AlchemancyProperties.CALCAREOUS, PropertyFunction.ACTIVATE, "Clears the target's Potion Effects and {property alchemancy:chromatize} tint."
      );
      this.addCodexFlavor(AlchemancyProperties.COZY, "Nice and warm");
      this.addCodexFunction(
         AlchemancyProperties.COZY,
         PropertyFunction.WHILE_WORN,
         "Increases the rate at which the user thaws out, enough to prevent {item Powder Snow} from having any effect."
      );
      this.addCodexFunction(AlchemancyProperties.COZY, PropertyFunction.WHILE_ROOTED, "Swiftly thaws out all entities standing inside of the item.");
      this.addCodexFlavor(AlchemancyProperties.WAXED, "Forget sunscreen, we got BEES!");
      this.addCodexFunction(
         AlchemancyProperties.WAXED,
         PropertyFunction.RECEIVE_DAMAGE_WORN,
         "Prevents the user from taking {fire Fire} damage up to 100 times before the {system Infusion} is removed."
      );
      this.addCodexFunction(
         AlchemancyProperties.WAXED,
         PropertyFunction.STACKED_OVER,
         "When stacking {item Honeycomb} over this item, it is consumed and used to fully restore the {system Infusion}'s uses."
      );
      this.addCodexFlavor(AlchemancyProperties.FIRE_RESISTANT, "Someone remembered to put on sunscreen");
      this.addCodexFunction(
         AlchemancyProperties.FIRE_RESISTANT,
         PropertyFunction.WHILE_WORN,
         "Reduces the user's amount of time set on fire by 75%, limiting their time set on fire to at most 12 seconds."
      );
      this.addCodexFunction(AlchemancyProperties.FIRE_RESISTANT, PropertyFunction.WHEN_DROPPED, "Makes the item immune to fire.");
      this.addCodexFlavor(AlchemancyProperties.BLAST_RESISTANT, "All of a sudden explosions feel safe");
      this.addCodexFunction(AlchemancyProperties.BLAST_RESISTANT, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces damage dealt by explosions by 50%.");
      this.addCodexFlavor(AlchemancyProperties.MAGIC_RESISTANT, "Hard counters Arcane Barrage");
      this.addCodexFunction(
         AlchemancyProperties.MAGIC_RESISTANT, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces incoming {arcane Magic} damage by 15%."
      );
      this.addCodexFlavor(AlchemancyProperties.INSULATED, "A key part of any Energized build");
      this.addCodexFunction(AlchemancyProperties.INSULATED, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces incoming {shock Electric} damage by 25%.");
      this.addCodexFunction(AlchemancyProperties.INSULATED, PropertyFunction.WHEN_DROPPED, "Makes the item immune to {shock Electric} damage.");
      this.addCodexFlavor(AlchemancyProperties.WARDING, "Become an Armored Bastion");
      this.addCodexFunction(AlchemancyProperties.WARDING, PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING, "Reduces ALL incoming damage by 15%.");
      this.addCodexFlavor(AlchemancyProperties.ETERNAL, "Hasn't aged a day");
      this.addCodexFunction(
         AlchemancyProperties.ETERNAL,
         PropertyFunction.WHEN_DROPPED,
         "Makes the item immune to all forms of damage, including {burning Fire}, {shock Electric}, and {item Cacti}. Levitates the item upwards for for 10 seconds after falling into the void. Prevents the item from despawning."
      );
      this.addCodexFlavor(AlchemancyProperties.MUFFLED, "Keeping it quiet");
      this.addCodexFunction(AlchemancyProperties.MUFFLED, PropertyFunction.WHILE_EQUIPPED, "Lowers the volume of most sounds emitted by the user by 25%.");
      this.addCodexFunction(
         AlchemancyProperties.MUFFLED,
         PropertyFunction.WHILE_HELD_MAINHAND,
         "Suppresses vibrations emitted by placing blocks, using items, interacting with Entities, shearing, and shooting projectiles."
      );
      this.addCodexFunction(
         AlchemancyProperties.MUFFLED,
         PropertyFunction.WHILE_HELD_OFFHAND,
         "Suppresses vibrations emitted by placing blocks, using items, interacting with Entities, and shearing."
      );
      this.addCodexFunction(AlchemancyProperties.MUFFLED, PropertyFunction.WHILE_WORN_HELMET, "Suppresses vibrations emitted by eating and drinking.");
      this.addCodexFunction(
         AlchemancyProperties.MUFFLED,
         PropertyFunction.WHILE_WORN_CHESTPLATE,
         "Suppresses vibrations emitted by taking damage, dying, gliding, and teleporting."
      );
      this.addCodexFunction(
         AlchemancyProperties.MUFFLED,
         PropertyFunction.WHILE_WORN_LEGGINGS,
         "Suppresses vibrations emitted by taking damage, dying, splashing, swimming, and teleporting."
      );
      this.addCodexFunction(
         AlchemancyProperties.MUFFLED, PropertyFunction.WHILE_WORN_BOOTS, "Suppresses vibrations emitted by steps, splashing, and hitting the ground."
      );
      this.addCodexFlavor(AlchemancyProperties.SOULBIND, "Trapped Spirits");
      this.addCodexFunction(
         AlchemancyProperties.SOULBIND,
         PropertyFunction.WHILE_IN_INVENTORY,
         "Has a 2% chance every second of the soul trapped inside of the item of escaping, removing the {system Infusion} and potentially damaging the holder."
      );
      this.addCodexFunction(AlchemancyProperties.SOULBIND, PropertyFunction.OTHER, "Can be used to create multiple new {system Infusions}.");
      this.addCodexFlavor(AlchemancyProperties.SPIRIT_BOND, "Your souls, unite");
      this.addCodexFunction(
         AlchemancyProperties.SPIRIT_BOND, PropertyFunction.ON_HEAL, "While equipped, restores 5 durability points for each health point restored."
      );
      this.addCodexFunction(AlchemancyProperties.SPIRIT_BOND, PropertyFunction.RECEIVE_DAMAGE_EQUIPPED, "Loses 1 durability point for each health point lost.");
      this.addCodexFlavor(AlchemancyProperties.PHASING, "Might be smart to put a Hopper under your Forge...");
      this.addCodexFunction(AlchemancyProperties.PHASING, PropertyFunction.WHEN_SHOT, "Allows the projectile to phase through blocks.");
      this.addCodexFunction(AlchemancyProperties.PHASING, PropertyFunction.WHEN_DROPPED, "Allows the item to phase through blocks.");
      this.addCodexFlavor(AlchemancyProperties.VENGEFUL, "An eye for an eye");
      this.addCodexFunction(
         AlchemancyProperties.VENGEFUL,
         PropertyFunction.MODIFY_DAMAGE,
         "Deals 85% more damage against the entity that last damaged the user, and 35% less damage to everyone else."
      );
      this.addCodexFlavor(AlchemancyProperties.LOYAL, "Always by your side");
      this.addCodexFunction(AlchemancyProperties.LOYAL, PropertyFunction.WHEN_DROPPED, "Slowly floats back to the dropper after a short period of time.");
      this.addCodexFunction(AlchemancyProperties.LOYAL, PropertyFunction.WHEN_SHOT, "Floats back to the shooter after impacting a block or an entity.");
      this.addCodexFlavor(AlchemancyProperties.RELENTLESS, "Not giving up");
      this.addCodexFunction(
         AlchemancyProperties.RELENTLESS,
         PropertyFunction.DURABILITY_CONSUMED,
         "Grants a chance to not consume durability relative to how much health the user has lost, with a maximum chance of 60%."
      );
      this.addCodexFunction(
         AlchemancyProperties.RELENTLESS,
         PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING,
         "Reduces all incoming damage relative to how much health the user has lost, with a maximum reduction of 20%."
      );
      this.addCodexFlavor(AlchemancyProperties.VAMPIRIC, "It doesn't suck... it scrapes");
      this.addCodexFunction(AlchemancyProperties.VAMPIRIC, PropertyFunction.MODIFY_DAMAGE, "Heals the user for 20% of the damage dealt.");
      this.addCodexFunction(
         AlchemancyProperties.VAMPIRIC,
         PropertyFunction.WHILE_EQUIPPED,
         "Has a 40% chance every second of consuming 1 point of durability under broad daylight, with a 10% chance on top of that of instead setting the user ablaze for 4 seconds."
      );
      this.addCodexFlavor(AlchemancyProperties.ENERGY_SAPPER, "A symbiotic relationship");
      this.addCodexFunction(AlchemancyProperties.ENERGY_SAPPER, PropertyFunction.ON_ATTACK, "Consumes a portion of the target player's Hunger.");
      this.addCodexFunction(
         AlchemancyProperties.ENERGY_SAPPER,
         PropertyFunction.WHILE_EQUIPPED,
         "Has a 20% chance every second to consume some of the user's Hunger in order to replenish the item's durability by 1 point, as long as the item has 10 or more points of durability consumed."
      );
      this.addCodexFlavor(AlchemancyProperties.PARASITIC, "Not so symbiotic after all");
      this.addCodexFunction(
         AlchemancyProperties.PARASITIC,
         PropertyFunction.WHILE_EQUIPPED,
         "Has a 20% chance every second to damage the user by 1 Health Point in order to replenish the item's durability by 10 points, as long as the item has 10 or more points of durability consumed."
      );
      this.addCodexFlavor(AlchemancyProperties.SOUL_HARVESTER, "FETCH ME THEIR SOULS!");
      this.addCodexFunction(
         AlchemancyProperties.SOUL_HARVESTER,
         PropertyFunction.MODIFY_DAMAGE,
         "Reduces damage dealt by 25%. Increases damage proportional to the target's missing health, with a maximum increase of 55% of the attack's original damage."
      );
      this.addCodexFunction(AlchemancyProperties.SOUL_HARVESTER, PropertyFunction.ON_KILL, "Heals the user for 10% of the target's {attribute Max Health}.");
      this.addCodexFlavor(AlchemancyProperties.HUNGERING, "Ever-famished");
      this.addCodexFunction(AlchemancyProperties.HUNGERING, PropertyFunction.WHILE_HELD, "Instantly consumes the item if it can be eaten.");
      this.addCodexFunction(
         AlchemancyProperties.HUNGERING,
         PropertyFunction.WHILE_EQUIPPED,
         "Constantly attempts to consume items in the user's inventory if they're hungry enough for them to take full-effect."
      );
      this.addCodexFlavor(AlchemancyProperties.LIGHT_SEEKING, "Like a moth to the flame");
      this.addCodexFunction(
         AlchemancyProperties.LIGHT_SEEKING,
         PropertyFunction.WHEN_SHOT,
         "Homes in on the nearest entity on fire or with the Glowing effect in a 24-block radius."
      );
      this.addCodexFunction(
         AlchemancyProperties.LIGHT_SEEKING,
         PropertyFunction.WHEN_DROPPED,
         "Pulls itself towards the nearest entity on fire or with the Glowing effect in a 24-block radius."
      );
      this.addCodexFlavor(AlchemancyProperties.INFUSION_CODEX, "Quite the inception");
      this.addCodexFunction(AlchemancyProperties.INFUSION_CODEX, PropertyFunction.WHEN_USED, "Opens the {item Infusion Codex}'s index menu.");
      this.addCodexFunction(
         AlchemancyProperties.INFUSION_CODEX,
         PropertyFunction.STACKED_OVER,
         "Inspects the targeted item, opening an {item Infusion Codex} index menu filtered to only show its applied {system Infusions}, {system Innate Properties}, and {property alchemancy:revealed} or {property alchemancy:awakened} {system Properties}"
      );
      this.addCodexFlavor(AlchemancyProperties.ETERNAL_GLOW, "Light up the skies");
      this.addCodexFunction(
         AlchemancyProperties.ETERNAL_GLOW,
         PropertyFunction.WHILE_EQUIPPED,
         "Automatically place down a {item Glowing Orb} at your feet when the light level is low enough."
      );
      this.addCodexFlavor(AlchemancyProperties.PHASE_STEP, "Physics are but a mere suggestion");
      this.addCodexFunction(
         AlchemancyProperties.PHASE_STEP,
         PropertyFunction.WHILE_EQUIPPED,
         "Grants you the ability to phase through blocks, including those below you. Use with caution."
      );
      this.addCodexFlavor(AlchemancyProperties.FRIENDLY, "Friendly-fire is overrated anyways");
      this.addCodexFunction(
         AlchemancyProperties.FRIENDLY, PropertyFunction.WHILE_EQUIPPED, "Prevents your attacks from damaging Passive Mobs and other Players."
      );
      this.addCodexFlavor(AlchemancyProperties.DEATH_WARD, "Born again!");
      this.addCodexFunction(
         AlchemancyProperties.DEATH_WARD,
         PropertyFunction.WHILE_EQUIPPED,
         "Saves the user from dying at the cost of 500 durability points, or the item itself."
      );
      this.addCodexFunction(AlchemancyProperties.DEATH_WARD, PropertyFunction.OTHER, "Makes the item non-stackable.");
      this.addCodexFlavor(AlchemancyProperties.ROCKET_POWERED, "Pchooooo!");
      this.addCodexFunction(
         AlchemancyProperties.ROCKET_POWERED,
         PropertyFunction.WHILE_USING,
         "Propels the user forwards at high speeds, consuming 2 durability points or the item itself every second."
      );
      this.addCodexFunction(
         AlchemancyProperties.ROCKET_POWERED,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Propels the user upwards at high speeds while jumping, consuming 2 durability points or the item itself every second."
      );
      this.addCodexFlavor(AlchemancyProperties.WAYWARD_WARP, "Always skip the way back");
      this.addCodexFunction(
         AlchemancyProperties.WAYWARD_WARP,
         PropertyFunction.WHEN_USED_BLOCK,
         "Saves the targeted {item Lodestone}'s position as a destination if none is present."
      );
      this.addCodexFunction(
         AlchemancyProperties.WAYWARD_WARP, PropertyFunction.WHEN_USED_ENTITY, "Saves the targeted Player as a destination if none is present."
      );
      this.addCodexFunction(
         AlchemancyProperties.WAYWARD_WARP,
         PropertyFunction.AFTER_USE,
         "Teleports the user to the saved destination if on the same dimension, consuming 10 durability points or the item in the process."
      );
      this.addCodexFunction(
         AlchemancyProperties.WAYWARD_WARP,
         PropertyFunction.ACTIVATE,
         "Teleports the target to the saved destination if on the same dimension, consuming 10 durability points or the item in the process."
      );
      this.addCodexFunction(
         AlchemancyProperties.WAYWARD_WARP,
         PropertyFunction.OTHER,
         "If {property alchemancy:death_tracker} is present, the saved destination will instead be overridden by the user's last point of death, increasing the teleportation's durability cost up to 500."
      );
      this.addCodexFlavor(AlchemancyProperties.FLAME_STEP, "Like TRON, but for arsonists");
      this.addCodexFunction(
         AlchemancyProperties.FLAME_STEP,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Creates a trail of short-lasting {item Fire} while sprinting. Extends the duration of {item Fire} under the player while standing still."
      );
      this.addCodexFlavor(AlchemancyProperties.BINDING, "Frost Jailer's signature move");
      this.addCodexFunction(
         AlchemancyProperties.BINDING,
         PropertyFunction.STACKED_OVER,
         "Applies or removes {property alchemancy:unmovable} from the target item, preventing it from being dropped or moved into a different slot."
      );
      this.addCodexFlavor(AlchemancyProperties.UNMOVABLE, "Locked into place");
      this.addCodexFunction(
         AlchemancyProperties.UNMOVABLE,
         PropertyFunction.OTHER,
         "Prevents the item from being dropped or moved into a different slot. Can be removed by stacking an item with {property alchemancy:binding} over this one."
      );
      this.addCodexFlavor(AlchemancyProperties.INFUSION_CLEANSE, "The most absorbent material I've ever used");
      this.addCodexFunction(AlchemancyProperties.INFUSION_CLEANSE, PropertyFunction.STACKED_OVER, "Removes all {system Infusions} from the target item.");
      this.addCodexFlavor(AlchemancyProperties.DIVINE_CLEANSE, "Evil begone!");
      this.addCodexFunction(
         AlchemancyProperties.DIVINE_CLEANSE,
         PropertyFunction.STACKED_OVER,
         "When stacked over another item, removes {property_list alchemancy:affected_by_divine_cleanse} from the target item."
      );
      this.addCodexFlavor(AlchemancyProperties.FLAME_EMPEROR, "The air is getting warmer around you");
      this.addCodexFunction(
         AlchemancyProperties.FLAME_EMPEROR, PropertyFunction.WHILE_HELD_MAINHAND, "Doubles the user's {attribute Mining Speed} while on fire."
      );
      this.addCodexFunction(
         AlchemancyProperties.FLAME_EMPEROR,
         PropertyFunction.MODIFY_DAMAGE,
         "Increases damage by 25%, with a limit of 3 times the attack's base damage, while the user is on fire."
      );
      this.addCodexFunction(
         AlchemancyProperties.FLAME_EMPEROR, PropertyFunction.ON_ATTACK, "Sets the target on fire for 3 seconds, or 6 seconds if the user is also on fire."
      );
      this.addCodexFunction(AlchemancyProperties.FLAME_EMPEROR, PropertyFunction.BLOCK_DESTROYED, "Smelts all dropped items while the user is on fire.");
      this.addCodexFlavor(AlchemancyProperties.GUST_JET, "Blow their socks off");
      this.addCodexFunction(
         AlchemancyProperties.GUST_JET,
         PropertyFunction.WHILE_USING,
         "Blows the user backwards unless they're crouching while standing on solid ground. Knocks all entities in front of the user back, with a 20% chance every tick of triggering {function on_attack} effects on them. Consumes 1 durability point or the item itself every 2 seconds."
      );
      this.addCodexFunction(
         AlchemancyProperties.GUST_JET,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Blows the user a moderate distance away from the ground.. Knocks all entities under the user back, with a 20% chance every tick of triggering {function on_attack} effects on them. Consumes 1 durability point or the item itself every 2 seconds."
      );
      this.addCodexFlavor(AlchemancyProperties.BLINKING, "Quick on our feet, hard to defeat!");
      this.addCodexFunction(
         AlchemancyProperties.BLINKING,
         PropertyFunction.WHILE_WORN,
         "Phase up to 10 blocks forward after initiating a sprint, consuming 5 durability points or the item itself. Up to 3 blinks can be performed in succession before touching the ground."
      );
      this.addCodexFunction(
         AlchemancyProperties.BLINKING,
         PropertyFunction.ACTIVATE,
         "The user phases up to 10 blocks in the direction they're looking, consuming 5 durability points or the item itself. Up to 3 blinks can be performed in succession before touching the ground."
      );
      this.addCodexFlavor(AlchemancyProperties.VAULTPICKING, "Hack the system");
      this.addCodexFunction(
         AlchemancyProperties.VAULTPICKING,
         PropertyFunction.WHEN_USED_BLOCK,
         "Resets a {item Trial Vault}'s inner mechanisms at the cost of 50 durability points or the item, allowing only the user to insert another key and gather additional loot."
      );
      this.addCodexFlavor(AlchemancyProperties.HOME_RUN, "And the crowd goes wild!");
      this.addCodexFunction(
         AlchemancyProperties.HOME_RUN,
         PropertyFunction.MODIFY_DAMAGE,
         "Knocks the target back a great distance if the attack deals at least 80% of the user's {attribute Attack Damage} stat, consuming 10 durability points or the item itself."
      );
      this.addCodexFunction(
         AlchemancyProperties.HOME_RUN,
         PropertyFunction.ACTIVATE,
         "Knocks the user away from the surface they're looking at if activated on self, consuming 10 durability points or the item itself."
      );
      this.addCodexFunction(AlchemancyProperties.HOME_RUN, PropertyFunction.ATTRIBUTE_MODIFIER, "Sets the item's base {attribute Attack Speed} to 0.2");
      this.addCodexFlavor(AlchemancyProperties.ITEM_PULL, "Great for mob farms and loot goblins");
      this.addCodexFunction(
         AlchemancyProperties.ITEM_PULL,
         PropertyFunction.WHILE_EQUIPPED,
         "Attempts to pick up all dropped items in a 5-block radius. If {property alchemancy:hollow} is present, only items that match the one being stored will be picked up."
      );
      this.addCodexFunction(
         AlchemancyProperties.ITEM_PULL,
         PropertyFunction.WHEN_SHOT,
         "Teleports all dropped items in a 5-block radius to itself. If {property alchemancy:hollow} is present, only items that match the one being stored will be picked up."
      );
      this.addCodexFunction(
         AlchemancyProperties.ITEM_PULL,
         PropertyFunction.WHEN_DROPPED,
         "Teleports all dropped items in a 5-block radius to itself. If {property alchemancy:hollow} is present, only items that match the one being stored will be picked up."
      );
      this.addCodexFunction(
         AlchemancyProperties.ITEM_PULL,
         PropertyFunction.WHILE_ROOTED,
         "Teleports all dropped items in a 5-block radius to itself. If {property alchemancy:hollow} is present, only items that match the one being stored will be picked up."
      );
      this.addCodexFlavor(AlchemancyProperties.CLOUD_DASH, "Great for climbing mountains");
      this.addCodexFunction(
         AlchemancyProperties.CLOUD_DASH,
         PropertyFunction.WHILE_EQUIPPED,
         "Allows the user to dash forwards in the direction they're facing at the start of a sprint, consuming 2 points of durability. Dash distance is influenced by the user's {attribute Movement Speed}. Only one dash can be performed before touching the ground."
      );
      this.addCodexFlavor(AlchemancyProperties.CRYSTAL_DASH, "Fully realized");
      this.addCodexFunction(
         AlchemancyProperties.CRYSTAL_DASH,
         PropertyFunction.WHILE_EQUIPPED,
         "Allows the user to dash forwards with great speed in the direction they're facing at the start of a sprint, consuming 2 points of durability. Dash distance is influenced by the user's {attribute Movement Speed}. Up to 2 dashes can be performed in succession before touching the ground."
      );
      this.addCodexFlavor(AlchemancyProperties.VOIDBORN, "The abyss stares back");
      this.addCodexFunction(
         AlchemancyProperties.VOIDBORN,
         PropertyFunction.WHILE_EQUIPPED,
         "Grants immunity to Void damage and {property alchemancy:voidtouch}. Levitates the user upwards for 10 seconds after falling into the void."
      );
      this.addCodexFunction(
         AlchemancyProperties.VOIDBORN, PropertyFunction.WHEN_DROPPED, "Levitates the item upwards for for 10 seconds after falling into the void."
      );
      this.addCodexFunction(
         AlchemancyProperties.VOIDBORN,
         PropertyFunction.OTHER,
         "This {system Infusion} can be obtained by tossing an item with {property alchemancy:depth_dweller} and {property alchemancy:undying} into the void."
      );
      this.addCodexFlavor(AlchemancyProperties.VOIDTOUCH, "Existence is but an opinion");
      this.addCodexFunction(
         AlchemancyProperties.VOIDTOUCH, PropertyFunction.ON_ATTACK, "Deletes the target from existence, completely destroying the item in the process."
      );
      this.addCodexFlavor(AlchemancyProperties.KINETIC_GRAB, "Wanna have a bad time?");
      this.addCodexFunction(
         AlchemancyProperties.KINETIC_GRAB,
         PropertyFunction.WHEN_USED_ENTITY,
         "Holds the target entity in front of the user for as long as the item is used or until the user takes damage."
      );
      this.addCodexFlavor(AlchemancyProperties.VACUUMING, "An attractive personality");
      this.addCodexFunction(AlchemancyProperties.VACUUMING, PropertyFunction.WHILE_USING, "Slowly pulls in ALL entities within an 8-block radius.");
      this.addCodexFunction(AlchemancyProperties.VACUUMING, PropertyFunction.WHEN_SHOT, "Slowly pulls in all entities within an 8-block radius.");
      this.addCodexFunction(AlchemancyProperties.VACUUMING, PropertyFunction.WHEN_DROPPED, "Slowly pulls in all entities within an 8-block radius.");
      this.addCodexFunction(
         AlchemancyProperties.VACUUMING, PropertyFunction.WHILE_ROOTED, "Slowly pulls in all entities except for Players within an 8-block radius."
      );
      this.addCodexFlavor(AlchemancyProperties.WARPED, "A glimpse into my twisted mind");
      this.addCodexFunction(
         AlchemancyProperties.WARPED,
         PropertyFunction.OTHER,
         "Alters every compatible {system Infusion} on the item at the end of the {system Infusion Process}, often turning them into an opposite, or related counterpart."
      );
      this.addCodexFlavor(AlchemancyProperties.DIRTY, "Filthy");
      this.addCodexFunction(AlchemancyProperties.DIRTY, PropertyFunction.OTHER, "It's dirt. Get it off, ew.");
      this.addCodexFlavor(AlchemancyProperties.AWKWARD, "A tad strange");
      this.addCodexFunction(
         AlchemancyProperties.AWKWARD, PropertyFunction.OTHER, "Can be used create various new {system Infusions} related to Potion Effects."
      );
      this.addCodexFlavor(AlchemancyProperties.LIMIT_BREAK, "This is to go even further beyond!");
      this.addCodexFunction(AlchemancyProperties.LIMIT_BREAK, PropertyFunction.ATTRIBUTE_MODIFIER, "Grants an additional {system Infusion Slot}");
      this.addCodexFlavor(AlchemancyProperties.AWAKENED, "Unlock your True Potential");
      this.addCodexFunction(
         AlchemancyProperties.AWAKENED,
         PropertyFunction.OTHER,
         "Makes {system Dormant Properties} act as if they were {system Infused} onto the item, triggering all related effects."
      );
      this.addCodexFlavor(AlchemancyProperties.PARADOXICAL, "Quite the conundrum");
      this.addCodexFunction(
         AlchemancyProperties.PARADOXICAL,
         PropertyFunction.OTHER,
         "Prevents {system Property Interactions} from affecting the item during the {system Infusion} process."
      );
      this.addCodexFlavor(AlchemancyProperties.REVEALED, "Know what's coming");
      this.addCodexFunction(
         AlchemancyProperties.REVEALED,
         PropertyFunction.VISUAL,
         "Shows the item's {system Dormant Properties} in its tooltip, making it easier to distinguish what {system Infusions} the item will provide."
      );
      this.addCodexFlavor(AlchemancyProperties.REVEALING, "An Alchemancer's greatest asset");
      this.addCodexFunction(
         AlchemancyProperties.REVEALING,
         PropertyFunction.WHILE_WORN_HELMET,
         "Shows every item's {system Dormant Properties} in their tooltips, making it easier to distinguish what {system Infusions} the item will provide."
      );
      this.addCodexFlavor(AlchemancyProperties.SCRAMBLED, "§kPractically illegible");
      this.addCodexFunction(
         AlchemancyProperties.SCRAMBLED,
         PropertyFunction.VISUAL,
         "Obfuscates the item's tooltip, making its important traits and information hard to make out."
      );
      this.addCodexFlavor(AlchemancyProperties.TINTED, "Bring color to the world");
      this.addCodexFunction(
         AlchemancyProperties.TINTED,
         PropertyFunction.WHEN_USED_BLOCK,
         "If used on a water-filled {item Cauldron}, the item's tint will be cleared, removing this {system Infusion} in the process."
      );
      this.addCodexFunction(AlchemancyProperties.TINTED, PropertyFunction.VISUAL, "Applies a colored tint to the item.");
      this.addCodexFunction(
         AlchemancyProperties.TINTED,
         PropertyFunction.OTHER,
         "When a {item Dye} is {system Infused} onto an item, its color will be blended with the item's current tint. If a {item Chroma Lens} is used instead, the item's tint will be overridden without consuming the lens. When multiple {item Chroma Lenses} are {system Infused} at a time, the item's tint color will gradually cycle through each of the {system Infused} colors."
      );
      this.addCodexFlavor(AlchemancyProperties.SEETHROUGH, "Partially invisible");
      this.addCodexFunction(AlchemancyProperties.SEETHROUGH, PropertyFunction.VISUAL, "Makes the item visually transparent.");
      this.addCodexFlavor(AlchemancyProperties.CONCEALED, "Completely invisible");
      this.addCodexFunction(AlchemancyProperties.CONCEALED, PropertyFunction.VISUAL, "Prevents the item from rendering outside of the inventory screen.");
      this.addCodexFlavor(AlchemancyProperties.DISGUISED, "Intruder alert! Red Spy is in the base");
      this.addCodexFunction(
         AlchemancyProperties.DISGUISED,
         PropertyFunction.STACKED_OVER,
         "Sets the item's disguise to the one being stacked on top of it if no disguise has been assigned yet."
      );
      this.addCodexFunction(
         AlchemancyProperties.DISGUISED,
         PropertyFunction.WHEN_USED,
         "While crouching, and if no disguise has been set yet, sets the item's disguise to the one in the user's offhand."
      );
      this.addCodexFunction(AlchemancyProperties.DISGUISED, PropertyFunction.VISUAL, "Allows the item to take up the appearance of any other item.");
      this.addCodexFlavor(AlchemancyProperties.FLATTENED, "Great for starting a poster collection");
      this.addCodexFunction(
         AlchemancyProperties.FLATTENED,
         PropertyFunction.WHEN_USED_BLOCK,
         "Places the item onto a solid surface, which can then be picked back up by punching it."
      );
      this.addCodexFunction(AlchemancyProperties.FLATTENED, PropertyFunction.VISUAL, "Makes the item appear completely flat.");
      this.addCodexFlavor(AlchemancyProperties.SPARKLING, "Pretty particles");
      this.addCodexFunction(
         AlchemancyProperties.SPARKLING,
         PropertyFunction.WHILE_EQUIPPED,
         "Causes the user to emit an aura of particles. Some {system Infusions} disable this behavior, but instead have their own effects be overridden."
      );
      this.addCodexFunction(
         AlchemancyProperties.SPARKLING,
         PropertyFunction.OTHER,
         "The particles emitted can be determined by the {system Infusions} added to the source of {property alchemancy:sparkling} when {system Infused}."
      );
      this.addCodexFlavor(AlchemancyProperties.NONLETHAL, "As deadly as a pillow");
      this.addCodexFunction(
         AlchemancyProperties.NONLETHAL,
         PropertyFunction.MODIFY_DAMAGE,
         "Negates all damage dealt by this item, while still triggering {function on_attack} effects."
      );
      this.addCodexFlavor(AlchemancyProperties.FLIMSY, "Barely sturdier than a wet noodle");
      this.addCodexFunction(AlchemancyProperties.FLIMSY, PropertyFunction.OTHER, "Makes the item incapable of breaking blocks.");
      this.addCodexFlavor(AlchemancyProperties.DEAD, "A husk of its former self");
      this.addCodexFunction(
         AlchemancyProperties.DEAD,
         PropertyFunction.OTHER,
         "Disables most of the item's base attributes and abilities, including {attribute Attribute Modifiers}, block-breaking efficiencies, right-click functionality, food effects, and {system Innate Properties}. Does not negate the effects of {system Infusions}"
      );
      this.addCodexFlavor(AlchemancyProperties.UNDEAD, "Nearly almost dead but not quite");
      this.addCodexFunction(
         AlchemancyProperties.UNDEAD,
         PropertyFunction.DURABILITY_CONSUMED,
         "Damaging the item restores durability points instead of consuming them, breaking once the item gets fully repaired."
      );
      this.addCodexFlavor(AlchemancyProperties.INFECTED, "You don't want it on your lawn");
      this.addCodexFunction(
         AlchemancyProperties.INFECTED,
         PropertyFunction.WHILE_IN_INVENTORY,
         "Slowly spreads itself onto other items in the user's inventory. Has a 0.2% chance every second to turn into {property alchemancy:dead}."
      );
      this.addCodexFunction(AlchemancyProperties.INFECTED, PropertyFunction.ON_ATTACK, "Spreads itself onto a random item in the target's inventory.");
      this.addCodexFunction(AlchemancyProperties.INFECTED, PropertyFunction.VISUAL, "Tints the item with an ugly color.");
      this.addCodexFlavor(AlchemancyProperties.SANITIZED, "Squeaky clean");
      this.addCodexFunction(AlchemancyProperties.SANITIZED, PropertyFunction.OTHER, "Prevents the item from becoming {property alchemancy:infected}.");
      this.addCodexFlavor(AlchemancyProperties.STICKY, "It just won't come off");
      this.addCodexFunction(
         AlchemancyProperties.STICKY,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Grants full knockback immunity while grounded. Reduces {attribute Movement Speed} by 25%. Greatly reduces the user's ability to jump while grounded."
      );
      this.addCodexFunction(AlchemancyProperties.STICKY, PropertyFunction.WHILE_WORN_CHESTPLATE, "Allows the user to stick to walls.");
      this.addCodexFunction(AlchemancyProperties.STICKY, PropertyFunction.WHILE_WORN_LEGGINGS, "Allows the user to stick to walls.");
      this.addCodexFunction(AlchemancyProperties.STICKY, PropertyFunction.WHILE_WORN_HELMET, "Allows the user to stick to ceilings.");
      this.addCodexFunction(AlchemancyProperties.STICKY, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Prevents the item from being dispensed.");
      this.addCodexFunction(
         AlchemancyProperties.STICKY,
         PropertyFunction.WHILE_ROOTED,
         "Greatly hinders the movement of all entities standing inside of the item, as if they were walking through {item Cobweb}."
      );
      this.addCodexFunction(AlchemancyProperties.STICKY, PropertyFunction.OTHER, "Prevents the item from being dropped.");
      this.addCodexFlavor(AlchemancyProperties.DEXTEROUS, "Acrobatics? Sleight of Hand? You got it");
      this.addCodexFunction(AlchemancyProperties.DEXTEROUS, PropertyFunction.WHILE_EQUIPPED, "Negates the {attribute Movement Speed} penalty from using items.");
      this.addCodexFlavor(AlchemancyProperties.CHROMATIZE, "Taste the rainbow!");
      this.addCodexFunction(
         AlchemancyProperties.CHROMATIZE,
         PropertyFunction.ACTIVATE,
         "Applies a colored tint to the target equal to the item's {property alchemancy:tinted} color."
      );
      this.addCodexFunction(
         AlchemancyProperties.CHROMATIZE,
         PropertyFunction.WHEN_SHOT,
         "When hitting an entity, applies a colored tint to the target equal to the item's {property alchemancy:tinted} color."
      );
      this.addCodexFlavor(AlchemancyProperties.TINTED_LENS, "Rose-colored glasses");
      this.addCodexFunction(
         AlchemancyProperties.TINTED_LENS,
         PropertyFunction.WHILE_WORN_HELMET,
         "Applies a colored tint to the user's vision equal to the item's {property alchemancy:tinted} color. Prevents Endermen from attacking the user after being looked at in the eyes."
      );
      this.addCodexFlavor(AlchemancyProperties.ROTATING, "Get rotated, idiot");
      this.addCodexFunction(AlchemancyProperties.ROTATING, PropertyFunction.WHEN_USED_BLOCK, "Rotates the facing direction of the targeted block if possible.");
      this.addCodexFunction(AlchemancyProperties.ROTATING, PropertyFunction.WHEN_USED_ENTITY, "Rotates the target.");
      this.addCodexFunction(AlchemancyProperties.ROTATING, PropertyFunction.VISUAL, "Makes the item rotate constantly.");
      this.addCodexFlavor(AlchemancyProperties.WAVE_RIDER, "I Think I saw a religious figure do this once");
      this.addCodexFunction(
         AlchemancyProperties.WAVE_RIDER,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Allows the user to stand, jump, and walk on {item Water}, unless they're crouching."
      );
      this.addCodexFunction(AlchemancyProperties.WAVE_RIDER, PropertyFunction.WHEN_DROPPED, "Makes the item rest on top of water as if it were on the ground.");
      this.addCodexFlavor(AlchemancyProperties.AIR_WALKER, "Think coyote time but without a time limit");
      this.addCodexFunction(
         AlchemancyProperties.AIR_WALKER,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Creates a platform of solid light under the user's feet, preventing them from falling down after walking off of a ledge. Crouching lowers the height of the platform."
      );
      this.addCodexFlavor(AlchemancyProperties.ATHLETIC, "Faster than a speeding bullet. Able to leap tall buildings in a single bound.");
      this.addCodexFunction(
         AlchemancyProperties.ATHLETIC,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Increases {attribute Movement Speed} and {attribute Jump Height} by 65% and {attribute Safe Fall Distance} by 3 blocks while sprinting."
      );
      this.addCodexFunction(AlchemancyProperties.ATHLETIC, PropertyFunction.WHILE_WORN, "Reduces the amount of hunger lost from moving by 10%.");
      this.addCodexFlavor(AlchemancyProperties.KINETIC_RECHARGE, "Just keep moving!");
      this.addCodexFunction(
         AlchemancyProperties.KINETIC_RECHARGE,
         PropertyFunction.WHILE_EQUIPPED,
         "Restores the item's durability while the user is in motion. The faster the user is going, the faster the item is repaired."
      );
      this.addCodexFlavor(AlchemancyProperties.LAZY, "...Perhaps another day");
      this.addCodexFunction(AlchemancyProperties.LAZY, PropertyFunction.WHILE_EQUIPPED, "Prevents the user from picking up dropped Items.");
      this.addCodexFunction(AlchemancyProperties.LAZY, PropertyFunction.WHEN_DROPPED, "Doubles the item's pickup delay, making it take longer to pick up.");
      this.addCodexFunction(AlchemancyProperties.LAZY, PropertyFunction.WHEN_SHOT, "Causes the projectile to slow down over time.");
      this.addCodexFlavor(AlchemancyProperties.ANCHORED, "Hold Still");
      this.addCodexFunction(AlchemancyProperties.ANCHORED, PropertyFunction.WHILE_WORN, "Holds the user in place, making them nearly impossible to move.");
      this.addCodexFunction(AlchemancyProperties.ANCHORED, PropertyFunction.WHEN_DROPPED, "Holds the item in place, making it nearly impossible to move.");
      this.addCodexFunction(AlchemancyProperties.ANCHORED, PropertyFunction.WHEN_SHOT, "Holds the projectile in place, making it nearly impossible to move.");
      this.addCodexFlavor(AlchemancyProperties.HOLLOW, "Store items within your items");
      this.addCodexFunction(
         AlchemancyProperties.HOLLOW, PropertyFunction.PICK_UP_WHILE_EQUIPPED, "Stores the picked up item if enough space is present for it."
      );
      this.addCodexFunction(AlchemancyProperties.HOLLOW, PropertyFunction.WHEN_USED, "Drops onto the ground any stored items.");
      this.addCodexFunction(
         AlchemancyProperties.HOLLOW,
         PropertyFunction.STACKED_OVER,
         "Stores the stacked item if enough space is available. Retrieves any stored items when right-clicked with an empty cursor."
      );
      this.addCodexFunction(
         AlchemancyProperties.HOLLOW, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Dispenses any stored items first before dispensing itself."
      );
      this.addCodexFunction(
         AlchemancyProperties.HOLLOW,
         PropertyFunction.WHILE_ROOTED,
         "When right-clicked, stores the user's held item if enough space is available. When right-clicked, retrieves the contents of the item."
      );
      this.addCodexFunction(
         AlchemancyProperties.HOLLOW, PropertyFunction.OTHER, "Allows the item to store up to one stack of a single item. Makes the item non-stackable."
      );
      this.addCodexFlavor(AlchemancyProperties.BUCKETING, "This... is a bucket");
      this.addCodexFunction(
         AlchemancyProperties.BUCKETING,
         PropertyFunction.WHEN_USED_BLOCK,
         "Picks up the targeted liquid if the item is empty. If not, then the liquid is instead placed down."
      );
      this.addCodexFunction(
         AlchemancyProperties.BUCKETING, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Dispenses the stored liquid first before dispensing itself."
      );
      this.addCodexFunction(
         AlchemancyProperties.BUCKETING,
         PropertyFunction.OTHER,
         "Allows the item to store up to one {item Bucket} of a single liquid. Makes the item non-stackable."
      );
      this.addCodexFlavor(AlchemancyProperties.ENCAPSULATING, "All you have to do is Carry On.");
      this.addCodexFunction(
         AlchemancyProperties.ENCAPSULATING,
         PropertyFunction.WHEN_USED_BLOCK,
         "Picks up and stores the targeted block if the item is empty. If not, then the stored block is instead placed down."
      );
      this.addCodexFunction(
         AlchemancyProperties.ENCAPSULATING, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Places the stored block down first before dispensing itself."
      );
      this.addCodexFunction(
         AlchemancyProperties.ENCAPSULATING,
         PropertyFunction.OTHER,
         "Allows the item to store a single block inside of itself, keeping all of its data when placed back down. Makes the item non-stackable."
      );
      this.addCodexFlavor(AlchemancyProperties.CAPTURING, "I Choose you!");
      this.addCodexFunction(
         AlchemancyProperties.CAPTURING,
         PropertyFunction.WHEN_USED_ENTITY,
         "Picks up and captures the targeted entity if the item is empty. If not, then the captured is instead released."
      );
      this.addCodexFunction(
         AlchemancyProperties.CAPTURING,
         PropertyFunction.WHEN_SHOT,
         "Releases the captured entity on impact. If empty, captures the impacted entity and drops the item onto the ground."
      );
      this.addCodexFunction(
         AlchemancyProperties.CAPTURING, PropertyFunction.WHEN_SHOT_FROM_DISPENSER, "Releases the stored entity first before dispensing itself."
      );
      this.addCodexFunction(
         AlchemancyProperties.CAPTURING, PropertyFunction.OTHER, "Allows the item to store a single entity inside of itself. Makes the item non-stackable."
      );
      this.addCodexFlavor(AlchemancyProperties.COMPACT, "Store more items per item");
      this.addCodexFunction(
         AlchemancyProperties.COMPACT,
         PropertyFunction.OTHER,
         "Multiplies the item's max stack size by 4, with a hard limit of 96. Some items with stored entities or items, such as {item Shulker Boxes} and {item Beehives} may negate this effect."
      );
      this.addCodexFlavor(AlchemancyProperties.DRIPPING, "You might want to call a plumber");
      this.addCodexFunction(
         AlchemancyProperties.DRIPPING,
         PropertyFunction.ON_ATTACK,
         "Releases the contents of {property alchemancy:hollow}, {property alchemancy:bucketing}, {property alchemancy:encapsulating}, and {property alchemancy:capturing}, on the target's position."
      );
      this.addCodexFunction(
         AlchemancyProperties.DRIPPING,
         PropertyFunction.RECEIVE_DAMAGE_WORN,
         "Releases the contents of {property alchemancy:hollow}, {property alchemancy:bucketing}, {property alchemancy:encapsulating}, and {property alchemancy:capturing}, on the user's position."
      );
      this.addCodexFlavor(AlchemancyProperties.ABSORBING, "shlorp");
      this.addCodexFunction(
         AlchemancyProperties.ABSORBING,
         PropertyFunction.WHILE_EQUIPPED,
         "Immediately picks up any items on the ground, completely bypassing their pickup delay. Automatically absorbs any materials that can be used to repair the item in the user's inventory, replenishing the item's durability in the process. If {property alchemancy:smelting} or {property alchemancy:waxed} are present, corresponding materials will be absorbed to replenish their uses. Picks up any liquids the user may be inside of if {property alchemancy:bucketing} is present and empty."
      );
      this.addCodexFunction(
         AlchemancyProperties.ABSORBING,
         PropertyFunction.STACKED_OVER,
         "Absorbs materials stacked over the item that cam be used to repair it to replenish its durability."
      );
      this.addCodexFlavor(AlchemancyProperties.EDIBLE, "Mmmm... tasty!");
      this.addCodexFunction(
         AlchemancyProperties.EDIBLE,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "If the item can already be eaten, its Nutrition value is increased by 50% and its saturation gain by 25%."
      );
      this.addCodexFunction(
         AlchemancyProperties.EDIBLE,
         PropertyFunction.WHEN_USED,
         "Allows the item to be eaten, {activate Activating} on the user and consuming 10 durability points or the item in its entirety."
      );
      this.addCodexFlavor(AlchemancyProperties.JAGGED, "Quite the thorn on my side");
      this.addCodexFunction(
         AlchemancyProperties.JAGGED,
         PropertyFunction.WHEN_HIT_WORN_OR_USING,
         "Damages the attacker equal to the the item's {attribute Attack Damage}, triggering {function modify_damage} effects."
      );
      this.addCodexFunction(
         AlchemancyProperties.JAGGED,
         PropertyFunction.PICK_UP,
         "Damages the user equal to the the item's {attribute Attack Damage}, triggering {function modify_damage} effects."
      );
      this.addCodexFlavor(AlchemancyProperties.ROOTED, "Plant your roots");
      this.addCodexFunction(
         AlchemancyProperties.ROOTED,
         PropertyFunction.WHEN_USED_BLOCK,
         "Places the item down as a {block Rooted Item} on top of any valid soil, constantly triggering {function while_rooted_unformatted} effects while planted."
      );
      this.addCodexFunction(AlchemancyProperties.ROOTED, PropertyFunction.WHILE_ROOTED, "Repairs the item for 1 durability point every 5 seconds.");
      this.addCodexFlavor(AlchemancyProperties.SENSITIVE, "Try not to hurt its feelings");
      this.addCodexFunction(
         AlchemancyProperties.SENSITIVE,
         PropertyFunction.WHILE_EQUIPPED,
         "{activate Activates} on the user if any entity is within a 1-block radius of the user, going into Cooldown for 5 seconds after doing so."
      );
      this.addCodexFunction(
         AlchemancyProperties.SENSITIVE, PropertyFunction.WHILE_ROOTED, "{activate Activates} on itself every second if an entity is standing on the item."
      );
      this.addCodexFunction(
         AlchemancyProperties.SENSITIVE,
         PropertyFunction.WHEN_SHOT,
         "{activate Activates} on itself if any entity is within a 1-block radius of the projectile, destroying itself after doing so."
      );
      this.addCodexFlavor(AlchemancyProperties.INTERACTABLE, "Clickity-click");
      this.addCodexFunction(
         AlchemancyProperties.INTERACTABLE,
         PropertyFunction.WHEN_USED,
         "{activate Activates} the item on the user, going into Cooldown for 1 second after doing so."
      );
      this.addCodexFunction(
         AlchemancyProperties.INTERACTABLE,
         PropertyFunction.WHEN_USED_ENTITY,
         "{activate Activates} the item on the target, going into Cooldown for 1 second after doing so."
      );
      this.addCodexFunction(AlchemancyProperties.INTERACTABLE, PropertyFunction.WHILE_ROOTED, "{activate Activates} the item on the user when right-clicked.");
      this.addCodexFunction(
         AlchemancyProperties.INTERACTABLE,
         PropertyFunction.WHEN_SHOT_FROM_DISPENSER,
         "{activate Activates} the item on the entity in front of the {item Dispenser} if any is present."
      );
      this.addCodexFunction(AlchemancyProperties.INTERACTABLE, PropertyFunction.WHEN_SHOT, "{activate Activates} the item on the impacted entity.");
      this.addCodexFlavor(AlchemancyProperties.MYCELLIC, "The fungus among us");
      this.addCodexFunction(
         AlchemancyProperties.MYCELLIC,
         PropertyFunction.ACTIVATE,
         "Extends the item's {activate Activation} effects to all entities within a 4-block radius, consuming 50 durability points or the item itself."
      );
      this.addCodexFunction(
         AlchemancyProperties.MYCELLIC,
         PropertyFunction.RECEIVE_DAMAGE_EQUIPPED,
         "{activate Activates} on all entities within a 4-block radius with a cooldown of 5 seconds, consuming 50 durability points or the item itself."
      );
      this.addCodexFlavor(AlchemancyProperties.SPORADIC, "Wacky and unpredictable");
      this.addCodexFunction(AlchemancyProperties.SPORADIC, PropertyFunction.WHILE_EQUIPPED, "{activate Activates} the item on the user at random intervals.");
      this.addCodexFunction(
         AlchemancyProperties.SPORADIC,
         PropertyFunction.WHILE_ROOTED,
         "{activate Activates} the item on the entities standing on the item at random intervals."
      );
      this.addCodexFlavor(AlchemancyProperties.SHATTERING, "Out with a bang");
      this.addCodexFunction(
         AlchemancyProperties.SHATTERING,
         PropertyFunction.ON_DESTROYED,
         "{activate Activates} the item on itself and triggers {function on_attack} effects on all entities in a 3-block radius."
      );
      this.addCodexFlavor(AlchemancyProperties.TOGGLEABLE, "Pull the lever and the lights go out");
      this.addCodexFunction(
         AlchemancyProperties.TOGGLEABLE, PropertyFunction.WHEN_USED, "Toggles the item's {system Infusions} on/off when used while crouching."
      );
      this.addCodexFunction(
         AlchemancyProperties.TOGGLEABLE, PropertyFunction.STACKED_ON, "Toggles the item's {system Infusions} on/off when right-clicked with an empty cursor."
      );
      this.addCodexFlavor(AlchemancyProperties.TICKING, "Tick tock");
      this.addCodexFunction(
         AlchemancyProperties.TICKING,
         PropertyFunction.WHILE_IN_INVENTORY,
         "{activate Activates} the item on the user after 5 seconds of being picked up. The timer is automatically reset if {property alchemancy:interactable} is present on the item. Timer length is halved if {property alchemancy:swift} is present. Timer length is doubled if {property alchemancy:sluggish} or {property alchemancy:lazy} is present."
      );
      this.addCodexFlavor(AlchemancyProperties.HYDROPHOBIC, "Really doesn't like showers");
      this.addCodexFunction(
         AlchemancyProperties.HYDROPHOBIC, PropertyFunction.WHILE_EQUIPPED, "{activate Activates} the item on the user right after entering {item Water}."
      );
      this.addCodexFunction(
         AlchemancyProperties.HYDROPHOBIC, PropertyFunction.WHEN_SHOT, "{activate Activates} the item on the projectile right after entering {item Water}."
      );
      this.addCodexFlavor(AlchemancyProperties.ALLERGIC, "Not fond of pollen season");
      this.addCodexFunction(
         AlchemancyProperties.ALLERGIC,
         PropertyFunction.WHILE_EQUIPPED,
         "{activate Activates} the item on the user when receiving a potion effect they don't already have."
      );
      this.addCodexFlavor(AlchemancyProperties.ARMOR_PULSE, "Bounce them back... Or bounce yourself back instead");
      this.addCodexFunction(
         AlchemancyProperties.ARMOR_PULSE,
         PropertyFunction.WHEN_HIT_WORN_OR_USING,
         "Reduces damage taken from blockable attacks within the user's {attribute Entity Interaction Range} by 1 point, {activate Activating} the item on the user in the process."
      );
      this.addCodexFlavor(AlchemancyProperties.RUNNING_START, "Quick feet, quick trigger");
      this.addCodexFunction(
         AlchemancyProperties.RUNNING_START,
         PropertyFunction.WHILE_EQUIPPED,
         "{activate Activates} the item on the user at the start of a sprint, going into Cooldown for 4 seconds after doing so."
      );
      this.addCodexFlavor(AlchemancyProperties.MENDING, "Gureto desu yo, koitsu wa");
      this.addCodexFunction(AlchemancyProperties.MENDING, PropertyFunction.MODIFY_DAMAGE, "Attacks heal the target instead of dealing damage.");
      this.addCodexFunction(
         AlchemancyProperties.MENDING, PropertyFunction.WHILE_ROOTED, "Heals all entities standing on the item for 2 health points every 3 seconds."
      );
      this.addCodexFunction(
         AlchemancyProperties.MENDING, PropertyFunction.WHILE_WORN, "Consumes 1 point of durability every 5 seconds to heal the user for 1 health point."
      );
      this.addCodexFunction(
         AlchemancyProperties.MENDING,
         PropertyFunction.RECEIVE_DAMAGE_WORN,
         "If the user is under 20% health, the item is destroyed to heal the user for 40% of their {attribute Max Health}."
      );
      this.addCodexFlavor(AlchemancyProperties.FLOURISH, "Blooms like a flower during spring");
      this.addCodexFunction(AlchemancyProperties.FLOURISH, PropertyFunction.WHILE_EQUIPPED, "Increases health recovery from all sources by 10% rounded up.");
      this.addCodexFlavor(AlchemancyProperties.UNDYING, "A second wind");
      this.addCodexFunction(
         AlchemancyProperties.UNDYING,
         PropertyFunction.DURABILITY_CONSUMED,
         "Restores 60% of the item's durability before breaking, consuming one use of the {system Infusion} in the process."
      );
      this.addCodexFunction(
         AlchemancyProperties.UNDYING,
         PropertyFunction.OTHER,
         "This {system Infusion} can be applied onto a single item a total of 5 times to increase its number of uses. Particularly valuable items such as the {item Totem of Undying} provide 3 additional uses."
      );
      this.addCodexFlavor(AlchemancyProperties.SWEET, "Might give you a sugar rush");
      this.addCodexFunction(AlchemancyProperties.SWEET, PropertyFunction.WHILE_EQUIPPED, "Makes Animals, Spiders, and Allays follow the user.");
      this.addCodexFunction(AlchemancyProperties.SWEET, PropertyFunction.WHILE_ROOTED, "Attracts Animals, Spiders, and Allays to the item.");
      this.addCodexFunction(
         AlchemancyProperties.SWEET,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "If the item can be eaten, it's saturation gain is increased by 50%, and it can be eaten even if the user isn't hungry."
      );
      this.addCodexFlavor(AlchemancyProperties.SCARY, "Boo!");
      this.addCodexFunction(
         AlchemancyProperties.SCARY, PropertyFunction.WHILE_WORN_HELMET, "Prevents Endermen from attack the user after being looked at in the eyes."
      );
      this.addCodexFunction(AlchemancyProperties.SCARY, PropertyFunction.WHILE_EQUIPPED, "Scares away Animals, Villagers, and Spiders.");
      this.addCodexFlavor(AlchemancyProperties.SEEDED, "Seed dispersal");
      this.addCodexFunction(AlchemancyProperties.SEEDED, PropertyFunction.ON_ATTACK, "Spreads {property alchemancy:seeded} to the target's armor.");
      this.addCodexFunction(AlchemancyProperties.SEEDED, PropertyFunction.WHILE_WORN, "Causes Chickens to become hostile towards the user.");
      this.addCodexFlavor(AlchemancyProperties.CONDUCTIVE, "Would be good to stay away from livewires for a while");
      this.addCodexFunction(
         AlchemancyProperties.CONDUCTIVE,
         PropertyFunction.RECEIVE_DAMAGE_EQUIPPED,
         "Emits a chaining {shock Electric} attack onto nearby entities for half of the damage received after taking {shock Electric} damage."
      );
      this.addCodexFunction(AlchemancyProperties.CONDUCTIVE, PropertyFunction.WHILE_EQUIPPED, "Makes the user prone to Lightning strikes during thunderstorms.");
      this.addCodexFlavor(AlchemancyProperties.CLUELESS, "huh?");
      this.addCodexFunction(
         AlchemancyProperties.CLUELESS,
         PropertyFunction.WHILE_IN_INVENTORY,
         "Resets any information stored by other {system Infusion}, such as items stored by {property alchemancy:hollow}, {property alchemancy:pristine} Points, {property alchemancy:wayfinding} target, etc. This {system Infusion} is consumed after successfully doing so."
      );
      this.addCodexFlavor(AlchemancyProperties.WISE, "Tricking a rock into thinking");
      this.addCodexFunction(AlchemancyProperties.WISE, PropertyFunction.WHILE_HELD_MAINHAND, "Doubles the experience yield from Mobs and blocks.");
      this.addCodexFunction(AlchemancyProperties.WISE, PropertyFunction.WHILE_WORN, "Increases the experience yield from Mobs and blocks by 10%.");
      this.addCodexFunction(AlchemancyProperties.WISE, PropertyFunction.ATTRIBUTE_MODIFIER, "Increases the item's {attribute Enchantability} by 5.");
      this.addCodexFlavor(AlchemancyProperties.EXPERIENCED, "Very good at what it does");
      this.addCodexFunction(
         AlchemancyProperties.EXPERIENCED,
         PropertyFunction.DURABILITY_CONSUMED,
         "Has a 30% chance to grant the user a random amount of Experience Points between 1 and the amount of durability consumed."
      );
      this.addCodexFunction(
         AlchemancyProperties.EXPERIENCED, PropertyFunction.ON_DESTROYED, "Drops Experience Points proportional to the amount of durability left on the item."
      );
      this.addCodexFlavor(AlchemancyProperties.ENCHANTING, "Both magically adept AND charismatic");
      this.addCodexFunction(
         AlchemancyProperties.ENCHANTING, PropertyFunction.ATTRIBUTE_MODIFIER, "Doubles the item's {attribute Enchantability}, or sets it to at least 20."
      );
      this.addCodexFunction(
         AlchemancyProperties.ENCHANTING, PropertyFunction.OTHER, "Makes items cost no more than 1 Experience Level to repair at an {item Anvil}."
      );
      this.addCodexFlavor(AlchemancyProperties.ENDER, "Gone in the blink of an eye");
      this.addCodexFunction(
         AlchemancyProperties.ENDER,
         PropertyFunction.WHILE_EQUIPPED,
         "Teleports the user to a random position right after entering {item Water} or making contact with rain, consuming 10 points of durability or having a 50% chance to destroy the item."
      );
      this.addCodexFunction(AlchemancyProperties.ENDER, PropertyFunction.ACTIVATE, "Teleports the target to a random position.");
      this.addCodexFunction(AlchemancyProperties.ENDER, PropertyFunction.WHEN_SHOT, "Teleports the projectile's owner to its position on impact.");
      this.addCodexFlavor(AlchemancyProperties.SCULKING, "Lurking deep below");
      this.addCodexFunction(
         AlchemancyProperties.SCULKING,
         PropertyFunction.ON_KILL,
         "Creates a {item Sculk Bud} at the target's position if possible, consuming the target's dropped Experience Points and converting them into {item Sculk}."
      );
      this.addCodexFlavor(AlchemancyProperties.LOOSE, "I don't like sand. It's coarse and it gets everywhere");
      this.addCodexFunction(
         AlchemancyProperties.LOOSE, PropertyFunction.WHEN_USED_BLOCK, "Causes blocks to fall like {item Sand} when placed for the first time."
      );
      this.addCodexFunction(AlchemancyProperties.LOOSE, PropertyFunction.OTHER, "Prevents items from being worn in any armor slot.");
      this.addCodexFlavor(AlchemancyProperties.SPARKING, "The perfect match for your stick");
      this.addCodexFunction(
         AlchemancyProperties.SPARKING,
         PropertyFunction.RECEIVE_DAMAGE_EQUIPPED,
         "Creates a block of {item Fire} at the user's position when taking Physical damage."
      );
      this.addCodexFunction(AlchemancyProperties.SPARKING, PropertyFunction.ACTIVATE, "Creates a block of {item Fire} at the target's position.");
      this.addCodexFunction(AlchemancyProperties.SPARKING, PropertyFunction.WHEN_SHOT, "Creates a block of {item Fire} at the projectile's position on impact.");
      this.addCodexFunction(AlchemancyProperties.SPARKING, PropertyFunction.ON_DESTROYED, "Creates a block of {item Fire} at the item's position.");
      this.addCodexFlavor(AlchemancyProperties.EXTENDED, "You might be reaching a little");
      this.addCodexFunction(
         AlchemancyProperties.EXTENDED,
         PropertyFunction.WHILE_HELD_MAINHAND,
         "Increases the user's {attribute Block Interaction Range} and {attribute Entity Interaction Range} by 2 blocks."
      );
      this.addCodexFunction(AlchemancyProperties.EXTENDED, PropertyFunction.WHILE_WORN_BOOTS, "Increases the user's {attribute Step Height} by 0.5 blocks.");
      this.addCodexFunction(AlchemancyProperties.EXTENDED, PropertyFunction.WHILE_USING, "Doubles the item's use time.");
      this.addCodexFunction(
         AlchemancyProperties.EXTENDED,
         PropertyFunction.ATTRIBUTE_MODIFIER,
         "Doubles the flight duration for {item Firework Rockets}. Increases the effect distance of some radius-based {system Infusions}."
      );
      this.addCodexFlavor(AlchemancyProperties.MUSICAL, "I think I feel a song coming on");
      this.addCodexFunction(
         AlchemancyProperties.MUSICAL,
         PropertyFunction.WHEN_USED,
         "Makes the item play a musical note. The note's pitch is determined by the user's looking pitch, and the instrument used is determined by the item being used."
      );
      this.addCodexFunction(
         AlchemancyProperties.MUSICAL,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Makes the user play a musical note when moving on the ground. The note's pitch is determined by the user's horizontal movement speed, and the instrument used is determined by the block they're standing on."
      );
      this.addCodexFlavor(AlchemancyProperties.TARGETED, "Like having an apple on your head");
      this.addCodexFunction(
         AlchemancyProperties.TARGETED, PropertyFunction.WHILE_EQUIPPED, "Causes all Projectiles in a 16-block radius to home in on the user."
      );
      this.addCodexFunction(AlchemancyProperties.TARGETED, PropertyFunction.WHEN_DROPPED, "Causes all Projectiles in a 16-block radius to home in on the item.");
      this.addCodexFunction(AlchemancyProperties.TARGETED, PropertyFunction.WHILE_ROOTED, "Causes all Projectiles in a 16-block radius to home in on the item.");
      this.addCodexFunction(
         AlchemancyProperties.TARGETED, PropertyFunction.WHEN_SHOT, "Causes all Projectiles in a 16-block radius to start following the projectile."
      );
      this.addCodexFlavor(AlchemancyProperties.REPELLED, "Not a fan of crowds");
      this.addCodexFunction(AlchemancyProperties.REPELLED, PropertyFunction.WHILE_EQUIPPED, "Repels the user from EVERY Entity in an 8-block radius.");
      this.addCodexFunction(AlchemancyProperties.REPELLED, PropertyFunction.WHEN_DROPPED, "Repels the item from every Entity in an 8-block radius.");
      this.addCodexFunction(AlchemancyProperties.REPELLED, PropertyFunction.WHEN_SHOT, "Repels the projectile from every Entity in an 8-block radius.");
      this.addCodexFlavor(AlchemancyProperties.MAGNETIC, "Become the master of magnetism!");
      this.addCodexFunction(
         AlchemancyProperties.MAGNETIC,
         PropertyFunction.WHILE_USING,
         "Pulls in Iron Golems and attempts to strip all Entities in a 20-block radius from their equipped items with {system Infused} or {system Dormant} {property_list_or alchemancy:pulled_in_by_magnetic}."
      );
      this.addCodexFunction(
         AlchemancyProperties.MAGNETIC,
         PropertyFunction.WHILE_ROOTED,
         "Pulls in Iron Golems and attempts to strip all Entities in a 20-block radius from their equipped items with {system Infused} or {system Dormant} {property_list_or alchemancy:pulled_in_by_magnetic}."
      );
      this.addCodexFunction(
         AlchemancyProperties.MAGNETIC,
         PropertyFunction.WHILE_EQUIPPED,
         "Repels the user from other Entities with {property alchemancy:magnetic} items equipped in a 5-block radius."
      );
      this.addCodexFunction(
         AlchemancyProperties.MAGNETIC,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Grants full knockback immunity and somewhat hinders the user's ability to jump while standing on {property alchemancy:ferrous}{system -Dormant} blocks."
      );
      this.addCodexFunction(
         AlchemancyProperties.MAGNETIC,
         PropertyFunction.WHILE_WORN_CHESTPLATE,
         "Allows the user to stick to {property alchemancy:ferrous}{system -Dormant} blocks."
      );
      this.addCodexFunction(
         AlchemancyProperties.MAGNETIC,
         PropertyFunction.WHILE_WORN_LEGGINGS,
         "Allows the user to stick to {property alchemancy:ferrous}{system -Dormant} blocks."
      );
      this.addCodexFunction(
         AlchemancyProperties.MAGNETIC,
         PropertyFunction.WHILE_WORN_HELMET,
         "Allows the user to stick to {property alchemancy:ferrous}{system -Dormant} blocks."
      );
      this.addCodexFlavor(AlchemancyProperties.ENTANGLED, "Two for the price of one");
      this.addCodexFunction(AlchemancyProperties.ENTANGLED, PropertyFunction.ACTIVATE, "Swaps this item with its {property alchemancy:entangled} counterpart.");
      this.addCodexFunction(
         AlchemancyProperties.ENTANGLED,
         PropertyFunction.STACKED_ON,
         "Allows the item to permanently store an additional item within itself as long as it has an additional {system Infusion Slot} to apply this {system Infusion} to it. If either item is destroyed or consumed, the other will also be lost."
      );
      this.addCodexFlavor(AlchemancyProperties.USE_ENTANGLED, "Innocuous Double");
      this.addCodexFunction(
         AlchemancyProperties.USE_ENTANGLED, PropertyFunction.WHEN_USED, "Swaps this item with its {property alchemancy:entangled} counterpart."
      );
      this.addCodexFunction(
         AlchemancyProperties.USE_ENTANGLED,
         PropertyFunction.STACKED_ON,
         "Allows the item to permanently store an additional item within itself as long as it has an additional {system Infusion Slot} to apply this {system Infusion} to it. If either item is destroyed or consumed, the other will also be lost."
      );
      this.addCodexFlavor(AlchemancyProperties.CROUCH_ENTANGLED, "Stealth mode: engaged");
      this.addCodexFunction(
         AlchemancyProperties.CROUCH_ENTANGLED,
         PropertyFunction.WHILE_EQUIPPED,
         "Swaps this item with its {property alchemancy:entangled} counterpart while crouching."
      );
      this.addCodexFunction(
         AlchemancyProperties.CROUCH_ENTANGLED,
         PropertyFunction.STACKED_ON,
         "Allows the item to permanently store an additional item within itself as long as it has an additional {system Infusion Slot} to apply this {system Infusion} to it. If either item is destroyed or consumed, the other will also be lost."
      );
      this.addCodexFlavor(AlchemancyProperties.JUMP_ENTANGLED, "Gives me an idea for a jetpack of sorts");
      this.addCodexFunction(
         AlchemancyProperties.JUMP_ENTANGLED,
         PropertyFunction.WHILE_EQUIPPED,
         "Swaps this item with its {property alchemancy:entangled} counterpart while jumping."
      );
      this.addCodexFunction(
         AlchemancyProperties.JUMP_ENTANGLED,
         PropertyFunction.STACKED_ON,
         "Allows the item to permanently store an additional item within itself as long as it has an additional {system Infusion Slot} to apply this {system Infusion} to it. If either item is destroyed or consumed, the other will also be lost."
      );
      this.addCodexFlavor(AlchemancyProperties.SPRINT_ENTANGLED, "One for standing still, another for going quick");
      this.addCodexFunction(
         AlchemancyProperties.SPRINT_ENTANGLED,
         PropertyFunction.WHILE_EQUIPPED,
         "Swaps this item with its {property alchemancy:entangled} counterpart while sprinting."
      );
      this.addCodexFunction(
         AlchemancyProperties.SPRINT_ENTANGLED,
         PropertyFunction.STACKED_ON,
         "Allows the item to permanently store an additional item within itself as long as it has an additional {system Infusion Slot} to apply this {system Infusion} to it. If either item is destroyed or consumed, the other will also be lost."
      );
      this.addCodexFlavor(AlchemancyProperties.QUANTUM_SHIFT, "Who said you needed a degree to mess with Quantum Superpositions?");
      this.addCodexFunction(
         AlchemancyProperties.QUANTUM_SHIFT,
         PropertyFunction.ACTIVATE,
         "Swaps the target's equipped {property alchemancy:entangled} items with their counterparts."
      );
      this.addCodexFunction(
         AlchemancyProperties.QUANTUM_SHIFT, PropertyFunction.STACKED_OVER, "Swaps the target item with its {property alchemancy:entangled} counterpart."
      );
      this.addCodexFlavor(AlchemancyProperties.RANDOM, "Feeling lucky?");
      this.addCodexFunction(
         AlchemancyProperties.RANDOM,
         PropertyFunction.OTHER,
         "Triggers the effect of ANY random {system Infusion} when an {function infusion_function} is triggered."
      );
      this.addCodexFlavor(AlchemancyProperties.AUXILIARY, "One ring to rule them all... or maybe 35...");
      this.addCodexFunction(
         AlchemancyProperties.AUXILIARY,
         PropertyFunction.OTHER,
         "Effects triggered by {function while_held_mainhand}, {function on_attack}, and {function on_kill} trigger for this item from anywhere in the user's inventory as if they were holding it in their Main Hand."
      );
      this.addCodexFlavor(AlchemancyProperties.WORLD_OBLITERATOR, "You are no god... but I shall feast upon your essence regardless!");
      this.addCodexFunction(
         AlchemancyProperties.WORLD_OBLITERATOR,
         PropertyFunction.WHILE_USING,
         "Rapidly breaks and absorbs blocks within a 5-block radius, consuming 1 durability point for each block destroyed."
      );
      this.addCodexFunction(
         AlchemancyProperties.WORLD_OBLITERATOR,
         PropertyFunction.STACKED_ON,
         "Sets the cursor item as the item's Targeted Block, only allowing it to absorb blocks of that same type. The Target Block can be cleared by right-clicking the item with an empty cursor."
      );
      this.addCodexFlavor(AlchemancyProperties.BATTERY_POWERED, "Battery companies hate it!");
      this.addCodexFunction(
         AlchemancyProperties.BATTERY_POWERED,
         PropertyFunction.WHILE_IN_INVENTORY,
         "Attempts to draw 100 FE from the item's buffer every tick to repair 1 point of durability."
      );
      this.addCodexFunction(
         AlchemancyProperties.BATTERY_POWERED, PropertyFunction.OTHER, "Allows the item to store up to 10,000 FE if it cannot already store energy."
      );
      this.addCodexFlavor(AlchemancyProperties.LIVING_BATTERY, "Amped up!");
      this.addCodexFunction(
         AlchemancyProperties.LIVING_BATTERY,
         PropertyFunction.OTHER,
         "Excess durability recovered from other {system Infusions} is converted into 50 FE per point and stored into the item's internal energy buffer."
      );
      this.addCodexFlavor(AlchemancyProperties.OVERGROWTH, "Let it grow");
      this.addCodexFunction(
         AlchemancyProperties.OVERGROWTH, PropertyFunction.OTHER, "Increases the amount of durability recovered from other {system Infusions} by 300%."
      );
      this.addCodexFunction(AlchemancyProperties.OVERGROWTH, PropertyFunction.WHILE_IN_INVENTORY, "Slowly spreads itself to items in adjacent inventory slots.");
      this.addCodexFunction(
         AlchemancyProperties.OVERGROWTH,
         PropertyFunction.WHILE_WORN,
         "Slowly spreads itself to other equipped items. If every piece of equipment already has {property alchemancy:overgrowth}, then it will instead spread to a random inventory slot."
      );
      this.addCodexFunction(
         AlchemancyProperties.OVERGROWTH,
         PropertyFunction.STACKED_ON,
         "This {system Infusion} is removed when {item Shears} or an item {system Infused} with {property alchemancy:shearing} is stacked on top of the affected item."
      );
      this.addCodexFlavor(AlchemancyProperties.WILDFIRE, "Only YOU can start forest fires");
      this.addCodexFunction(
         AlchemancyProperties.WILDFIRE,
         PropertyFunction.WHILE_IN_INVENTORY,
         "Slowly spreads itself to items in adjacent inventory slots. Has a 10% chance every second to set the user on fire for 1 second. Items with {system Infused} or {system Dormant} {property_list_or alchemancy:burnt_up_by_wildfire} are slowly consumed. The {system Infusion} is removed if the user wears a {property alchemancy:wet} item or goes into water."
      );
      this.addCodexFunction(AlchemancyProperties.WILDFIRE, PropertyFunction.WHILE_EQUIPPED, "Sets the user on fire for 1 second.");
      this.addCodexFunction(
         AlchemancyProperties.WILDFIRE,
         PropertyFunction.WHILE_WORN,
         "Slowly spreads itself to other equipped items. If every piece of equipment already has {property alchemancy:wildfire}, then it will instead spread to a random inventory slot."
      );
      this.addCodexFlavor(AlchemancyProperties.FLEETING, "Bye bye armor");
      this.addCodexFunction(
         AlchemancyProperties.FLEETING, PropertyFunction.WHILE_IN_INVENTORY, "Has a 10% chance for the item to disappear from the user's inventory."
      );
      this.addCodexFunction(AlchemancyProperties.FLEETING, PropertyFunction.WHEN_DROPPED, "Causes the dropped item to completely disappear.");
      this.addCodexFlavor(AlchemancyProperties.ANCIENT, "Could get a senior discount at theme parks");
      this.addCodexFunction(AlchemancyProperties.ANCIENT, PropertyFunction.WHEN_DROPPED, "Prevents the item from despawning.");
      this.addCodexFlavor(AlchemancyProperties.SMOOTH, "Better late than never");
      this.addCodexFunction(
         AlchemancyProperties.SMOOTH,
         PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING,
         "Grants complete immunity to prickly forms of damage, such as {item Cacti}, {item Sweet Berries}, {item Bee} stings, the {enchantment thorns} Enchantment, and the {property alchemancy:jagged} {system Infusion}."
      );
      this.addCodexFunction(AlchemancyProperties.SMOOTH, PropertyFunction.WHEN_DROPPED, "Makes the item immune to {item Cactus} damage.");
      this.addCodexFlavor(AlchemancyProperties.CONVEYED, "Look at it go!");
      this.addCodexFunction(
         AlchemancyProperties.CONVEYED,
         PropertyFunction.WHILE_WORN_BOOTS,
         "Makes the user move forwards when grounded. If {property alchemancy:antigrav} is present, the user will instead fly in the direction they're facing."
      );
      this.addCodexFunction(
         AlchemancyProperties.CONVEYED,
         PropertyFunction.WHEN_DROPPED,
         "Makes the item move forwards when grounded. If {property alchemancy:antigrav} is present, the item will instead fly forwards. If {property alchemancy:bouncy} is present, the item will bounce off of any walls it hits. If {property alchemancy:spiking} is present, the item will climb up any walls it hits."
      );
      this.addCodexFlavor(AlchemancyProperties.DISPENSING, "If you glued a dispenser to your arm, would that make you a cyborg?");
      this.addCodexFunction(
         AlchemancyProperties.DISPENSING, PropertyFunction.WHEN_SHOT, "Uses the item on the impacted block as if it were right-clicked by a player."
      );
      this.addCodexFunction(
         AlchemancyProperties.DISPENSING,
         PropertyFunction.WHEN_USED,
         "If {property alchemancy:hollow} is present, the stored item is used instead of this one. If {property alchemancy:throwable} is also present, the stored item is instead thrown as if it was {property alchemancy:throwable}."
      );
      this.addCodexFunction(
         AlchemancyProperties.DISPENSING,
         PropertyFunction.ACTIVATE,
         "If {property alchemancy:hollow} is present, the stored item is used as if it were right-clicked."
      );
      this.addCodexFunction(
         AlchemancyProperties.DISPENSING,
         PropertyFunction.ACTIVATE_BY_BLOCK,
         "If {property alchemancy:hollow} is present, the stored item is used as if it were right-clicked from the rooted item's position facing the target."
      );
      this.addCodexFunction(
         AlchemancyProperties.DISPENSING,
         PropertyFunction.WHILE_WORN_BOOTS,
         "If {property alchemancy:hollow} is present, the stored item will attempt to be used at the user's feet while moving."
      );
      this.addCodexFlavor(AlchemancyProperties.RESTOCKER, "Inventory Tweaks? What's that?");
      this.addCodexFunction(
         AlchemancyProperties.RESTOCKER,
         PropertyFunction.WHILE_EQUIPPED,
         "Constantly gets similar items in the user's inventory and attempts to stack them on top of this one, keeping it fully stocked. If {property alchemancy:hollow} is present, the item's stored item will instead attempt to be restocked."
      );
      this.addCodexFunction(
         AlchemancyProperties.RESTOCKER, PropertyFunction.WHILE_WORN, "Constantly attempts to restock the held item using other items in the user's inventory."
      );
      this.addCodexFlavor(AlchemancyProperties.NULLIFIER, "World's most expensive trash can");
      this.addCodexFunction(
         AlchemancyProperties.NULLIFIER,
         PropertyFunction.WHILE_EQUIPPED,
         "Deletes the contents of {property alchemancy:hollow}, {property alchemancy:bucketing}, {property alchemancy:encapsulating} and {property alchemancy:capturing}. Retains a single item for {property alchemancy:hollow} if {property alchemancy:restocker} is present for filtering purposes."
      );
      this.addCodexFunction(
         AlchemancyProperties.NULLIFIER,
         PropertyFunction.STACKED_ON,
         "When stacked on an item or an item is stacked on top of this one, the target item is completely deleted. Cannot delete items with {property alchemancy:hollow}."
      );
      this.addCodexFlavor(AlchemancyProperties.SCATTERSHOT, "Eat lead, slackers!");
      this.addCodexFunction(AlchemancyProperties.SCATTERSHOT, PropertyFunction.WHEN_USED, "Throws up to 8 of the stored item as if they were projectiles.");
      this.addCodexFunction(
         AlchemancyProperties.SCATTERSHOT,
         PropertyFunction.STACKED_OVER,
         "Stores the stacked item if enough space is available. Retrieves any stored items when right-clicked with an empty cursor."
      );
      this.addCodexFunction(
         AlchemancyProperties.SCATTERSHOT,
         PropertyFunction.WHEN_SHOT_FROM_DISPENSER,
         "Throws up to 8 of the stored item as if they were projectiles dispensing itself."
      );
      this.addCodexFunction(
         AlchemancyProperties.SCATTERSHOT, PropertyFunction.OTHER, "Allows the item to store up to one stack of a single item. Makes the item non-stackable."
      );
      this.addCodexFlavor(AlchemancyProperties.ECHOED, "Ever get that feeling of déjà vu?");
      this.addCodexFunction(
         AlchemancyProperties.ECHOED, PropertyFunction.ON_ATTACK, "Re-triggers the item's {function on_attack} effect on the target after 1 second."
      );
      this.addCodexFunction(
         AlchemancyProperties.ECHOED,
         PropertyFunction.MODIFY_DAMAGE,
         "Re-triggers the item's {function modify_damage} effect on the target after 1 second, dealing 25% of the attack's base damage."
      );
      this.addCodexFunction(
         AlchemancyProperties.ECHOED, PropertyFunction.ON_CRIT, "Re-triggers the item's {function on_crit} effect on the target after 1 second."
      );
      this.addCodexFunction(
         AlchemancyProperties.ECHOED, PropertyFunction.ACTIVATE, "Re-triggers the item's {function activate_unformatted} effect on the target after 1 second."
      );
      this.addCodexFlavor(AlchemancyProperties.DEATH_TRACKER, "Hardcore players HATE it");
      this.addCodexFunction(AlchemancyProperties.DEATH_TRACKER, PropertyFunction.VISUAL, "Makes the item rotate towards the user's last point of death.");
      this.addCodexFunction(
         AlchemancyProperties.DEATH_TRACKER,
         PropertyFunction.OTHER,
         "Makes {property alchemancy:wayward_warp} teleport the the target to the user's last point of death instead of its bound destination, consuming 500 durability points or the item itself in the process."
      );
      this.addCodexFlavor(AlchemancyProperties.DEATHRATTLE, "One last parting gift");
      this.addCodexFunction(
         AlchemancyProperties.DEATHRATTLE,
         PropertyFunction.WHILE_EQUIPPED,
         "{activate Activates} the item on the user upon death. Cannot be used to heal or resurrect the user."
      );
      this.addCodexFlavor(AlchemancyProperties.PUTRID, "Smells rotten... But somehow zombies like it");
      this.addCodexFunction(AlchemancyProperties.PUTRID, PropertyFunction.ON_ATTACK, "Spreads {property alchemancy:putrid} to the target's armor.");
      this.addCodexFunction(AlchemancyProperties.PUTRID, PropertyFunction.WHILE_EQUIPPED, "Increases the aggro range of undead mobs.");
      this.addCodexFunction(AlchemancyProperties.PUTRID, PropertyFunction.WHILE_ROOTED, "Attracts undead mobs to the item.");
      this.addCodexFlavor(AlchemancyProperties.CHARMING, "Likes long walks at the beach");
      this.addCodexFunction(AlchemancyProperties.CHARMING, PropertyFunction.ON_ATTACK, "Makes Animals enter love mode.");
      this.addCodexFunction(AlchemancyProperties.CHARMING, PropertyFunction.WHILE_EQUIPPED, "Makes all pathfinding mobs follow the user.");
      this.addCodexFunction(AlchemancyProperties.CHARMING, PropertyFunction.WHILE_ROOTED, "Attracts all pathfinding mobs to the item.");
      this.addCodexFlavor(AlchemancyProperties.CHANCE_STRIKE, "Hit the jackpot!");
      this.addCodexFunction(AlchemancyProperties.CHANCE_STRIKE, PropertyFunction.MODIFY_DAMAGE, "Randomizes damage dealt between 70% and 130%.");
      this.addCodexFunction(AlchemancyProperties.CHANCE_STRIKE, PropertyFunction.ON_ATTACK, "Has a 50% chance to {activate Activate} on the target.");
      this.addCodexFunction(
         AlchemancyProperties.CHANCE_STRIKE, PropertyFunction.RECEIVE_DAMAGE_WORN, "Has a 50% chance to {activate Activate} on the attacker."
      );
      this.addCodexFunction(AlchemancyProperties.CHANCE_STRIKE, PropertyFunction.OTHER, "Makes all {activate Activation} sources have a 50% to succeed.");
      this.addCodexFlavor(AlchemancyProperties.IMBUED, "People call me The Drink");
      this.addCodexFunction(
         AlchemancyProperties.IMBUED,
         PropertyFunction.ON_ATTACK,
         "Consumes 10 seconds of the {system Infusion}'s Potion Effect and applies them to the target."
      );
      this.addCodexFunction(
         AlchemancyProperties.IMBUED, PropertyFunction.ACTIVATE, "Consumes 10 seconds of the {system Infusion}'s Potion Effect and applies them to the target."
      );
      this.addCodexFunction(
         AlchemancyProperties.IMBUED, PropertyFunction.WHEN_SHOT, "Applies and consumes the {system Infusion}'s Potion Effect to the impacted entity."
      );
      this.addCodexFunction(
         AlchemancyProperties.IMBUED,
         PropertyFunction.OTHER,
         "When a {item Potion} providing this {system Infusion} is {system Infused} onto the item, one of its potion effects will be stored and used for attacks and {activate Activations}."
      );
      this.addCodexFlavor(AlchemancyProperties.INFERNAL, "Quite spicy with a touch of tangy");
      this.addCodexFunction(AlchemancyProperties.INFERNAL, PropertyFunction.ON_ATTACK, "Applies Inferno to the target for 3 seconds.");
      this.addCodexFunction(AlchemancyProperties.INFERNAL, PropertyFunction.WHILE_WORN, "Applies Inferno to the user for 3 seconds.");
      this.addCodexFlavor(AlchemancyProperties.CORROSIVE, "Melts through pretty much anything");
      this.addCodexFunction(
         AlchemancyProperties.CORROSIVE, PropertyFunction.ON_ATTACK, "Consumes 5 points of durability from a random item worn by the target."
      );
      this.addCodexFunction(
         AlchemancyProperties.CORROSIVE,
         PropertyFunction.WHILE_WORN,
         "Has a 50% chance every tick to consume 3 points of durability from a random equipped item."
      );
      this.addCodexFunction(
         AlchemancyProperties.CORROSIVE,
         PropertyFunction.ACTIVATE,
         "Deals 5 points of {property alchemancy:corrosive} damage and consumes 5 points of durability from a random item worn by the target."
      );
      this.addCodexFlavor(AlchemancyProperties.ENDER_POCKET, "Is this what they meant by \"Hammerspace\"?");
      this.addCodexFunction(AlchemancyProperties.ENDER_POCKET, PropertyFunction.WHEN_USED, "Opens the {item Ender Chest} interface.");
      this.addCodexFunction(AlchemancyProperties.ENDER_POCKET, PropertyFunction.WHILE_ROOTED, "When right-clicked, opens the {item Ender Chest} interface.");
   }

   protected void addCodexFlavor(Holder<Property> propertyHolder, String text) {
      String translationKey = "infusion_codex.%s.flavor".formatted(propertyHolder.getRegisteredName());
      this.add(translationKey, text);
      CodexEntryProvider.ENTRIES
         .put(propertyHolder, new CodexEntryReloadListenener.CodexEntry(Component.translatable(translationKey), new ArrayList<>(), new ArrayList<>()));
   }

   protected void addCodexFunction(Holder<Property> propertyHolder, PropertyFunction function, String text) {
      this.add("infusion_codex.%s.%s".formatted(propertyHolder.getRegisteredName(), function.localizationKey), text);
      if (CodexEntryProvider.ENTRIES.containsKey(propertyHolder)) {
         CodexEntryProvider.ENTRIES.get(propertyHolder).functions().add(function);
         if (function == PropertyFunction.WHILE_ROOTED) {
            CodexEntryProvider.addRelatedProperties(AlchemancyProperties.ROOTED, List.of(propertyHolder));
         } else if (function == PropertyFunction.ON_ATTACK) {
            CodexEntryProvider.addRelatedProperties(AlchemancyProperties.GUST_JET, List.of(propertyHolder));
            CodexEntryProvider.addRelatedProperties(AlchemancyProperties.JAGGED, List.of(propertyHolder));
         } else if (function == PropertyFunction.WHEN_SHOT) {
            CodexEntryProvider.addRelatedProperties(AlchemancyProperties.THROWABLE, List.of(propertyHolder));
            CodexEntryProvider.addRelatedProperties(AlchemancyProperties.SHARPSHOOTING, List.of(propertyHolder));
         }

         if (function == PropertyFunction.WHEN_HIT_WORN_OR_USING
            || function == PropertyFunction.WHEN_HIT_USING
            || function == PropertyFunction.RECEIVE_DAMAGE_USING
            || function == PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING) {
            CodexEntryProvider.addRelatedProperties(
               propertyHolder,
               List.of(
                  AlchemancyProperties.ROCKET_POWERED,
                  AlchemancyProperties.SHARPSHOOTING,
                  AlchemancyProperties.SHIELDING,
                  AlchemancyProperties.CEASELESS_VOID,
                  AlchemancyProperties.BRUSHING,
                  AlchemancyProperties.EDIBLE,
                  AlchemancyProperties.MAGNETIC
               )
            );
         }

         if (function == PropertyFunction.WHILE_WORN_HELMET
            || function == PropertyFunction.WHILE_WORN
            || function == PropertyFunction.WHILE_EQUIPPED
            || function == PropertyFunction.RECEIVE_DAMAGE_EQUIPPED
            || function == PropertyFunction.RECEIVE_DAMAGE_WORN
            || function == PropertyFunction.RECEIVE_DAMAGE_WORN_OR_USING
            || function == PropertyFunction.WHEN_HIT_EQUIPPED
            || function == PropertyFunction.WHEN_HIT_WORN
            || function == PropertyFunction.WHEN_HIT_WORN_OR_USING
            || function == PropertyFunction.PICK_UP_WHILE_EQUIPPED) {
            CodexEntryProvider.addRelatedProperties(AlchemancyProperties.HEADWEAR, List.of(propertyHolder));
         }
      }
   }
}
