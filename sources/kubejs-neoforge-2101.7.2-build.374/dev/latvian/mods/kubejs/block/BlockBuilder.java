package dev.latvian.mods.kubejs.block;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.block.callback.AfterEntityFallenOnBlockCallback;
import dev.latvian.mods.kubejs.block.callback.BlockExplodedCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateMirrorCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateModifyCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateModifyPlacementCallback;
import dev.latvian.mods.kubejs.block.callback.BlockStateRotateCallback;
import dev.latvian.mods.kubejs.block.callback.CanBeReplacedCallback;
import dev.latvian.mods.kubejs.block.callback.EntityBlockCallback;
import dev.latvian.mods.kubejs.block.callback.EntityFallenOnBlockCallback;
import dev.latvian.mods.kubejs.block.callback.RandomTickCallback;
import dev.latvian.mods.kubejs.block.drop.BlockDropSupplier;
import dev.latvian.mods.kubejs.block.drop.BlockDrops;
import dev.latvian.mods.kubejs.block.entity.BlockEntityBuilder;
import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.AABBWrapper;
import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.ModelledBuilderBase;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Holder.Direct;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour.StateArgumentPredicate;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@ReturnsSelf
public abstract class BlockBuilder extends ModelledBuilderBase<Block> {
   private static final StatePredicate ALWAYS_FALSE_STATE_PREDICATE = (blockState, blockGetter, blockPos) -> false;
   private static final StateArgumentPredicate<?> ALWAYS_FALSE_STATE_ARG_PREDICATE = (blockState, blockGetter, blockPos, type) -> false;
   public transient Block copyPropertiesFrom;
   public transient SoundType soundType;
   public transient Function<BlockState, MapColor> mapColorFn;
   public transient float hardness;
   public transient float resistance;
   public transient float lightLevel;
   public transient boolean opaque;
   public transient boolean fullBlock;
   public transient boolean requiresTool;
   public transient BlockRenderType renderType;
   public transient BlockTintFunction tint;
   public transient ItemBuilder itemBuilder;
   public transient List<AABB> customShape;
   public transient boolean noCollision;
   public transient boolean notSolid;
   public transient float slipperiness = 0.0F / 0.0F;
   public transient float speedFactor = 0.0F / 0.0F;
   public transient float jumpFactor = 0.0F / 0.0F;
   public Consumer<RandomTickCallback> randomTickCallback;
   public BlockDropSupplier drops;
   public transient boolean noValidSpawns;
   public transient boolean suffocating;
   public transient boolean viewBlocking;
   public transient boolean redstoneConductor;
   public transient boolean transparent;
   public transient NoteBlockInstrument instrument;
   public transient Set<Property<?>> blockStateProperties;
   public transient Consumer<BlockStateModifyCallback> defaultStateModification;
   public transient Consumer<BlockStateModifyPlacementCallback> placementStateModification;
   public transient Predicate<CanBeReplacedCallback> canBeReplacedFunction;
   public transient Consumer<EntityBlockCallback> insideCallback;
   public transient Consumer<EntityBlockCallback> stepOnCallback;
   public transient Consumer<EntityFallenOnBlockCallback> fallOnCallback;
   public transient Consumer<AfterEntityFallenOnBlockCallback> afterFallenOnCallback;
   public transient Consumer<BlockExplodedCallback> explodedCallback;
   public transient Consumer<BlockStateRotateCallback> rotateStateModification;
   public transient Consumer<BlockStateMirrorCallback> mirrorStateModification;
   public transient Consumer<BlockRightClickedKubeEvent> rightClick;
   public transient BlockEntityInfo blockEntityInfo;

   public BlockBuilder(ResourceLocation id) {
      super(id);
      this.baseTexture = id.withPath(ID.BLOCK).toString();
      this.soundType = null;
      this.mapColorFn = null;
      this.hardness = 1.5F;
      this.resistance = 3.0F;
      this.lightLevel = 0.0F;
      this.opaque = true;
      this.fullBlock = false;
      this.requiresTool = false;
      this.renderType = BlockRenderType.SOLID;
      this.itemBuilder = this.getOrCreateItemBuilder();
      if (this.itemBuilder instanceof BlockItemBuilder b) {
         b.blockBuilder = this;
      }

      this.customShape = new ArrayList<>();
      this.noCollision = false;
      this.notSolid = false;
      this.randomTickCallback = null;
      this.drops = null;
      this.noValidSpawns = false;
      this.suffocating = true;
      this.viewBlocking = true;
      this.redstoneConductor = true;
      this.transparent = false;
      this.blockStateProperties = new HashSet<>();
      this.defaultStateModification = null;
      this.placementStateModification = null;
      this.canBeReplacedFunction = null;
   }

