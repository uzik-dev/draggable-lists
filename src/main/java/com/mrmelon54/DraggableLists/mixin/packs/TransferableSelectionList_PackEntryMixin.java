package com.mrmelon54.DraggableLists.mixin.packs;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.PackEntryDuck;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class TransferableSelectionList_PackEntryMixin extends ObjectSelectionList.Entry<TransferableSelectionList.Entry> implements PackEntryDuck {
    @Shadow
    @Final
    private PackSelectionModel.Entry pack;

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private boolean draggable_lists$hideArrows(GuiGraphicsExtractor graphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        return !DraggableLists.CONFIG.disableResourcePackArrows || !DraggableLists.isMoveArrow(sprite);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void draggable_lists$ignoreHiddenArrows(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        // the whole icon stays a button on rows that show the select arrow
        if (!DraggableLists.CONFIG.disableResourcePackArrows || pack.canSelect()) return;

        double x = event.x() - getContentX();
        if (x >= 16 && x < 32) cir.setReturnValue(true);
    }

    @Override
    public PackSelectionModel.Entry draggable_lists$getPack() {
        return pack;
    }
}
