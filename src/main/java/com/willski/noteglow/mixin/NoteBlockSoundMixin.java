package com.willski.noteglow.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockSoundMixin {
    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void willski$deepslateSound(BlockState state, Level level, BlockPos pos, int id, int param,
                                        CallbackInfoReturnable<Boolean> cir) {
        if (level.isClientSide()) {
            return;
        }

        Identifier belowId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
        String path = belowId.getPath();
        // Leave The Sift's own sonorous deepslate block alone, its mod already handles it.
        if (!path.contains("deepslate") || belowId.getNamespace().equals("the_sift")) {
            return;
        }

        int note = state.getValue(NoteBlock.NOTE);
        int index = (note % 8) + 1;
        Identifier soundId = Identifier.fromNamespaceAndPath("the_sift", "block.sonorous_deepslate.sound_" + index);
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
        if (sound == null) {
            // The Sift isn't installed, so fall back to vanilla behavior.
            return;
        }

        float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);
        level.playSound(null, pos, sound, SoundSource.RECORDS, 3.0F, pitch);
        cir.setReturnValue(true);
    }
}
