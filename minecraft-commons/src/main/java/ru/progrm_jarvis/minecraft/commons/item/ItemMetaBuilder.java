package ru.progrm_jarvis.minecraft.commons.item;

import lombok.*;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.enchant.Enchant;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = true)
public class ItemMetaBuilder<M extends ItemMeta, B extends ItemMetaBuilder<M, B>> {

    protected static final ItemFactory ITEM_FACTORY = Bukkit.getItemFactory();

    @Getter(AccessLevel.NONE) @SuppressWarnings("unchecked") protected final B self = (B) this;

    @Nullable protected Boolean unbreakable;
    @Nullable protected String displayName;
    @Nullable protected String localizedName;
    Map<Enchantment, Integer> enchantments;

    @Nullable
    public Boolean unbreakable() {return this.unbreakable;}

    @Nullable
    public String displayName() {return this.displayName;}

    @Nullable
    public String localizedName() {return this.localizedName;}

    public Map<Enchantment, Integer> enchantments() {return this.enchantments;}

    public B unbreakable(@Nullable Boolean unbreakable) {
        this.unbreakable = unbreakable;

        return self;
    }

    public B displayName(@Nullable String displayName) {
        this.displayName = displayName;

        return self;
    }

    public B localizedName(@Nullable String localizedName) {
        this.localizedName = localizedName;

        return self;
    }

    public B enchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;

        return self;
    }

    //<editor-fold desc="Enchantments" defaultstate="collapsed">

    protected void initEnchantments() {
        if (enchantments == null) enchantments = new HashMap<>();
    }

    public B addEnchantment(final @NonNull Enchantment enchantment, final int level) {
        initEnchantments();

        enchantments.put(enchantment, level);

        return self;
    }

    public B addEnchantment(final @NonNull Enchant enchant) {
        initEnchantments();

        enchantments.put(enchant.getEnchantment(), enchant.getLevel());

        return self;
    }

    public B addEnchantments(final @NonNull Enchant... enchants) {
        initEnchantments();

        for (val enchant : enchants) enchantments.put(enchant.getEnchantment(), enchant.getLevel());

        return self;
    }

    public B addEnchantments(final @NonNull Iterable<Enchant> enchants) {
        initEnchantments();

        for (val enchant : enchants) enchantments.put(enchant.getEnchantment(), enchant.getLevel());

        return self;
    }

    public B addEnchantments(final @NonNull Map<Enchantment, Integer> enchantments) {
        initEnchantments();

        for (val enchantment : enchantments.entrySet()) this.enchantments.put(enchantment.getKey(), enchantment.getValue());

        return self;
    }

    public B removeEnchantment(final @NonNull Enchantment enchantment) {
        if (this.enchantments != null) enchantments.remove(enchantment);

        return self;
    }

    public B removeEnchantments(final @NonNull Enchantment... enchantments) {
        if (this.enchantments != null) for (val enchantment : enchantments) this.enchantments.remove(enchantment);

        return self;
    }

    public B removeEnchantments(final @NonNull Iterable<Enchantment> enchantments) {
        if (this.enchantments != null) for (val enchantment : enchantments) this.enchantments.remove(enchantment);

        return self;
    }
    //</editor-fold>

    public M build() {
        @SuppressWarnings("unchecked") val meta = (M) ITEM_FACTORY.getItemMeta(Material.IRON_ORE);
        if (meta == null) throw new UnsupportedOperationException("Cannot create ItemMeta");
        fillMeta(meta);

        return meta;
    }

    public ItemMetaBuilder applyTo(@NonNull M itemMeta) {
        fillMeta(itemMeta);

        return this;
    }

    @OverridingMethodsMustInvokeSuper
    protected void fillMeta(final @NonNull M meta) {
        if (unbreakable != null) meta.setUnbreakable(unbreakable);
        if (displayName != null) meta.setDisplayName(displayName);
        if (localizedName != null) meta.setLocalizedName(localizedName);
        if (enchantments != null) {
            for (val enchantment : meta.getEnchants().keySet()) meta.removeEnchant(enchantment);
            for (val enchantment : enchantments.entrySet()) meta
                    .addEnchant(enchantment.getKey(), enchantment.getValue(), true);
        }
    }

    public static <M extends ItemMeta, B extends ItemMetaBuilder<M, B>> ItemMetaBuilder<M, B> create() {
        return new ItemMetaBuilder<>();
    }
}
