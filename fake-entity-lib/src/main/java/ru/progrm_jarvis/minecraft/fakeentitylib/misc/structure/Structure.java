package ru.progrm_jarvis.minecraft.fakeentitylib.misc.structure;

import com.comphenix.protocol.wrappers.Vector3F;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

/**
 * A structure consisting of multiple elements
 */
public interface Structure {

    /**
     * Gets this structure's descriptor describing its visual details.
     *
     * @return structure descriptor of this structure
     */
    StructureDescriptor getDescriptor();

    Element getElement(int id);

    default void updateElement(int id, Element.Updater updater) {
        updater.update(getElement(id));
    }

    /*?*
     *? Updates this structure to its next frame.
     *? If the last one was reached then <i>(normally)</i> the first one should be displayed.
     *?/
    void nextFrame();*/

    interface Element {

        void setVisible(boolean visible);

        boolean isVisible();

        Location getPosition();

        void setPosition(double x, double y, double z);

        default void setPosition(Location position) {
            setPosition(position.getX(), position.getY(), position.getZ());
        }

        default void setPosition(Vector position) {
            setPosition(position.getX(), position.getY(), position.getZ());
        }

        default void setPosition(Vector3F position) {
            setPosition(position.getX(), position.getY(), position.getZ());
        }

        void setRotation(double xRotation, double yRotation, double zRotation);

        default void setRotation(final Vector rotation) {
            setRotation(rotation.getX(), rotation.getY(), rotation.getZ());
        }

        default void setRotation(final Vector3F rotation) {
            setRotation(rotation.getX(), rotation.getY(), rotation.getZ());
        }

        Vector getRotation();

        enum Size {
            SMALL, MEDIUM, LARGE, SOLID
        }

        /**
         * Updater of an element used for framing logic.
         */
        @FunctionalInterface
        interface Updater extends Consumer<Element> {

            /**
             * Updates the element specified to some state.
             *
             * @param element element to update
             */
            void update(Structure.Element element);

            @Override
            default void accept(final Structure.Element element) {
                update(element);
            }

            default Updater alsoThen(final @NonNull Updater updater) {
                return element -> {
                    update(element);
                    updater.update(element);
                };
            }

            default Updater alsoBefore(final @NonNull Updater updater) {
                return element -> {
                    update(element);
                    updater.update(element);
                };
            }
        }
    }
}
