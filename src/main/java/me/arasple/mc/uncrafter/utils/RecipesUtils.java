package me.arasple.mc.uncrafter.utils;

import com.google.common.collect.Lists;
import io.izzel.taboolib.module.inject.TSchedule;
import io.izzel.taboolib.module.locale.TLocale;
import me.arasple.mc.uncrafter.Uncrafter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arasple
 * @date 2019/8/17 15:34
 */
public class RecipesUtils {

    private static HashMap<Material, Recipe> caches;

    @TSchedule(delay = 1, async = true)
    static void init() {
        caches = new HashMap<>();
        for (Material value : Material.values()) {
            ItemStack example = new ItemStack(value, 1);
            if (Bukkit.getRecipesFor(example).size() > 0) {
                caches.put(value, Bukkit.getRecipesFor(example).stream().findFirst().orElse(null));
            }
        }
        caches.entrySet().removeIf(x -> x.getValue() == null || x.getValue().getResult().getAmount() != 1);

        TLocale.sendToConsole("PLUGIN.CACHED", caches.size());
    }

    public static List<ItemStack> uncraftItems(boolean uncraftEnchants, ItemStack... itemStacks) {
        return uncraftItems(Arrays.asList(itemStacks), uncraftEnchants);
    }

    public static List<ItemStack> uncraftItems(List<ItemStack> itemStacks, boolean uncraftEnchants) {
        List<ItemStack> results = Lists.newArrayList();

        for (ItemStack itemStack : itemStacks) {
            if (fitConditions(itemStack)) {
                ItemMeta meta = itemStack.getItemMeta();
                int amount = itemStack.getAmount();
                Recipe recipe = caches.get(itemStack.getType());

                if (uncraftEnchants && meta.getEnchants().size() > 0 && Uncrafter.getSettings().getBoolean("UNCRAFT.ENCHANTED-ITEMS.RETURN-ENCHANT-BOOKS")) {
                    for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                        EnchantmentStorageMeta eMeta = (EnchantmentStorageMeta) book.getItemMeta();
                        eMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                        book.setItemMeta(eMeta);
                        results.add(book);
                    }
                }

                if (recipe instanceof ShapedRecipe) {
                    for (int i = 0; i < amount; i++) {
                        results.addAll(((ShapedRecipe) recipe).getIngredientMap().values());
                    }
                } else if (recipe instanceof ShapelessRecipe) {
                    for (int i = 0; i < amount; i++) {
                        results.addAll(((ShapelessRecipe) recipe).getIngredientList());
                    }
                } else if (recipe instanceof MerchantRecipe) {
                    for (int i = 0; i < amount; i++) {
                        results.addAll(((MerchantRecipe) recipe).getIngredients());
                    }
                } else {
                    results.add(itemStack);
                }
            } else {
                results.add(itemStack);
            }
        }
        return merge(results);
    }

    private static List<ItemStack> merge(List<ItemStack> results) {
        results.removeIf(r -> r == null || r.getType() == Material.AIR);

        for (int i = 0; i < results.size(); i++) {
            for (ItemStack item : results) {
                if (results.get(i) != item && results.get(i).isSimilar(item)) {
                    results.get(i).setAmount(results.get(i).getAmount() + item.getAmount());
                    item.setAmount(0);
                }
            }
        }
        return results.stream().filter(x -> x != null && x.getType() != Material.AIR && x.getAmount() > 0).collect(Collectors.toList());
    }

    private static boolean fitConditions(ItemStack itemStack) {
        String material = itemStack.getType().name();
        List<String> lore = itemStack.hasItemMeta() ? itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : null : null;

        if (Uncrafter.getSettings().getBoolean("UNCRAFT.FULL-DURABILITY") && itemStack.getDurability() != 0) {
            return false;
        }
        if (Uncrafter.getSettings().getBoolean("UNCRAFT.CUSTOM-NAME") && itemStack.getItemMeta().hasDisplayName()) {
            return false;
        }
        if (!Uncrafter.getSettings().getBoolean("UNCRAFT.ENCHANTED-ITEMS.ALLOW") && itemStack.getEnchantments().size() > 0) {
            return false;
        }
        for (String s : Uncrafter.getSettings().getStringList("UNCRAFT.BLACKLIST.MATERIALS")) {
            if (s.equalsIgnoreCase(material)) {
                return false;
            }
        }

        if (lore != null && lore.size() > 0) {
            for (String s : Uncrafter.getSettings().getStringList("UNCRAFT.BLACKLIST.LORES")) {
                for (String l : lore) {
                    if (l.toLowerCase().contains(s.toLowerCase())) {
                        return false;
                    }
                }
            }
        }
        return caches.containsKey(itemStack.getType());
    }


}
