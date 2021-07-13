package ru.progrm_jarvis.minecraft.commons.block;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.*;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SnakyBlockChain implements BlocksChain {

    final @NonNull Plugin plugin;
    @Getter final @NonNull World world;
    @Getter final @NonNull Block initialBlock;

    /**
     * The current layer of blocks to handle
     */
    @NonNull Queue<Block> blocks;

    /**
     * The next layer of blocks to handle
     */
    @NonNull Queue<Block> nextBlocks;

    /**
     * Blocks that have been handled
     */
    final @NonNull Set<Block> handledBlocks;

    protected SnakyBlockChain(final @NonNull Plugin plugin, final @NonNull Block initialBlock,
                              final @NonNull Queue<Block> blocks, final @NonNull Queue<Block> nextBlocks,
                              final @NonNull Set<Block> handledBlocks) {
        this.plugin = plugin;
        world = initialBlock.getWorld();
        this.initialBlock = initialBlock;
        this.blocks = blocks;
        this.nextBlocks = nextBlocks;
        this.handledBlocks = handledBlocks;

        blocks.add(initialBlock);
    }

    protected SnakyBlockChain(final @NonNull Plugin plugin, final @NonNull Block initialBlock) {
        this(plugin, initialBlock, new ArrayDeque<>(), new ArrayDeque<>(), new HashSet<>());
    }

    public static SnakyBlockChain create(final @NonNull Plugin plugin,
                                         final @NonNull Block initialBlock,
                                         final @NonNull BlockHandler blockHandler) {
        return new SnakyBlockChain(plugin, initialBlock) {
            @Override
            protected Collection<Block> handle(final Block block) {
                return blockHandler.handle(block);
            }
        };
    }

    @Override
    public boolean hasNext() {
        return !blocks.isEmpty() || !nextBlocks.isEmpty();
    }

    @Override
    public Block next() {
        // handle case when there are no blocks in at current layer
        if (blocks.isEmpty()) {
            // throw exception if there are no blocks on the next layer too (hasNext() might have not been checked)
            if (nextBlocks.isEmpty()) throw new NoSuchElementException("No more blocks available in BlockChain");

            // swap blocks and next blocks (micro-optimizations <3)
            // this switches the current layer to the one of blocks from handle(Block) of each previous layer's block
            val nextBlocks = this.nextBlocks;
            this.nextBlocks = blocks;
            blocks = nextBlocks;
        }

        // take tke next block queued and handle it
        val block = blocks.remove();
        // clear duplicate occurrences of this block in blocks and nextBlocks

        // get all blocks returned by this block's handling;
        // remove the block from nextBlocks too if it is present there
        val blocksToHandle = handle(block);
        // mark this block as handled
        handledBlocks.add(block);
        // mark all of the blocks returned as next to handle if they haven't been handled yet and are not in the queue
        for (val blockToHandle : blocksToHandle) {
            //  block should not be the handled (now or before)
            if (blocksToHandle.equals(block)|| handledBlocks.contains(blockToHandle)
                    // block should not be in the queue already
                    || blocks.contains(block) || nextBlocks.contains(block)) continue;

            nextBlocks.add(blockToHandle);
        }

        return block;
    }

    /**
     * Gets the bukkit plugin contained by this object.
     *
     * @return bukkit plugin of this object
     */
    @Override
    public Plugin getBukkitPlugin() {
        return plugin;
    }

    /**
     * Handles the specified block.
     *
     * @param block block to handle
     * @return next blocks to handle in this chain
     */
    protected abstract Collection<Block> handle(Block block);
}
