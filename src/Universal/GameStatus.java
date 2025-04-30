package Universal;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class GameStatus {
    private static GameStatus instance = new GameStatus();

    private GameStatus() {
    }

    public static GameStatus getInstance() {
        return instance;
    }
    String[] allTeamNames = {
            ChatColor.GOLD + "豹猫队",
            ChatColor.AQUA + "激流队",
            ChatColor.GREEN + "高压队",
            ChatColor.RED + "红石队",
            ChatColor.LIGHT_PURPLE + "粉红羊队",
            ChatColor.YELLOW + "僵尸医生队",
            ChatColor.DARK_BLUE + "附魔师队",
            ChatColor.DARK_PURPLE + "地狱门队",
            ChatColor.DARK_AQUA + "初音队",
            ChatColor.WHITE + "嘟嘟哒嘟哒哒",
            ChatColor.DARK_RED + "垂泪藤队",
            ChatColor.BLUE + "青金石队",
            ChatColor.DARK_GREEN + "苔石队",
            ChatColor.RED + "G"
                    + ChatColor.GOLD + "a"
                    + ChatColor.YELLOW + "y"
                    + ChatColor.GREEN + "m"
                    + ChatColor.DARK_AQUA + "e"
                    + ChatColor.BLUE + "r"
                    + ChatColor.LIGHT_PURPLE + "队",
    };
    Scoreboard gameBoard;
    Random random = new Random();
    PlayerStats playerStats = PlayerStats.INSTANCE;
    String[] teamNames = {ChatColor.RED + "CNS_1",ChatColor.RED + "CNS_2",ChatColor.RED + "CNS_3",};
    Location[] playerSpawnPoint = new Location[]{};
    Location[] vaultSpawnPoint = new Location[]{};
    Location[] cashOutStationSpawnPoint = new Location[]{};
    Location[] jarSpawnPoint = new Location[]{};
    Location[] jumpStartPoint = new Location[]{};
    Location[] jumpEndPoint = new Location[]{};
    Player[][] allTeams = new Player[][]{
            new Player[3],
            new Player[3],
            new Player[3],
    };
    HashMap<String,Integer> playerBoardSlot = new HashMap<>();
    HashMap<String,Integer>teamBoardSlot = new HashMap<>();
    HashMap<String,Integer>worldGameMode = new HashMap<>();
    HashMap<String,Integer>teamMoney = new HashMap<>();
    HashMap<Entity,Integer>vauntNum = new HashMap<>();
    HashMap<Entity,Integer>stationNum = new HashMap<>();
    HashSet<String>worldGaming = new HashSet<>();
    HashSet<String>suddenDeath = new HashSet<>();
    HashSet<Entity>isCashing = new HashSet<>();
    int[]teamRanking = new int[teamNames.length];
    int[]suddenDeathRanking = new int[teamNames.length];
    int worldEvent = -1;
    int map = -1;
    int vaultCount,stationCount = 0;
    int gameTime = 0;

    public void shuffleString(String[] strings) {
        for (int i = strings.length - 1; i > 0; i--) {
            int randomNum = random.nextInt(i + 1);
            String str = strings[randomNum];
            strings[randomNum] = strings[i];
            strings[i] = str;
        }
    }
    public void shuffleLocation(Location[] locations) {
        for (int i = locations.length - 1; i > 0; i--) {
            int randomNum = random.nextInt(i + 1);
            Location loc = locations[randomNum];
            locations[randomNum] = locations[i];
            locations[i] = loc;
        }
    }
    public void setTeamNames(int num) {
        if (num <= 0) {
            Bukkit.getLogger().info("设置队伍名字时数目出错");
            return;
        }
        String[] clone = allTeamNames.clone();
        String[] teamNamesClone = new String[num];
        shuffleString(clone);
        for (int i = 0; i < num; i++) {
            teamNamesClone[i] = clone[i];
        }
        teamNames = teamNamesClone;
    }
    public int addPlayerToTeam(Player p, int team) {
        if (team < 0 || team > this.allTeams.length) {
            return 1;
        }
        int[]loadOut = playerStats.getSelectedLoadOut(p);
        if(loadOut.length == 0)return 2;
        if(playerStats.isSpector(p)){
            playerStats.stopSpectating(p);
        }
        Player[][] allTeamsClone = this.allTeams.clone();
        Player[] targetTeam = allTeamsClone[team];
        List<Player>newTargetTeam = new ArrayList<>();
        for(Player player : targetTeam){
            if(player == null)continue;
            newTargetTeam.add(player);
        }
        if(newTargetTeam.contains(p)){
            return 3;
        }else {
            if(newTargetTeam.size() >= 3){//队伍满员（5v5?）
                return 4;
            }else {
                int oldTeamID = playerStats.getTeam(p);
                if(oldTeamID >= 0) {//玩家加了队伍
                    Player[] oldTeam = allTeamsClone[oldTeamID];
                    List<Player> oldTeamArray = new ArrayList<>();
                    for (Player player : oldTeam) {
                        if (player == null) continue;
                        oldTeamArray.add(player);
                    }
                    oldTeamArray.remove(p);
                    allTeamsClone[oldTeamID] = oldTeamArray.toArray(new Player[0]);
                }
                newTargetTeam.add(p);
                allTeamsClone[team] = newTargetTeam.toArray(new Player[0]);
                playerStats.setTeam(p,team);
                allTeams = allTeamsClone;
                return 0;
            }
        }
    }
    public boolean removePlayerFromTeam(Player p){
        if(playerStats.isSpector(p)){
            playerStats.stopSpectating(p);
        }
        Player[][] allTeamsClone = this.allTeams.clone();
        int oldTeamID = this.playerStats.getTeam(p);
        if (oldTeamID >= 0) {
            Player[] oldTeam = allTeamsClone[oldTeamID];
            List<Player>oldTeamArray = new ArrayList<>();
            for(Player player : oldTeam){
                if(player == null)continue;
                oldTeamArray.add(player);
            }
            oldTeamArray.remove(p);
            allTeamsClone[oldTeamID] = oldTeamArray.toArray(new Player[0]);
            this.allTeams = allTeamsClone;
            playerStats.setTeam(p,-1);
            return true;
        }
        return false;
    }
    public boolean isTeamMate(Player p1, Player p2) {
        int t1 = playerStats.getTeam(p1);
        int t2 = playerStats.getTeam(p2);
        return t1 == t2;
    }
    public String getTeamName(int id) {
        if (id < 0 || id > teamNames.length) {
            return ChatColor.RED + "" + ChatColor.MAGIC + "123456"
                    + ChatColor.RESET + ChatColor.RED + "WE ARE CNS"
                    + ChatColor.MAGIC + "12346";
        }
        return teamNames[id];
    }
    public Player[] getTeamByID(int id) {
        if (id < 0 || id >= allTeams.length) return new Player[]{};
        return allTeams[id];
    }
    public void setSpawnPoints(World w) {
        ArrayList<Location> player = new ArrayList<>();
        ArrayList<Location> vault = new ArrayList<>();
        ArrayList<Location> jar = new ArrayList<>();
        ArrayList<Location> station = new ArrayList<>();
        ArrayList<Location> jumpS = new ArrayList<>();
        ArrayList<Location> jumpE = new ArrayList<>();
        HashMap<Integer,Location>startMap = new HashMap<>();
        HashMap<Integer,Location>endMap = new HashMap<>();
        Collection<ArmorStand> entities = w.getEntitiesByClass(ArmorStand.class);
        for (ArmorStand a : entities) {
            String name = a.getName();
            Location loc = a.getLocation();
            if (name.contains("金库")) vault.add(loc);
            if (name.contains("罐")) jar.add(loc);
            if (name.contains("玩家")) player.add(loc);
            if (name.contains("提现站")) station.add(loc);
            if (name.contains("滑索开始点")) {
                int cut = name.indexOf("滑索开始点");
                String indexString = name.substring(cut + 5);
                int index = Integer.parseInt(indexString);
                startMap.put(index - 1,loc);
            }
            if (name.contains("滑索结束点")) {
                int cut = name.indexOf("滑索结束点");
                String indexString = name.substring(cut + 5);
                int index = Integer.parseInt(indexString);
                endMap.put(index - 1,loc);
            }
            a.remove();
        }
        if(startMap.size() == endMap.size()) {
            for (int i = 0; i < startMap.size(); i++) {
                Location loc1 = startMap.getOrDefault(i,null);
                Location loc2 = endMap.getOrDefault(i,null);
                if(loc1 != null){
                    jumpS.add(loc1);
                }
                if(loc2 != null){
                    jumpE.add(loc2);
                }
            }
        }else {
            Bukkit.broadcastMessage("绳索位置出错");
        }
        playerSpawnPoint = player.toArray(new Location[]{});
        vaultSpawnPoint = vault.toArray(new Location[]{});
        cashOutStationSpawnPoint = station.toArray(new Location[]{});
        jarSpawnPoint = jar.toArray(new Location[]{});
        jumpStartPoint = jumpS.toArray(new Location[]{});
        jumpEndPoint = jumpE.toArray(new Location[]{});
        shuffleLocation(vaultSpawnPoint);
        shuffleLocation(cashOutStationSpawnPoint);
    }
    public Location[] getPlayerSpawnPoint() {
        return playerSpawnPoint;
    }
    public void setPlayerSpawnPoint(Location[] loc){
        playerSpawnPoint = loc;
    }
    public Location[] getVaultSpawnPoint() {
        return vaultSpawnPoint;
    }
    public void setVaultSpawnPoint(Location[] loc){vaultSpawnPoint = loc;}
    public Location[] getCashOutStationSpawnPoint() {
        return cashOutStationSpawnPoint;
    }
    public void setCashOutStationSpawnPoint(Location[]loc){cashOutStationSpawnPoint = loc;}
    public Location[] getJarSpawnPoint() {
        return jarSpawnPoint;
    }
    public void setJarSpawnPoint(Location[]loc){jarSpawnPoint = loc;}
    public Location[] getJumpStartPoint() {
        return jumpStartPoint;
    }
    public void setJumpStartPoint(Location[] jumpStartPoint) {
        this.jumpStartPoint = jumpStartPoint;
    }
    public Location[] getJumpEndPoint() {
        return jumpEndPoint;
    }
    public void setJumpEndPoint(Location[] jumpEndPoint) {
        this.jumpEndPoint = jumpEndPoint;
    }

    public int getWorldEvent() {
        return worldEvent;
    }
    public void setWorldEvent(int id) {
        worldEvent = id;
    }
    public int getMap() {
        return map;
    }
    public void setMap(int map) {
        this.map = map;
    }
    public Scoreboard getGameBoard(){
        return gameBoard;
    }
    public void setGameBoard(Player p){
        if(gameBoard != null){
            p.setScoreboard(gameBoard);
        }
    }
    public void clearGameBoard(Player p){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if(gameBoard != null){
            p.setScoreboard(manager.getNewScoreboard());
        }
    }
    public void registerBoard(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        gameBoard = manager.getNewScoreboard();
        Objective o1 = gameBoard.registerNewObjective("cash","dummy",ChatColor.GREEN + "队伍现金排名");
        Objective o2 = gameBoard.registerNewObjective("health",Criterias.HEALTH,ChatColor.RED + "♥");
        o1.setDisplaySlot(DisplaySlot.SIDEBAR);
        o2.setDisplaySlot(DisplaySlot.BELOW_NAME);
        int line = -2;
        for(int i = 0;i < 3;i ++){
            Team t = gameBoard.registerNewTeam(teamNames[i]);
            addScore(teamNames[i],0);
            t.setDisplayName(teamNames[i]);
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            o1.getScore(teamNames[i]).setScore(line);
            teamBoardSlot.put(teamNames[i],line);
            line -= 1;
            ChatColor c = switch (i) {
                case 0 -> ChatColor.AQUA;
                case 1 -> ChatColor.GOLD;
                case 2 -> ChatColor.LIGHT_PURPLE;
                default -> ChatColor.GREEN;
            };
            Player[]team = getTeamByID(i);
            for(Player p : team){
                if(p == null)continue;
                t.addEntry(p.getName());
                setPlayerKD(p);
                Score pScore = o1.getScore(p.getName() + " ");
                pScore.setScore(line);
                playerBoardSlot.put(p.getName() + " ",line);
                line-=1;
            }
            t.setColor(c);
        }
    }
    public void addScore(String team,int amount){
        if(gameBoard != null){
            Objective o = gameBoard.getObjective("cash");
            if(o != null) {
                int money = teamMoney.getOrDefault(team, 0);
                teamMoney.put(team, amount + money);
                Team t;
                if (gameBoard.getTeam(team) == null) {
                    t = gameBoard.registerNewTeam(team);
                } else {
                    t = gameBoard.getTeam(team);
                }
                t.addEntry(team);
                t.setSuffix(ChatColor.YELLOW + "：" + (amount + money) + "＄");
                int slot = teamBoardSlot.getOrDefault(team, 0);
                if (slot < 0) {
                    o.getScore(team).setScore(slot);
                }
            }
        }
    }
    public int getScore(String team){
        return teamMoney.getOrDefault(team,0);
    }
    public void resetGameBoard(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        gameBoard = manager.getNewScoreboard();
    }
    public void setPlayerBoardTeam(Player p,int team){
        String teamName = getTeamName(team);
        Team t = gameBoard.getTeam(teamName);
        if(t != null){
            t.addEntry(p.getName());
        }
    }
    public void setPlayerKD(Player p) {
        int k = playerStats.getPlayerKills(p);
        int d = playerStats.getPlayerDeaths(p);
        if (playerStats.getTeam(p) >= 0) {
            Objective o = gameBoard.getObjective("cash");
            Team team;
            if(gameBoard.getTeam(p.getName() + " ") == null){
                team = gameBoard.registerNewTeam(p.getName() + " ");
            }else {
                team = gameBoard.getTeam(p.getName() + " ");
            }
            team.addEntry(p.getName() + " ");
            team.setSuffix(ChatColor.AQUA + "K" + k + ChatColor.GREEN + "：" + ChatColor.RED + "D" + d);
            ChatColor c = switch (playerStats.getTeam(p)) {
                case 0 -> ChatColor.AQUA;
                case 1 -> ChatColor.GOLD;
                case 2 -> ChatColor.LIGHT_PURPLE;
                default -> ChatColor.GREEN;
            };
            team.setColor(c);
            int score = playerBoardSlot.getOrDefault(p.getName() + " ", 0);
            if (o != null) {
                if (score < 0) {
                    o.getScore(p.getName() + " ").setScore(score);
                }
            }
        }
    }
    public void setGameMode(World w,int gameMode){
        String mode = switch (gameMode){
            case 1 -> "存钱至上";
            case 2 -> "xX_CNS_GLITCHCRAFT_Xx";
            default -> "快速提现";
        };
        worldGameMode.put(w.getName(),gameMode);
        Bukkit.broadcastMessage(ChatColor.GREEN + "游戏模式已更改为：" + mode);
    }
    public int getGameMode(World w){return worldGameMode.getOrDefault(w.getName(),0);}
    public void setGaming(World w){worldGaming.add(w.getName());}
    public void stopGaming(World w){worldGaming.remove(w.getName());}
    public boolean isGaming(World w){return worldGaming.contains(w.getName());}
    public void setSuddenDeath(World w){suddenDeath.add(w.getName());}
    public void stopSuddenDeath(World w){suddenDeath.remove(w.getName());}
    public boolean isSuddenDeath(World w){return suddenDeath.contains(w.getName());}
    public void setCashing(Entity v){
        isCashing.add(v);
    }
    public void stopCashing(Entity v){
        isCashing.remove(v);
    }
    public boolean isCashing(Entity v){
        return isCashing.contains(v);
    }
    public int getStationNum(Entity e) {return stationNum.getOrDefault(e,-1);}
    public int getStationCount() {return stationCount;}
    public int getVaultNum(Entity e) {return vauntNum.getOrDefault(e,-1);}
    public int getVaultCount() {return vaultCount;}
    public void setStationNum(Entity e) {
        stationCount += 1;
        stationNum.put(e,stationCount);
    }
    public void setVaultNum(Entity e) {
        vaultCount += 1;
        vauntNum.put(e,vaultCount);
    }
    public void clearNums(){
        teamMoney.clear();
        stationNum.clear();
        vauntNum.clear();
        stationCount = 0;
        vaultCount = 0;
    }
    public void updateRanking(){
        teamRanking = new int[teamNames.length];
        int max = 0;
        int slot = 0;
        for(int i = 0;i < teamNames.length;i++){
            String name = teamNames[0];
            int score = teamMoney.getOrDefault(name,0);
            if(score >= max){
                teamRanking[0] = i;
                max = score;
            }else {
                String n1 = teamNames[i];
                int s1 = teamMoney.getOrDefault(n1,0);
                if(score >= s1){
                    int oldI = teamRanking[slot];
                    teamRanking[slot] = i;
                    teamRanking[slot + 1] = oldI;
                    slot++;
                }
            }
        }
        if(max == 0){
            teamRanking = new int[]{};
        }
    }
    public void setSuddenDeathRanking(int team, int ranking){
        suddenDeathRanking[team] = ranking;
    }
    public int getRanking(int team){
        for(int i = 0;i < teamRanking.length;i++){
            if(i == team)return i;
        }
        return -1;
    }
    public int getSuddenDeathRanking(int team){
        if(team >= 0 && team < suddenDeathRanking.length){
            return suddenDeathRanking[team];
        }
        return -1;
    }
    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }
    public int getGameTime() {
        return gameTime;
    }
}
