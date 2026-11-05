package com.mrmelon54.DraggableLists;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;

import java.util.List;

/**
 * Drag state of one list. In 26.x every entry owns its position, so rows are moved by shifting
 * their y for the length of one draw call and the list layout itself stays untouched.
 */
public class DragManager<E extends ObjectSelectionList.Entry<E>> {
    /** The icon holds the vanilla click targets, so a drag cannot start on it. */
    private static final int ICON_WIDTH = 32;
    private static final int START_DISTANCE = 3;
    private static final double SCROLL_PIXELS_PER_MS = 0.2;
    private static final int DRAGGED_BACKGROUND = ARGB.colorFromFloat(0.5f, 0.75f, 0.75f, 0.75f);

    private final DragList<E> list;
    private E pressed;
    private double pressY;
    private E dragged;
    private double grabOffsetY;
    private double mouseY;
    private long lastScrollTime;

    public DragManager(DragList<E> list) {
        this.list = list;
    }

    public boolean isDragging() {
        return dragged != null;
    }

    public void mousePressed(MouseButtonEvent event) {
        pressed = null;
        dragged = null;
        if (event.button() != InputConstants.MOUSE_BUTTON_LEFT || !list.draggable_lists$isEnabled()) return;
        if (list.draggable_lists$isOverScrollbar(event.x(), event.y())) return;

        E entry = list.draggable_lists$getEntryAt(event.x(), event.y());
        if (entry == null || !list.draggable_lists$canDrag(entry)) return;
        if (event.x() - entry.getContentX() < ICON_WIDTH) return;

        pressed = entry;
        pressY = event.y();
    }

    public boolean mouseDragged(MouseButtonEvent event) {
        if (dragged != null) {
            mouseY = event.y();
            return true;
        }
        if (pressed == null || Math.abs(event.y() - pressY) < START_DISTANCE) return false;
        if (!list.draggable_lists$getEntries().contains(pressed)) {
            pressed = null;
            return false;
        }

        dragged = pressed;
        pressed = null;
        grabOffsetY = dragged.getY() - pressY;
        mouseY = event.y();
        lastScrollTime = 0;
        return true;
    }

    public boolean mouseReleased() {
        pressed = null;
        if (dragged == null) return false;

        E entry = dragged;
        dragged = null;
        List<E> entries = list.draggable_lists$getEntries();
        int from = entries.indexOf(entry);
        if (from < 0) return true;

        int to = targetIndex(entries, entry, from);
        if (to != from) list.draggable_lists$move(entry, entries.get(to));
        return true;
    }

    /** Scrolls when the dragged row is pushed against the top or bottom of the list. */
    public void beforeExtract() {
        if (dragged == null) return;
        if (!list.draggable_lists$getEntries().contains(dragged)) {
            // the list was rebuilt under the drag, e.g. by the pack folder watcher
            dragged = null;
            return;
        }

        double wanted = mouseY + grabOffsetY;
        int direction = wanted < topLimit() ? -1 : wanted > bottomLimit(dragged) ? 1 : 0;
        if (direction == 0) {
            lastScrollTime = 0;
            return;
        }

        long now = Util.getMillis();
        if (lastScrollTime != 0) {
            double step = (now - lastScrollTime) * SCROLL_PIXELS_PER_MS;
            list.draggable_lists$setScroll(list.draggable_lists$getScroll() + direction * step);
        }
        lastScrollTime = now;
    }

    /** Draws the other rows around the gap. Returns false when no drag is running. */
    public boolean extractListItems(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (dragged == null) return false;

        List<E> entries = list.draggable_lists$getEntries();
        int from = entries.indexOf(dragged);
        int to = targetIndex(entries, dragged, from);
        int height = dragged.getHeight();

        for (int i = 0; i < entries.size(); i++) {
            if (i == from) continue;
            E entry = entries.get(i);
            int shift = 0;
            if (from < to && i > from && i <= to) shift = -height;
            else if (from > to && i >= to && i < from) shift = height;

            int y = entry.getY();
            int shownY = y + shift;
            if (shownY + entry.getHeight() < list.draggable_lists$getTop() || shownY > list.draggable_lists$getBottom()) continue;

            entry.setY(shownY);
            list.draggable_lists$extractEntry(graphics, mouseX, mouseY, delta, entry);
            entry.setY(y);
        }
        return true;
    }

    /** Draws the dragged row on top of the list. */
    public void extractDragged(GuiGraphicsExtractor graphics, float delta) {
        if (dragged == null) return;

        int y = dragged.getY();
        int shownY = draggedY(dragged);
        dragged.setY(shownY);
        graphics.fill(dragged.getX(), shownY, dragged.getX() + dragged.getWidth(), shownY + dragged.getHeight(), DRAGGED_BACKGROUND);
        list.draggable_lists$extractEntry(graphics, -1, -1, delta, dragged);
        dragged.setY(y);
        graphics.requestCursor(CursorTypes.RESIZE_NS);
    }

    private int topLimit() {
        return list.draggable_lists$getTop();
    }

    private int bottomLimit(E entry) {
        return Math.max(topLimit(), list.draggable_lists$getBottom() - entry.getHeight());
    }

    private int draggedY(E entry) {
        return (int) Math.max(topLimit(), Math.min(bottomLimit(entry), mouseY + grabOffsetY));
    }

    /** The slot under the middle of the dragged row, limited to the run of rows it may swap with. */
    private int targetIndex(List<E> entries, E entry, int from) {
        int first = from;
        while (first > 0 && list.draggable_lists$canDropOn(entries.get(first - 1))) first--;
        int last = from;
        while (last < entries.size() - 1 && list.draggable_lists$canDropOn(entries.get(last + 1))) last++;

        int center = draggedY(entry) + entry.getHeight() / 2;
        for (int i = first; i < last; i++) {
            E slot = entries.get(i);
            if (center < slot.getY() + slot.getHeight()) return i;
        }
        return last;
    }
}
