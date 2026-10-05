package com.example.dropall.mixin;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GenericContainerScreen.class)
public abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler> {

    public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        // Кнопка в верхнем правом углу панели сундука
        int buttonX = this.x + this.backgroundWidth - 62;
        int buttonY = this.y + 4;

        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Drop All"), button -> dropChestItems())
                .dimensions(buttonX, buttonY, 56, 14)
                .build()
        );
    }

    @Unique
    private void dropChestItems() {
        if (this.client == null || this.client.interactionManager == null || this.client.player == null) {
            return;
        }

        GenericContainerScreenHandler screenHandler = this.getScreenHandler();
        int containerSlotsCount = screenHandler.getRows() * 9;

        for (int slotId = 0; slotId < containerSlotsCount; slotId++) {
            if (screenHandler.getSlot(slotId).hasStack()) {
                this.client.interactionManager.clickSlot(
                    screenHandler.syncId,
                    slotId,
                    1,
                    SlotActionType.THROW,
                    this.client.player
                );
            }
        }
    }
}
