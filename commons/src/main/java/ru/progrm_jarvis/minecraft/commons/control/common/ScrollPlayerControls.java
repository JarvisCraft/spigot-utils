package ru.progrm_jarvis.minecraft.commons.control.common;

import com.comphenix.packetwrapper.WrapperPlayClientHeldItemSlot;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.control.AbstractPlayerControls;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * Player Controls using the player's hotbar scrolling
 */
public class ScrollPlayerControls<P extends Plugin>
        extends AbstractPlayerControls<P, ScrollPlayerControls.Session, ScrollPlayerControls.Event> {

    public ScrollPlayerControls(@NonNull final P plugin, final boolean global,
                                @NonNull final Map<@NonNull Player, @NonNull ScrollPlayerControls.Session> sessions) {
        super(plugin, global, sessions);
    }

    @Override
    @NonNull protected Session createSession(final Player player) {
        return new Session(player);
    }

    /**
     * Handles the slot-change packet-event sending the event if needed.
     *
     * @param player player for whom to handle slot change
     * @param slot slot now selected by the player
     *
     * @implNote initializes the event object only if needed (event handler is not null)
     */
    protected void handleSlotChanged(@NonNull final Player player, final int slot) {
        val eventHandler = this.eventHandler.get();
        if (eventHandler != null) eventHandler.accept(new Event(player, (byte) (slot - 4)));
    }

    /**
     * Session of this scroll player controls
     */
    public class Session extends AbstractPlayerControls.Session {

        public Session(final Player player) {
            super(player);
        }
    }

    /**
     * An event to be passed to event handler whenever a player scrolls.
     */
    @Value
    @FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
    public static class Event {

        /**
         * Player whose event this one is
         */
        Player player;

        /**
         * Positive value for scroll to right, negative value for scroll to left, {@code 0} for no update.
         */
        byte delta;
    }

    /**
     * Function used to fill the player's hotbar with items.
     */
    @FunctionalInterface
    public interface HotbarFiller {

        /**
         * Gets the item to be set in player's inventory at the specified slot.
         *
         * @param player player for whom to update the item in inventory
         * @param slot value between {@code 0} and {@code 8} (inclusive), an index of hotbar slot (from left to right)
         * @return non-empty optional containing a non-null item to be set in player's hotbar at the specified slot
         * or empty optional if the slot should not be updated for the player
         */
        Optional<@NonNull ItemStack> getItem(@Nonnull Player player, int slot);
    }

    /**
     * Packet handler to be used to intercept incoming selected-slot-change packets.
     */
    protected class SlotChangePacketHandler extends PacketAdapter {

        public SlotChangePacketHandler() {
            super(ScrollPlayerControls.this.plugin, PacketType.Play.Client.HELD_ITEM_SLOT);
        }

        @Override
        public void onPacketReceiving(final PacketEvent event) {
            val player = event.getPlayer();
            if (sessions.containsKey(player)) {
                handleSlotChanged(player, new WrapperPlayClientHeldItemSlot(event.getPacket()).getSlot());
                event.setCancelled(true);
            }
        }
    }
}
