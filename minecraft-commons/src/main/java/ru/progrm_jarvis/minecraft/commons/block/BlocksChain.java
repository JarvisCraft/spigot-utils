package ru.progrm_jarvis.minecraft.commons.block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.plugin.BukkitPluginContainer;

import java.util.Collection;
import java.util.Iterator;

/**
 * A chain of operations of {@link org.bukkit.block.Block}s in some {@link org.bukkit.World}.
 * The chain of operations
 */
public interface BlocksChain extends BukkitPluginContainer, Iterator<Block> {

    /**
     * Gets the world in which this blocks chain exists.
     *
     * @return world of this blocks chain
     */
    World getWorld();

    /**
     * Gets the initial location of this blocks chain.
     *
     * @return initial location of this blocks chain
     */
    Block getInitialBlock();

    @FunctionalInterface
    interface BlockHandler {

        /**
         * Handles the specified block.
         *
         * @param block block to handle
         * @return next blocks to handle in this chain
         */
        Collection<Block> handle(Block block);
    }
}
