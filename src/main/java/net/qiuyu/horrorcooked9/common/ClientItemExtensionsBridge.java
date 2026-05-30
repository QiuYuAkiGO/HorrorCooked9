package net.qiuyu.horrorcooked9.common;

import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 客户端物品扩展桥接，避免非 client 包直接依赖 client 实现类。
 */
public final class ClientItemExtensionsBridge {
    private static volatile Handler handler = new NoopHandler();

    private ClientItemExtensionsBridge() {
    }

    public static void install(Handler newHandler) {
        handler = Objects.requireNonNullElseGet(newHandler, NoopHandler::new);
    }

    public static void initialize(String itemId, Consumer<IClientItemExtensions> consumer) {
        handler.initialize(itemId, consumer);
    }

    public interface Handler {
        void initialize(String itemId, Consumer<IClientItemExtensions> consumer);
    }

    private static final class NoopHandler implements Handler {
        @Override
        public void initialize(String itemId, Consumer<IClientItemExtensions> consumer) {
            // no-op on dedicated server or before client bootstrap
        }
    }
}
