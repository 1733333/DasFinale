package Commands;

import Events.GameEndEvent;
import Events.GameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameEndCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        World w = null;
        if(commandSender instanceof BlockCommandSender b){
            w = b.getBlock().getWorld();
        }else if(commandSender instanceof Player p) {
            Bukkit.broadcastMessage(ChatColor.RED + p.getName() + "使用指令停止了游戏！");
            w = p.getWorld();
        }
        if(w != null) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(w,-1));
        }else {
            Bukkit.broadcastMessage(ChatColor.RED + "执行命令时出现错误");
        }
        return true;
    }
}
