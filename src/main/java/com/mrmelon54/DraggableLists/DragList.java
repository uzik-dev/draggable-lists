package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;

import java.util.List;

/**
 * What {@link DragManager} needs from a list. Implemented by the list mixins.
 */
public interface DragList<E extends ObjectSelectionList.Entry<E>> {
    boolean draggable_lists$isEnabled();

    boolean draggable_lists$canDrag(E entry);

    boolean draggable_lists$canDropOn(E entry);

    /** Moves {@code dragged} into the slot held by {@code target}. */
    void draggable_lists$move(E dragged, E target);

    E draggable_lists$getEntryAt(double mouseX, double mouseY);

    boolean draggable_lists$isOverScrollbar(double mouseX, double mouseY);

    List<E> draggable_lists$getEntries();

    int draggable_lists$getTop();

    int draggable_lists$getBottom();

    double draggable_lists$getScroll();

    void draggable_lists$setScroll(double scroll);

    void draggable_lists$extractEntry(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, E entry);
}
