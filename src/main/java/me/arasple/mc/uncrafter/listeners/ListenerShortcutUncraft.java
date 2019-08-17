package me.arasple.mc.uncrafter.listeners;

import com.google.common.collect.Lists;
import io.izzel.taboolib.module.inject.TListener;
import io.izzel.taboolib.module.locale.TLocale;
import me.arasple.mc.uncrafter.Uncrafter;
import me.arasple.mc.uncrafter.utils.RecipesUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * @author Arasple
 * @date 2019/8/17 17:02
 */
@TListener
public class ListenerShortcutUncraft implements Listener {

    private List<UUID> availables = Lists.newArrayList();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();

        if (Uncrafter.getSettings().getBoolean("UNCRAFT-SHORTCUT.ENABLE")) {
            if (e.isSneaking() && !availables.contains(p.getUniqueId())) {
                availables.add(p.getUniqueId());
                Bukkit.getScheduler().scheduleSyncDelayedTask(Uncrafter.getInstance(), () -> availables.remove(p.getUniqueId()), Uncrafter.getSettings().getLong("UNCRAFT-SHORTCUT.TIME"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();

        if (Uncrafter.getSettings().getBoolean("UNCRAFT-SHORTCUT.ENABLE") && availables.contains(p.getUniqueId()) && p.isSneaking()) {
            ItemStack drop = e.getItemDrop().getItemStack().clone();
            e.getItemDrop().getItemStack().setAmount(0);
            RecipesUtils.uncraftItems(drop).forEach(i -> p.getLocation().getWorld().dropItem(p.getLocation(), i));
            TLocale.sendTo(p, "UNCRAFT.DROP-UNCRAFT");
        }
    }

}
