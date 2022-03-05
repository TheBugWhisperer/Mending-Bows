package com.elmakers.mine.bukkit.plugins;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
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
        // Get the items in the anvil inventory, first two slots
        ItemStack[] itemList = event.getInventory().getContents();
        ItemStack bow = itemList[0];
        ItemStack book = itemList[1];

        // If either slot is empty, we are done
        if (bow == null || book == null ) return;

        // We need a bow in slot 1 and a book in slot 2
        if (bow.getType() != Material.BOW) return;
        if (book.getType() != Material.ENCHANTED_BOOK) return;

        // If the bow already has mending and infinity we are done
        if (bow.containsEnchantment(Enchantment.MENDING) && bow.containsEnchantment(Enchantment.ARROW_INFINITE)) {
            return;
        }

        // Get the book meta data, make sure it is not empty
        ItemMeta bookItemMeta = book.getItemMeta();
        if (bookItemMeta == null) {
            return;
        }
        // The book should have book meta, but let's check just in case
        if (!(bookItemMeta instanceof EnchantmentStorageMeta)) {
            return;
        }

        // We are now sure this is book meta, so cast it to that class
        // This way we can access enchanted book specific methods
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)bookItemMeta;

        // We can combine the two items if one has mending and the other has infinity
        boolean combine = false;

        // See if the bow has mending and the book has infinity
        if (bow.containsEnchantment(Enchantment.MENDING)) {
            if (bookMeta.hasStoredEnchant(Enchantment.ARROW_INFINITE)) {
                combine = true;
            }

        }

        // See if the bow has infinity and the book has mending
        if (bow.containsEnchantment(Enchantment.ARROW_INFINITE)) {
            if (bookMeta.hasStoredEnchant(Enchantment.MENDING)) {
                combine = true;
            }
        }

        // Combine the items if we can
        if (combine) {
            // Get and check the bow metadata
            ItemMeta bowMeta = bow.getItemMeta();
            if (bowMeta == null) {
                return;
            }

            // The bow should be repairable
            if (!(bowMeta instanceof Repairable)) {
                return;
            }
            Repairable repairableBow = (Repairable)bowMeta;

            // This adds 3 to the repair cost of the bow
            repairableBow.setRepairCost(repairableBow.getRepairCost() + 3);
            event.getInventory().setRepairCost(repairableBow.getRepairCost());

            ItemStack result = combineItem(bow, bookMeta);
            event.setResult(result);
        }
    }

    private ItemStack combineItem(ItemStack bow, EnchantmentStorageMeta bookMeta){
        ItemStack result = bow.clone();
        ItemMeta bowMeta = bow.getItemMeta();
        if (bowMeta == null) {
            return result;
        }
        for (Map.Entry<Enchantment, Integer> entry : bookMeta.getStoredEnchants().entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            // if this is not mending or infinity, we will first check that it's an ok enchantment to add
            if (!enchantment.equals(Enchantment.MENDING) && !enchantment.equals(Enchantment.ARROW_INFINITE)) {
                // If this enchant doesn't apply to bows, skip it
                if (!enchantment.canEnchantItem(bow)) {
                    continue;
                }

                // If there is some other conflicting enchant, skip it
                // Not sure this really applies to bows for
                if (bowMeta.hasConflictingEnchant(enchantment)) {
                    continue;
                }
            }

            // Add the enchantment to the bow
            result.addEnchantment(enchantment, level);
        }
        return result;
    }
}
