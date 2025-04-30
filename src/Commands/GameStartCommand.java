package Commands;

import Listeners.InventoryListener;
import Universal.Ui;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GameStartCommand implements CommandExecutor {
    Ui ui = Ui.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            openStartMenu(p);
            InventoryListener.playerInvStatus.put(p.getName(), InventoryListener.InvStatus.MAP_MENU);
        }
        return true;
    }

    public void openStartMenu(Player p){
        Inventory inv = Bukkit.createInventory(p, 9, ChatColor.GOLD +""+ ChatColor.BOLD + "选择地图");
        inv.addItem(ui.poorStadium());
        inv.addItem(ui.monaco());
        inv.addItem(ui.sandStadium());
        inv.addItem(ui.kyoto());
        inv.addItem(ui.sys());
        inv.addItem(ui.mansion());
        p.openInventory(inv);
    }
}
