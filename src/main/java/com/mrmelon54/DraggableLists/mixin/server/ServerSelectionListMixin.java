package com.mrmelon54.DraggableLists.mixin.server;

import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.DraggableLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ServerSelectionList.class)
public abstract class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> implements DragList<ServerSelectionList.Entry> {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @Shadow
    public abstract void updateOnlineServers(ServerList servers);

    @Unique
    private final DragManager<ServerSelectionList.Entry> draggable_lists$drag = new DragManager<>(this);

    public ServerSelectionListMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
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
        return DraggableLists.CONFIG.serverDraggingEnabled.isEnabled();
    }

    @Override
    public boolean draggable_lists$canDrag(ServerSelectionList.Entry entry) {
        return entry instanceof ServerSelectionList.OnlineServerEntry;
    }

    @Override
    public boolean draggable_lists$canDropOn(ServerSelectionList.Entry entry) {
        return entry instanceof ServerSelectionList.OnlineServerEntry;
    }

    @Override
    public void draggable_lists$move(ServerSelectionList.Entry dragged, ServerSelectionList.Entry target) {
        // saved servers come first in the list, so a row index is also the index in ServerList
        int from = children().indexOf(dragged);
        int to = children().indexOf(target);
        ServerList servers = screen.getServers();
        int step = from < to ? 1 : -1;
        for (int i = from; i != to; i += step) {
            servers.swap(i, i + step);
        }
        servers.save();
        updateOnlineServers(servers);
    }

    @Override
    public ServerSelectionList.Entry draggable_lists$getEntryAt(double mouseX, double mouseY) {
        return getEntryAtPosition(mouseX, mouseY);
    }

    @Override
    public boolean draggable_lists$isOverScrollbar(double mouseX, double mouseY) {
        return isOverScrollbar(mouseX, mouseY);
    }

    @Override
    public List<ServerSelectionList.Entry> draggable_lists$getEntries() {
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
    public void draggable_lists$extractEntry(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, ServerSelectionList.Entry entry) {
        extractItem(graphics, mouseX, mouseY, delta, entry);
    }
}
