import Commands.*;
import Listeners.*;
import Universal.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class DF extends JavaPlugin {
    PlayerStats playerStats = PlayerStats.INSTANCE;
    GameStatus gameStatus = GameStatus.getInstance();
    public void onEnable() {
        for(Player p : this.getServer().getOnlinePlayers()){
            p.sendTitle(ChatColor.AQUA + "！插件已重新加载！"," ",10,20,10);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
        }
        PluginManager manager = this.getServer().getPluginManager();
        Kits k = Kits.getInstance();
        BroadCast broadCast = BroadCast.getInstance();

        SkillListeners skillListeners = new SkillListeners();
        GameListeners gameListeners = new GameListeners();
        OtherStuffListener otherStuffListener = new OtherStuffListener();
        GadgetsListener gadgetsListener = new GadgetsListener();
        InventoryListener inventoryListener = new InventoryListener();
        WeaponListener weaponListener = new WeaponListener();
        AchievementListener achievementListener = new AchievementListener();
        SummonVaultCommand summonVaultCommand = new SummonVaultCommand();
        SummonCashBoxCommand summonCashBoxCommand = new SummonCashBoxCommand();
        SummonCashOutStationCommand summonCashOutStationCommand = new SummonCashOutStationCommand();
        SummonDamageTester summonDamageTester = new SummonDamageTester();
        SummonJarCommand summonJarCommand = new SummonJarCommand();
        GetItemCommand getItemCommand = new GetItemCommand();
        SetSkillCommand setSkillCommand = new SetSkillCommand();
        OpenConstantMenuCommand openConstantMenuCommand = new OpenConstantMenuCommand();
        OpenTeamMenuCommand openTeamMenuCommand = new OpenTeamMenuCommand();
        GameShowEventCommand gameShowEventCommand = new GameShowEventCommand();
        GameStartCommand gameStartCommand = new GameStartCommand();
        GameEndCommand gameEndCommand = new GameEndCommand();
        SetSelectedLoadOutCommand setSelectedLoadOutCommand = new SetSelectedLoadOutCommand();
        SetJumpPointCommand setJumpPointCommand = new SetJumpPointCommand();
        SetGameModeCommand setGameModeCommand = new SetGameModeCommand();
        SummonBankStationCommand summonBankStationCommand = new SummonBankStationCommand();
        SuddenDeathCommand suddenDeathCommand = new SuddenDeathCommand();
        AchievementCommand achievementCommand = new AchievementCommand();

        k.setPlugin(this);
        broadCast.setPlugin(this);
        skillListeners.setPlugin(this);
        gameListeners.setPlugin(this);
        gadgetsListener.setPlugin(this);
        inventoryListener.setPlugin(this);
        weaponListener.setPlugin(this);
        otherStuffListener.setPlugin(this);
        setJumpPointCommand.setPlugin(this);

        manager.registerEvents(skillListeners,this);
        manager.registerEvents(gameListeners,this);
        manager.registerEvents(otherStuffListener,this);
        manager.registerEvents(gadgetsListener,this);
        manager.registerEvents(inventoryListener,this);
        manager.registerEvents(weaponListener,this);
        manager.registerEvents(achievementListener,this);

        this.getCommand("vault").setExecutor(summonVaultCommand);
        this.getCommand("cashbox").setExecutor(summonCashBoxCommand);
        this.getCommand("cashoutstation").setExecutor(summonCashOutStationCommand);
        this.getCommand("damagetest").setExecutor(summonDamageTester);
        this.getCommand("getitem").setExecutor(getItemCommand);
        this.getCommand("summonjar").setExecutor(summonJarCommand);
        this.getCommand("setskill").setExecutor(setSkillCommand);
        this.getCommand("contestant").setExecutor(openConstantMenuCommand);
        this.getCommand("teams").setExecutor(openTeamMenuCommand);
        this.getCommand("gameshowevent").setExecutor(gameShowEventCommand);
        this.getCommand("gamestart").setExecutor(gameStartCommand);
        this.getCommand("gameend").setExecutor(gameEndCommand);
        this.getCommand("loadout").setExecutor(setSelectedLoadOutCommand);
        this.getCommand("jumppoint").setExecutor(setJumpPointCommand);
        this.getCommand("setgamemode").setExecutor(setGameModeCommand);
        this.getCommand("bankstation").setExecutor(summonBankStationCommand);
        this.getCommand("suddendeath").setExecutor(suddenDeathCommand);
        this.getCommand("achievements").setExecutor(achievementCommand);

        for(Player p : this.getServer().getOnlinePlayers()){
            int[][]loadOuts = new int[6][];
            List<Integer> recordedAchievements = this.getConfig().getIntegerList(
                    "DasFinale.Achievements." + p.getName());
            Integer[] recordedSLoadOut = this.getConfig().getIntegerList(
                    "DasFinale.SelectedLoadOut." + p.getName()).toArray(new Integer[0]);
            int[]selectedLoadOut = Arrays.stream(recordedSLoadOut).mapToInt(Integer::valueOf).toArray();
            for(int i = 0; i < 6;i ++) {
                Integer[] recordedLoadOut = this.getConfig().getIntegerList(
                        "DasFinale.LoadOuts." + p.getName() + "." + i).toArray(new Integer[0]);
                int[]loadOut = Arrays.stream(recordedLoadOut).mapToInt(Integer::valueOf).toArray();
                loadOuts[i] = loadOut;
            }
            playerStats.setSelectedLoadOut(p,selectedLoadOut);
            playerStats.setPlayerLoadOuts(p,loadOuts);
            playerStats.readPlayerAchievements(p,recordedAchievements);
        }
        for(OfflinePlayer op : this.getServer().getOfflinePlayers()) {
            Player p = op.getPlayer();
            if (p != null) {
                int[][]loadOuts = new int[6][];
                Integer[] recordedSLoadOut = this.getConfig().getIntegerList(
                        "DasFinale.SelectedLoadOut." + p.getName()).toArray(new Integer[0]);
                List<Integer> recordedAchievements = this.getConfig().getIntegerList(
                        "DasFinale.Achievements." + p.getName());
                int[]selectedLoadOut = Arrays.stream(recordedSLoadOut).mapToInt(Integer::valueOf).toArray();
                for(int i = 0; i < 6;i ++) {
                    Integer[] recordedLoadOut = this.getConfig().getIntegerList(
                            "DasFinale.LoadOuts." + p.getName() + "." + i).toArray(new Integer[0]);
                    int[]loadOut = Arrays.stream(recordedLoadOut).mapToInt(Integer::valueOf).toArray();
                    if(loadOut.length != 6){
                        loadOut = playerStats.getDefaultLoadOut()[i];
                    }
                    loadOuts[i] = loadOut;
                }
                playerStats.setSelectedLoadOut(p,selectedLoadOut);
                playerStats.setPlayerLoadOuts(p,loadOuts);
                playerStats.readPlayerAchievements(p,recordedAchievements);
            }
        }
    }

    public void onDisable() {
        for(Player p : this.getServer().getOnlinePlayers()){
            p.closeInventory();
            List<Integer> achievements = playerStats.getPlayerAchievements(p);
            List<Integer> recordedAchievements = this.getConfig().getIntegerList(
                    "DasFinale.Achievements." + p.getName());
            if(recordedAchievements.size() < achievements.size()){
                this.getConfig().set("DasFinale.Achievements." + p.getName(), achievements);
            }
            int[]currentLoadOut = playerStats.getSelectedLoadOut(p);
            int[][]loadOuts = playerStats.getPlayerLoadOuts(p);
            this.getConfig().set("DasFinale.SelectedLoadOut." + p.getName(), currentLoadOut);
            for(int i = 0; i < loadOuts.length;i ++){
                int[]loadOut = loadOuts[i];
                this.getConfig().set("DasFinale.LoadOuts." + p.getName() + "." + i,loadOut);
            }
        }
        for(OfflinePlayer op : this.getServer().getOfflinePlayers()){
            Player p = op.getPlayer();
            if(p != null) {
                List<Integer> achievements = playerStats.getPlayerAchievements(p);
                List<Integer> recordedAchievements = this.getConfig().getIntegerList(
                        "DasFinale.Achievements." + p.getName());
                if(recordedAchievements.size() < achievements.size()){
                    this.getConfig().set("DasFinale.Achievements." + p.getName(), achievements);
                }
                int[] currentLoadOut = playerStats.getSelectedLoadOut(p);
                int[][] loadOuts = playerStats.getPlayerLoadOuts(p);
                this.getConfig().set("DasFinale.SelectedLoadOut." + p.getName(), currentLoadOut);
                for (int i = 0; i < loadOuts.length; i++) {
                    int[] loadOut = loadOuts[i];
                    this.getConfig().set("DasFinale.LoadOuts." + p.getName() + "." + i, loadOut);
                }
            }
        }
        this.saveConfig();
        this.reloadConfig();
    }
}
