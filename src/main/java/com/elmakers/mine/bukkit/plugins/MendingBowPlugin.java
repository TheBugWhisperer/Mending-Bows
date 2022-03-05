package com.elmakers.mine.bukkit.plugins;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MendingBowPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Put here anything you want to happen when the server starts

        // Register our events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Put here anything you want to happen when the server stops
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack[] itemlist = event.getInventory().getContents();
        if (itemlist[0] == null || itemlist[1] == null ) return;

        if (itemlist[0].getType() != Material.BOW) return;
        if (itemlist[1].getType() != Material.ENCHANTED_BOOK) return;
        ItemStack book = itemlist[1];
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)book.getItemMeta();

        if (itemlist[0].containsEnchantment(Enchantment.MENDING)) {
            if (bookMeta.hasStoredEnchant(Enchantment.ARROW_INFINITE)) {
                event.getInventory().setRepairCost(5);
                ItemStack result = combineItem(itemlist[0],bookMeta);
                event.setResult(result);
            }

        }
        if (itemlist[0].containsEnchantment(Enchantment.ARROW_INFINITE)) {
            if (bookMeta.hasStoredEnchant(Enchantment.MENDING)) {
                event.getInventory().setRepairCost(5);
                ItemStack result = combineItem(itemlist[0],bookMeta);
                event.setResult(result);
            }
        }
    }

    private ItemStack combineItem(ItemStack bow,EnchantmentStorageMeta bookMeta){
        ItemStack result = bow.clone();
        for (Map.Entry<Enchantment, Integer> entry : bookMeta.getStoredEnchants().entrySet()) {
            result.addEnchantment(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
