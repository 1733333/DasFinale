package Commands;

import Universal.PlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSkillCommand implements CommandExecutor {
    PlayerStats stats = PlayerStats.INSTANCE;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player p) {
            try {
                if (!p.isOp()) return true;
                int skill = Integer.parseInt(strings[0]);
                stats.setSkill(p,skill);
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Oops,输入的值好像不是数字");
            }
        }
        return true;
    }
}
