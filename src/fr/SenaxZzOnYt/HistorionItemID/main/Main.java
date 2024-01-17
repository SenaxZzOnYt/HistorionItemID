package fr.SenaxZzOnYt.HistorionItemID.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin implements CommandExecutor {

    private Map<UUID, String> itemIDs = new HashMap<>();

    @Override
    public void onEnable() {
        // Charger la configuration et enregistrer le gestionnaire de commandes
        this.saveDefaultConfig();
        getCommand("checkdupli").setExecutor(this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("checkdupli") && sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasLore()) {
                String itemID = itemInHand.getItemMeta().getLore().get(0);
                if (itemIDs.containsValue(itemID)) {
                    player.sendMessage(getConfig().getString("messages.duplication-detected"));
                } else {
                    player.sendMessage(getConfig().getString("messages.item-unique"));
                }
            } else {
                player.sendMessage(getConfig().getString("messages.no-unique-id"));
            }

            return true;
        }

        return false;
    }

    private class ItemListener implements Listener {
        @EventHandler
        public void onItemSpawn(ItemSpawnEvent event) {
            ItemStack itemStack = event.getEntity().getItemStack();
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                ItemMeta meta = itemStack.getItemMeta();
                String itemID = UUID.randomUUID().toString();
                List<String> lore = new ArrayList<>();
                lore.add(itemID);
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Optional: Hides attributes in item lore
                itemStack.setItemMeta(meta);
                itemIDs.put(event.getEntity().getUniqueId(), itemID);
            }
        }
    }
}
