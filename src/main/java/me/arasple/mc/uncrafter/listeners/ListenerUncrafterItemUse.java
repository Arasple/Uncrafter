package me.arasple.mc.uncrafter.listeners;

import io.izzel.taboolib.module.inject.TListener;
import me.arasple.mc.uncrafter.Uncrafter;
import me.arasple.mc.uncrafter.objects.UncrafterItem;
import me.arasple.mc.uncrafter.objects.UncrafterMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Arasple
 * @date 2019/8/17 17:02
 */
@TListener
public class ListenerUncrafterItemUse implements Listener {

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getItem() != null) {
            ItemStack item = e.getItem();
            if (item.hasItemMeta() && item.getItemMeta().getLore() != null && item.getItemMeta().getLore().size() > 0 && item.getItemMeta().getLore().get(0).equals(UncrafterItem.getUncrafterSign())) {
                if (Uncrafter.getUncrafterItem().isSneaking() && !p.isSneaking()) {
                    return;
                }
                // Open uncrafter menu
                UncrafterMenu.openFor(p);
                e.setCancelled(true);
            }
        }

    }

}
