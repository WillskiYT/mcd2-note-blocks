package com.willski.noteglow;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public final class DeepslateSequence {
    // Notes to play on deepslate note blocks, in order.
    private static final int[] ORDER = {0, 2, 6, 5, 4, 1, 3, 7};
    // Delay before the sound plays: 3 seconds = 60 ticks.
    private static final long DELAY_TICKS = 60;

    private static final SoundEvent SING = SoundEvent.createVariableRangeEvent(
            Identifier.fromNamespaceAndPath("willski_noteglow", "sing"));

    private static int progress = 0;
    private static ServerLevel pendingLevel = null;
    private static BlockPos pendingPos = null;
    private static long pendingTick = 0;

    private DeepslateSequence() {
    }

    public static void onNotePlayed(ServerLevel level, BlockPos pos, int note) {
        if (note == ORDER[progress]) {
            progress++;
        } else {
            progress = note == ORDER[0] ? 1 : 0;
        }

        if (progress == ORDER.length) {
            progress = 0;
            pendingLevel = level;
            pendingPos = pos.immutable();
            pendingTick = level.getGameTime() + DELAY_TICKS;
        }
    }

    public static void tick() {
        if (pendingLevel != null && pendingLevel.getGameTime() >= pendingTick) {
            pendingLevel.playSound(null, pendingPos, SING, SoundSource.RECORDS, 3.0F, 1.0F);
            pendingLevel = null;
            pendingPos = null;
        }
    }
}
