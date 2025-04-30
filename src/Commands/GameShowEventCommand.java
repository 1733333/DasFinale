package Commands;

import Events.GameShowEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameShowEventCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p) {
            if (!p.isOp()) return true;
            World w = p.getWorld();
            try {
                int id = Integer.parseInt(strings[0]);
                Bukkit.getPluginManager().callEvent(new GameShowEvent(w, id, true));
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }
        return true;
    }
}
