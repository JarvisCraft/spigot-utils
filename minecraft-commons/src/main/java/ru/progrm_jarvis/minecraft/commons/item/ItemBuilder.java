package ru.progrm_jarvis.minecraft.commons.item;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.progrm_jarvis.minecraft.commons.enchant.Enchant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data(staticConstructor = "create")
@Accessors(fluent = true, chain = true)
public class ItemBuilder<M extends ItemMeta> implements Cloneable {

    @NonNull Material material = Material.STONE;
    int amount = 1;

    M metadata;

    //<editor-fold desc="Enchantments" defaultstate="collapsed">

    public Map<Enchantment, Integer> enchantments() {
        return metadata == null ? Collections.emptyMap() : new HashMap<>(metadata.getEnchants());
    }

    public ItemBuilder<M> addEnchantment(final @NonNull Enchantment enchantment, final int level) {
        initMetadata();

        metadata.addEnchant(enchantment, level, true);

        return this;
    }

    public ItemBuilder<M> addEnchantment(final @NonNull Enchant enchant) {
        initMetadata();

        metadata.addEnchant(enchant.getEnchantment(), enchant.getLevel(), true);

        return this;
    }

    public ItemBuilder<M> addEnchantments(final @NonNull Enchant... enchants) {
        initMetadata();

        for (val enchant : enchants) metadata.addEnchant(enchant.getEnchantment(), enchant.getLevel(), true);

        return this;
    }

    public ItemBuilder<M> addEnchantments(final @NonNull Iterable<Enchant> enchants) {
        initMetadata();

        for (val enchant : enchants) metadata.addEnchant(enchant.getEnchantment(), enchant.getLevel(), true);

        return this;
    }

    public ItemBuilder<M> addEnchantments(final @NonNull Map<Enchantment, Integer> enchantments) {
        initMetadata();

        for (val enchantment : enchantments.entrySet()) metadata.addEnchant(
                enchantment.getKey(), enchantment.getValue(), true
        );

        return this;
    }

    public ItemBuilder<M> removeEnchantment(final @NonNull Enchantment enchantment) {
        if (metadata != null) metadata.removeEnchant(enchantment);

        return this;
    }

    public ItemBuilder<M> removeEnchantments(final @NonNull Enchantment... enchantments) {
        if (metadata != null) for (val enchantment : enchantments) metadata.removeEnchant(enchantment);

        return this;
    }

    public ItemBuilder<M> removeEnchantments(final @NonNull Iterable<Enchantment> enchantments) {
        if (metadata != null) for (val enchantment : enchantments) metadata.removeEnchant(enchantment);

        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Metadata" defaultstate="collapsed">

    @SuppressWarnings("unchecked")
    protected void initMetadata() {
        if (metadata == null) metadata = (M) Bukkit.getItemFactory().getItemMeta(material);
    }

    @SuppressWarnings("unchecked")
    public ItemBuilder<M> metadata(final @NonNull M metadata) {
        this.metadata = (M) metadata.clone();

        return this;
    }

    public ItemMeta metadata() {
        initMetadata();

        return metadata.clone();
    }

    public ItemBuilder<M> metadata(final @NonNull Consumer<M> metadataModifier) {
        metadataModifier.accept(metadata);

        return this;
    }

    //</editor-fold>

    //<editor-fold desc="Metadata shorthands">
    public ItemBuilder<M> unbreakable(final boolean unbreakable) {
        initMetadata();

        metadata.setUnbreakable(unbreakable);

        return this;
    }

    public boolean unbreakable() {
        initMetadata();

        return metadata.isUnbreakable();
    }

    public ItemBuilder<M> displayName(final @NonNull String displayName) {
        initMetadata();

        metadata.setDisplayName(displayName);

        return this;
    }

    public String displayName() {
        initMetadata();

        return metadata.getDisplayName();
    }

    public ItemBuilder<M> localizedName(final @NonNull String localizedName) {
        initMetadata();

        metadata.setLocalizedName(localizedName);

        return this;
    }

    public String localizedName() {
        initMetadata();

        return metadata.getLocalizedName();
    }
    //</editor-fold>

    public ItemStack build() {
        initMetadata();

        val item = new ItemStack(material, amount);
        item.setItemMeta(metadata.clone());

        return item;
    }
}
