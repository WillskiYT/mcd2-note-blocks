package com.willski.noteglow.mixin;

import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {
    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void willski$glow(CallbackInfoReturnable<Integer> cir) {
        if (((BlockState) (Object) this).getBlock() instanceof NoteBlock) {
            cir.setReturnValue(15);
        }
    }
}
