package Commands;

import Events.GameStartEvent;
import Listeners.InventoryListener;
import Universal.GameStatus;
import Universal.Ui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SetGameModeCommand implements CommandExecutor {
    Ui ui = Ui.getInstance();
    GameStatus gameStatus = GameStatus.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            World w = p.getWorld();
            openModeMenu(p);
            InventoryListener.playerInvStatus.put(p.getName(), InventoryListener.InvStatus.GAMEMODE_MENU);
            try {
                int mode = Integer.parseInt(strings[0]);
                gameStatus.setGameMode(w,mode);
            } catch (Exception ignored) {
            }
        }
        return true;
    }
    public void openModeMenu(Player p){
        World w = p.getWorld();
        String mode = switch(gameStatus.getGameMode(w)){
            case 1 -> "存钱至上";
            case 2 -> "xX_CNS_GLITCHCRAFT_Xx";
            default -> "快速提现";
        };
        Inventory inv = Bukkit.createInventory(p, 9, ChatColor.DARK_AQUA +""+ ChatColor.BOLD + "当前模式：" + mode);
        inv.addItem(ui.quickCash());
        inv.addItem(ui.bankIt());
        inv.addItem(ui.witchCraft());
        p.openInventory(inv);
    }
}
