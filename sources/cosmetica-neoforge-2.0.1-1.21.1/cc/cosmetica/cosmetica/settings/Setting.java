package cc.cosmetica.cosmetica.settings;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Component;
import gg.cloaks.javaclient.model.Settings.TypeEnum;
import java.util.Objects;
import javax.annotation.Nullable;

public abstract class Setting<T> {
   public final Text name;
   @Nullable
   private T parentManagedValue = (T)null;
   @Nullable
   private T packValue = (T)null;
   private T userValue;
   private T oldUserValue;
   private boolean modified;
   private boolean hidden;
   private boolean superHidden;
   protected final State<T> actualValue;

   public Setting(String key, T defaultValue) {
      this.name = Text.translatable(key, new String[0]);
      this.oldUserValue = this.userValue = defaultValue;
      this.actualValue = new State(defaultValue);
   }

   public final T get() {
      return (T)this.actualValue.peek();
   }

   public final boolean isVisible() {
      return !this.hidden && !this.superHidden;
   }

   void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   void setSuperHidden(boolean hidden) {
      this.superHidden = hidden;
   }

   public final T getUserValue() {
      return this.userValue;
   }

   public final T acquire(Component component) {
      return (T)this.actualValue.acquire(component);
   }

   public final Setting.Management getManagement() {
      return this.parentManagedValue != null
         ? Setting.Management.PARENT_SETTING
         : (this.packValue != null && !CosmeticaSettings.USE_CLOUD_SETTINGS.get() ? Setting.Management.MODPACK : Setting.Management.USER);
   }

   public boolean isModified() {
      return this.modified;
   }

   public boolean hasDescription() {
      return true;
   }

   public void set(T newValue) {
      this.update(newValue);
      this.modified = !Objects.equals(this.userValue, this.oldUserValue);
   }

   public void apiUpdate(T newValue, TypeEnum typeEnum) {
      if (typeEnum == TypeEnum.CLOUD) {
         this.packValue = null;
         this.oldUserValue = newValue;
         if (!this.isModified()) {
            this.modified = false;
            this.update(newValue);
         } else if (newValue == this.userValue) {
            this.modified = false;
         }
      } else {
         this.packValue = newValue;
         this.updateValue();
      }
   }

   private void update(T newValue) {
      this.userValue = newValue;
      this.updateValue();
   }

   void parentManage(@Nullable T managedValue) {
      this.parentManagedValue = managedValue;
      this.updateValue();
   }

   void updateValue() {
      if (this.parentManagedValue != null) {
         this.actualValue.set(this.parentManagedValue);
         this.onUpdate();
      } else if (this != CosmeticaSettings.USE_CLOUD_SETTINGS && this.packValue != null && !CosmeticaSettings.USE_CLOUD_SETTINGS.get()) {
         this.actualValue.set(this.packValue);
         this.onUpdate();
      } else {
         this.actualValue.set(this.userValue);
         this.onUpdate();
      }
   }

   protected void onUpdate() {
   }

   public void clean() {
      this.modified = false;
      this.oldUserValue = this.userValue;
   }

   public abstract Component createController();

   public abstract Text createDescription(T var1);

   public static enum Management {
      USER,
      MODPACK,
      PARENT_SETTING;
   }
}
