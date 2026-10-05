package com.example.dropall.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addDropAllButton(CallbackInfo ci) {
        int buttonX = this.x + 104;
        int buttonY = this.height / 2 - 22;

        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Drop All"), button -> dropAllItems())
                .dimensions(buttonX, buttonY, 56, 20)
                .build()
        );
    }

    private void dropAllItems() {
        if (this.client == null || this.client.interactionManager == null || this.client.player == null) {
            return;
        }

        PlayerScreenHandler handler = this.client.player.playerScreenHandler;

        // Slots: 9 to 45 (main inventory, hotbar, offhand)
        for (int slotId = 9; slotId <= 45; slotId++) {
            if (handler.getSlot(slotId).hasStack()) {
                this.client.interactionManager.clickSlot(
                    handler.syncId,
                    slotId,
                    1,
                    SlotActionType.THROW,
                    this.client.player
                );
            }
        }
    }
}
