package Commands;

import Listeners.GameListeners;
import Universal.Items;
import Universal.Kits;
import Universal.PlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSelectedLoadOutCommand implements CommandExecutor {
    PlayerStats playerStats = PlayerStats.INSTANCE;
    GameListeners gameListeners = new GameListeners();
    Kits k = Kits.getInstance();
    Items items = Items.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            try {
                int loadOut = Integer.parseInt(strings[0]);
                if(loadOut == 0){
                    removePlayerLoadOut(p);
                }else {
                    gameListeners.setPlayerLoadOut(p);
                }
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }
        return true;
    }

    public void removePlayerLoadOut(Player p){
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
        p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
        p.getInventory().clear();
    }
}
