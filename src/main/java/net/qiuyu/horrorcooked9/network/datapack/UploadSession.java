package net.qiuyu.horrorcooked9.network.datapack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public record UploadSession(
        UUID playerId,
        String packName,
        int totalSize,
        int totalChunks,
        String sha256,
        Map<Integer, byte[]> chunks,
        AtomicInteger receivedBytes
) {
    public UploadSession(UUID playerId, String packName, int totalSize, int totalChunks, String sha256) {
        this(playerId, packName, totalSize, totalChunks, sha256, new ConcurrentHashMap<>(), new AtomicInteger(0));
    }
}
