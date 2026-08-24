package net.joefoxe.hexerei.data.books;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Optional;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class BookEntity {
   public float x;
   public float y;
   public float rot;
   public float rotO;
   public float scale;
   public String entityType;
   public Entity entity;
   public CompoundTag entityTags;
   public ArrayList<CompoundTag> entityTagsList;
   public int entityTagsListOn;
   public int entityTagsLastChange;
   public int entityTagsListOnSet;
   public BookHoverOffset offset;
   public float toRotate;
   public float toRotateO;
   public boolean hovered;
   public boolean clicked;
   public float hoverTick;
   public float hoverTickO;
   public float hoverTickRender;
   public boolean markedForUpdate;
   public static final Codec<BookEntity> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.FLOAT.fieldOf("scale").orElse(1.0F).forGetter(e -> e.scale),
            Codec.FLOAT.fieldOf("x").orElse(0.0F).forGetter(e -> e.x),
            Codec.FLOAT.fieldOf("y").orElse(0.0F).forGetter(e -> e.y),
            Codec.STRING.fieldOf("id").orElse("player").forGetter(e -> e.entityType),
            Codec.STRING.optionalFieldOf("tag", "{}").xmap(str -> {
               try {
                  return TagParser.parseTag(str);
               } catch (CommandSyntaxException var2) {
                  throw new RuntimeException(var2);
               }
            }, Tag::getAsString).forGetter(e -> e.entityTags),
            Codec.STRING.listOf().optionalFieldOf("tag_array", new ArrayList()).xmap(strings -> strings.stream().map(s -> {
               try {
                  return TagParser.parseTag(s);
               } catch (CommandSyntaxException var2) {
                  throw new RuntimeException(var2);
               }
            }).toList(), compoundTags -> compoundTags.stream().map(Tag::getAsString).toList()).forGetter(e -> e.entityTagsList),
            BookHoverOffset.CODEC.fieldOf("hover_offset").forGetter(e -> e.offset)
         )
         .apply(instance, (scale, x, y, entityType, entityTags, entityTagsList, offset) -> {
            Entity entity = null;
            Optional<EntityType<?>> type = EntityType.byString(entityType);
            if (type.isPresent() && Hexerei.proxy.getLevel() != null) {
               entity = type.get().create(Hexerei.proxy.getLevel());
               if (entity != null) {
                  entity.load(entityTags);
               }
            }

            return new BookEntity(scale, x, y, entityType, entity, entityTags, new ArrayList<>(entityTagsList), offset);
         })
   );

   BookEntity(
      float scale, float x, float y, String entityType, Entity entity, CompoundTag entityTags, ArrayList<CompoundTag> entityTagsList, BookHoverOffset offset
   ) {
      this.x = x;
      this.y = y;
      this.rot = 0.0F;
      this.rotO = 0.0F;
      this.scale = scale;
      this.entityType = entityType;
      this.entityTags = entityTags;
      this.entityTagsList = entityTagsList;
      this.entityTagsListOn = 0;
      this.entityTagsListOnSet = 0;
      this.entityTagsLastChange = 0;
      this.entity = entity;
      this.offset = offset;
      this.toRotate = 0.0F;
      this.toRotateO = 0.0F;
      this.hoverTick = 0.0F;
      this.hoverTickO = 0.0F;
      this.hoverTickRender = 0.0F;
      this.hovered = false;
      this.clicked = false;
      this.markedForUpdate = false;
   }

   private float normalizeAngle(float angle) {
      while (angle > 90.0F) {
         angle -= 360.0F;
      }

      while (angle < -270.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public float getRot(float partial) {
      return HexereiUtil.lerpAngle(this.rotO, this.rot, partial);
   }

   public void tick() {
      this.rotO = this.rot;
      this.hoverTickO = this.hoverTick;
      if (this.toRotate != 0.0F) {
         if (this.toRotate > 0.0F) {
            this.rot = this.rot + Math.max(Math.abs(this.toRotate) / 10.0F, 0.01F) / 3.0F;
         } else {
            this.rot = this.rot - Math.max(Math.abs(this.toRotate) / 10.0F, 0.01F) / 3.0F;
         }

         this.toRotate = HexereiUtil.moveTo(this.toRotate, 0.0F, Math.max(Math.abs(this.toRotate) / 10.0F, 0.01F));
      }

      this.rot = this.normalizeAngle(this.rot);
      if (!this.hovered && !this.clicked) {
         this.hoverTick = HexereiUtil.moveTo(this.hoverTick, 0.0F, 0.08F);
      } else {
         this.hoverTick = HexereiUtil.moveTo(this.hoverTick, 1.0F, 0.06F);
      }

      this.hovered = false;
   }
}
