package Listeners;

import Events.*;
import Universal.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import ru.xezard.glow.data.glow.Glow;

import java.util.*;

public class GameListeners implements Listener {
    JavaPlugin plugin;
    Random random = new Random();
    HashSet<Entity>opening = new HashSet<>();
    HashMap<Entity,Player> itemsLastThrowerMap = new HashMap<>();
    HashMap<Villager,Integer>cashOutTeam = new HashMap<>();
    HashMap<Villager,String>whoIsStealing = new HashMap<>();
    HashMap<ArmorStand,String>whoIsReviving = new HashMap<>();
    HashMap<String,BukkitRunnable>playerTask = new HashMap<>();
    HashMap<String,BukkitRunnable>playerZiplineTask = new HashMap<>();
    HashMap<String,BukkitRunnable>playerHealTask = new HashMap<>();
    HashMap<String,List<Location>>playerTrace = new HashMap<>();
    HashMap<String,Integer>playerTeam = new HashMap<>();
    HashMap<World,BukkitRunnable>timerTask = new HashMap<>();
    public static HashMap<Player,Entity> playerCarryItemMap = new HashMap<>();
    public static HashMap<Entity,Player> itemCarriedByPlayerMap = new HashMap<>();
    public static HashMap<Entity,Vector>jumpPointVector = new HashMap<>();
    public static HashMap<Location,Location>jumpPointLocation = new HashMap<>();
    public static HashMap<String,BukkitRunnable>playerRespawnTask = new HashMap<>();
    public static HashMap<String,Double>playerDamage = new HashMap<>();
    HashMap<Player,Player>lastDamageSource = new HashMap<>();
    HashSet<Player>stealing = new HashSet<>();
    HashSet<Player>reviving = new HashSet<>();
    HashSet<Player>beingRevive = new HashSet<>();
    HashSet<Player>isRich = new HashSet<>();
    HashSet<Entity>isPrimed = new HashSet<>();
    HashSet<Entity>speedRunBox = new HashSet<>();
    public static HashSet<Entity>isCashingOut = new HashSet<>();
    public static HashSet<Entity>canRespawn = new HashSet<>();
    List<ArmorStand>meteors = new ArrayList<>();
    List<LivingEntity>netherEntity = new ArrayList<>();
    Kits k = Kits.getInstance();
    Items items = Items.getInstance();
    PlayerStats playerStats = PlayerStats.INSTANCE;
    GameStatus gameStatus = GameStatus.getInstance();
    BroadCast bc = BroadCast.getInstance();
    Ui ui = Ui.getInstance();
    SimplexNoiseGenerator simplex = new SimplexNoiseGenerator(random);
    Material[]netherBlocks = {
            Material.NETHERRACK
            ,Material.GILDED_BLACKSTONE
            ,Material.BLACKSTONE
            ,Material.WARPED_NYLIUM
            ,Material.CRIMSON_NYLIUM
            ,Material.MAGMA_BLOCK};
    String[]jars={
            "爆炸罐", "毒气罐", "粘胶罐", "火焰罐", "烟雾罐","紊乱罐","陨石核心","高压核心"
    };
    String[]gameShowName = {
            ChatColor.GOLD + "天外来物",
            ChatColor.AQUA + "高层扰流",
            ChatColor.DARK_PURPLE + "下界入侵",
            ChatColor.RED + "虚空噬灭",
            ChatColor.GREEN + "重力紊乱",
    };
    public int jarType(String jar){
        for (int i = 0;i < jars.length;i++){
            if(jar.contains(jars[i])){
                return i;
            }
        }
        return -1;
    }
    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void doNotHurtThat(EntityDamageEvent damageEvent) {
        Entity damaged = damageEvent.getEntity();
        if (damaged.getCustomName() != null) {
            if (damaged.getName().contains("伤害测试假人") || damaged.getName().contains("罐")) {
                damageEvent.setCancelled(false);
            }
            if (damaged instanceof Villager ||
                    damaged instanceof ArmorStand) {
                damageEvent.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void villagerJob(VillagerCareerChangeEvent changeEvent){
        Entity e = changeEvent.getEntity();
        if(e.getCustomName() != null){
            changeEvent.setCancelled(true);
        }
    }
    @EventHandler
    public void transform(EntityTransformEvent transformEvent){
        Entity e = transformEvent.getEntity();
        if(e.getCustomName() != null){
            transformEvent.setCancelled(true);
        }
    }
    @EventHandler
    public void playerCommand(PlayerCommandPreprocessEvent commandEvent){
        Player p = commandEvent.getPlayer();
        if(!p.isOp()) {
            if (playerStats.isGaming(p) || playerStats.isSpector(p)) {
                String message = switch (random.nextInt(3)) {
                    case 0 -> ChatColor.RED + "打比赛呢，认真点";
                    case 1 -> ChatColor.RED + "你的队友需要你！";
                    case 2 -> ChatColor.RED + "你的指令被CNS黑掉了";
                    default -> "";
                };
                p.sendMessage(message);
                if(random.nextInt(500) == 0){
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "金胡萝卜神：看起来我们的一位选手试图在竞技场中使用指令" +
                            ",但是什么也没有发生");
                }
            }
        }
    }
    @EventHandler
    public void itemDamage(PlayerItemDamageEvent damageEvent){
        damageEvent.setCancelled(true);
    }
    @EventHandler
    public void playerHunger(FoodLevelChangeEvent event){
        event.setFoodLevel(20);
    }
    @EventHandler
    public void breakBlock(BlockBreakEvent breakEvent){
        Block b = breakEvent.getBlock();
        Player p = breakEvent.getPlayer();
        if(!p.isOp()){
            if(!k.isBreakable(p,b)){
                breakEvent.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void playerCraft(CraftItemEvent craftItemEvent){
        craftItemEvent.setCancelled(true);
    }
    @EventHandler
    public void playerQuit(PlayerQuitEvent quitEvent){
        Player p = quitEvent.getPlayer();
        World w = p.getWorld();
        int count = 0;
        int team = playerStats.getTeam(p);
        gameStatus.removePlayerFromTeam(p);
        playerTeam.put(p.getName(),team);
        p.closeInventory();
        for(Player player : w.getPlayers()){
            if(playerStats.isGaming(player)){
                count += 1;
            }
        }
        if(count == 0){
            Bukkit.getPluginManager().callEvent(
                    new GameEndEvent(w,-1)
            );
            gameStatus.stopGaming(w);
        }
        List<Integer> achievements = playerStats.getPlayerAchievements(p);
        List<Integer> recordedAchievements = plugin.getConfig().getIntegerList(
                "DasFinale.Achievements." + p.getName());
        if(recordedAchievements.size() < achievements.size()){
            plugin.getConfig().set("DasFinale.Achievements." + p.getName(), achievements);
        }
        plugin.saveConfig();
        plugin.reloadConfig();
    }
    @EventHandler
    public void playerJoin(PlayerJoinEvent joinEvent){
        Player p = joinEvent.getPlayer();
        World w = p.getWorld();
        int team = playerTeam.getOrDefault(p.getName(),-1);
        if (gameStatus.isGaming(w)) {
            if (team >= 0) {
                if (gameStatus.getGameMode(w) == 2) {
                    setPlayerLoadOut(p);
                }
                k.respawn(p, false, false, false);
                gameStatus.addPlayerToTeam(p, team);
                gameStatus.registerBoard();
                Scoreboard board = gameStatus.getGameBoard();
                for (Player player : w.getPlayers()) {
                    player.setScoreboard(board);
                }
            }else {
                gameStatus.clearGameBoard(p);
                playerStats.stopGaming(p);
                p.teleport(w.getSpawnLocation());
            }
        }else {
            gameStatus.clearGameBoard(p);
            playerStats.stopGaming(p);
            p.teleport(w.getSpawnLocation());
        }
        List<Integer> recordedAchievements = plugin.getConfig().getIntegerList(
                "DasFinale.Achievements." + p.getName());
        playerStats.readPlayerAchievements(p,recordedAchievements);
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void playerRegeneration(EntityDamageEvent damageEvent) {
        Entity damaged = damageEvent.getEntity();
        World w = damaged.getWorld();
        if(!gameStatus.isSuddenDeath(w)) {
            if (damaged instanceof Player p) {
                int body = playerStats.getClass(p);
                if (body >= 0) {
                    int delay = 5 + body * 2;
                    regeneration(p, delay);
                }
            }
        }
    }
    public void regeneration(Player p,int delay){
        BukkitRunnable task = playerHealTask.getOrDefault(p.getName(),null);
        if(task != null)task.cancel();
        BukkitRunnable heal = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                if(p.getHealth() == max || p.isDead()){
                    this.cancel();
                    return;
                }
                if(count > delay) {
                    int body = playerStats.getClass(p);
                    k.heal(p,4 - body * 0.5);
                }
                count += 1;
            }
        };
        heal.runTaskTimer(plugin,0L,20L);
        playerHealTask.put(p.getName(),heal);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerHitJar(EntityDamageByEntityEvent damageByEntityEvent){
        Entity damaged = damageByEntityEvent.getEntity();
        Entity attacker = damageByEntityEvent.getDamager();
        World w = damaged.getWorld();
        if(attacker instanceof Player p){
            if(damaged instanceof ArmorStand a){
                if(a.getName().contains("罐")){
                    if(playerCarryItemMap.getOrDefault(p,null) == null) {
                        if (!isPrimed.contains(a)) {
                            if (itemCarriedByPlayerMap.getOrDefault(a, null) == null) {
                                w.playSound(a.getLocation(), Sound.BLOCK_LANTERN_BREAK, 1, 1);
                                w.playSound(a.getLocation(), Sound.BLOCK_LANTERN_BREAK, 1, 1);
                                throwingJars(p, a, false);
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(EntityDamageEvent damageEvent){
        Entity dead = damageEvent.getEntity();
        double damage = damageEvent.getFinalDamage();
        World w = dead.getWorld();
        if(dead instanceof Player p){
            if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                if(damage > 0) {
                    p.playSound(p.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,24,0));
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    k.setPlayerEquipment(p);
                }
            }
            double health = p.getHealth();
            if(damageEvent.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION){
                k.breakBallGoo(p.getEyeLocation(),1);
                damageEvent.setCancelled(true);
                return;
            }
            if (damageEvent.getCause() == EntityDamageEvent.DamageCause.VOID) {
                eliminated(p);
                if(gameStatus.getPlayerSpawnPoint().length == 0){
                    p.teleport(w.getSpawnLocation());
                }else {
                    p.teleport(gameStatus.getPlayerSpawnPoint()[0]);
                }
                return;
            }
            if(damage >= health){
                for(int i = 0;i < 30;i++) {
                    ItemStack money = new ItemStack(Material.EMERALD);
                    k.addLore(money, new String[]{"" + i});
                    Item ball = w.dropItem(p.getLocation().add(0, 1, 0), money);
                    ball.setPickupDelay(6000);
                    ball.setTicksLived(5940);
                    double y = random.nextDouble(1) - random.nextDouble(1);
                    if(p.isOnGround()){
                       y = 1;
                    }
                    Vector spread = new Vector(random.nextDouble(1) - random.nextDouble(1),
                            y,
                            random.nextDouble(1) - random.nextDouble(1));
                    ball.setVelocity(spread.normalize()
                            .multiply(0.5)
                    );
                }
                w.spawnParticle(Particle.ITEM_CRACK,p.getEyeLocation(),100,0.5,0.5,0.5,0.1,new ItemStack(Material.EMERALD_BLOCK));
                damageEvent.setCancelled(true);
                p.setHealth(1);
                eliminated(p);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void playerKillPlayer(EntityDamageByEntityEvent damageByEntityEvent) {
        Entity damaged = damageByEntityEvent.getEntity();
        Entity attacker = damageByEntityEvent.getDamager();
        World w = damaged.getWorld();
        double damage = damageByEntityEvent.getFinalDamage();
        if (damage == 0) return;
        Player p = null, p1 = null;
        if (attacker instanceof Player player) {
            p = player;
        } else if (attacker instanceof Projectile pr) {
            if (pr instanceof Firework) {
                damageByEntityEvent.setDamage(0);
                damageByEntityEvent.setCancelled(true);
                return;
            }
            if (pr.getShooter() instanceof Player player) {
                p = player;
            }
        }
        if (damaged instanceof Player player1) {
            p1 = player1;
        }
        if (p != null && p1 != null) {
            boolean acheivement = false;
            Player lastDamager = lastDamageSource.getOrDefault(p1, null);
            if (lastDamager != null) {
                if (lastDamager != p) {
                    acheivement = true;
                }
            }
            lastDamageSource.put(p1, p);
            if (gameStatus.isTeamMate(p, p1)) {
                if (p != p1) {
                    damageByEntityEvent.setDamage(0);
                    damageByEntityEvent.setCancelled(true);
                    return;
                }
            } else if (p1.getHealth() <= damage) {
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.5f, 1);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                p.sendTitle(" ", ChatColor.WHITE + "淘汰 " + ChatColor.RED + p1.getName(), 10, 30, 10);
                if (acheivement) {
                    playerStats.grantAchievement(p, 3);
                }
                if(stealing.contains(p1)){
                    playerStats.grantAchievement(p, 23);
                }
                Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p,p1,0));
                if (playerStats.isGaming(p)) {
                    playerStats.addKillCount(p);
                    gameStatus.setPlayerKD(p);
                    int team = playerStats.getTeam(p);
                    ItemStack glass = switch (team) {
                        case 0 -> new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS, 4);
                        case 1 -> new ItemStack(Material.ORANGE_STAINED_GLASS, 4);
                        case 2 -> new ItemStack(Material.MAGENTA_STAINED_GLASS, 4);
                        default -> new ItemStack(Material.WHITE_STAINED_GLASS, 4);
                    };
                    Item item = w.dropItem(p.getLocation(), glass);
                    item.setOwner(p.getUniqueId());
                    item.setPickupDelay(0);
                }
            } else if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                if (damage > 0) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 24, 0));
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    k.setPlayerEquipment(p);
                }
            }
            if(playerStats.isGaming(p)){
                double recordedDamage = playerDamage.getOrDefault(p.getName(),0D);
                recordedDamage += damage;
                playerDamage.put(p.getName(),recordedDamage);
                if(recordedDamage >= 300){
                    playerStats.grantAchievement(p,15);
                }
            }
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(ChatColor.RED + "造成伤害：" + String.format("%.1f", damage)));
        } else if (p == null) {
            lastDamageSource.remove(p1);
        }
    }
    public void eliminated(Player p) {
        World w = p.getWorld();
        int mode = gameStatus.getGameMode(w);
        int map = gameStatus.getMap();
        int time = switch (mode){
            case 1 -> 10;
            default -> 15;
        };
        if(!gameStatus.isSuddenDeath(w)) {
            if(mode != 1 && map == 3){
                time += 5;
            }
            respawnCountDown(p, time, false);
        }
        openDeadMenu(p);
        if(playerStats.isGaming(p)) {
            if (gameStatus.getWorldEvent() == 3) {
                deathExplosion(p);
            }
            Player killer = lastDamageSource.getOrDefault(p, null);
            String name;
            if (killer == null) {
                name = ChatColor.RED + "环境伤害";
            } else {
                ChatColor c1 = switch (playerStats.getTeam(killer)) {
                    case 0 -> ChatColor.AQUA;
                    case 1 -> ChatColor.GOLD;
                    case 2 -> ChatColor.LIGHT_PURPLE;
                    default -> ChatColor.GREEN;
                };
                name = c1 + killer.getName();
            }
            if(killer != null){
                if(killer == p){
                    playerStats.grantAchievement(killer,16);
                }
            }
            ChatColor c2 = switch (playerStats.getTeam(p)) {
                case 0 -> ChatColor.AQUA;
                case 1 -> ChatColor.GOLD;
                case 2 -> ChatColor.LIGHT_PURPLE;
                default -> ChatColor.GREEN;
            };
            Bukkit.broadcastMessage(name +
                    ChatColor.YELLOW + " 淘汰了 " + c2 + p.getName());
            p.setGameMode(GameMode.SPECTATOR);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
            p.sendTitle(ChatColor.RED + "！被淘汰！", ChatColor.RED + "淘汰者：" + name, 10, 40, 10);
            if(!gameStatus.isSuddenDeath(w)) {
                ArmorStand statue = (ArmorStand) w.spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
                statue.setSmall(true);
                statue.setCustomName(p.getName() + "的雕像");
                statue.setBasePlate(false);
                setStatueArmor(p, statue);
                playerStats.setPlayerStatue(p, statue);
            }
            lastDamageSource.remove(p);
            playerStats.addDeathCount(p);
            gameStatus.setPlayerKD(p);
            isRich.remove(p);
            int teamID = playerStats.getTeam(p);
            String teamName = gameStatus.getTeamName(teamID);
            if(gameStatus.isSuddenDeath(w)) {
                suddenDeathWinner(w);
            }
            if(mode == 1) {
                int coin = k.getCoin(p);
                summonCoins(p.getLocation() ,coin + 1);
                if(coin >= 9){
                    bc.spitCoins(teamName);
                }
                k.removeCoin(p);
                return;
            }
            Player[] team = gameStatus.getTeamByID(teamID);
            for (Player player : team) {
                if(player == null)continue;
                if (!playerStats.isDead(player)) {
                    return;
                }
            }
            bc.teamWipeBC(teamName);
            teamWipe(w, team);
            w.strikeLightningEffect(p.getLocation());
            if(killer != null && !gameStatus.isTeamMate(p,killer)){
                playerStats.grantAchievement(killer,7);
            }
        }else {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,23,0));
            p.playSound(p.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            p.teleport(w.getSpawnLocation());
            p.setHealth(20);
            p.setFireTicks(0);
            Inventory inv = p.getInventory();
            for (ItemStack i : inv.getContents()) {
                if (i == null) continue;
                p.setCooldown(i.getType(), 0);
            }
        }
    }
    public void suddenDeathWinner(World w){
        HashSet<Integer>teams = new HashSet<>();
        for (Player p : w.getPlayers()){
            if(playerStats.isGaming(p)){
                if(p.getGameMode() == GameMode.SPECTATOR)continue;
                int team = playerStats.getTeam(p);
                teams.add(team);
            }
        }
        if(teams.size() == 1){
            int winTeam = -1;
            for(int i = 0;i < 3;i ++){
                if(teams.contains(i)){
                    gameStatus.setSuddenDeathRanking(i,0);
                    winTeam = i;
                }else {
                    gameStatus.setSuddenDeathRanking(i,4);
                }
            }
            Bukkit.getPluginManager().callEvent(new GameEndEvent(w,winTeam));
        }
    }
    public void setStatueArmor(Player p, ArmorStand a){
        World w = p.getWorld();
        ChatColor c = switch (playerStats.getTeam(p)) {
            case 0 -> ChatColor.AQUA;
            case 1 -> ChatColor.GOLD;
            case 2 -> ChatColor.LIGHT_PURPLE;
            default -> ChatColor.GREEN;
        };
        EntityEquipment equipA = a.getEquipment();
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(p);
        head.setItemMeta(meta);
        equipA.setHelmet(head);
        equipA.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        equipA.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        equipA.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        a.setGlowing(true);
        Glow glow = Glow.builder().color(c).name(a.getName()).build();
        glow.addHolders(a);
        for(Player player : w.getPlayers()){
            glow.display(player);
        }
    }
    public void teamWipe(World w,Player[]team){
        int time = switch (gameStatus.getMap()){
            case 3 -> 25;
            default -> 20;
        };
        for(Player p : team){
            if(p == null)continue;
            playerStats.removePlayerStatue(p);
            p.sendTitle(ChatColor.RED + "！团灭！"," ",10,40,10);
            if(!gameStatus.isSuddenDeath(w)) {
                respawnCountDown(p, time, true);
            }
        }
        List<Player>allPlayers = w.getPlayers();
        for(Player p1:allPlayers){
            if(playerStats.isSpector(p1) || playerStats.isGaming(p1)) {
                p1.playSound(p1.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            }
        }
    }
    public void respawnCountDown(Player p, int time,boolean teamWipe){
        BukkitRunnable respawnTask = playerRespawnTask.getOrDefault(p.getName(),null);
        if(respawnTask != null)respawnTask.cancel();
        BukkitRunnable countDown = new BukkitRunnable() {
            int count = time;
            @Override
            public void run() {
                if(count <= 0 || p.getGameMode() != GameMode.SPECTATOR){
                    if(p.getGameMode() == GameMode.SPECTATOR) {
                        BukkitRunnable checkRespawn = new BukkitRunnable() {
                            @Override
                            public void run() {
                                InventoryListener.InvStatus status =
                                        InventoryListener
                                                .playerInvStatus
                                                .getOrDefault(p.getName(), InventoryListener.InvStatus.NOT_MENU);
                                if(status == InventoryListener.InvStatus.NOT_MENU) {
                                    k.respawn(p, false, false, teamWipe);
                                    this.cancel();
                                }
                            }
                        };
                        checkRespawn.runTaskTimer(plugin,0L,10L);
                    }
                    this.cancel();
                }
                if(!beingRevive.contains(p)) {
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 1, 1);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatColor.RED + "复活时间：" + count));
                }
                count -= 1;
            }
        };
        countDown.runTaskTimer(plugin,0L,20L);
        playerRespawnTask.put(p.getName(),countDown);
    }
    public void openDeadMenu(Player p){
        Inventory inv = Bukkit.createInventory(p,9,ChatColor.GREEN + "复活菜单");
        inv.setItem(3,ui.respawn());
        if(gameStatus.getGameMode(p.getWorld()) == 2){
            inv.setItem(5, ui.canNotChange());
        }else {
            inv.setItem(5, ui.changeInGameLoadOut());
        }
        InventoryListener.playerInvStatus.put(p.getName(), InventoryListener.InvStatus.DEAD_MENU);
        p.openInventory(inv);
    }
    @EventHandler
    public void interactAtEntityEvent(PlayerInteractAtEntityEvent entityEvent) {
        Player p = entityEvent.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() != Material.AIR) {
            ItemMeta handMeta = hand.getItemMeta();
            int itemID = items.getItemID(handMeta.getDisplayName());
            if (itemID == 15 && p.isSneaking()) {
                entityEvent.setCancelled(true);
                return;
            }
        }
        String playerName = p.getName();
        Entity entity = entityEvent.getRightClicked();
        World w = p.getWorld();
        int mode = gameStatus.getGameMode(w);
        String clickedName = entity.getName();
        if(p.getGameMode() == GameMode.SPECTATOR)return;
        if(entity instanceof Snowman s){
            if(s.getName().contains("炮塔")){
                playerCarryItemMap.put(p, s);
                itemCarriedByPlayerMap.put(s, p);
                playerStats.setCarrying(p);
                k.carryItem(p,s);
                w.playSound(p.getLocation(),Sound.BLOCK_SNOW_BREAK,1,1);
                w.playSound(p.getLocation(),Sound.BLOCK_SNOW_BREAK,1,1);
            }
        }
        if (entity instanceof ArmorStand stand) {
            if (stand.getCustomName() != null) {
                entityEvent.setCancelled(true);
                if(clickedName.contains("传送门")){
                    if(!p.hasPotionEffect(PotionEffectType.LUCK)) {
                        ArmorStand gate = GadgetsListener.gatewayToGateway.getOrDefault(stand, null);
                        if (gate != null) {
                            Vector eye = p.getEyeLocation().getDirection();
                            Vector speed = p.getVelocity();
                            p.teleport(gate.getLocation().setDirection(eye));
                            p.setVelocity(speed);
                            w.playSound(stand.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                            w.playSound(gate.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 20, 0));
                        }
                    }
                    return;
                }
                if(clickedName.contains("滑索")) {
                    w.playSound(stand.getLocation(),Sound.ENTITY_FISHING_BOBBER_THROW,1,0.7f);
                    Vector jump = jumpPointVector.getOrDefault(stand,new Vector(0,2,0));
                    p.teleport(stand.getLocation().setDirection(jump));
                    Location to = jumpPointLocation.getOrDefault(stand.getLocation(),null);
                    if(to != null){
                        p.setGravity(false);
                        BukkitRunnable task = playerZiplineTask.getOrDefault(p.getName(),null);
                        if(task != null)task.cancel();
                        BukkitRunnable zipLine = new BukkitRunnable() {
                            int count = 0;
                            @Override
                            public void run() {
                                double distance = k.distance(p.getLocation(),to);
                                if(distance <= 3 || p.isSneaking() || count > 20){
                                    this.cancel();
                                    p.setGravity(true);
                                }
                                p.setVelocity(jump.clone().normalize());
                                count += 1;
                            }
                        };
                        zipLine.runTaskTimer(plugin,0L,5L);
                        playerZiplineTask.put(p.getName(),zipLine);
                    }else {
                        p.setVelocity(jump);
                    }
                    return;
                }
                if(clickedName.contains("雕像")){
                    if(p.isSneaking()){
                        String dead = playerStats.getPlayerByStatue(stand);
                        if (dead != null){
                            Player deadPlayer = Bukkit.getPlayer(dead);
                            if(!gameStatus.isTeamMate(deadPlayer,p))return;
                        }
                        String reviving = whoIsReviving.getOrDefault(stand,"");
                        if(reviving.equals(playerName) || reviving.equals("")){
                            if(playerStats.getSkill(p) == 3){
                                reviveTeammate(stand, p, 3);
                            }else {
                                reviveTeammate(stand, p, 5);
                            }
                        }else {
                            p.sendTitle(" ",ChatColor.AQUA + "正在被复活",0,10,10);
                        }
                        return;//救人
                    }else {
                        k.carryItem(p,stand);
                    }
                }
                if (clickedName.contains("金库")) {
                    if (!opening.contains(stand)) {
                        opening.add(stand);
                        int time = switch (mode){
                            case 1 -> 10;
                            default -> 15;
                        };
                        openVault(stand, time);//开启金库时间
                        int teamID = playerStats.getTeam(p);
                        String teamName = gameStatus.getTeamName(teamID);
                        if(mode != 1) {
                            bc.openVault(teamName);
                        }
                    }
                    return;
                }
                Entity carried = playerCarryItemMap.getOrDefault(p, null);
                Player carrier = itemCarriedByPlayerMap.getOrDefault(carried, null);
                if (carrier == null) {
                    if (carried == null) {
                        if(!isPrimed.contains(stand)) {
                            playerCarryItemMap.put(p, stand);
                            itemCarriedByPlayerMap.put(stand, p);
                            playerStats.setCarrying(p);
                            k.carryItem(p, stand);
                            w.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                        }
                    }
                }
            }
        }
        if(entity instanceof Villager v){
            if(clickedName.contains("提现站")) {//队伍判断
                entityEvent.setCancelled(true);
                if(isCashingOut.contains(v)) {
                    int currentTeam = cashOutTeam.getOrDefault(entity, -1);
                    int playerTeam = playerStats.getTeam(p);
                    if (currentTeam != playerTeam) {
                        String stealing = whoIsStealing.getOrDefault(v, "");
                        if (stealing.equals(playerName) || stealing.equals("")) {
                            stealCashOut(v, p, 6);
                        } else {
                            p.sendTitle(" ", ChatColor.GREEN + "正在被偷取", 0, 10, 10);
                        }
                    } else {
                        p.sendTitle(" ", ChatColor.GREEN + "这个提现站已经是你们队的了", 0, 10, 10);
                    }
                }
            }
            if(clickedName.contains("存钱站")){
                entityEvent.setCancelled(true);
                int coin = k.getCoin(p);
                if(coin > 0){
                    bankCoins(v,p,4);
                }else {
                    p.sendTitle(" ", ChatColor.GREEN + "身上没有金币", 0, 10, 10);
                }
            }
        }
        if(entity instanceof WanderingTrader){
            entityEvent.setCancelled(true);
        }
    }
    @EventHandler
    public void interactEvent(PlayerInteractEvent interactEvent){
        Player p = interactEvent.getPlayer();
        Action action = interactEvent.getAction();
        if(action == Action.RIGHT_CLICK_BLOCK){
            Block b = interactEvent.getClickedBlock();
            Material type = b.getType();
            if(type.name().contains("SIGN")) {
                Sign sign = (Sign) b.getState();
                String line = sign.getLine(0);
                if (line.contains("contestant") ||
                        line.contains("getitem") ||
                        line.contains("gamestart") ||
                        line.contains("gameend") ||
                        line.contains("teams") ||
                        line.contains("setgamemode") ||
                        line.contains("ready") ||
                        line.contains("achievements") ||
                        line.contains("loadout")) {
                    p.performCommand(line.substring(2));
                }
            }
            if(type.name().contains("BED")){
                interactEvent.setCancelled(true);
            }
        }
        if(action == Action.LEFT_CLICK_BLOCK){
            Block b = interactEvent.getClickedBlock();
            k.breakBallGlass(b.getLocation(),1);
        }
    }
    public void stealCashOut(Villager v,Player p,int time){
        if(!v.isDead()) {
            World w = p.getWorld();
            BukkitRunnable task = playerTask.getOrDefault(p.getName(), null);
            stealing.add(p);
            if (task == null) {
                whoIsStealing.put(v, p.getName());
                BukkitRunnable checkStealing = new BukkitRunnable() {
                    final int step = time * 2;
                    int check = 0;
                    @Override
                    public void run() {
                        if (!stealing.contains(p) && check < step) {
                            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                            playerTask.remove(p.getName());
                            whoIsStealing.remove(v);
                            p.sendTitle(" ", ChatColor.GREEN + "偷取中断", 0, 10, 10);
                            this.cancel();
                        }
                        if (check >= step) {
                            w.playSound(v.getLocation(), Sound.ENTITY_VILLAGER_YES, 2, 1);
                            w.playSound(v.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                            w.spawnParticle(Particle.COMPOSTER, v.getEyeLocation(), 100, 1, 1, 1);
                            playerStats.grantAchievement(p,6);
                            if(gameStatus.getGameTime() <= 10){
                                playerStats.grantAchievement(p,20);
                            }
                            int team = playerStats.getTeam(p);
                            String name = gameStatus.getTeamName(team);
                            bc.stealCashOutBC(name);
                            cashOutTeam.put(v, team);
                            playerTask.remove(p.getName());
                            whoIsStealing.remove(v);
                            this.cancel();
                        }
                        if (stealing.contains(p)) {
                            check += 1;
                            String progress = stealProgress(step, check);
                            p.sendTitle(" ", ChatColor.GREEN + progress, 0, 20, 0);
                            if (!v.isDead()) {
                                w.playSound(v.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 1);
                            }
                            stealing.remove(p);
                        }
                    }
                };
                checkStealing.runTaskTimer(plugin, 0L, 10L);
                playerTask.put(p.getName(), checkStealing);
            }
        }
    }
    public String stealProgress(int total,int step){
        StringBuilder progress = new StringBuilder();
        if(step >= total)return "偷取成功！";
        progress.append("偷取进度：");
        for(int i = 0;i < total;i ++){
            if(i < step){
                progress.append("||");
            }else {
                progress.append("..");
            }
        }
        return progress.toString();
    }
    public void bankCoins(Villager v,Player p,int time){
        if(!v.isDead()) {
            World w = p.getWorld();
            BukkitRunnable task = playerTask.getOrDefault(p.getName(), null);
            stealing.add(p);
            if (task == null) {
                BukkitRunnable checkStealing = new BukkitRunnable() {
                    final int step = time * 2;
                    int check = 0;
                    @Override
                    public void run() {
                        if (!stealing.contains(p) && check < step) {
                            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                            playerTask.remove(p.getName());
                            p.sendTitle(" ", ChatColor.GREEN + "存取中断", 0, 10, 10);
                            this.cancel();
                        }
                        if (check >= step) {
                            w.playSound(v.getLocation(), Sound.ENTITY_WANDERING_TRADER_YES, 2, 1);
                            w.playSound(v.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                            w.spawnParticle(Particle.COMPOSTER, v.getEyeLocation(), 100, 1, 1, 1);
                            int team = playerStats.getTeam(p);
                            String name = gameStatus.getTeamName(team);
                            int coins = k.getCoin(p);
                            Bukkit.getPluginManager().callEvent(new CashOutEvent(w,team,coins * 1000));
                            playerStats.grantAchievement(p,2);
                            if(coins >= 40){
                                playerStats.grantAchievement(p,25);
                            }
                            bc.bankCoins(name,coins > 9);
                            k.removeCoin(p);
                            playerTask.remove(p.getName());
                            this.cancel();
                        }
                        if (stealing.contains(p)) {
                            check += 1;
                            String progress = bankProgress(step, check);
                            p.sendTitle(" ", ChatColor.GREEN + progress, 0, 20, 0);
                            if (!v.isDead()) {
                                w.playSound(v.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 2, 0.8f + check * 0.05f);
                            }
                            stealing.remove(p);
                        }
                    }
                };
                checkStealing.runTaskTimer(plugin, 0L, 10L);
                playerTask.put(p.getName(), checkStealing);
            }
        }
    }
    public String bankProgress(int total,int step){
        StringBuilder progress = new StringBuilder();
        if(step >= total)return "存取成功！";
        progress.append("存取进度：");
        for(int i = 0;i < total;i ++){
            if(i < step){
                progress.append("||");
            }else {
                progress.append("..");
            }
        }
        return progress.toString();
    }
    public void reviveTeammate(ArmorStand a,Player p,int time){
        if(!a.isDead()) {
            BukkitRunnable task = playerTask.getOrDefault(p.getName(), null);
            reviving.add(p);
            if (task == null) {
                whoIsReviving.put(a, p.getName());
                BukkitRunnable checkReviving = new BukkitRunnable() {
                    final int step = time * 2;
                    int check = 0;
                    @Override
                    public void run() {
                        String p1Name = playerStats.getPlayerByStatue(a);
                        if (!reviving.contains(p) && check < step) {
                            if(p1Name != null){
                                Player p1 = Bukkit.getPlayer(p1Name);
                                beingRevive.remove(p1);
                            }
                            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                            playerTask.remove(p.getName());
                            whoIsReviving.remove(a);
                            p.sendTitle(" ", ChatColor.AQUA + "复活中断", 0, 10, 10);
                            this.cancel();
                        }
                        if (check >= step) {
                            p.playSound(a.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                            if(p1Name != null){
                                ChatColor c = switch (playerStats.getTeam(p)) {
                                    case 0 -> ChatColor.AQUA;
                                    case 1 -> ChatColor.GOLD;
                                    case 2 -> ChatColor.LIGHT_PURPLE;
                                    default -> ChatColor.GREEN;
                                };
                                Player p1 = Bukkit.getPlayer(p1Name);
                                k.respawn(p1,true, playerStats.getSkill(p) == 3,false);
                                Bukkit.broadcastMessage(c + p.getName() + ChatColor.YELLOW + "复活了" + c + p1Name);
                            }
                            playerTask.remove(p.getName());
                            playerStats.grantAchievement(p,5);
                            whoIsReviving.remove(a);
                            this.cancel();
                        }
                        if (reviving.contains(p)) {
                            check += 1;
                            String progress = reviveProgress(step, check);
                            p.sendTitle(" ", ChatColor.AQUA + progress, 0, 20, 0);
                            if (!a.isDead()) {
                                p.playSound(a.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 2, 0.6f + 0.1f * check);
                                p.playSound(a.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 2, 0.6f + 0.1f * check);
                                p.playSound(a.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 2, 0.6f + 0.1f * check);
                            }
                            if(p1Name != null) {
                                Player p1 = Bukkit.getPlayer(p1Name);
                                beingRevive.add(p1);
                                p1.sendTitle(" ", ChatColor.AQUA + "正在被复活", 0, 20, 0);
                                p1.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                        TextComponent.fromLegacyText(ChatColor.AQUA + progress));
                                p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 2, 0.6f + 0.1f * check);
                                p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 2, 0.6f + 0.1f * check);
                                p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 2, 0.6f + 0.1f * check);
                            }
                            reviving.remove(p);
                        }
                    }
                };
                checkReviving.runTaskTimer(plugin, 0L, 10L);
                playerTask.put(p.getName(), checkReviving);
            }
        }
    }
    public String reviveProgress(int total,int step){
        StringBuilder progress = new StringBuilder();
        if(step >= total)return "复活成功！";
        progress.append("复活进度：");
        for(int i = 0;i < total;i ++){
            if(i < step){
                progress.append("|");
            }else {
                progress.append(".");
            }
        }
        return progress.toString();
    }
    @EventHandler(priority = EventPriority.LOW)
    public void gravityGloves(PlayerInteractEvent interactEvent) {
        Player p = interactEvent.getPlayer();
        World w = p.getWorld();
        Action action = interactEvent.getAction();
        Entity carried = playerCarryItemMap.getOrDefault(p, null);
        if (carried != null) {
            interactEvent.setCancelled(true);
            carried.setGravity(true);
            if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                carried.teleport(p.getLocation());
            } else if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
                if (carried.getName().contains("罐") || carried.getName().contains("陨石")) {
                    if (!k.isC4Planted(carried)) {
                        isPrimed.add(carried);
                    }
                    throwingJars(p, carried, true);
                } else {
                    k.playerThrow(p, carried, 2);
                }
            }
            w.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
            playerCarryItemMap.remove(p);
            itemCarriedByPlayerMap.remove(carried);
            itemsLastThrowerMap.put(carried, p);
            playerStats.stopCarrying(p);
        }
    }
    public void openVault(Entity vault,double time){
        World w = vault.getWorld();
        BukkitRunnable opening = new BukkitRunnable() {
            int timer = 0;
            final int max = (int) (time * 4);
            @Override
            public void run() {
                w.spawnParticle(Particle.ITEM_CRACK,vault.getLocation(),20,0.5,1.5,0.5,0,new ItemStack(Material.EMERALD_BLOCK));
                Location vLoc = vault.getLocation();
                if(timer >= max || vault.isDead()) {
                    this.cancel();
                    vault.remove();
                    Location vaultLoc = vault.getLocation();
                    int gameMode = gameStatus.getGameMode(w);
                    w.playSound(vaultLoc, Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1);
                    w.playSound(vaultLoc, Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1);
                    w.playSound(vaultLoc, Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1);
                    w.spawnParticle(Particle.EXPLOSION_LARGE, vaultLoc, 1);
                    int vaultNum = gameStatus.getVaultNum(vault);
                    int count = 3 + (vaultNum - 1) / 2;
                    switch (gameMode) {
                        case 1 -> summonCoins(vaultLoc, count);
                        default -> cashBoxPop(vaultLoc);
                    }
                    return;
                }
                if(timer >= max - 1 && timer < max){
                    w.playSound(vLoc,Sound.BLOCK_NOTE_BLOCK_PLING,1,1);
                }else {
                    w.playSound(vLoc,Sound.BLOCK_CHAIN_HIT,1,1);
                }
                timer += 1;
            }
        };
        opening.runTaskTimer(plugin,0L,5L);
    }
    public void cashBoxPop(Location location){
        World w = location.getWorld();
        Entity box = k.createCashBox(location,true);
        speedRunBox.add(box);
        BukkitRunnable speedRun = new BukkitRunnable() {
            @Override
            public void run() {
                speedRunBox.remove(box);
            }
        };
        speedRun.runTaskLater(plugin,300L);
        BlockData data = Bukkit.createBlockData(Material.EMERALD_BLOCK);
        w.spawnParticle(Particle.BLOCK_CRACK,location,200,1,1,1,data);
    }
    public void summonCoins(Location loc,int count){
        World w = loc.getWorld();
        for(int i = 0;i < count;i++){
            Item coin =w.dropItem(loc.clone().add(0,1,0),items.coin());
            Vector spread = new Vector(random.nextDouble(1) - random.nextDouble(1),
                    random.nextDouble(1) - random.nextDouble(1),
                    random.nextDouble(1) - random.nextDouble(1));
            coin.setVelocity(spread.normalize().multiply(0.3));
            coin.setPickupDelay(10);
        }
    }
    @EventHandler
    public void checkCashBoxEvent(EntitySpawnEvent spawnEvent){
        Entity entity = spawnEvent.getEntity();
        if(entity instanceof Villager v){
            if(v.getName().contains("提现站")){
                checkCashBox(v);
            }
        }
    }
    public void checkCashBox(Villager v){
        World w = v.getWorld();
        int time = switch (gameStatus.getMap()){
            case -1 -> 90;
            case 3 -> 150;
            default -> 120;
        };
        BukkitRunnable check = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(v.isDead()){
                    this.cancel();
                }
                count += 1;
                List<Entity>entities = v.getNearbyEntities(1.5,1.5,1.5);
                for(Entity e :entities){
                    if(e instanceof ArmorStand a){
                        if(a.getName().contains("钱箱")){
                            Glow g = Glow.builder().color(ChatColor.GREEN).name(v.getName()).build();
                            g.addHolders(v);
                            for(Player p : w.getPlayers()){
                                g.display(p);
                            }
                            a.remove();
                            w.spawnParticle(Particle.EXPLOSION_LARGE,a.getLocation(),1);
                            v.getEquipment().setHelmet(new ItemStack(Material.EMERALD_BLOCK));
                            w.spawnParticle(Particle.COMPOSTER,v.getEyeLocation(),100,1,1,1);
                            Player carrier = itemCarriedByPlayerMap.getOrDefault(a,null);
                            Player thrower = itemsLastThrowerMap.getOrDefault(a,null);
                            if(carrier != null){
                                playerCarryItemMap.remove(carrier);
                            }
                            w.playSound(v.getLocation(),Sound.ENTITY_VILLAGER_YES,1,1);
                            w.playSound(v.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,2);
                            setCashOutTeam(v,a,carrier,thrower);
                            startCashOut(v,time);            //提现时间
                            int gameTime = gameStatus.getGameTime();
                            if(gameTime <= time){
                                gameStatus.setGameTime(time);
                                for (Player player : w.getPlayers()){
                                    if(playerStats.isGaming(player)
                                            || playerStats.isSpector(player)){
                                        player.sendTitle(ChatColor.GREEN + "！加时赛！"," ",10,50,10);
                                        player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
                                    }
                                }
                            }
                            if(random.nextBoolean()) {
                                Bukkit.getPluginManager().callEvent(new GameShowEvent(w, random.nextInt(8), false));
                            }
                            isCashingOut.add(v);
                            itemCarriedByPlayerMap.remove(a);
                            itemsLastThrowerMap.remove(a);
                            gameStatus.setCashing(v);
                            this.cancel();//锦标赛？
                            break;
                        }
                    }
                }
            }
        };
        check.runTaskTimer(plugin,0L,10L);
    }
    public void setCashOutTeam(Villager station,Entity box,Player carrier,Player thrower){
        station.setGlowing(true);
        Player finalPlayer;
        if(carrier == null && thrower == null){
            Bukkit.broadcastMessage(ChatColor.GREEN + "钱箱提现了他自己！这怎么可能！");
            cashOutTeam.put(station,-1);
            return;
        }else finalPlayer = Objects.requireNonNullElse(carrier, thrower);
        int team = playerStats.getTeam(finalPlayer);
        playerStats.stopCarrying(finalPlayer);
        String carrierTeam = gameStatus.getTeamName(team);
        bc.cashOutStartBC(carrierTeam);
        cashOutTeam.put(station,team);
        playerStats.grantAchievement(finalPlayer,2);
        if(gameStatus.getGameTime() <= 10){
            playerStats.grantAchievement(finalPlayer,19);
        }
        if(k.distance(finalPlayer.getLocation(),station.getLocation()) >= 20){
            playerStats.grantAchievement(finalPlayer,21);
        }
        if(speedRunBox.contains(box)){
            playerStats.grantAchievement(finalPlayer,22);
        }
    }
    public void startCashOut(Villager v,double time){
        World w = v.getWorld();
        List<Player>players = w.getPlayers();
        BossBar cashOutProgress = Bukkit.createBossBar(ChatColor.GREEN + "提现进度", BarColor.GREEN, BarStyle.SOLID);
        for(Player p : players){
            cashOutProgress.addPlayer(p);
        }
        BukkitRunnable bar = new BukkitRunnable() {
            double progress = 0;
            final double step = 1.0 / (time * 20);
            int count = 0;
            @Override
            public void run() {
                if(count % 20 == 0){
                    playSound(v);
                }
                if(progress >= 1 || v.isDead()){
                    this.cancel();
                    cashOutProgress.setProgress(1);
                    cashOutProgress.removeAll();
                    w.playSound(v.getLocation(),Sound.ENTITY_VILLAGER_DEATH,1,1);
                    w.playSound(v.getLocation(),Sound.ENTITY_VILLAGER_DEATH,1,1);
                    w.spawnParticle(Particle.EXPLOSION_HUGE,v.getLocation(),1);
                    Firework firework = (Firework) w.spawnEntity(v.getEyeLocation().add(0,0.5,0),EntityType.FIREWORK);
                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.setPower(0);
                    meta.addEffect(FireworkEffect.builder()
                            .withColor(Color.AQUA)
                            .withColor(Color.WHITE)
                            .trail(true)
                            .flicker(true)
                            .with(FireworkEffect.Type.BURST).build());
                    firework.setFireworkMeta(meta);
                    firework.detonate();
                    if(v.isDead())return;
                    v.setHealth(0);
                    int team = cashOutTeam.getOrDefault(v,-1);
                    if(team >= 0) {
                        Bukkit.getPluginManager().callEvent(new CashOutEvent(w, team,10000));
                    }else {
                        Bukkit.broadcastMessage(ChatColor.RED + "CNS黑掉了这个提现站");
                    }
                    cashOutTeam.remove(v);
                    gameStatus.stopCashing(v);
                }else {
                    if(whoIsStealing.getOrDefault(v,"").equals("")){
                        int team = cashOutTeam.getOrDefault(v, -1);
                        ChatColor c = switch (team) {
                            case 0 -> ChatColor.AQUA;
                            case 1 -> ChatColor.GOLD;
                            case 2 -> ChatColor.LIGHT_PURPLE;
                            default -> ChatColor.GREEN;
                        };
                        String name = gameStatus.getTeamName(team);
                        cashOutProgress.setColor(BarColor.GREEN);
                        cashOutProgress.setTitle(ChatColor.GREEN + "提现进度丨提现队伍： "+ c +"队伍"+ (team + 1) + " "+ name);
                    }else {
                        String name = whoIsStealing.get(v);
                        Player p = Bukkit.getPlayer(name);
                        if(p != null){
                            int team = playerStats.getTeam(p);
                            String teamName = gameStatus.getTeamName(team);
                            cashOutProgress.setTitle(teamName + ChatColor.RED + " 正在偷取提现");
                        }else {
                            cashOutProgress.setTitle(ChatColor.RED + "正在被偷取");
                        }
                        cashOutProgress.setColor(BarColor.RED);
                    }
                    cashOutProgress.setProgress(progress);
                }
                progress += step;
                count += 1;
            }
        };
        bar.runTaskTimer(plugin,0L,1L);
    }
    public void playSound(Villager v){
        List<Entity>entities = v.getNearbyEntities(10,10,10);
        for (Entity e : entities){
            if(e instanceof Player p){
                String name = whoIsStealing.getOrDefault(v,"");
                if(name.equals("")){
                    switch (random.nextInt(4)) {
                        case 0 -> p.playSound(v.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1, 1);
                        case 1 -> p.playSound(v.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                        case 2 -> {
                            p.playSound(v.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                            p.playSound(v.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1, 1);
                        }
                    }
                }
            }
        }
    }
    public void throwingJars(Player p,Entity jar,boolean isThrown){
        int type = jarType(jar.getName());
        switch (type){
            case 0:
                explosiveJar(p,jar,isThrown);
                break;
            case 1:
                gasJar(p,jar,isThrown);
                break;
            case 2:
                gooJar(p, jar, isThrown);
                break;
            case 3:
                fireJar(p, jar, isThrown);
                break;
            case 4:
                smokeJar(p, jar, isThrown);
                break;
            case 5:
                glitchJar(p, jar, isThrown);
                break;
            case 6:
                meteor(p, jar);
                break;
            case 7:
                superMeteor(p, jar);
                break;
        }
    }
    public void explosiveJar(Player p,Entity jar,boolean isThrown){
        World w = p.getWorld();
        Vector shootVec;
        if(isThrown) {
            Location shootLoc = p.getEyeLocation();
            shootVec = shootLoc.getDirection();
        }else {
            double randomX = random.nextDouble(2) - 1;
            double randomY = random.nextDouble(2) - 1;
            double randomZ = random.nextDouble(2) - 1;
            shootVec = new Vector(randomX,randomY,randomZ);
            shootVec.normalize();
        }
        w.playSound(jar.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1,0.8f);
        BukkitRunnable fly = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                boolean hit = false;
                List<Entity> nearby = jar.getNearbyEntities(1, 1, 1);
                for (Entity e : nearby){
                    if(e.getName().contains("传送门"))continue;
                    if(e == p)continue;
                    if(e instanceof LivingEntity l){
                        l.damage(10,p);
                        w.playSound(l.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,2,1);
                        hit = true;
                        if(l instanceof Player p1){
                            if(p1.getHealth() <= 10){
                                Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p,p1,2));
                            }
                        }
                        break;
                    }
                }
                w.spawnParticle(Particle.EXPLOSION_NORMAL,jar.getLocation(),10,0,0,0,0.1);
                jar.setVelocity(shootVec);
                if(count >= 30 || hit || (count > 10 && k.hitBallBlock(jar)) || jar.isDead()) {
                    this.cancel();
                    w.spawnParticle(Particle.EXPLOSION_HUGE, jar.getLocation(), 1);
                    w.playSound(jar.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
                    if(k.isC4Planted(jar)){
                        k.explode(p,jar,26,1,8);
                        k.breakBallBlock(p,jar.getLocation(),4,0.5);
                    }else {
                        k.explode(p,jar,20,1,5);
                        k.breakBallBlock(p,jar.getLocation(),3,0.3);
                    }
                    w.spawnParticle(Particle.EXPLOSION_HUGE,jar.getLocation(),1);
                    jar.remove();
                    isPrimed.remove(jar);
                }
                count += 1;
            }
        };
        fly.runTaskTimer(plugin,0L,2L);
    }
    public void gasJar(Player p,Entity jar,boolean isThrown){
        World w = p.getWorld();
        double power = 0;
        if(isThrown){
            power = 2;
        }
        k.playerThrow(p,jar,power);
        BukkitRunnable land = new BukkitRunnable() {
            @Override
            public void run() {
                Particle.DustOptions dust = new Particle.DustOptions(Color.LIME,2);
                w.spawnParticle(Particle.REDSTONE,jar.getLocation(),1,dust);
                boolean hit = false;
                if(!k.isC4Planted(jar)) {
                    List<Entity> nearby = jar.getNearbyEntities(1, 1, 1);
                    for (Entity e : nearby) {
                        if(e.getName().contains("传送门"))continue;
                        if (e == p) continue;
                        if (e instanceof LivingEntity l) {
                            if (l instanceof ArmorStand) continue;
                            l.damage(10, p);
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2, 1);
                            hit = true;
                            if(l instanceof Player p1){
                                if(p1.getHealth() <= 10){
                                    Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p,p1,2));
                                }
                            }
                            break;
                        }
                    }
                }
                if(!k.isC4Planted(jar) || jar.isDead()) {
                    if (hit || k.hitSquareBlock(jar) || jar.isDead()) {
                        this.cancel();
                        isPrimed.remove(jar);
                        w.playSound(jar.getLocation(), Sound.BLOCK_LANTERN_BREAK, 2, 1);
                        w.spawnParticle(Particle.EXPLOSION_LARGE, jar.getLocation(), 1);
                        k.gas(p, jar, 20, 5);
                    }
                }
            }
        };
        land.runTaskTimer(plugin,10L,2L);
    }
    public void gooJar(Player p,Entity jar,boolean isThrown){
        World w = p.getWorld();
        double power = 0;
        if(isThrown){
            power = 2;
        }
        k.playerThrow(p,jar,power);
        Vector forwardVec = p.getEyeLocation().getDirection().setY(0).normalize();
        Vector downVec = new Vector(0, -1, 0).normalize();
        Vector rightVec = forwardVec.clone().crossProduct(downVec);
        BukkitRunnable land = new BukkitRunnable() {
            @Override
            public void run() {
                BlockData data = Bukkit.createBlockData(Material.WHITE_WOOL);
                w.spawnParticle(Particle.BLOCK_DUST, jar.getLocation(), 10, data);
                boolean hit = false;
                if(!k.isC4Planted(jar)) {
                    List<Entity> nearby = jar.getNearbyEntities(3, 3, 3);
                    for (Entity e : nearby) {
                        if(e.getName().contains("传送门"))continue;
                        if (e == p) continue;
                        if (e instanceof LivingEntity l) {
                            if (l instanceof ArmorStand) continue;
                            l.damage(5, p);
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2, 1);
                            hit = true;
                            if(l instanceof Player p1){
                                if(p1.getHealth() <= 5){
                                    Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p,p1,2));
                                }
                            }
                            break;
                        }
                    }
                }
                if (!k.isC4Planted(jar) || jar.isDead()) {
                    if (hit || k.hitSquareBlock(jar) || jar.isDead()) {
                        this.cancel();
                        isPrimed.remove(jar);
                        k.goo(p, jar.getLocation(), 2);
                        k.goo(p, jar.getLocation().add(rightVec.clone().multiply(3)), 2);
                        k.goo(p, jar.getLocation().add(rightVec.clone().multiply(-3)), 2);
                        jar.remove();
                    }
                }
            }
        };
        land.runTaskTimer(plugin,10L,2L);
    }
    public void fireJar(Player p,Entity jar,boolean isThrown){
        World w = p.getWorld();
        double power = 0;
        if(isThrown){
            power = 2;
        }
        k.playerThrow(p,jar,power);
        BukkitRunnable land = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.FLAME,jar.getLocation(),10,0,0,0,0.1);
                boolean hit = false;
                if(!k.isC4Planted(jar)) {
                    List<Entity> nearby = jar.getNearbyEntities(1, 1, 1);
                    for (Entity e : nearby) {
                        if(e.getName().contains("传送门"))continue;
                        if (e == p) continue;
                        if (e instanceof LivingEntity l) {
                            l.damage(10, p);
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2, 1);
                            hit = true;
                            if(l instanceof Player p1){
                                if(p1.getHealth() <= 10){
                                    Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p,p1,2));
                                }
                            }
                            break;
                        }
                    }
                }
                if(!k.isC4Planted(jar) || jar.isDead()) {
                    if (hit || k.hitSquareBlock(jar) || jar.isDead()) {
                        this.cancel();
                        isPrimed.remove(jar);
                        w.playSound(jar.getLocation(), Sound.ITEM_FIRECHARGE_USE, 2, 1);
                        k.fire(p, jar, 10, 3);
                    }
                }
            }
        };
        land.runTaskTimer(plugin,10L,2L);
    }
    public void smokeJar(Player p,Entity jar,boolean isThrown){
        World w = p.getWorld();
        double power = 0;
        if(isThrown){
            power = 2;
        }
        k.playerThrow(p,jar,power);
        BukkitRunnable land = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.CRIT,jar.getLocation(),10,0,0,0,0.3);
                boolean hit = false;
                if(!k.isC4Planted(jar)) {
                    List<Entity> nearby = jar.getNearbyEntities(1, 1, 1);
                    for (Entity e : nearby) {
                        if(e.getName().contains("传送门"))continue;
                        if (e == p) continue;
                        if (e instanceof LivingEntity l) {
                            l.damage(10, p);
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2, 1);
                            hit = true;
                            if(l instanceof Player p1){
                                if(p1.getHealth() <= 10){
                                    Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p,p1,2));
                                }
                            }
                            break;
                        }
                    }
                }
                if(!k.isC4Planted(jar) || jar.isDead()) {
                    if (hit || k.hitSquareBlock(jar) || jar.isDead()) {
                        this.cancel();
                        isPrimed.remove(jar);
                        w.playSound(jar.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 1);
                        k.smoke(jar, 25, 5);
                    }
                }
            }
        };
        land.runTaskTimer(plugin,10L,2L);
    }
    public void glitchJar(Player p,Entity jar,boolean isThrown){
        World w = p.getWorld();
        double power = 0;
        if(isThrown){
            power = 2;
        }
        k.playerThrow(p,jar,power);
        BukkitRunnable land = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.ELECTRIC_SPARK,jar.getLocation(),5,0,0,0,0.1);
                boolean hit = false;
                if(!k.isC4Planted(jar)) {
                    List<Entity> nearby = jar.getNearbyEntities(1, 1, 1);
                    for (Entity e : nearby) {
                        if(e.getName().contains("传送门"))continue;
                        if (e == p) continue;
                        if (e instanceof LivingEntity l) {
                            l.damage(10, p);
                            w.playSound(l.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2, 1);
                            hit = true;
                            if(l instanceof Player p1){
                                if(p1.getHealth() <= 10){
                                    Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(p,p1,2));
                                }
                            }
                            break;
                        }
                    }
                }
                if(!k.isC4Planted(jar) || jar.isDead()) {
                    if (hit || k.hitSquareBlock(jar) || jar.isDead()) {
                        this.cancel();
                        isPrimed.remove(jar);
                        w.playSound(jar.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 2, 1);
                        w.playSound(jar.getLocation(), Sound.BLOCK_LANTERN_BREAK, 2, 1);
                        k.glitch(p,jar, 3, 4,20);
                    }
                }
            }
        };
        land.runTaskTimer(plugin,10L,2L);
    }
    public void meteor(Player p,Entity jar) {
        World w = p.getWorld();
        k.playerThrow(p, jar, 2);
        BukkitRunnable land = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.FLAME, jar.getLocation(), 10, 0, 0, 0, 0.1);
                boolean hit = false;
                if(!k.isC4Planted(jar)) {
                    List<Entity> nearby = jar.getNearbyEntities(1, 1, 1);
                    for (Entity e : nearby) {
                        if(e.getName().contains("传送门"))continue;
                        if (e == p) continue;
                        if (e instanceof LivingEntity l) {
                            l.damage(10, p);
                            k.knockBack(l, jar.getLocation(), 3);
                            hit = true;
                            break;
                        }
                    }
                }
                if (!k.isC4Planted(jar)) {
                    if (hit || k.hitSquareBlock(jar) || jar.isDead()) {
                        this.cancel();
                        isPrimed.remove(jar);
                        w.playSound(jar.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        w.spawnParticle(Particle.EXPLOSION_LARGE, jar.getLocation(), 1);
                        jar.remove();
                        k.breakBallBlock(p, jar.getLocation(), 2,1);
                    }
                }
            }
        };
        land.runTaskTimer(plugin, 0L, 2L);
    }
    public void superMeteor(Player p,Entity jar) {
        World w = p.getWorld();
        k.playerThrow(p, jar, 2);
        BukkitRunnable land = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(Particle.SOUL_FIRE_FLAME, jar.getLocation(), 10, 0, 0, 0, 0.1);
                boolean hit = false;
                if(!k.isC4Planted(jar)) {
                    List<Entity> nearby = jar.getNearbyEntities(1, 1, 1);
                    for (Entity e : nearby) {
                        if(e.getName().contains("传送门"))continue;
                        if (e == p) continue;
                        if (e instanceof LivingEntity l) {
                            l.damage(20, p);
                            hit = true;
                            break;
                        }
                    }
                }
                if (!k.isC4Planted(jar)) {
                    if (hit || k.hitBallBlock(jar) || jar.isDead()) {
                        this.cancel();
                        isPrimed.remove(jar);
                        w.playSound(jar.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                        w.spawnParticle(Particle.EXPLOSION_LARGE, jar.getLocation(), 1);
                        jar.remove();
                        k.explode(p,jar,20,0,3);
                    }
                }
            }
        };
        land.runTaskTimer(plugin, 0L, 2L);
    }
    @EventHandler
    public void gameShowEvent(GameShowEvent event){
        World w = event.getWorld();
        if(!gameStatus.isGaming(w))return;
        int id = event.getGameShowID();
        if(id >= 0) {
            if (event.isWorldEvent()) {
                mapEvent(w, id);
            } else {
                sponsorEvent(w, id);
            }
        }else {
            bc.gameShowEventBC(-1);
            gameStatus.setWorldEvent(-1);
        }
    }
    @EventHandler
    public void entityShoot(ProjectileLaunchEvent launchEvent){
        Projectile projectile = launchEvent.getEntity();
        if(projectile.getShooter() instanceof Entity shooter) {
            World w = shooter.getWorld();
            if (shooter instanceof LivingEntity l) {
                if (l instanceof Blaze) {
                    launchEvent.setCancelled(true);
                    Location shootLoc = l.getEyeLocation();
                    Vector shootVec = shootLoc.getDirection();
                    Arrow a = w.spawnArrow(shootLoc, shootVec, 1.5f, 10);
                    a.setDamage(1);
                    a.setColor(Color.ORANGE);
                    a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                }
            }
        }
    }
    @EventHandler
    public void entityDeath(EntityDeathEvent deathEvent){
        Entity dead = deathEvent.getEntity();
        World w = dead.getWorld();
        if(dead.getName().contains("罐")){
            deathEvent.getDrops().clear();
        }
    }
    public void mapEvent(World w,int id){
        String eventName = gameShowName[id];
        BukkitRunnable countDown = new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                if(count <= 0){
                    gameStatus.setWorldEvent(id);
                    bc.gameShowEventBC(id);
                    List<Player>players = w.getPlayers();
                    for(Player p : players){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                            p.sendTitle(ChatColor.GREEN + "已启用：" + eventName, " ", 10, 100, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.5f);
                        }
                    }
                    switch (id) {
                        case 0 -> meteorEvent(w);
                        case 1 -> lightningEvent(w);
                        case 2 -> netherEvent(w);
                        case 3 -> deathEvent(w);
                        case 4 -> gravityEvent(w);
                    }
                    this.cancel();
                }
                if(count == 10){
                    List<Player>players = w.getPlayers();
                    for(Player p : players){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                            p.sendTitle(" ", eventName + ChatColor.GREEN + " 事件即将到来", 10, 40, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        }
                    }
                }
                if(count <= 5 && count > 0){
                    List<Player>players = w.getPlayers();
                    for(Player p : players){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                            p.sendTitle(" ", ChatColor.RED + "" + count, 0, 20, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                    }
                }
                count -= 1;
            }
        };
        countDown.runTaskTimer(plugin,0L,20L);
    }
    public void sponsorEvent(World w,int id){
        bc.sponsorShowEventBC(id);
        String sponsor = "";
        switch (id) {
            case 0 -> {
                sponsor = ChatColor.YELLOW + "OSPUZE";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0));
                    }
                }
            }
            case 1 -> {
                sponsor = ChatColor.GOLD + "HOLTOW";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        p.setAbsorptionAmount(10);
                    }
                }
            }
            case 2 -> {
                sponsor = ChatColor.AQUA + "ISEUL-" + ChatColor.LIGHT_PURPLE + "T";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 1));
                    }
                }
            }
            case 3 -> {
                sponsor = ChatColor.DARK_AQUA + "ENGIMO";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 0));
                    }
                }
            }
            case 4 -> {
                sponsor = ChatColor.RED + "DISSUN";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0));
                    }
                }
            }
            case 5 -> {
                sponsor = ChatColor.WHITE + "VAIIYA";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 0));
                    }
                }
            }
            case 6 -> {
                sponsor = ChatColor.DARK_RED + "VOLPE";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 0));
                    }
                }
            }
            case 7 -> {
                sponsor = ChatColor.DARK_RED + "" + ChatColor.MAGIC + "WE_ARE_CNS";
                for (Player p : w.getPlayers()) {
                    if (playerStats.isGaming(p)) {
                        Inventory inv = p.getInventory();
                        ItemStack[] content = inv.getContents();
                        k.shuffleItem(content);
                        inv.setContents(content);
                    }
                }
            }
        }
        for(Player p : w.getPlayers()){
            if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
                p.sendTitle(ChatColor.GREEN +"！赞助商事件！"
                        ,sponsor + ChatColor.GREEN + "赞助了比赛！" ,10,50,10);
            }
        }
    }
    public void meteorEvent(World w){
        List<Location>locations = new ArrayList<>();
        List<Entity>entities = w.getEntities();
        for(Entity e : entities){
            if(e.getName().contains("站")){
                locations.add(e.getLocation());
            }
        }
        Location[]locs = locations.toArray(new Location[0]);
        BukkitRunnable summon = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count > 11 || locs.length == 0 || gameStatus.getWorldEvent() == -1){
                    this.cancel();
                    Bukkit.getPluginManager().callEvent(new GameShowEvent(w,-1,true));
                    return;
                }
                count += 1;
                for(Location l : locs){
                    BukkitRunnable summon = new BukkitRunnable() {
                        int countS = 0;
                        @Override
                        public void run() {
                            if(countS > 5){
                                this.cancel();
                                return;
                            }
                            countS += 1;
                            double spreadX = random.nextDouble() - random.nextDouble();
                            double spreadZ = random.nextDouble() - random.nextDouble();
                            Vector spread = new Vector(spreadX,0,spreadZ).normalize();
                            Location spawnLoc = l.clone().add(spread.multiply(10)).add(0,50,0);
                            summonMeteor(spawnLoc);
                        }
                    };
                    summon.runTaskTimer(plugin,0L,10L);
                }
            }
        };
        summon.runTaskTimer(plugin,0L,200L);
    }
    public void summonMeteor(Location loc){
        World w = loc.getWorld();
        BlockData data = Bukkit.createBlockData(Material.MAGMA_BLOCK);
        FallingBlock block = w.spawnFallingBlock(loc,data);
        block.setDropItem(false);
        block.setGlowing(true);
        BukkitRunnable falling = new BukkitRunnable() {
            @Override
            public void run() {
                if(block.isDead()) {
                    k.breakBallBlock(null,block.getLocation(),3,0.5);
                    w.spawnParticle(Particle.EXPLOSION_HUGE,block.getLocation(),1);
                    w.getBlockAt(block.getLocation()).setType(Material.AIR);
                    w.playSound(block.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,3,1);
                    ArmorStand a = (ArmorStand) w.spawnEntity(block.getLocation(),EntityType.ARMOR_STAND);
                    a.setCustomName(ChatColor.GOLD + "陨石核心");
                    a.setCustomNameVisible(true);
                    a.setInvulnerable(true);
                    a.setVisible(false);
                    a.setSmall(true);
                    a.getEquipment().setHelmet(new ItemStack(Material.MAGMA_BLOCK));
                    meteors.add(a);
                    if(meteors.size() > 8){
                        ArmorStand meteor = meteors.get(0);
                        meteor.remove();
                        meteors.remove(0);
                    }
                    this.cancel();
                }
                w.spawnParticle(Particle.LAVA,block.getLocation(),2);
            }
        };
        falling.runTaskTimer(plugin,0L,1L);
    }
    public void lightningEvent(World w){
        if(w.isClearWeather()){
            w.setStorm(true);
        }
        BukkitRunnable check = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count > 5 || gameStatus.getWorldEvent() == -1){
                    w.setStorm(false);
                    Bukkit.getPluginManager().callEvent(new GameShowEvent(w,-1,true));
                    this.cancel();
                    return;
                }
                strikeLightning(w);
                count += 1;
            }
        };
        check.runTaskTimer(plugin,300L,400L);
    }
    public void strikeLightning(World w){
        for(Player p : w.getPlayers()){
            if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                if (random.nextBoolean()) {
                    Location pLoc = p.getLocation();
                    Location strikeLoc = pLoc.add(random.nextInt(10) - random.nextInt(10),
                            0, random.nextInt(10) - random.nextInt(10));
                    w.strikeLightningEffect(strikeLoc);
                }
            }
        }
        BukkitRunnable check = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                int maxY = -64;
                List<Player>allPlayer = w.getPlayers();
                Player highPlayer = null;
                String name = ChatColor.RED + "非洲酋长";
                for(Player p : allPlayer){
                    if(p.getGameMode() == GameMode.SPECTATOR)continue;
                    if(!playerStats.isGaming(p))continue;
                    int playerY = p.getLocation().getBlockY();
                    if(playerY > maxY){
                        maxY = playerY;
                        highPlayer = p;
                        name = p.getName();
                    }
                }
                for (Player p : allPlayer){
                    p.sendTitle(" ",ChatColor.AQUA + "雷击目标：" + name,0,20,10);
                }
                if(count % 2 == 0){
                    int step = count / 2;
                    for(Player p : w.getPlayers()){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    TextComponent.fromLegacyText(ChatColor.AQUA + lightningProgress(10, step)));
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.5f + 0.1f * step);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.5f + 0.1f * step);
                        }
                    }
                }
                if(count > 20){
                    if(highPlayer != null) {
                        double max = highPlayer.getMaxHealth();
                        k.breakBallBlock(highPlayer,highPlayer.getLocation(),2,0);
                        w.strikeLightningEffect(highPlayer.getLocation());
                        highPlayer.damage(max * 0.4);
                        highPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                        highPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
                    }else {
                        for (Player p : allPlayer){
                            p.sendTitle(" ",ChatColor.AQUA + "闪电击中了某个非洲酋长",0,20,10);
                        }
                    }
                    this.cancel();
                }
                count += 1;
            }
        };
        check.runTaskTimer(plugin,0L,5L);
    }
    public String lightningProgress(int total,int step){
        StringBuilder progress = new StringBuilder();
        progress.append("雷击充能：");
        for(int i = 0;i < total;i ++){
            if(i < step){
                progress.append("|");
            }else {
                progress.append(".");
            }
        }
        return progress.toString();
    }
    public void netherEvent(World w){
        List<Villager>stations = new ArrayList<>();
        List<Entity>entities = w.getEntities();
        for(Entity e : entities){
            if(e.getName().contains("站")){
                if(e instanceof Villager v) {
                    stations.add(v);
                }
            }
        }
        Villager[]vs = stations.toArray(new Villager[0]);
        BukkitRunnable summon = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count >= 24 || vs.length == 0 || gameStatus.getWorldEvent() == -1){
                    for(Villager v : vs) {
                        v.teleport(v.getLocation());
                    }
                    for(Entity e : netherEntity){
                        w.playSound(e.getLocation(),Sound.ITEM_FIRECHARGE_USE,2,1);
                        w.spawnParticle(Particle.FLAME,e.getLocation(),50,0,0,0,0.1);
                        e.remove();
                    }
                    Bukkit.getPluginManager().callEvent(new GameShowEvent(w,-1,true));
                    this.cancel();
                }
                if(count == 0) {
                    for (Villager v : vs) {
                        if (v.isDead()) continue;
                        if (v.getVehicle() != null) continue;
                        Piglin piglin = (Piglin) w.spawnEntity(v.getLocation(), EntityType.PIGLIN);
                        piglin.setBaby();
                        piglin.setImmuneToZombification(true);
                        piglin.removeMaterialOfInterest(Material.GOLDEN_SWORD);
                        piglin.addPassenger(v);
                        piglin.setCustomName(ChatColor.GOLD + "地狱搬运工");
                        netherEntity.add(piglin);
                        netherInvade(v.getLocation());
                    }
                }
                for (Villager v: vs){
                    w.playSound(v.getLocation(),Sound.BLOCK_LAVA_POP,1,1);
                    w.spawnParticle(Particle.LAVA,v.getEyeLocation(),10);
                }
                count += 1;
            }
        };
        summon.runTaskTimer(plugin,0L,100L);
    }
    public void netherInvade(Location loc){
        World w = loc.getWorld();
        for(int a = -5;a <= 5;a ++) {
            for (int b = -5; b <= 5; b++) {
                for (int c = -5; c <= 5; c++) {
                    Location bLoc = loc.clone().add(a, b, c);
                    Block block = w.getBlockAt(bLoc);
                    if(k.isBreakable(null,block)) {
                        double offsetA = a + random.nextDouble();
                        double offsetC = c + random.nextDouble();
                        double noise1 = simplex.noise(offsetA / 100.0, offsetC / 100.0);
                        double noise2 = simplex.noise(offsetA / 50.0, offsetC / 50.0);
                        double noise3 = simplex.noise(offsetA / 25.0, offsetC / 25.0);
                        double noise = (noise1 + noise2 * 0.5 + noise3 * 0.25);
                        double absNoise = Math.sin(Math.abs(noise));
                        int id = k.compare(netherBlocks.length, absNoise);
                        if (id == -1) {
                            block.setType(netherBlocks[0]);
                            continue;
                        }
                        block.setType(netherBlocks[id - 1]);
                    }
                }
            }
        }
        Block ghastBlock = w.getHighestBlockAt(loc);
        Location ghostLoc = ghastBlock.getLocation().add(0,20,0);
        ArmorStand stand = (ArmorStand) w.spawnEntity(ghostLoc,EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setGravity(false);
        stand.setCustomName("炮手基座");
        Ghast ghast = (Ghast) w.spawnEntity(ghostLoc,EntityType.GHAST);
        ghast.setCustomName(ChatColor.GOLD + "地狱炮手");
        ghast.setGravity(true);
        ghast.setSilent(true);
        ghast.setGlowing(true);
        ghast.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,86400,10));
        stand.addPassenger(ghast);
        BukkitRunnable shoot = new BukkitRunnable() {
            @Override
            public void run() {
                if (ghast.isDead()) {
                    this.cancel();
                    return;
                }
                w.playSound(ghast.getLocation(),Sound.ENTITY_GHAST_WARN,5,0.8f + random.nextFloat(0.4f));
                for(int i = 0;i < 5;i++){
                    Snowball ball = (Snowball) w.spawnEntity(ghast.getLocation(),EntityType.SNOWBALL);
                    ball.setItem(new ItemStack(Material.FIRE_CHARGE));
                    ball.setShooter(ghast);
                    double spreadX = random.nextDouble() - random.nextDouble();
                    double spreadY = random.nextDouble() - random.nextDouble();
                    double spreadZ = random.nextDouble() - random.nextDouble();
                    Vector spread = new Vector(spreadX,spreadY,spreadZ);
                    ball.setVelocity(spread.normalize().multiply(0.2));
                    ball.setFireTicks(1200);
                    BukkitRunnable hit = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(ball.isDead()){
                                this.cancel();
                                k.breakBallBlock(null,ball.getLocation(),2,0.5);
                                w.spawnParticle(Particle.EXPLOSION_HUGE,ball.getLocation(),1);
                                w.playSound(ball.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,2,1);
                            }
                            w.spawnParticle(Particle.FLAME,ball.getLocation(),0);
                        }
                    };
                    hit.runTaskTimer(plugin,0L,5L);
                }
            }
        };
        shoot.runTaskTimer(plugin,random.nextLong(40L),200L);
        netherEntity.add(ghast);
        for(int i = 0;i < 2;i++){
            Location spawnLoc = loc.clone().add(
                    random.nextInt(10) - random.nextInt(10),
                    0,random.nextInt(10) - random.nextInt(10));
            WitherSkeleton sk = (WitherSkeleton) w.spawnEntity(spawnLoc,EntityType.WITHER_SKELETON);
            sk.setCustomName(ChatColor.GOLD + "地狱弓手");
            sk.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
            netherEntity.add(sk);
        }
        for(int i = 0;i < 3;i++) {
            Location spawnLoc = loc.clone().add(
                    random.nextInt(5) - random.nextInt(5),
                    0, random.nextInt(5) - random.nextInt(5));
            Blaze blaze = (Blaze) w.spawnEntity(spawnLoc,EntityType.BLAZE);
            blaze.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,86400,2));
            blaze.setCustomName(ChatColor.GOLD + "地狱射手");
            netherEntity.add(blaze);
        }
    }
    public void deathEvent(World w) {
        BukkitRunnable cancel = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new GameShowEvent(w, -1, true));
            }
        };
        cancel.runTaskLater(plugin, 2400L);
    }
    public void deathExplosion(Player p){
        Location loc = p.getLocation();
        World w = p.getWorld();
        w.playSound(loc,Sound.ENTITY_TNT_PRIMED,2,0.8f);
        TNTPrimed tnt = (TNTPrimed) w.spawnEntity(loc,EntityType.PRIMED_TNT);
        tnt.setGravity(false);
        BukkitRunnable explode = new BukkitRunnable() {
            @Override
            public void run() {
                tnt.remove();
                w.spawnParticle(Particle.EXPLOSION_LARGE,loc,1);
                w.playSound(loc,Sound.ENTITY_GENERIC_EXPLODE,1,1);
                k.breakBallBlock(p,loc,2,0);
                for(Entity e : w.getNearbyEntities(loc,10,10,10)){
                    k.knockBack(e,loc,-2);
                }
            }
        };
        explode.runTaskLater(plugin,40L);
    }
    public void gravityEvent(World w){
        for(Player p : w.getPlayers()){
            if(playerStats.isGaming(p)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2400, 5));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 2400, 0));
            }
        }
        BukkitRunnable cancel = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count > 12) {
                    Bukkit.getPluginManager().callEvent(new GameShowEvent(w, -1, true));
                    this.cancel();
                    return;
                }
                for(Entity e : w.getEntities()){
                    if(e instanceof LivingEntity l) {
                        if(l instanceof Player p){
                            if(!playerStats.isGaming(p))continue;
                        }
                        l.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0));
                    }
                }
                count += 1;
            }
        };
        cancel.runTaskTimer(plugin,0L,200L);
    }
    @EventHandler
    public void gameStart(GameStartEvent startEvent) {
        gameStatus.setTeamNames(3);
        gameStatus.clearNums();
        World w = startEvent.getWorld();
        w.setTime(random.nextInt(25) * 1000);
        int map = startEvent.getMap();
        int mode = gameStatus.getGameMode(w);
        Location loc,teleportLoc;
        switch (map){
            case 0:
                loc = new Location(w,-184,-13,-107);
                teleportLoc = new Location(w,-180,30,-120);
                break;
            case 1:
                loc = new Location(w,68,35,1096);
                teleportLoc = new Location(w,95,80,1110);
                break;
            case 2:
                loc = new Location(w,-1022,-1,-330);
                teleportLoc = new Location(w,-1000,35,-300);
                w.setGameRule(GameRule.DO_FIRE_TICK,false);
                break;
            case 3:
                loc = new Location(w,-834,40,719);
                teleportLoc = new Location(w,-840,90,815);
                break;
            case 4:
                loc = new Location(w,-61,70,1340);
                teleportLoc = new Location(w,-50,84,1341);
                w.setGameRule(GameRule.DO_FIRE_TICK,false);
                break;
            default:
                loc = new Location(w,-226,-20,500);
                teleportLoc = new Location(w,-195,5,525);
        }
        playerStats.clearKD();
        gameStatus.registerBoard();
        gameStatus.setMap(map);
        for (Player p : w.getPlayers()) {
            if(playerStats.getTeam(p) >= 0 || playerStats.isSpector(p)) {
                if(!playerStats.isSpector(p)){
                    playerStats.setGaming(p);
                }
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(teleportLoc);
                p.setAbsorptionAmount(0);
                gameStatus.setGameBoard(p);
            }
        }
        BukkitRunnable placeBlock = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count == 0){
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Villager
                                || e instanceof ArmorStand
                                || e instanceof ItemFrame
                                || e instanceof Item) {
                            e.remove();
                        }
                        if(e instanceof Snowman s){
                            s.setHealth(0);
                        }
                    }
                }
                if(count == 1){
                    gameStatus.setGaming(w);
                    w.getBlockAt(loc).setType(Material.REDSTONE_BLOCK);
                }
                if(count == 2){
                    w.getBlockAt(loc).setType(Material.AIR);
                    gameStatus.setSpawnPoints(w);
                }
                if(count == 3){
                    summonStation(w);
                    summonStation(w);
                    if(mode == 1){
                        bankStationTimer(w,90);
                    }
                }
                if(count == 4){
                    summonVault(w,map);
                    if(mode == 1){
                        summonVault(w,map);
                    }
                    summonJar(w);
                    setJumpPoint(w);
                    startTimer(w);
                    bc.introducing(map);
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Item) {
                            e.remove();
                        }
                    }
                    BukkitRunnable later = new BukkitRunnable() {
                        int count = 10;
                        @Override
                        public void run() {
                            if(count <= 0) {
                                if(gameStatus.getPlayerSpawnPoint().length > 1) {
                                    teleportPlayer(3);
                                    bc.start(mode);
                                }
                                this.cancel();
                            }else {
                                for (Player p : w.getPlayers()) {
                                    if(playerStats.isGaming(p) || playerStats.isSpector(p)){
                                        p.sendTitle(ChatColor.AQUA +""+ count," ",0,20,10);
                                        p.playSound(p.getLocation(),Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON,1,1);
                                    }
                                }
                            }
                            count -= 1;
                        }
                    };
                    later.runTaskTimer(plugin, 0L,20L);
                    this.cancel();
                }
                count += 1;
            }
        };
        placeBlock.runTaskTimer(plugin,0L,30L);
    }
    @EventHandler
    public void cashOut(CashOutEvent cashOutEvent) {
        World w = cashOutEvent.getWorld();
        int money = cashOutEvent.getMoney();
        int mode = gameStatus.getGameMode(w);
        int win = switch (mode){
            case 1 -> 40000;
            default -> 20000;
        };
        if(gameStatus.isGaming(w)) {
            int team = cashOutEvent.getTeam();
            String teamName = gameStatus.getTeamName(team);
            gameStatus.addScore(teamName, money);
            int score = gameStatus.getScore(teamName);
            int map = gameStatus.getMap();
            if (score >= win) {
                Bukkit.getPluginManager().callEvent(new GameEndEvent(w, team));
            } else {
                if(mode != 1) {
                    bc.cashOutCompleteBC(teamName);
                    summonStation(w);
                    summonVault(w, map);
                }
            }
        }
    }
    @EventHandler
    public void bankStationVanish(EntityDeathEvent deathEvent) {
        Entity e = deathEvent.getEntity();
        World w = e.getWorld();
        int map = gameStatus.getMap();
        if (e.getCustomName() != null) {
            if (gameStatus.isGaming(w)) {
                if (e.getCustomName().contains("存钱站")) {
                    summonStation(w);
                    summonVault(w, map);
                }
            }
        }
    }
    @EventHandler
    public void playerPickUpCoin(EntityPickupItemEvent pickupItemEvent){
        Entity e = pickupItemEvent.getEntity();
        if(e instanceof Player p){
            World w = p.getWorld();
            ItemStack item = pickupItemEvent.getItem().getItemStack();
            if(item.getType() == Material.EMERALD){
                BukkitRunnable recordLoc = new BukkitRunnable() {
                    @Override
                    public void run() {
                        int coins = Math.min(20,k.getCoin(p));
                        if(coins <= 0){
                            this.cancel();
                            playerTrace.remove(p.getName());
                            return;
                        }
                        List<Location> loc = playerTrace.getOrDefault(p.getName(),new ArrayList<>());
                        loc.add(p.getLocation().add(0,1,0));
                        if(loc.size() >= coins + 3){
                            loc.remove(0);
                        }
                        playerTrace.put(p.getName(),loc);
                    }
                };
                BukkitRunnable readLoc = new BukkitRunnable() {
                    @Override
                    public void run() {
                        int coins = k.getCoin(p);
                        if(coins <= 0){
                            this.cancel();
                            return;
                        }
                        List<Location> loc = playerTrace.getOrDefault(p.getName(),new ArrayList<>());
                        Color c = switch (playerStats.getTeam(p)){
                            case 0-> Color.AQUA;
                            case 1-> Color.ORANGE;
                            case 2-> Color.FUCHSIA;
                            default->Color.WHITE;
                        };
                        Particle.DustOptions dust = new Particle.DustOptions(c,1);
                        for(Location l : loc){
                            w.spawnParticle(Particle.REDSTONE,l,5,0,0.1,0,0,dust);
                        }
                    }
                };
                recordLoc.runTaskTimer(plugin,0L,5L);
                readLoc.runTaskTimer(plugin,0L,20L);
                if(playerStats.isGaming(p)) {
                    if (k.getCoin(p) >= 10) {
                        if (!isRich.contains(p)) {
                            int team = playerStats.getTeam(p);
                            String name = gameStatus.getTeamName(team);
                            bc.carryManyCoins(name);
                            isRich.add(p);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void gameEnd(GameEndEvent endEvent){
        World w = endEvent.getWorld();
        if(!gameStatus.isGaming(w))return;
        w.setGameRule(GameRule.DO_FIRE_TICK,true);
        w.setTime(6000);
        int win = endEvent.getWinTeam();
        String winName = gameStatus.getTeamName(win);
        if(!gameStatus.isSuddenDeath(w)) {
            gameStatus.updateRanking();
        }
        bc.end(winName);
        for (Entity e : w.getEntities()) {
            if (e instanceof Villager
                    || e instanceof ArmorStand
                    || e instanceof ItemFrame
                    || e instanceof Item) {
                e.remove();
            }
            if(e instanceof Snowman s){
                s.setHealth(0);
            }
        }
        for(Player p : w.getPlayers()) {
            if (playerStats.isGaming(p) || playerStats.isSpector(p)) {
                if (playerStats.isGaming(p)) {
                    p.closeInventory();
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
                    p.getInventory().clear();
                    if(gameStatus.getGameMode(w) == 2 || gameStatus.isSuddenDeath(w)){
                        playerStats.setSelectedLoadOut(p,new int[]{});
                    }
                }
                int team = playerStats.getTeam(p);
                int teamRank;
                if(gameStatus.isSuddenDeath(w)){
                    teamRank = gameStatus.getSuddenDeathRanking(team);
                }else {
                    teamRank = gameStatus.getRanking(team);
                }
                String title = switch (teamRank) {
                    case 0 -> "！胜利！";
                    case 1 -> "第二名";
                    case 2 -> "第三名";
                    default -> "！比赛结束！";
                };
                p.sendTitle(ChatColor.GREEN + title,
                        ChatColor.AQUA + "比赛结束！5秒后返回大厅", 10, 50, 10);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            }
        }
        gameStatus.setPlayerSpawnPoint(new Location[]{w.getSpawnLocation()});
        gameStatus.stopGaming(w);
        gameStatus.setWorldEvent(-1);
        gameStatus.setMap(-1);
        playerTeam.clear();
        playerStats.clearKD();
        gameStatus.clearNums();
        gameStatus.stopSuddenDeath(w);
        isRich.clear();
        isCashingOut.clear();
        jumpPointLocation.clear();
        jumpPointVector.clear();
        playerDamage.clear();
        gameStatus.setGameTime(0);
        GadgetsListener.bounced.clear();
        BukkitRunnable teleport = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : w.getPlayers()){
                    if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                        playerStats.stopSpectating(p);
                        playerStats.stopGaming(p);
                        playerStats.cancelReady(p);
                        p.setGameMode(GameMode.SURVIVAL);
                        p.teleport(w.getSpawnLocation());
                        p.removePotionEffect(PotionEffectType.JUMP);
                        p.removePotionEffect(PotionEffectType.SLOW_FALLING);
                        p.removePotionEffect(PotionEffectType.GLOWING);
                    }
                    gameStatus.clearGameBoard(p);
                }
                gameStatus.resetGameBoard();
            }
        };
        teleport.runTaskLater(plugin,100L);
        BukkitRunnable timer = timerTask.getOrDefault(w,null);
        if(timer != null)timer.cancel();
    }
    public void summonStation(World w){
        Location[]stationLoc = gameStatus.getCashOutStationSpawnPoint();
        int num = gameStatus.getStationCount();
        int mode = gameStatus.getGameMode(w);
        while(num >= stationLoc.length){
            num -= (stationLoc.length);
        }
        int age = switch (gameStatus.getMap()){
            case -1 -> 60;
            case 3 -> 120;
            default -> 90;
        };
        if(stationLoc.length > 0){
            switch (mode){
                case 1 -> k.createBankStation(stationLoc[num],age);
                default -> k.createCashOutStation(stationLoc[num]);
            }
        }else {
            Bukkit.broadcastMessage(ChatColor.RED + "找不到提现站位置");
        }
    }
    public void summonVault(World w,int map) {
        int VSD = switch (map) {//Vault Station Distance
            case 0,2 -> 36;
            case 1 -> 48;
            case 3 -> 64;
            default -> 16;
        };
        Location[] vaultLoc = gameStatus.getVaultSpawnPoint();
        if (vaultLoc.length > 0) {//金库位置正常记录
            for(Location l : vaultLoc){
                boolean canSpawn = true;
                for (Entity e : w.getNearbyEntities(l,VSD,VSD,VSD)) {
                    if (e instanceof Villager v) {
                        if(v.getName().contains("站")) {
                            if (k.distance(l, e.getLocation()) <= VSD) {
                                canSpawn = false;
                            }
                        }
                    }
                    if (e instanceof ArmorStand a) {
                        if(a.getName().contains("金库")) {
                            if (k.distance(l, e.getLocation()) <= 10) {
                                canSpawn = false;
                            }
                        }
                    }
                }
                if(canSpawn) {
                    k.createVault(l);
                    gameStatus.shuffleLocation(vaultLoc);
                    gameStatus.setVaultSpawnPoint(vaultLoc);
                    break;
                }
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "找不到金库位置");
        }
    }
    public void teleportPlayer(int teamNum){
        Location[]spawnPoints = gameStatus.getPlayerSpawnPoint();
        gameStatus.shuffleLocation(spawnPoints);
        for(int i = 0;i < teamNum;i++){
            Player[]team = gameStatus.getTeamByID(i);
            for (Player p : team) {
                if (p == null) continue;
                p.setGameMode(GameMode.SURVIVAL);
                p.setCustomNameVisible(false);
                playerStats.setPlayerSpawnPoint(p, spawnPoints[i]);
                gameStatus.setPlayerBoardTeam(p, i);
                p.teleport(spawnPoints[i]);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 23, 0));
                setPlayerLoadOut(p);
                String teamName = gameStatus.getTeamName(i);
                p.sendTitle(" ", ChatColor.GREEN + "你的队伍：" + teamName, 10, 40, 10);
            }
        }
    }
    public void setPlayerLoadOut(Player p){
        p.getInventory().clear();
        World w = p.getWorld();
        int[]loadOut;
        if(gameStatus.getGameMode(w) == 2 && playerStats.isGaming(p)){
            loadOut = k.mixLoadOut();
            playerStats.setSelectedLoadOut(p,loadOut);
        }else {
            loadOut = playerStats.getSelectedLoadOut(p);
        }
        if(loadOut.length > 0){
            int body = loadOut[0];
            int skill = loadOut[1];
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30 + body * 15);
            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.13 - 0.015 * body);
            p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(body * 0.25);
            p.setHealth(p.getMaxHealth());
            Inventory inv = p.getInventory();
            switch (loadOut[2]) {
                case 3 -> {
                    inv.addItem(items.stick());
                    p.getInventory().setItemInOffHand(items.shield());
                }
                case 4 -> {
                    inv.addItem(items.dualBlade());
                    p.getInventory().setItemInOffHand(items.dualBlade());
                }
                default -> inv.setItem(0,items.getWeapon(loadOut[2]));
            }
            inv.addItem(items.getGadget(loadOut[3]));
            inv.addItem(items.getGadget(loadOut[4]));
            inv.addItem(items.getGadget(loadOut[5]));
            inv.setItem(7,items.indicator());
            inv.setItem(8,items.getSkill(skill));
            inv.setItem(9,new ItemStack(Material.ARROW,1));
            int team = playerStats.getTeam(p);
            EntityEquipment equipment = p.getEquipment();
            ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
            ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
            LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
            Color c = switch (team) {
                case 0 -> Color.AQUA;
                case 1 -> Color.ORANGE;
                case 2 -> Color.FUCHSIA;
                default -> Color.WHITE;
            };
            if(skill != 8 && playerStats.isGaming(p)){
                ItemStack glass = switch (team){
                    case 0 -> new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS,16);
                    case 1 -> new ItemStack(Material.ORANGE_STAINED_GLASS,16);
                    case 2 -> new ItemStack(Material.MAGENTA_STAINED_GLASS,16);
                    default -> new ItemStack(Material.WHITE_STAINED_GLASS,16);
                };
                inv.addItem(glass);
            }
            ItemStack head  = switch (body){
                case 0 -> new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS);
                case 1 -> new ItemStack(Material.YELLOW_STAINED_GLASS);
                case 2 -> new ItemStack(Material.RED_STAINED_GLASS);
                default -> new ItemStack(Material.OBSERVER);
            };
            chestMeta.setColor(c);
            legMeta.setColor(c);
            bootsMeta.setColor(c);
            chest.setItemMeta(chestMeta);
            leg.setItemMeta(legMeta);
            boots.setItemMeta(bootsMeta);
            equipment.setHelmet(head);
            equipment.setChestplate(chest);
            equipment.setLeggings(leg);
            equipment.setBoots(boots);
        }
    }
    public void summonJar(World w){
        int map = gameStatus.getMap();
        int mode = gameStatus.getGameMode(w);
        double chance = switch (map){
            case -1 -> 1;
            case 3 -> 0.4;
            case 4 -> 0.3;
            default -> 0.5;
        };
        Location[]jarPoints = gameStatus.getJarSpawnPoint();
        if(jarPoints.length > 0) {
            for (Location l : jarPoints) {
                if(random.nextDouble() < chance) {
                    k.createJar(l, random.nextInt(6), false);
                }
            }
        }else {
            Bukkit.broadcastMessage(ChatColor.RED + "找不到罐子位置");
        }
    }
    public void setJumpPoint(World w){
        Location[]jumpS = gameStatus.getJumpStartPoint();
        Location[]jumpE = gameStatus.getJumpEndPoint();
        if(jumpS.length != jumpE.length || jumpS.length == 0)return;
        for(int i = 0;i < jumpS.length;i++){
            Location from = jumpS[i].clone();
            Location to = jumpE[i].clone();
            Vector fromV = from.toVector();
            Vector toV = to.toVector();
            Vector jump = toV.subtract(fromV);
            ArmorStand a = (ArmorStand) w.spawnEntity(from, EntityType.ARMOR_STAND);
            ArmorStand a1 = (ArmorStand) w.spawnEntity(to, EntityType.ARMOR_STAND);
            a.setCustomName(ChatColor.AQUA + "滑索A" + (i + 1));
            a1.setCustomName(ChatColor.AQUA + "滑索B" + (i + 1));
            a.setCustomNameVisible(true);
            a1.setCustomNameVisible(true);
            a.setInvulnerable(true);
            a1.setInvulnerable(true);
            a.setGravity(false);
            a1.setGravity(false);
            a.setInvisible(true);
            a1.setInvisible(true);
            jumpPointVector.put(a,jump);
            jumpPointVector.put(a1,jump.clone().multiply(-1));
            jumpPointLocation.put(from,to);
            jumpPointLocation.put(to,from);
            for(int j = 0;j < 2;j ++){
                Block b = w.getBlockAt(a.getLocation().add(0,j,0));
                Block b1 = w.getBlockAt(a1.getLocation().add(0,j,0));
                if(b.getType() == Material.AIR || b.getType() == Material.LIGHT ||k.isBreakable(null,b)){
                    b.setType(Material.BIRCH_FENCE);
                }
                if(b1.getType() == Material.AIR || b1.getType() == Material.LIGHT ||k.isBreakable(null,b1)){
                    b1.setType(Material.BIRCH_FENCE);
                }
            }
            BukkitRunnable particle = new BukkitRunnable() {
                @Override
                public void run() {
                    if(a.isDead()){
                        for(int j = 0;j < 2;j ++){
                            Block b = w.getBlockAt(a.getLocation().add(0,j,0));
                            Block b1 = w.getBlockAt(a1.getLocation().add(0,j,0));
                            if(b.getType() == Material.BIRCH_FENCE){
                                b.setType(Material.AIR);
                            }
                            if(b1.getType() == Material.BIRCH_FENCE){
                                b1.setType(Material.AIR);
                            }
                        }
                        this.cancel();
                        return;
                    }
                    Location pLoc = a.getEyeLocation();
                    for (int i = 0; i < jump.length(); i++) {
                        w.spawnParticle(Particle.END_ROD, pLoc, 0);
                        pLoc.add(jump.clone().normalize());
                    }
                }
            };
            particle.runTaskTimer(plugin,0L,100L);
        }
    }
    public void startTimer(World w){
        int mode = gameStatus.getGameMode(w);
        int map = gameStatus.getMap();
        int[]events;
        switch (map){
            case 2,3,4 -> events = new int[]{1,3,4};
            default -> events = new int[]{0, 1, 2, 3, 4};
        }
        int max = switch (map){
            case -1 -> 600;
            case 3 -> 1200;
            default -> 900;
        };
        gameStatus.setGameTime(max);
        BukkitRunnable startTimer = new BukkitRunnable() {
            @Override
            public void run() {
                int count = gameStatus.getGameTime();
                Scoreboard board = gameStatus.getGameBoard();
                Objective o = board.getObjective("cash");
                Team t = board.getTeam("gameTime");
                if(t == null)t = board.registerNewTeam("gameTime");
                t.addEntry(ChatColor.GREEN + "游戏时间：");
                if(count <= 0 || !gameStatus.isGaming(w) || gameStatus.isSuddenDeath(w)){
                    t.setSuffix(ChatColor.AQUA + "0：00");
                    if(o != null) {
                        o.getScore(ChatColor.GREEN + "游戏时间：").setScore(-1);
                    }
                    this.cancel();
                    if(!gameStatus.isSuddenDeath(w)){
                        Bukkit.getPluginManager().callEvent(new SuddenDeathEvent(w,random.nextInt(5)));
                    }
                    return;
                }
                if(count == (max / 2) - 30){
                    int event =events[random.nextInt(events.length)];
                    Bukkit.getPluginManager().callEvent(new GameShowEvent(w,event,true));
                }
                if(mode == 2){
                    if(count % 120 == 0 && count > 0){
                        for(Player p : w.getPlayers()){
                            if(playerStats.isGaming(p)){
                                randomizeLoadOut(p);
                            }
                        }
                    }
                }
                if(count == 60){
                    for(Player p : w.getPlayers()){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)){
                            p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "还剩1分钟",
                                    " ",10,40,10);
                            p.playSound(p.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
                        }
                    }
                }
                int min = count / 60;
                int sec = count % 60;
                String time;
                if(sec < 10){
                    time = min + "：0" + sec;
                }else {
                    time = min + "：" + sec;
                }
                t.setSuffix(ChatColor.AQUA + time);
                if(o != null) {
                    o.getScore(ChatColor.GREEN + "游戏时间：").setScore(-1);
                }
                count -= 1;
                gameStatus.setGameTime(count);
            }
        };
        startTimer.runTaskTimer(plugin,200L,20L);
        timerTask.put(w,startTimer);
    }
    public void bankStationTimer(World w,int time){
        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                if(!gameStatus.isGaming(w)){
                    this.cancel();
                    return;
                }
                bankStationBar(w,time);
            }
        };
        timer.runTaskTimer(plugin,0L,time * 20L);
    }
    public void bankStationBar(World w,int time) {
        List<Player> players = w.getPlayers();
        BossBar cashOutProgress = Bukkit.createBossBar(ChatColor.GREEN + "存钱站刷新进度", BarColor.GREEN, BarStyle.SOLID);
        for (Player p : players) {
            if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                cashOutProgress.addPlayer(p);
            }
        }
        BukkitRunnable bar = new BukkitRunnable() {
            double progress = 0;
            final double step = 1.0 / (time * 20);
            int count = 0;

            @Override
            public void run() {
                if (progress >= 1 || !gameStatus.isGaming(w)) {
                    cashOutProgress.setProgress(1);
                    cashOutProgress.removeAll();
                    this.cancel();
                    return;
                }
                cashOutProgress.setProgress(progress);
                progress += step;
                count += 1;
            }
        };
        bar.runTaskTimer(plugin, 0L, 1L);
    }
    public void randomizeLoadOut(Player p){
        World w = p.getWorld();
        BukkitRunnable countDown = new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                if(!gameStatus.isGaming(w) || gameStatus.isSuddenDeath(w)){
                    this.cancel();
                    return;
                }
                if(count <= 0){
                    setPlayerLoadOut(p);
                    p.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_NETHERITE,1,1);
                    p.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_NETHERITE,1,1);
                    this.cancel();
                }
                if(count == 10){
                    List<Player>players = w.getPlayers();
                    for(Player p : players){
                        if(playerStats.isGaming(p)) {
                            p.sendTitle(ChatColor.RED +""+ ChatColor.BOLD + "WE ARE CNS",
                                    ChatColor.RED + "即将随机替换选手配装", 10, 40, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        }
                    }
                }
                if(count <= 5 && count > 0){
                    List<Player>players = w.getPlayers();
                    for(Player p : players){
                        if(playerStats.isGaming(p)) {
                            p.sendTitle(" ", ChatColor.RED + "" + count, 0, 20, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.6f);
                        }
                    }
                }
                count -= 1;
            }
        };
        countDown.runTaskTimer(plugin,0L,20L);
    }
    @EventHandler
    public void suddenDeathEvent(SuddenDeathEvent event){
        World w = event.getWorld();
        int rule = event.getRule();
        int map = gameStatus.getMap();
        gameStatus.setSuddenDeath(w);
        Location[] spawnLocs = getSpawnLocs(map, w);
        gameStatus.shuffleLocation(spawnLocs);
        Location bLoc = switch (map){
            case 0 -> new Location(w,-131,-13,-108);
            case 1 -> new Location(w,68,34,1152);
            case 3 -> new Location(w,-781,38,821);
            default -> null;
        };
        if(bLoc != null){
            w.getBlockAt(bLoc).setType(Material.REDSTONE_BLOCK);
            BukkitRunnable later = new BukkitRunnable() {
                @Override
                public void run() {
                    w.getBlockAt(bLoc).setType(Material.AIR);
                }
            };
            later.runTaskLater(plugin,20L);
        }
        suddenDeathTimer(w,rule,spawnLocs);
    }
    public Location[] getSpawnLocs(int map, World w) {
        Location[]spawnLocs;
        switch (map){
            case 0 -> spawnLocs = new Location[]{
                    new Location(w,-129,1,-65),
                    new Location(w,-131,1,-139),
                    new Location(w,-101,1,-123),
                    new Location(w,-98,1,-62),
            };
            case 1 -> spawnLocs = new Location[]{
                    new Location(w,67,40,1147),
                    new Location(w,23,40,1156),
                    new Location(w,39,45,1172),
                    new Location(w,65,40,1172),
            };
            case 2 -> spawnLocs = new Location[]{
                    new Location(w,-1015,66,-290),
                    new Location(w,-985,66,-290),
                    new Location(w,-985,66,-327),
                    new Location(w,-1015,66,-327),

            };
            case 3 -> spawnLocs = new Location[]{
                    new Location(w,-784,45,812),
                    new Location(w,-738,45,812),
                    new Location(w,-738,45,862),
                    new Location(w,-784,45,862),
            };
            case 4 -> spawnLocs = new Location[]{
                    new Location(w,-74,118,1309),
                    new Location(w,-92,118,1327),
                    new Location(w,-87,118,1363),
                    new Location(w,-62,118,1336),
            };
            default -> spawnLocs = gameStatus.getPlayerSpawnPoint();
        }
        return spawnLocs;
    }
    public void suddenDeathTimer(World w,int rule,Location[]locs){
        BukkitRunnable countDown = new BukkitRunnable() {
            int count = 10;
            @Override
            public void run() {
                if(!gameStatus.isGaming(w)){
                    this.cancel();
                    return;
                }
                if(count <= 0){
                    switch (rule){
                        case 0 -> snowBallFight(w);
                        case 1 -> floorIsLava(w);
                        case 2 -> cashBoxFight(w);
                        case 3 -> dashFight(w);
                        case 4 -> swordFight(w);
                    }
                    for(Player p : w.getPlayers()){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)){
                            if(playerStats.isGaming(p)){
                                int team = playerStats.getTeam(p);
                                p.closeInventory();
                                p.setGameMode(GameMode.SURVIVAL);
                                p.teleport(locs[team]);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,23,0));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,86400,0));
                            }else {
                                p.teleport(locs[random.nextInt(locs.length)]);
                            }
                            p.playSound(p.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                        }
                    }
                    this.cancel();
                }
                if(count == 10){
                    bc.suddenDeath(rule);
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Villager
                                || e instanceof ArmorStand
                                || e instanceof ItemFrame
                                || e instanceof Item) {
                            e.remove();
                        }
                        if(e instanceof Snowman s){
                            s.setHealth(0);
                        }
                    }
                    for(Player p :w.getPlayers()){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                            if(playerStats.isGaming(p)) {
                                k.respawn(p,false,false,false);
                                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                                p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
                                playerStats.setSkill(p,-1);
                                p.getInventory().clear();
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 10));
                                w.strikeLightningEffect(p.getLocation());
                            }
                            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Sudden Death",
                                    ChatColor.RED + "！游戏时间结束！", 10, 40, 10);
                        }
                    }
                }
                if(count <= 3 && count > 0){
                    for(Player p : w.getPlayers()){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                            p.sendTitle(" ", ChatColor.RED + "" + count, 0, 20, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, 1, 1);
                        }
                    }
                }
                count -= 1;
            }
        };
        countDown.runTaskTimer(plugin,0L,20L);
    }
    public void snowBallFight(World w){
        for(Player p :w.getPlayers()) {
            if (playerStats.isGaming(p)) {
                p.getInventory().addItem(items.snowball());
            }
        }
    }
    public void floorIsLava(World w){
        for(Player p :w.getPlayers()) {
            if (playerStats.isGaming(p)) {
                p.getInventory().remove(Material.GLASS);
                p.getInventory().addItem(items.gravityGrenade());
                p.getInventory().addItem(new ItemStack(Material.GLASS,16));
            }
        }
        BukkitRunnable check = new BukkitRunnable() {
            @Override
            public void run() {
                if(!gameStatus.isGaming(w)){
                    this.cancel();
                    return;
                }
                floorLava(w);
            }
        };
        check.runTaskTimer(plugin,200L,300L);
    }
    public void floorLava(World w){
        BukkitRunnable check = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                int minY = 256;
                List<Player>allPlayer = w.getPlayers();
                Player lowPlayer = null;
                String name = ChatColor.RED + "非洲酋长";
                for(Player p : allPlayer){
                    if(p.getGameMode() == GameMode.SPECTATOR)continue;
                    if(!playerStats.isGaming(p))continue;
                    int playerY = p.getLocation().getBlockY();
                    if(playerY < minY){
                        minY = playerY;
                        lowPlayer = p;
                        name = p.getName();
                    }
                }
                for (Player p : allPlayer){
                    if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                        p.sendTitle(" ", ChatColor.GOLD + "即将燃烧：" + name, 0, 20, 10);
                    }
                }
                if(count % 2 == 0){
                    int step = count / 2;
                    for(Player p : w.getPlayers()){
                        if(playerStats.isGaming(p) || playerStats.isSpector(p)) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    TextComponent.fromLegacyText(ChatColor.GOLD + lavaProgress(10, step)));
                            p.playSound(p.getLocation(), Sound.BLOCK_LAVA_POP, 1, 0.5f + 0.1f * step);
                            p.playSound(p.getLocation(), Sound.BLOCK_LAVA_POP, 1, 0.5f + 0.1f * step);
                        }
                    }
                }
                if(count > 20){
                    if(lowPlayer != null) {
                        w.playSound(lowPlayer.getLocation(),Sound.ENTITY_PLAYER_HURT_ON_FIRE,10,1);
                        w.playSound(lowPlayer.getLocation(),Sound.ITEM_BUCKET_EMPTY_LAVA,10,1);
                        w.spawnParticle(Particle.LAVA,lowPlayer.getLocation(),20,0,2,0);
                        eliminated(lowPlayer);
                    }else {
                        for (Player p : allPlayer){
                            p.sendTitle(" ",ChatColor.GOLD + "岩浆烫死了某个非洲酋长",0,20,10);
                        }
                    }
                    this.cancel();
                }
                count += 1;
            }
        };
        check.runTaskTimer(plugin,0L,5L);
    }
    public String lavaProgress(int total,int step){
        StringBuilder progress = new StringBuilder();
        progress.append("岩浆活跃度：");
        for(int i = 0;i < total;i ++){
            if(i < step){
                progress.append("|");
            }else {
                progress.append(".");
            }
        }
        return progress.toString();
    }
    public void cashBoxFight(World w){
        BukkitRunnable summon = new BukkitRunnable() {
            @Override
            public void run() {
                for(Player p : w.getPlayers()){
                    if(playerStats.isGaming(p)){
                        p.setHealth(5);
                        Entity box = k.createCashBox(p.getEyeLocation(),false);
                        k.carryItem(p,box);
                        p.sendTitle(" ",ChatColor.GREEN + "钱箱已生成！",10,20,10);
                        p.playSound(p.getLocation(),Sound.ENTITY_ARMOR_STAND_BREAK,1,1);
                    }
                }
            }
        };
        summon.runTaskLater(plugin,20L);
    }
    public void dashFight(World w){
        for(Player p :w.getPlayers()) {
            if (playerStats.isGaming(p)) {
                w.playSound(p.getLocation(),Sound.ENTITY_VINDICATOR_HURT,2,1);
                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                k.heal(p,40);
                BukkitRunnable rushing = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location eyeLoc = p.getEyeLocation();
                        Vector eyeVec = eyeLoc.getDirection();
                        Vector front = new Vector(eyeVec.getX(),0,eyeVec.getZ());
                        Location breakLoc = eyeLoc.add(front);
                        if(p.getGameMode().equals(GameMode.SPECTATOR)){
                            this.cancel();
                            return;
                        }
                        if(!gameStatus.isGaming(w)){
                            this.cancel();
                            return;
                        }
                        Collection<Entity>entities = w.getNearbyEntities(breakLoc,2,2,2);
                        for(Entity e : entities){
                            if(e.getName().contains("传送门")  || e.getName().contains("滑索"))continue;
                            k.knockBack(e,breakLoc,2);
                            if(e instanceof LivingEntity l){
                                if(l == p)continue;
                                l.damage(10,p);
                                w.playSound(l.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                            }
                        }
                        if(p.isSprinting()){
                            p.setVelocity(front.multiply(0.5));
                        }else {
                            p.setVelocity(front.multiply(1));
                        }
                    }
                };
                rushing.runTaskTimer(plugin,10L,5L);
            }
        }
    }
    public void swordFight(World w){
        for(Player p :w.getPlayers()) {
            if (playerStats.isGaming(p)) {
                p.getInventory().addItem(items.masterSword());
            }
        }
    }
}
