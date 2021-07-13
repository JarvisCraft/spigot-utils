package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;

public interface FakeEntityInteraction {

    int getEntityId();

    Type getType();

    int getX();

    int getY();

    int getZ();

    Hand getHand();

    static FakeEntityInteraction interact(final int entityId, final @NonNull Hand hand) {
        return new InteractInteraction(entityId, hand);
    }

    static FakeEntityInteraction attack(final int entityId) {
        return new AttackInteraction(entityId);
    }

    static FakeEntityInteraction exactInteract(final int entityId, final @NonNull Hand hand,
                                               final int x, final int y, final int z) {
        return new ExactInteractInteraction(entityId, hand, x, y, z);
    }

    static FakeEntityInteraction exactInteract(final int entityId, final @NonNull Hand hand,
                                               final Vector vector) {
        // TODO: 20.11.2018 Check if right
        return new ExactInteractInteraction(entityId, hand, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class InteractInteraction implements FakeEntityInteraction {

        int entityId;
        Hand hand;

        @Override
        public Type getType() {
            return Type.INTERACT;
        }

        ///////////////////////////////////////////////////////////////////////////
        // Stubs
        ///////////////////////////////////////////////////////////////////////////

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public int getZ() {
            return 0;
        }
    }

    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class AttackInteraction implements FakeEntityInteraction {

        int entityId;

        @Override
        public Type getType() {
            return Type.ATTACK;
        }

        ///////////////////////////////////////////////////////////////////////////
        // Stubs
        ///////////////////////////////////////////////////////////////////////////

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public int getZ() {
            return 0;
        }

        @Override
        public Hand getHand() {
            return null;
        }
    }

    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class ExactInteractInteraction implements FakeEntityInteraction {

        int entityId;
        Hand hand;
        final int x, y, z;

        @Override
        public Type getType() {
            return Type.EXACT_INTERACT;
        }
    }

    @RequiredArgsConstructor
    enum Hand {
        MAIN(EquipmentSlot.HAND),
        OFF(NmsUtil.getVersion().getGeneration() > 8 ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);

        @Getter final EquipmentSlot slot;
    }

    enum Type {
        INTERACT, ATTACK, EXACT_INTERACT
    }
}