   public Block transformObject(Block obj) {
      obj.kjs$setBlockBuilder(this);
      return obj;
   }

   @Override
   public void createAdditionalObjects(AdditionalObjectRegistry registry) {
      if (this.itemBuilder != null) {
         registry.add(Registries.ITEM, this.itemBuilder);
      }

      if (this.blockEntityInfo != null) {
         registry.add(Registries.BLOCK_ENTITY_TYPE, new BlockEntityBuilder(this.id, this.blockEntityInfo));
      }
   }

   @Info("Sets the display name for this object, e.g. `Stone`.\n\nThis will be overridden by a lang file if it exists.\n")
   @Override
   public BuilderBase<Block> displayName(Component name) {
      if (this.itemBuilder != null) {
         this.itemBuilder.displayName(name);
      }

      return super.displayName(name);
   }

   @Override
   public void generateData(KubeDataGenerator generator) {
      LootTable table = this.generateLootTable(generator);
      if (table != null) {
         generator.json(
            this.id.withPath(ID.BLOCK_LOOT_TABLE),
            (JsonElement)((DataResult)generator.getRegistries().json().withEncoder(LootTable.CODEC).apply(new Direct(table))).getOrThrow()
         );
      }
   }

   @Deprecated(
      forRemoval = true
   )
   @NonExtendable
   public LootTable generateLootTable() {
      if (this.drops == BlockDropSupplier.NO_DROPS) {
         return null;
      } else {
         BlockDrops blockDrops = this.drops == null ? BlockDrops.createDefault(this.get().asItem().getDefaultInstance()) : this.drops.get();
         if (blockDrops.items().length == 0) {
            return null;
         } else {
            net.minecraft.world.level.storage.loot.LootPool.Builder pool = new net.minecraft.world.level.storage.loot.LootPool.Builder();
            if (blockDrops.rolls() != null) {
               pool.setRolls(blockDrops.rolls());
            }

            pool.when(ExplosionCondition.survivesExplosion());

            for (ItemStack drop : blockDrops.items()) {
               Builder<? extends Builder<?>> item = LootItem.lootTableItem(drop.getItem());
               if (drop.getCount() > 1) {
                  item.apply(SetItemCountFunction.setCount(ConstantValue.exactly(drop.getCount())));
               }

               if (!drop.isComponentsPatchEmpty()) {
                  item.apply(LootItemConditionalFunction.simpleBuilder(c -> new SetComponentsFunction(c, drop.getComponentsPatch())));
               }

               pool.add(item);
            }

            return new net.minecraft.world.level.storage.loot.LootTable.Builder().withPool(pool).build();
         }
      }
   }

   @Nullable
   public LootTable generateLootTable(KubeDataGenerator generator) {
      return this.generateLootTable();
   }

   @Override
   public void generateAssets(KubeAssetGenerator generator) {
      if (this.useMultipartBlockState()) {
         generator.multipartState(this.id, this::generateMultipartBlockState);
      } else {
         generator.blockState(this.id, this::generateBlockState);
      }

      this.generateBlockModels(generator);
      if (this.itemBuilder != null) {
         generator.itemModel(this.itemBuilder.id, this::generateItemModel);
      }
   }

   protected void generateBlockModels(KubeAssetGenerator generator) {
      generator.blockModel(this.id, m -> {
         if (this.modelGenerator != null) {
            this.modelGenerator.accept(m);
         } else {
            if (this.parentModel != null) {
               m.parent(this.parentModel);
               m.textures(this.textures);
            } else if (this.textures.isEmpty()) {
               m.parent(KubeAssetGenerator.CUBE_ALL_BLOCK_MODEL);
               m.texture("all", this.baseTexture);
            } else {
               m.parent(KubeAssetGenerator.CUBE_BLOCK_MODEL);
               m.textures(this.textures);
            }

            if (this.tint != null || !this.customShape.isEmpty()) {
               for (AABB box : this.customShape.isEmpty() ? List.of(AABBWrapper.CUBE) : this.customShape) {
                  m.element(e -> {
                     e.size(box);
                     e.allFaces(face -> {
                        face.tex("#" + face.side.getSerializedName());
                        face.cull();
                        if (this.tint != null) {
                           face.tintindex(0);
                        }
                     });
                  });
               }
            }
         }
      });
   }

