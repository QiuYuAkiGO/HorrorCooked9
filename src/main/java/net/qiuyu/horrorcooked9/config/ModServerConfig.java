package net.qiuyu.horrorcooked9.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ModServerConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DATAPACK_UPLOAD;
    public static final ForgeConfigSpec.LongValue MAX_UPLOAD_SIZE_MB;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("datapackUpload");
        ENABLE_DATAPACK_UPLOAD = builder
                .comment("Whether /datapack upload is enabled on dedicated servers.")
                .define("enabled", true);
        MAX_UPLOAD_SIZE_MB = builder
                .comment("Maximum uploaded datapack archive size in MB.")
                .defineInRange("maxUploadSizeMb", 64L, 1L, 512L);
        builder.pop();

        SPEC = builder.build();
    }

    private ModServerConfig() {
    }

    public static boolean isDatapackUploadEnabled() {
        return ENABLE_DATAPACK_UPLOAD.get();
    }

    public static long getMaxUploadSizeBytes() {
        return MAX_UPLOAD_SIZE_MB.get() * 1024L * 1024L;
    }
}
