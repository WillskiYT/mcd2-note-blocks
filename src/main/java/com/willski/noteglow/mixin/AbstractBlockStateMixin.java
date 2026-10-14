package com.willski.noteglow.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true)
    private void willski$glow(CallbackInfoReturnable<Integer> cir) {
        if (((BlockState) (Object) this).getBlock() instanceof NoteBlock) {
            cir.setReturnValue(15);
        }
    }
}
