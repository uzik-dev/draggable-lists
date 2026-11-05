package com.mrmelon54.DraggableLists;

import com.mrmelon54.DraggableLists.config.DraggableConfig;
import net.minecraft.resources.Identifier;

public class DraggableLists {
    public static final String MOD_ID = "draggable_lists";
    public static final DraggableConfig CONFIG = DraggableConfig.load();

    /** The up and down arrows of the pack and server lists, in both states. */
    public static boolean isMoveArrow(Identifier sprite) {
        return sprite.getPath().contains("/move_up") || sprite.getPath().contains("/move_down");
    }
}
