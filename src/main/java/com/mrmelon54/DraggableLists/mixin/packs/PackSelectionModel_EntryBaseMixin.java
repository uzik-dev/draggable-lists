package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.duck.PackModelEntryDuck;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PackSelectionModel.EntryBase.class)
public abstract class PackSelectionModel_EntryBaseMixin implements PackModelEntryDuck {
    @Shadow
    @Final
    private Pack pack;

    @Shadow
    protected abstract List<Pack> getSelfList();

    @Shadow
    protected abstract void move(int offset);

    @Override
    public void draggable_lists$moveTo(PackSelectionModel.Entry target) {
        // positions come from the model: with a search filter the rows on screen are only a part of it
        if (!(target instanceof PackSelectionModel_EntryBaseMixin other)) return;

        List<Pack> list = getSelfList();
        int from = list.indexOf(pack);
        int to = list.indexOf(other.pack);
        if (from < 0 || to < 0 || from == to) return;
        move(to - from);
    }
}
