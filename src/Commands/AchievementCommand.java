package Commands;

import Listeners.InventoryListener;
import Universal.AchievementList;
import Universal.PlayerStats;
import Universal.Ui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AchievementCommand implements CommandExecutor {
    Ui ui = Ui.getInstance();
    AchievementList achievementList = AchievementList.INSTANCE;
    PlayerStats playerStats = PlayerStats.INSTANCE;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            Inventory inv = Bukkit.createInventory(p, 54, ChatColor.RED + ""+ org.bukkit.ChatColor.BOLD +"成就列表");
            ItemStack[] weapons = achievementList.achievements.clone();
            for (int i = 0; i < 52; i++) {
                if (i >= weapons.length) break;
                ItemStack item = weapons[i].clone();
                ItemMeta meta = item.getItemMeta();
                String name = meta.getDisplayName();
                String grant;
                if(playerStats.hasAchievement(p,i)){
                    item.setType(Material.EMERALD_BLOCK);
                    grant =ChatColor.GREEN + "【已获得】";
                }else {
                    grant = ChatColor.RED + "【未获得】";
                }
                String finalName = org.bukkit.ChatColor.RESET + "" + (i + 1) + "：" + name + grant;
                meta.setDisplayName(finalName);
                item.setItemMeta(meta);
                inv.addItem(item);
            }
            inv.setItem(52, ui.pageUp());
            inv.setItem(53, ui.pageDown());
            InventoryListener.playerInvStatus.put(p.getName(), InventoryListener.InvStatus.ACHIEVEMENT_MENU);
            p.openInventory(inv);
        }
        return true;
    }
}
