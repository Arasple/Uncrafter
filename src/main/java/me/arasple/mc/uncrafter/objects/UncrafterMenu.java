package me.arasple.mc.uncrafter.objects;

import io.izzel.taboolib.cronus.CronusUtils;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.util.item.ItemBuilder;
import io.izzel.taboolib.util.item.inventory.MenuBuilder;
import io.izzel.taboolib.util.lite.Materials;
import me.arasple.mc.uncrafter.Uncrafter;
import me.arasple.mc.uncrafter.utils.InvUtils;
import me.arasple.mc.uncrafter.utils.RecipesUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Arasple
 * @date 2019/8/17 17:05
 */
public class UncrafterMenu {


    public static void openFor(Player player) {
        player.openInventory(
                new MenuBuilder(Uncrafter.getInstance())
                        .title(TLocale.asString("UNCRAFTER-GUI.TITLE"))
                        .rows(6)
                        .items(
                                "#########",
                                "         ",
                                "         ",
                                "         ",
                                "         ",
                                "####Y####"
                        )
                        .put('#', new ItemBuilder(Materials.GRAY_STAINED_GLASS_PANE.parseMaterial()).name("§8Uncrafter").build())
                        .put('Y', new ItemBuilder(Materials.LIME_STAINED_GLASS_PANE.parseMaterial()).name("§a确认分解").lore("", "§c一旦确认分解，操作不可逆.", "", "§7系统将试图将你放进的物品", "§7还原成基础材料.").build())
                        .close(e -> {
                            Player p = (Player) e.getPlayer();
                            Inventory inv = e.getInventory();
                            InvUtils.collectItems(inv, 9, 44).forEach(i -> p.getInventory().addItem(i));
                            TLocale.sendTo(player, "UNCRAFTER-GUI.CLOSE");
                        })
                        .event(e -> {
                            Player p = e.getClicker();
                            Inventory inv;
                            try {
                                inv = e.castClick().getInventory();
                            } catch (ClassCastException ex) {
                                inv = e.castDrag().getInventory();
                            }

                            char slot = e.getSlot();

                            if (slot != ' ') {
                                e.castClick().setCancelled(true);

                                if (slot == 'Y') {
                                    List<ItemStack> items = InvUtils.collectItems(inv, 9, 44);
                                    if (items.size() == 0) {
                                        p.closeInventory();
                                        TLocale.sendTo(p, "UNCRAFT.EMPTY");
                                        return;
                                    }

                                    List<ItemStack> results = RecipesUtils.uncraftItems(items, p.hasPermission("uncrafter.uncraft.enchants"));

                                    for (int i = 9; i < 44; i++) {
                                        inv.setItem(i, null);
                                    }
                                    p.closeInventory();

                                    if (Uncrafter.getSettings().getBoolean("UNCRAFT.REQUIRED-FREE-SLOTS") && InvUtils.getFreeSlots(p.getInventory()) < results.size()) {
                                        TLocale.sendTo(p, "UNCRAFT.INV-NO-ROOM", String.valueOf(results.size()), String.valueOf(InvUtils.getFreeSlots(p.getInventory())));
                                        items.forEach(i -> CronusUtils.addItem(p, i));
                                    } else {
                                        results.forEach(i -> p.getInventory().addItem(i).values().forEach(item -> p.getWorld().dropItemNaturally(p.getLocation(), item)));
                                        TLocale.sendTo(p, "UNCRAFT.SUCCESS");
                                    }
                                }
                            }
                        })
                        .build()
        );
        TLocale.sendTo(player, "UNCRAFTER-GUI.OPEN");
    }

}