   protected void generateItemModel(ModelGenerator m) {
      m.parent(this.id.withPath(ID.BLOCK));
   }

   protected boolean useMultipartBlockState() {
      return false;
   }

   protected void generateBlockState(VariantBlockStateGenerator bs) {
      bs.simpleVariant("", this.id.withPath(ID.BLOCK));
   }

   protected void generateMultipartBlockState(MultipartBlockStateGenerator bs) {
   }

   public BlockBuilder copyPropertiesFrom(Block block) {
      this.copyPropertiesFrom = block;
      return this;
   }

   @Info("Sets the block's sound type. Defaults to wood.")
   public BlockBuilder soundType(SoundType m) {
      if (m != null && m != SoundType.EMPTY) {
         this.soundType = m;
         return this;
      } else {
         this.soundType = SoundType.EMPTY;
         ConsoleJS.STARTUP.error("Invalid sound type!");
         ConsoleJS.STARTUP.warn("Valid sound types: " + SoundTypeWrapper.INSTANCE.getMap().keySet());
         return this;
      }
   }

   public BlockBuilder noSoundType() {
      this.soundType = SoundType.EMPTY;
      return this;
   }

   public BlockBuilder woodSoundType() {
      return this.soundType(SoundType.WOOD);
   }

   public BlockBuilder stoneSoundType() {
      return this.soundType(SoundType.STONE);
   }

   public BlockBuilder gravelSoundType() {
      return this.soundType(SoundType.GRAVEL);
   }

   public BlockBuilder grassSoundType() {
      return this.soundType(SoundType.GRASS);
   }

   public BlockBuilder sandSoundType() {
      return this.soundType(SoundType.SAND);
   }

   public BlockBuilder cropSoundType() {
      return this.soundType(SoundType.CROP);
   }

   public BlockBuilder glassSoundType() {
      return this.soundType(SoundType.GLASS);
   }

   @Info("Sets the block's map color. Defaults to NONE.")
   public BlockBuilder mapColor(MapColor m) {
      this.mapColorFn = MapColorHelper.reverse(m);
      return this;
   }

   @Info("Sets the block's map color dynamically per block state. If unset, defaults to NONE.")
   public BlockBuilder dynamicMapColor(@Nullable Function<BlockState, Object> m) {
      this.mapColorFn = (Function<BlockState, MapColor>)(m == null ? MapColorHelper.NONE : s -> MapColorHelper.wrap(m.apply(s)));
      return this;
   }

   @Info("Sets the hardness of the block. Defaults to 1.5.\n\nSetting this to -1 will make the block unbreakable like bedrock.\n")
   public BlockBuilder hardness(float h) {
      this.hardness = h;
      return this;
   }

   @Info("Sets the blast resistance of the block. Defaults to 3.\n")
   public BlockBuilder resistance(float r) {
      this.resistance = r;
      return this;
   }

   @Info("Makes the block unbreakable.")
   public BlockBuilder unbreakable() {
      this.hardness = -1.0F;
      this.resistance = 3.4028235E38F;
      return this;
   }

   @Info("Sets the light level of the block. Defaults to 0 (no light).")
   public BlockBuilder lightLevel(float light) {
      this.lightLevel = light;
      return this;
   }

   @Info("Sets the opacity of the block. Opaque blocks do not let light through.")
   public BlockBuilder opaque(boolean o) {
      this.opaque = o;
      return this;
   }

   @Info("Sets the block should be a full block or not, like cactus or doors.")
   public BlockBuilder fullBlock(boolean f) {
      this.fullBlock = f;
      return this;
   }

   @Info("Makes the block require a tool to have drops when broken.")
   public BlockBuilder requiresTool(boolean f) {
      this.requiresTool = f;
      return this;
   }

   @Info("Makes the block require a tool to have drops when broken.")
   public BlockBuilder requiresTool() {
      return this.requiresTool(true);
   }

   @Info("Sets the render type of the block. Can be `cutout`, `cutout_mipped`, `translucent`, or `basic`.\n")
   public BlockBuilder renderType(BlockRenderType l) {
      this.renderType = l;
      return this;
   }

