package Commands;

import Listeners.InventoryListener;
import Universal.Items;
import Universal.Ui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GetItemCommand implements CommandExecutor {
    Items items = Items.getInstance();
    Ui ui = Ui.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            Inventory inv = Bukkit.createInventory(p, 54, ChatColor.RED + ""+ org.bukkit.ChatColor.BOLD +"物品列表");
            ItemStack[] weapons = Items.getInstance().getItems();
            for (int i = 0; i < 52; i++) {
                if (i >= weapons.length) break;
                inv.addItem(weapons[i]);
            }
            inv.setItem(52, ui.pageUp());
            inv.setItem(53, ui.pageDown());
            InventoryListener.playerInvStatus.put(p.getName(), InventoryListener.InvStatus.DEV_MENU);
            p.openInventory(inv);
            try {
                if(p.isOp()) {
                    int id = Integer.parseInt(strings[0]);
                    p.getInventory().addItem(items.devItems[id]);
                }
            }catch (Exception ignored){

            }
        }
        return true;
    }
}
