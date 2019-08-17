package me.arasple.mc.uncrafter.objects;

import me.arasple.mc.uncrafter.utils.SkullUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

/**
 * @author Arasple
 * @date 2019/8/17 16:39
 */
public class UncrafterItem {

    private static final String UNCRAFTER_SIGN = "§0UNCRAFTER_ITEM";
    private static boolean newVersion;

    static {
        try {
            Material.valueOf("SKULL");
            newVersion = false;
        } catch (Exception e) {
            newVersion = true;
        }
    }

    private Material material;
    private String texture;
    private String name;
    private List<String> lores;
    private boolean sneaking;
    private ItemStack cache;

    public UncrafterItem(String material, String texture, String name, List<String> lores, boolean sneaking) {
        this(getMaterial(material), texture, name, lores, sneaking);
    }

    public UncrafterItem(Material material, String texture, String name, List<String> lores, boolean sneaking) {
        this.material = material;
        this.texture = texture;
        this.name = name;
        this.lores = lores;
        this.sneaking = sneaking;
        if (lores != null) {
            this.lores.add(0, UNCRAFTER_SIGN);
        } else {
            this.lores = Collections.singletonList(UNCRAFTER_SIGN);
        }
    }

    public static String getUncrafterSign() {
        return UNCRAFTER_SIGN;
    }

    private static Material getMaterial(String material) {
        Material mat;
        try {
            mat = Material.valueOf(material);
        } catch (Exception e) {
            mat = getDefaultMaterial();
        }
        return mat;
    }

    private static Material getDefaultMaterial() {
        return newVersion ? Material.valueOf("PLAYER_HEAD") : Material.valueOf("SKULL");
    }

    public static boolean isNewVersion() {
        return newVersion;
    }

    public ItemStack getItem() {
        return cache != null ? cache : buildItem();
    }

    private ItemStack buildItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof SkullMeta && texture != null) {
            itemStack = SkullUtils.setTexture(itemStack, texture);
            assert itemStack != null;
            itemMeta = itemStack.getItemMeta();
        }
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        cache = itemStack;
        return cache;
    }

    public boolean isSneaking() {
        return sneaking;
    }

}
