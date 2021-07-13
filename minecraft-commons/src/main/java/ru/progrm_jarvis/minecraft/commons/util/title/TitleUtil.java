package ru.progrm_jarvis.minecraft.commons.util.title;

import com.comphenix.packetwrapper.WrapperPlayServerTitle;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.entity.Player;

/**
 * Utility for title rendering on client.
 */
@UtilityClass
public class TitleUtil {

    /**
     * Packet for clearing the title
     */
    @NonNull protected final WrapperPlayServerTitle CLEAR_TITLE_PACKET = new WrapperPlayServerTitle(),
    /**
     * Packet for resetting the title
     */
    RESET_TITLE_PACKET = new WrapperPlayServerTitle();

    static {
        CLEAR_TITLE_PACKET.setAction(EnumWrappers.TitleAction.CLEAR);
        RESET_TITLE_PACKET.setAction(EnumWrappers.TitleAction.RESET);
    }

    /**
     * Clears the title for the player.
     *
     * @param player player to whom to clear the title
     *
     * @apiNote clearing the title means that the player will be able to see it again if title-time is sent to him
     */
    public void clearTitle(final @NonNull Player player) {
        CLEAR_TITLE_PACKET.sendPacket(player);
    }

    /**
     * Clears the title for the players.
     *
     * @param players players to whom to clear the title
     *
     * @apiNote clearing the title means that the player will be able to see it again if title-time is sent to him
     */
    public void clearTitle(final @NonNull Player... players) {
        for (val player : players) CLEAR_TITLE_PACKET.sendPacket(player);
    }

    /**
     * Clears the title for the players.
     *
     * @param players players to whom to clear the title
     *
     * @apiNote clearing the title means that the player will be able to see it again if title-time is sent to him
     */
    public void clearTitle(final @NonNull Iterable<Player> players) {
        for (val player : players) CLEAR_TITLE_PACKET.sendPacket(player);
    }

    /**
     * Resets the title for the player.
     *
     * @param player player to whom to reset the title
     *
     * @apiNote resetting the title means that the player won't be able to see it again without resending the text
     */
    public void resetTitle(final @NonNull Player player) {
        RESET_TITLE_PACKET.sendPacket(player);
    }

    /**
     * Resets the title for the players.
     *
     * @param players players to whom to reset the title
     *
     * @apiNote resetting the title means that the player won't be able to see it again without resending the text
     */
    public void resetTitle(final @NonNull Player... players) {
        for (val player : players) RESET_TITLE_PACKET.sendPacket(player);
    }

    /**
     * Resets the title for the players.
     *
     * @param players players to whom to reset the title
     *
     * @apiNote resetting the title means that the player won't be able to see it again without resending the text
     */
    public void resetTitle(final @NonNull Iterable<Player> players) {
        for (val player : players) RESET_TITLE_PACKET.sendPacket(player);
    }

    /**
     * Sends the title to the player.
     *
     * @param title text of the title message
     * @param player player to whom to send the title
     *
     * @apiNote title won't display until title-time is sent
     */
    public void sendTitle(final @NonNull WrappedChatComponent title,
                          final @NonNull Player player) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.TITLE);
        packet.setTitle(title);

        packet.sendPacket(player);
    }

    /**
     * Sends the title to the player.
     *
     * @param title text of the title message
     * @param players players to whom to send the title
     *
     * @apiNote title won't display until title-time is sent
     */
    public void sendTitle(final @NonNull WrappedChatComponent title,
                          final @NonNull Player... players) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.TITLE);
        packet.setTitle(title);

        for (val player : players) packet.sendPacket(player);
    }

    /**
     * Sends the title to the player.
     *
     * @param title text of the title message
     * @param players players to whom to send the title
     *
     * @apiNote title won't display until title-time is sent
     */
    public void sendTitle(final @NonNull WrappedChatComponent title,
                          final @NonNull Iterable<Player> players) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.TITLE);
        packet.setTitle(title);

        for (val player : players) packet.sendPacket(player);
    }

    /**
     * Sends the subtitle to the player.
     *
     * @param title text of the subtitle message
     * @param player player to whom to send the subtitle
     *
     * @apiNote subtitle won't display until title-time is sent
     */
    public void sendSubtitle(final @NonNull WrappedChatComponent title,
                             final @NonNull Player player) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.SUBTITLE);
        packet.setTitle(title);

        packet.sendPacket(player);
    }

    /**
     * Sends the subtitle to the player.
     *
     * @param subtitle text of the subtitle message
     * @param players players to whom to send the subtitle
     *
     * @apiNote subtitle won't display until title-time is sent
     */
    public void sendSubtitle(final @NonNull WrappedChatComponent subtitle,
                             final @NonNull Player... players) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.SUBTITLE);
        packet.setTitle(subtitle);

        for (val player : players) packet.sendPacket(player);
    }

    /**
     * Sends the subtitle to the players.
     *
     * @param subtitle text of the subtitle message
     * @param players players to whom to send the subtitle
     *
     * @apiNote subtitle won't display until title-time is sent
     */
    public void sendSubtitle(final @NonNull WrappedChatComponent subtitle,
                             final @NonNull Iterable<Player> players) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.SUBTITLE);
        packet.setTitle(subtitle);

        for (val player : players) packet.sendPacket(player);
    }

    /**
     * Sends the title-time to the player.
     *
     * @param fadeIn time for the title to fade in
     * @param stay time for the title to stay
     * @param fadeOut time for the title to fade in
     * @param player player to whom to send the subtitle
     *
     * @apiNote sending time is required to make the player see title and subtitle
     * @apiNote sending time reverts the effect of clearing the title
     */
    public void sendTitleTime(final int fadeIn, final int stay, final int fadeOut,
                              final @NonNull Player player) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.TIMES);
        packet.setFadeIn(fadeIn);
        packet.setStay(stay);
        packet.setFadeOut(fadeOut);

        packet.sendPacket(player);
    }

    /**
     * Sends the title-time to the players.
     *
     * @param fadeIn time for the title to fade in
     * @param stay time for the title to stay
     * @param fadeOut time for the title to fade in
     * @param players players to whom to send the subtitle
     *
     * @apiNote sending time is required to make the player see title and subtitle
     * @apiNote sending time reverts the effect of clearing the title
     */
    public void sendTitleTime(final int fadeIn, final int stay, final int fadeOut,
                              final @NonNull Player... players) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.TIMES);
        packet.setFadeIn(fadeIn);
        packet.setStay(stay);
        packet.setFadeOut(fadeOut);

        for (val player : players) packet.sendPacket(player);
    }

    /**
     * Sends the title-time to the players.
     *
     * @param fadeIn time for the title to fade in
     * @param stay time for the title to stay
     * @param fadeOut time for the title to fade in
     * @param players players to whom to send the subtitle
     *
     * @apiNote sending time is required to make the player see title and subtitle
     * @apiNote sending time reverts the effect of clearing the title
     */
    public void sendTitleTime(final int fadeIn, final int stay, final int fadeOut,
                              final @NonNull Iterable<Player> players) {
        val packet = new WrapperPlayServerTitle();
        packet.setAction(EnumWrappers.TitleAction.TIMES);
        packet.setFadeIn(fadeIn);
        packet.setStay(stay);
        packet.setFadeOut(fadeOut);

        for (val player : players) packet.sendPacket(player);
    }
}
