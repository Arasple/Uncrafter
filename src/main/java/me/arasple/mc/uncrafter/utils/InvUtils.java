package me.arasple.mc.uncrafter.utils;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arasple
 * @date 2019/8/17 18:06
 */
public class InvUtils {

    public static List<ItemStack> collectItems(Inventory inv, int from, int to) {
        List<ItemStack> items = Lists.newArrayList();

        for (int i = from; i < to; i++) {
            items.add(inv.getItem(i));
        }

        return items.stream().filter(i -> i != null && i.getType() != Material.AIR).collect(Collectors.toList());
    }

    public static int getFreeSlots(Inventory inv) {
        return (int) Arrays.stream(inv.getContents()).filter(i -> i == null || i.getType() == Material.AIR).count();
    }

}
