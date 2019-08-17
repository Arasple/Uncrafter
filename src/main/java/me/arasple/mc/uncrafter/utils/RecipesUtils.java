package me.arasple.mc.uncrafter.utils;

import com.google.common.collect.Lists;
import me.arasple.mc.uncrafter.Uncrafter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arasple
 * @date 2019/8/17 15:34
 */
public class RecipesUtils {

    public static List<ItemStack> uncraftItems(ItemStack... itemStacks) {
        return uncraftItems(Arrays.asList(itemStacks));
    }

    public static List<ItemStack> uncraftItems(List<ItemStack> itemStacks) {
        List<ItemStack> results = Lists.newArrayList();

        for (ItemStack itemStack : itemStacks) {
            ItemStack copyer = new ItemStack(itemStack.getType(), itemStack.getAmount());

            if (fitConditions(itemStack) && Bukkit.getRecipesFor(copyer).size() > 0) {
                ItemMeta meta = itemStack.getItemMeta();

                if (meta.getEnchants().size() > 0 && Uncrafter.getSettings().getBoolean("UNCRAFT.ENCHANTED-ITEMS.RETURN-ENCHANT-BOOKS")) {
                    meta.getEnchants().forEach((key, value) -> {
                        ItemStack enchatedBook = new ItemStack(Material.ENCHANTED_BOOK);
                        ItemMeta eMeta = enchatedBook.getItemMeta();
                        eMeta.addEnchant(key, value, true);
                        enchatedBook.setItemMeta(eMeta);
                        results.add(enchatedBook);
                    });
                }

                Recipe recipe = Bukkit.getRecipesFor(copyer).get(0);
                if (recipe instanceof ShapedRecipe) {
                    for (int i = 0; i < itemStack.getAmount(); i++) {
                        results.addAll(((ShapedRecipe) recipe).getIngredientMap().values());
                    }
                } else if (recipe instanceof ShapelessRecipe) {
                    for (int i = 0; i < itemStack.getAmount(); i++) {
                        results.addAll(((ShapelessRecipe) recipe).getIngredientList());
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
        return results.stream().filter(x -> x != null && x.getType() != Material.AIR).collect(Collectors.toList());
    }

    private static boolean fitConditions(ItemStack itemStack) {
        String material = itemStack.getType().name();
        List<String> lore = itemStack.getLore();

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

        return true;
    }


}
