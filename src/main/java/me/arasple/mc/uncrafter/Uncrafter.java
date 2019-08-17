package me.arasple.mc.uncrafter;

import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.locale.logger.TLogger;
import me.arasple.mc.uncrafter.bstats.Metrics;
import me.arasple.mc.uncrafter.objects.UncrafterItem;

/**
 * @author Arasple
 * <p>
 * Uncrafter 分解者
 * <p>
 * - 分解已合成的物品为配方需求
 * - 强制物品为满耐久
 */
public final class Uncrafter extends Plugin {

    private static Uncrafter instance;
    private static UncrafterItem uncrafterItem;
    @TInject("§3L§bChat")
    private static TLogger logger;
    @TInject("settings.yml")
    private static TConfig settings;

    public static TLogger getTLogger() {
        return logger;
    }

    public static TConfig getSettings() {
        return settings;
    }

    public static Uncrafter getInstance() {
        return instance;
    }

    public static UncrafterItem getUncrafterItem() {
        return uncrafterItem;
    }

    @Override
    public void onStarting() {
        instance = this;

        settings.listener(() -> {
            loadUncrafterItem();
            getTLogger().fine("&7重新载入配置...");
        });
        loadUncrafterItem();

        new Metrics(this);
    }

    @Override
    public void onStopping() {

    }

    private void loadUncrafterItem() {
        uncrafterItem = new UncrafterItem(settings.getString("UNCREATER-ITEM.ID", "PLAYER_HEAD"), settings.getString("UNCREATER-ITEM.SKULL-TEXTURE", null), settings.getStringColored("UNCREATER-ITEM.NAME", "§f分解者"), settings.getStringListColored("UNCREATER-ITEM.LORES"), settings.getBoolean("UNCREATER-ITEM.SNEAKING-OPEN", false));
    }

}
