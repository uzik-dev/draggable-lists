package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.PackEntryDuck;
import com.mrmelon54.DraggableLists.duck.PackModelEntryDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(TransferableSelectionList.class)
public abstract class TransferableSelectionListMixin extends ObjectSelectionList<TransferableSelectionList.Entry> implements DragList<TransferableSelectionList.Entry> {
    @Unique
    private final DragManager<TransferableSelectionList.Entry> draggable_lists$drag = new DragManager<>(this);

    public TransferableSelectionListMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Unique
    private static PackSelectionModel.Entry draggable_lists$packOf(TransferableSelectionList.Entry entry) {
        return entry instanceof PackEntryDuck duck ? duck.draggable_lists$getPack() : null;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        draggable_lists$drag.mousePressed(event);
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return draggable_lists$drag.mouseDragged(event) || super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean handled = super.mouseReleased(event);
        return draggable_lists$drag.mouseReleased() || handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return draggable_lists$drag.isDragging() || super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        draggable_lists$drag.beforeExtract();
        // an off-screen mouse keeps hover effects off the rows moving under the dragged one
        boolean dragging = draggable_lists$drag.isDragging();
        super.extractWidgetRenderState(graphics, dragging ? -1 : mouseX, dragging ? -1 : mouseY, delta);
        draggable_lists$drag.extractDragged(graphics, delta);
    }

    @Override
    protected void extractListItems(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!draggable_lists$drag.extractListItems(graphics, mouseX, mouseY, delta)) {
            super.extractListItems(graphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean draggable_lists$isEnabled() {
        return DraggableLists.CONFIG.resourcePackDraggingEnabled.isEnabled();
    }

    @Override
    public boolean draggable_lists$canDrag(TransferableSelectionList.Entry entry) {
        // the available list has no order of its own, like its rows have no arrows
        PackSelectionModel.Entry pack = draggable_lists$packOf(entry);
        return pack != null && !pack.canSelect() && !pack.isFixedPosition();
    }

    @Override
    public boolean draggable_lists$canDropOn(TransferableSelectionList.Entry entry) {
        return draggable_lists$canDrag(entry);
    }

    @Override
    public void draggable_lists$move(TransferableSelectionList.Entry dragged, TransferableSelectionList.Entry target) {
        if (draggable_lists$packOf(dragged) instanceof PackModelEntryDuck duck) {
            duck.draggable_lists$moveTo(draggable_lists$packOf(target));
        }
    }

    @Override
    public TransferableSelectionList.Entry draggable_lists$getEntryAt(double mouseX, double mouseY) {
        return getEntryAtPosition(mouseX, mouseY);
    }

    @Override
    public boolean draggable_lists$isOverScrollbar(double mouseX, double mouseY) {
        return isOverScrollbar(mouseX, mouseY);
    }

    @Override
    public List<TransferableSelectionList.Entry> draggable_lists$getEntries() {
        return children();
    }

    @Override
    public int draggable_lists$getTop() {
        return getY();
    }

    @Override
    public int draggable_lists$getBottom() {
        return getBottom();
    }

    @Override
    public double draggable_lists$getScroll() {
        return scrollAmount();
    }

    @Override
    public void draggable_lists$setScroll(double scroll) {
        setScrollAmount(scroll);
    }

    @Override
    public void draggable_lists$extractEntry(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, TransferableSelectionList.Entry entry) {
        extractItem(graphics, mouseX, mouseY, delta, entry);
    }
}
