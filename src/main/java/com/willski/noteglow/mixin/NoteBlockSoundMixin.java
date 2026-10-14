package com.willski.noteglow.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockSoundMixin {
    // Deepslate note blocks only use notes 0-7, one sound per note.
    private static final int DEEPSLATE_NOTE_COUNT = 8;
    private static final SoundEvent[] DEEPSLATE_SOUNDS = new SoundEvent[DEEPSLATE_NOTE_COUNT];

    static {
        for (int i = 0; i < DEEPSLATE_SOUNDS.length; i++) {
            Identifier id = Identifier.fromNamespaceAndPath("willski_noteglow",
                    "block.sonorous_deepslate.sound_" + (i + 1));
            DEEPSLATE_SOUNDS[i] = SoundEvent.createVariableRangeEvent(id);
        }
    }

    private static boolean isOnDeepslate(Level level, BlockPos pos) {
        Identifier belowId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos.below()).getBlock());
        // Only regular deepslate, not its variants.
        return belowId.getNamespace().equals("minecraft") && belowId.getPath().equals("deepslate");
    }

    // Clicking from note 7 wraps back to 0 instead of going to 8, so only notes 0-7 are reachable.
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void willski$limitDeepslateNotes(BlockState state, Level level, BlockPos pos, Player player,
                                             BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!isOnDeepslate(level, pos) || state.getValue(NoteBlock.NOTE) < DEEPSLATE_NOTE_COUNT - 1) {
            return;
        }
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(NoteBlock.NOTE, 0), 3);
            level.blockEvent(pos, state.getBlock(), 0, 0);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void willski$deepslateSound(BlockState state, Level level, BlockPos pos, int id, int param,
                                        CallbackInfoReturnable<Boolean> cir) {
        if (level.isClientSide() || !isOnDeepslate(level, pos)) {
            return;
        }

        int note = state.getValue(NoteBlock.NOTE);
        SoundEvent sound = DEEPSLATE_SOUNDS[note % DEEPSLATE_NOTE_COUNT];
        float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);
        level.playSound(null, pos, sound, SoundSource.RECORDS, 3.0F, pitch);
        cir.setReturnValue(true);
    }
}
