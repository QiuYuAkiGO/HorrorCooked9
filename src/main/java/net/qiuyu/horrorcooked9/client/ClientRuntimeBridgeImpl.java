package net.qiuyu.horrorcooked9.client;

import net.minecraft.core.BlockPos;
import net.qiuyu.horrorcooked9.common.ClientRuntimeBridge;

public final class ClientRuntimeBridgeImpl implements ClientRuntimeBridge.Handler {

    @Override
    public void openChopMinigame(BlockPos pos) {
        ClientHelper.openChopMinigame(pos);
    }

    @Override
    public void openStirMinigame(BlockPos pos, int requiredStirCount) {
        ClientHelper.openStirMinigame(pos, requiredStirCount);
    }

    @Override
    public void openDataPackPicker(long maxUploadBytes) {
        DataPackUploadClient.openFilePicker(maxUploadBytes);
    }
}
