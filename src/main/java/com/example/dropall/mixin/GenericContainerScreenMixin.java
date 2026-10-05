package com.example.dropall.mixin;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreen.class)
public abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler> {

    public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addDropAllChestButton(CallbackInfo ci) {
        // Размещаем кнопку в верхнем правом углу окна сундука
        int buttonX = this.x + this.backgroundWidth - 62;
        int buttonY = this.y + 4;

        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Drop All"), button -> dropChestItems())
                .dimensions(buttonX, buttonY, 56, 14)
                .build()
        );
    }

    private void dropChestItems() {
        if (this.client == null || this.client.interactionManager == null || this.client.player == null) {
            return;
        }

        GenericContainerScreenHandler screenHandler = this.getScreenHandler();
        // Количество слотов именно сундука (строк * 9: для одинарного 27, для двойного 54)
        int containerSlotsCount = screenHandler.getRows() * 9;

        // Перебираем только слоты сундука (от 0 до containerSlotsCount - 1), инвентарь игрока не трогаем
        for (int slotId = 0; slotId < containerSlotsCount; slotId++) {
            if (screenHandler.getSlot(slotId).hasStack()) {
                this.client.interactionManager.clickSlot(
                    screenHandler.syncId,
                    slotId,
                    1, // 1 + THROW = выбросить всю пачку
                    SlotActionType.THROW,
                    this.client.player
                );
            }
        }
    }
}
