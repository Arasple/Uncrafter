package me.arasple.mc.uncrafter.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @author Arasple
 * @date 2019/8/17 16:29
 */
public class SkullUtils {

    public static ItemStack setTexture(ItemStack skull, String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        Field field;
        try {
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            profile.getProperties().put("textures", new Property("textures", texture, null));
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
            skull.setItemMeta(meta);
            return skull;
        } catch (ClassCastException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