   @Info("Set the color of a specific layer of the block.\n")
   public BlockBuilder color(int index, BlockTintFunction color) {
      if (!(this.tint instanceof BlockTintFunction.Mapped)) {
         this.tint = new BlockTintFunction.Mapped();
      }

      ((BlockTintFunction.Mapped)this.tint).map.put(index, color);
      return this;
   }

   @Info("Set the color of a specific layer of the block.\n")
   public BlockBuilder color(BlockTintFunction color) {
      this.tint = color;
      return this;
   }

   @Info("Modifies the block's item representation.\n")
   public BlockBuilder item(@Nullable Consumer<ItemBuilder> i) {
      if (i == null) {
         this.itemBuilder = null;
         this.drops = BlockDropSupplier.NO_DROPS;
      } else {
         if (this.itemBuilder == null) {
            this.itemBuilder = this.getOrCreateItemBuilder();
            if (this.itemBuilder instanceof BlockItemBuilder b) {
               b.blockBuilder = this;
            }

            ScriptType.STARTUP
               .console
               .warn("`item` is called with non-null builder callback after block item is set to null! Creating another block item as fallback.");
         }

         i.accept(this.itemBuilder);
      }

      return this;
   }

   @HideFromJS
   protected ItemBuilder getOrCreateItemBuilder() {
      return this.itemBuilder == null ? (this.itemBuilder = new BlockItemBuilder(this.id)) : this.itemBuilder;
   }

   @Info("Set the block to have no corresponding item.\n")
   public BlockBuilder noItem() {
      return this.item(null);
   }

   @Info("Set the shape of the block.")
   public BlockBuilder box(double x0, double y0, double z0, double x1, double y1, double z1, boolean scale16) {
      if (scale16) {
         this.customShape.add(new AABB(x0 / 16.0, y0 / 16.0, z0 / 16.0, x1 / 16.0, y1 / 16.0, z1 / 16.0));
      } else {
         this.customShape.add(new AABB(x0, y0, z0, x1, y1, z1));
      }

      return this;
   }

   @Info("Set the shape of the block.")
   public BlockBuilder box(double x0, double y0, double z0, double x1, double y1, double z1) {
      return this.box(x0, y0, z0, x1, y1, z1, true);
   }

   public static VoxelShape createShape(List<AABB> boxes) {
      if (boxes.isEmpty()) {
         return Shapes.block();
      } else {
         VoxelShape shape = Shapes.create((AABB)boxes.getFirst());

         for (int i = 1; i < boxes.size(); i++) {
            shape = Shapes.or(shape, Shapes.create(boxes.get(i)));
         }

         return shape;
      }
   }

   @Info("Makes the block not collide with entities.")
   public BlockBuilder noCollision() {
      this.noCollision = true;
      return this;
   }

   @Info("Makes the block not be solid.")
   public BlockBuilder notSolid() {
      this.notSolid = true;
      return this;
   }

