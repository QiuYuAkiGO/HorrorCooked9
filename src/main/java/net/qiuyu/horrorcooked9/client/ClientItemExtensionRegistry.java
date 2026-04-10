package net.qiuyu.horrorcooked9.client;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.qiuyu.horrorcooked9.common.ClientItemExtensionsBridge;
import net.qiuyu.horrorcooked9.client.renderer.CaptainHatRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 统一管理客户端物品扩展注册，便于后续新增更多物品渲染扩展。
 */
public final class ClientItemExtensionRegistry implements ClientItemExtensionsBridge.Handler {
    private static final ClientItemExtensionRegistry INSTANCE = new ClientItemExtensionRegistry();
    private final Map<String, Supplier<IClientItemExtensions>> extensions = new HashMap<>();

    private ClientItemExtensionRegistry() {
        register("horrorcooked9:captain_hat", () -> CaptainHatRenderer.CLIENT_EXTENSIONS);
    }

    public static void install() {
        ClientItemExtensionsBridge.install(INSTANCE);
    }

    public void register(String itemId, Supplier<IClientItemExtensions> extensionSupplier) {
        if (itemId == null || extensionSupplier == null) {
            return;
        }
        extensions.put(itemId, extensionSupplier);
    }

    @Override
    public void initialize(String itemId, Consumer<IClientItemExtensions> consumer) {
        if (consumer == null) {
            return;
        }
        Supplier<IClientItemExtensions> supplier = extensions.get(itemId);
        if (supplier == null) {
            return;
        }
        IClientItemExtensions extension = supplier.get();
        if (extension != null) {
            consumer.accept(extension);
        }
    }
}
