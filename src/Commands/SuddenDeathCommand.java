package Commands;

import Events.SuddenDeathEvent;
import Universal.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SuddenDeathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            try {
                if (!p.isOp()) return true;
                World w = p.getWorld();
                int rule = Integer.parseInt(strings[0]);
                Bukkit.getPluginManager().callEvent(new SuddenDeathEvent(w,rule));
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }
        return true;
    }
}
