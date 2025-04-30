package Commands;

import Listeners.InventoryListener;
import Universal.GameStatus;
import Universal.PlayerStats;
import Universal.Ui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OpenTeamMenuCommand implements CommandExecutor {
    Ui ui = Ui.getInstance();
    GameStatus gameStatus = GameStatus.getInstance();
    PlayerStats playerStats = PlayerStats.INSTANCE;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            openTeamMenu(p);
            InventoryListener.playerInvStatus.put(p.getName(), InventoryListener.InvStatus.TEAM_MENU);
        }
        return true;
    }

    public void openTeamMenu(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, ChatColor.AQUA+""+ ChatColor.BOLD + "选择队伍(点击图标即可选择队伍)");
        inv.addItem(new ItemStack(Material.LIGHT_BLUE_SHULKER_BOX));
        inv.addItem(new ItemStack(Material.ORANGE_SHULKER_BOX));
        inv.addItem(new ItemStack(Material.MAGENTA_SHULKER_BOX));
        inv.setItem(6, ui.spector());
        inv.setItem(7, ui.quit());
        inv.setItem(8, ui.refresh());
        if(playerStats.isReady(p)){
            inv.setItem(5,ui.ready());
        }else {
            inv.setItem(5,ui.notReady());
        }
        for (int i = 0; i < 3; i++) {
            ItemStack item = inv.getItem(i);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "队伍" + (i + 1));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "队员列表:");
            Player[] team = gameStatus.getTeamByID(i);
            int count = 1;
            for (Player player : team) {
                if (player == null) continue;
                String name = player.getName();
                lore.add(ChatColor.WHITE + "玩家" + count + "：" + ChatColor.AQUA + name);
                count += 1;
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        p.openInventory(inv);
    }
}
