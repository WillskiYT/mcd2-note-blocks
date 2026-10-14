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
    private static final SoundEvent[] DEEPSLATE_SOUNDS = new SoundEvent[8];

    static {
        for (int i = 0; i < DEEPSLATE_SOUNDS.length; i++) {
            Identifier id = Identifier.fromNamespaceAndPath("willski_noteglow",
                    "block.sonorous_deepslate.sound_" + (i + 1));
            DEEPSLATE_SOUNDS[i] = SoundEvent.createVariableRangeEvent(id);
        }
    }

    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void willski$deepslateSound(BlockState state, Level level, BlockPos pos, int id, int param,
                                        CallbackInfoReturnable<Boolean> cir) {
        if (level.isClientSide()) {
            return;
        }

        Identifier belowId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
        // Only regular deepslate, not its variants.
        if (!belowId.getNamespace().equals("minecraft") || !belowId.getPath().equals("deepslate")) {
            return;
        }

        int note = state.getValue(NoteBlock.NOTE);
        SoundEvent sound = DEEPSLATE_SOUNDS[note % 8];
        float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);
        level.playSound(null, pos, sound, SoundSource.RECORDS, 3.0F, pitch);
        cir.setReturnValue(true);
    }
}
