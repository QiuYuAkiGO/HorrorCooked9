package net.qiuyu.horrorcooked9.common;

import net.minecraft.core.BlockPos;

import java.util.Objects;

/**
 * Common-side bridge for client-only actions.
 * Default behavior is no-op until installed on client setup.
 */
public final class ClientRuntimeBridge {

    private static volatile Handler HANDLER = new NoopHandler();

    private ClientRuntimeBridge() {
    }

    public static void install(Handler handler) {
        HANDLER = Objects.requireNonNull(handler, "handler");
    }

    public static void openChopMinigame(BlockPos pos) {
        HANDLER.openChopMinigame(pos);
    }

    public static void openStirMinigame(BlockPos pos, int requiredStirCount) {
        HANDLER.openStirMinigame(pos, requiredStirCount);
    }

    public static void openDataPackPicker(long maxUploadBytes) {
        HANDLER.openDataPackPicker(maxUploadBytes);
    }

    public interface Handler {
        default void openChopMinigame(BlockPos pos) {
        }

        default void openStirMinigame(BlockPos pos, int requiredStirCount) {
        }

        default void openDataPackPicker(long maxUploadBytes) {
        }
    }

    private static final class NoopHandler implements Handler {
    }
}
