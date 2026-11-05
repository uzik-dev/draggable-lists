package com.mrmelon54.DraggableLists.duck;

import net.minecraft.client.gui.screens.packs.PackSelectionModel;

public interface PackModelEntryDuck {
    /** Moves this pack into the slot of {@code target}. Both must be in the same list. */
    void draggable_lists$moveTo(PackSelectionModel.Entry target);
}
