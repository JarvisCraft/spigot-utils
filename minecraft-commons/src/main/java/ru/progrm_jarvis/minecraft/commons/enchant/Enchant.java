package ru.progrm_jarvis.minecraft.commons.enchant;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.bukkit.enchantments.Enchantment;

import javax.annotation.Nonnegative;

@Value(staticConstructor = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Enchant {

    @NonNull Enchantment enchantment;
    @Nonnegative int level;


    public Enchant(final @NonNull Enchantment enchantment, final int level) {
        Preconditions.checkArgument(level >= 0, "Level should be non-negative");

        this.enchantment = enchantment;
        this.level = level;
    }
}
