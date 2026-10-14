package com.willski.noteglow.mixin;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockStateDefinition;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockFacingMixin {
    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void willski$addFacing(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void willski$setFacing(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        cir.setReturnValue(cir.getReturnValue().setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
    }
}