   @Deprecated(
      forRemoval = true
   )
   public BlockBuilder setWaterlogged(boolean waterlogged) {
      ScriptType.STARTUP
         .console
         .warn("\"BlockBuilder.waterlogged\" is a deprecated property! Please use \"BlockBuilder.property(BlockProperties.WATERLOGGED)\" instead.");
      if (waterlogged) {
         this.property(BlockStateProperties.WATERLOGGED);
      }

      return this;
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean getWaterlogged() {
      ScriptType.STARTUP
         .console
         .warn("\"BlockBuilder.waterlogged\" is a deprecated property! Please use \"BlockBuilder.property(BlockProperties.WATERLOGGED)\" instead.");
      return this.canBeWaterlogged();
   }

   @Info("Makes the block can be waterlogged.")
   public BlockBuilder waterlogged() {
      return this.property(BlockStateProperties.WATERLOGGED);
   }

   @Info("Checks if the block can be waterlogged.")
   public boolean canBeWaterlogged() {
      return this.blockStateProperties.contains(BlockStateProperties.WATERLOGGED);
   }

   @Info("Change drops of this block")
   public BlockBuilder drops(BlockDropSupplier drops) {
      this.drops = drops == null ? BlockDropSupplier.NO_DROPS : drops;
      return this;
   }

   @Info("Clears all drops for the block.")
   public BlockBuilder noDrops() {
      this.drops = BlockDropSupplier.NO_DROPS;
      return this;
   }

   @Info("Set how slippery the block is.")
   public BlockBuilder slipperiness(float f) {
      this.slipperiness = f;
      return this;
   }

   @Info("Set how fast you can walk on the block.\n\nAny value above 1 will make you walk insanely fast as your speed is multiplied by this value each tick.\n\nRecommended values are between 0.1 and 1, useful for mimicking soul sand or ice.\n")
   public BlockBuilder speedFactor(float f) {
      this.speedFactor = f;
      return this;
   }

   @Info("Set how high you can jump on the block.")
   public BlockBuilder jumpFactor(float f) {
      this.jumpFactor = f;
      return this;
   }

   @Info("Sets random tick callback for this black.")
   public BlockBuilder randomTick(@Nullable Consumer<RandomTickCallback> randomTickCallback) {
      this.randomTickCallback = randomTickCallback;
      return this;
   }

   @Info("Makes mobs not spawn on the block.")
   public BlockBuilder noValidSpawns(boolean b) {
      this.noValidSpawns = b;
      return this;
   }

   @Info("Makes the block suffocating.")
   public BlockBuilder suffocating(boolean b) {
      this.suffocating = b;
      return this;
   }

   @Info("Makes the block view blocking.")
   public BlockBuilder viewBlocking(boolean b) {
      this.viewBlocking = b;
      return this;
   }

   @Info("Makes the block a redstone conductor.")
   public BlockBuilder redstoneConductor(boolean b) {
      this.redstoneConductor = b;
      return this;
   }

   @Info("Makes the block transparent.")
   public BlockBuilder transparent(boolean b) {
      this.transparent = b;
      return this;
   }

   @Info("Helper method for setting the render type of the block to `cutout` correctly.")
   public BlockBuilder defaultCutout() {
      return this.renderType(BlockRenderType.CUTOUT)
         .notSolid()
         .noValidSpawns(true)
         .suffocating(false)
         .viewBlocking(false)
         .redstoneConductor(false)
         .transparent(true);
   }

   @Info("Helper method for setting the render type of the block to `translucent` correctly.")
   public BlockBuilder defaultTranslucent() {
      return this.defaultCutout().renderType(BlockRenderType.TRANSLUCENT);
   }

   @Info("Note block instrument.")
   public BlockBuilder instrument(NoteBlockInstrument i) {
      this.instrument = i;
      return this;
   }

   @Info("Tags both the block and the item with the given tag.")
   public BlockBuilder tag(ResourceLocation[] tag) {
      return this.tagBoth(tag);
   }

   @Info("Tags both the block and the item with the given tag.")
   public BlockBuilder tagBoth(ResourceLocation[] tag) {
      this.tagBlock(tag);
      this.tagItem(tag);
      return this;
   }

   @Info("Tags the block with the given tag.")
   public BlockBuilder tagBlock(ResourceLocation[] tag) {
      super.tag(tag);
      return this;
   }

   @Info("Tags the item with the given tag.")
   public BlockBuilder tagItem(ResourceLocation[] tag) {
      this.itemBuilder.tag(tag);
      return this;
   }

   @Info("Set the default state of the block.")
   public BlockBuilder defaultState(Consumer<BlockStateModifyCallback> callbackJS) {
      this.defaultStateModification = callbackJS;
      return this;
   }

   @Info("Set the callback for determining the blocks state when placed.")
   public BlockBuilder placementState(Consumer<BlockStateModifyPlacementCallback> callbackJS) {
      this.placementStateModification = callbackJS;
      return this;
   }

   @Info("Set if the block can be replaced by something else.")
   public BlockBuilder canBeReplaced(Predicate<CanBeReplacedCallback> callbackJS) {
      this.canBeReplacedFunction = callbackJS;
      return this;
   }

   @Info("Set what happens when an entity is inside the block\nThis is called every tick for every entity inside the block, so be careful what you do here.\nThis will only be called if the entity's bounding box overlaps with the block's collision.\n")
   public BlockBuilder entityInside(Consumer<EntityBlockCallback> callbackJS) {
      this.insideCallback = callbackJS;
      return this;
   }

   @Info("Set what happens when an entity steps on the block\nThis is called every tick for every entity standing on the block, so be careful what you do here.\n")
   public BlockBuilder steppedOn(Consumer<EntityBlockCallback> callbackJS) {
      this.stepOnCallback = callbackJS;
      return this;
   }

   @Info("Set what happens when an entity falls on the block. Do not use this for moving them, use bounce instead!")
   public BlockBuilder fallenOn(Consumer<EntityFallenOnBlockCallback> callbackJS) {
      this.fallOnCallback = callbackJS;
      return this;
   }

   @Info("Bounces entities that land on this block by bounciness * their fall velocity.\nDo not make bounciness negative, as that is a recipe for a long and laggy trip to the void\n")
   public BlockBuilder bounciness(float bounciness) {
      return this.afterFallenOn(ctx -> ctx.bounce(bounciness));
   }

   @Info("Set how this block bounces/moves entities that land on top of this. Do not use this to modify the block, use fallOn instead!\nUse ctx.bounce(height) or ctx.setVelocity(x, y, z) to change the entities velocity.\n")
   public BlockBuilder afterFallenOn(Consumer<AfterEntityFallenOnBlockCallback> callbackJS) {
      this.afterFallenOnCallback = callbackJS;
      return this;
   }

   @Info("Set how this block reacts after an explosion. Note the block has already been destroyed at this point")
   public BlockBuilder exploded(Consumer<BlockExplodedCallback> callbackJS) {
      this.explodedCallback = callbackJS;
      return this;
   }

   @Info("Add a blockstate property to the block.\n\nFor example, facing, lit, etc.\n")
   public BlockBuilder property(Property<?> property) {
      if (property.getPossibleValues().size() <= 1) {
         throw new IllegalArgumentException(
            String.format(
               "Block \"%s\" has an illegal Blockstate Property \"%s\" which has <= 1 possible values. (%d possible values)",
               this.id,
               property.getName(),
               property.getPossibleValues().size()
            )
         );
      } else {
         this.blockStateProperties.add(property);
         return this;
      }
   }

   @Info("Set the callback used for determining how the block rotates")
   public BlockBuilder rotateState(Consumer<BlockStateRotateCallback> callbackJS) {
      this.rotateStateModification = callbackJS;
      return this;
   }

   @Info("Set the callback used for determining how the block is mirrored")
   public BlockBuilder mirrorState(Consumer<BlockStateMirrorCallback> callbackJS) {
      this.mirrorStateModification = callbackJS;
      return this;
   }

   @Info("Set the callback used for right-clicking on the block")
   public BlockBuilder rightClick(Consumer<BlockRightClickedKubeEvent> callbackJS) {
      this.rightClick = callbackJS;
      return this;
   }

   @Info("Creates a Block Entity for this block")
   public BlockBuilder blockEntity(Consumer<BlockEntityInfo> callback) {
      this.blockEntityInfo = new BlockEntityInfo(this);
      callback.accept(this.blockEntityInfo);
      return this;
   }

   public Properties createProperties() {
      KubeJSBlockProperties properties = new KubeJSBlockProperties(this, this.copyPropertiesFrom);
      if (this.soundType != null) {
         properties.sound(this.soundType);
      }

      if (this.mapColorFn != null) {
         properties.mapColor(this.mapColorFn);
      }

      if (this.resistance >= 0.0F) {
         properties.strength(this.hardness, this.resistance);
      } else {
         properties.strength(this.hardness);
      }

      if (this.lightLevel > 0.0F) {
         properties.lightLevel(state -> (int)(this.lightLevel * 15.0F));
      }

      if (this.noCollision) {
         properties.noCollission();
      }

      if (this.notSolid) {
         properties.noOcclusion();
      }

      if (this.requiresTool) {
         properties.requiresCorrectToolForDrops();
      }

      if (this.drops == BlockDropSupplier.NO_DROPS) {
         properties.noLootTable();
      }

      if (!Float.isNaN(this.slipperiness)) {
         properties.friction(this.slipperiness);
      }

      if (!Float.isNaN(this.speedFactor)) {
         properties.speedFactor(this.speedFactor);
      }

      if (!Float.isNaN(this.jumpFactor)) {
         properties.jumpFactor(this.jumpFactor);
      }

      if (this.noValidSpawns) {
         properties.isValidSpawn(Cast.to(ALWAYS_FALSE_STATE_ARG_PREDICATE));
      }

      if (!this.suffocating) {
         properties.isSuffocating(ALWAYS_FALSE_STATE_PREDICATE);
      }

      if (!this.viewBlocking) {
         properties.isViewBlocking(ALWAYS_FALSE_STATE_PREDICATE);
      }

      if (!this.redstoneConductor) {
         properties.isRedstoneConductor(ALWAYS_FALSE_STATE_PREDICATE);
      }

      if (this.randomTickCallback != null) {
         properties.randomTicks();
      }

      if (this.instrument != null) {
         properties.instrument(this.instrument);
      }

      return properties;
   }
}
