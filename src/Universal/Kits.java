package Universal;

import Events.PlayerKillPlayerEvent;
import Listeners.GadgetsListener;
import Listeners.GameListeners;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.xezard.glow.data.glow.Glow;

import java.util.*;

import static Listeners.GameListeners.itemCarriedByPlayerMap;
import static Listeners.GameListeners.playerCarryItemMap;

public class Kits {
    private static Kits instance = new Kits();
    private Kits(){}
    public static Kits getInstance() {
        return instance;
    }
    JavaPlugin plugin;
    Random random = new Random();
    Items items = Items.getInstance();
    PlayerStats playerStats = PlayerStats.INSTANCE;
    GameStatus gameStatus = GameStatus.getInstance();
    BroadCast bc = BroadCast.getInstance();
    HashMap<Entity,ItemStack>entityHelm = new HashMap<>();
    HashMap<String,ItemStack[]>playerEquipment = new HashMap<>();
    GameListeners gameListeners = new GameListeners();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String getLore(ItemStack item) {
        if(item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                String[] lore = meta.getLore().toArray(new String[0]);
                return lore[0];
            } else return "";
        }else return "";
    }

    public void shuffleItem(ItemStack[] items) {
        for (int i = items.length - 1; i > 0; i--) {
            int randomNum = random.nextInt(i + 1);
            ItemStack str = items[randomNum];
            items[randomNum] = items[i];
            items[i] = str;
        }
    }
    public void shuffleInt(int[] ints) {
        for (int i = ints.length - 1; i > 0; i--) {
            int randomNum = random.nextInt(i + 1);
            int str = ints[randomNum];
            ints[randomNum] = ints[i];
            ints[i] = str;
        }
    }
    public void createVault(Location location){
        World w = location.getWorld();
        ArmorStand vault = (ArmorStand) w.spawnEntity(location, EntityType.ARMOR_STAND);
        EntityEquipment equipment = vault.getEquipment();
        vault.setInvisible(true);
        vault.setCustomNameVisible(true);
        vault.setInvulnerable(true);
        vault.setGlowing(true);
        vault.setBasePlate(false);
        equipment.setHelmet(new ItemStack(Material.EMERALD_ORE));
        equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.IRON_BOOTS));
        gameStatus.setVaultNum(vault);
        int num = gameStatus.getVaultNum(vault);
        vault.setCustomName(ChatColor.GREEN + "金库" + num);
        Glow g = Glow.builder().color(ChatColor.YELLOW).name(vault.getName()).build();
        g.addHolders(vault);
        for(Player p : w.getPlayers()){
            g.display(p);
        }
        if(gameStatus.getGameMode(w) != 1) {
            stationParticle(vault, Color.YELLOW,100L);
        }
    }
    public Entity createCashBox(Location location,boolean pop){
        World w = location.getWorld();
        ArmorStand cashbox = (ArmorStand) w.spawnEntity(location, EntityType.ARMOR_STAND);
        EntityEquipment equipment = cashbox.getEquipment();
        cashbox.setInvisible(true);
        cashbox.setCustomName(ChatColor.GREEN + "钱箱");
        cashbox.setInvulnerable(true);
        cashbox.setSmall(true);
        cashbox.setCustomNameVisible(true);
        cashbox.setGlowing(true);
        cashbox.setBasePlate(false);
        equipment.setHelmet(new ItemStack(Material.EMERALD_BLOCK));
        equipment.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        Glow g = Glow.builder().color(ChatColor.GOLD).name(cashbox.getName()).build();
        g.addHolders(cashbox);
        for(Player p : w.getPlayers()){
            g.display(p);
        }
        if(pop){
            cashbox.setVelocity(new Vector(0,0.8,0));
            stationParticle(cashbox,Color.ORANGE,100L);
        }
        return cashbox;
    }
    public Entity createCashOutStation(Location location){
        World w = location.getWorld();
        Villager station = (Villager) w.spawnEntity(location,EntityType.VILLAGER);
        station.setCustomNameVisible(true);
        station.setSilent(true);
        station.setInvulnerable(true);
        station.setGlowing(true);
        station.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,86400,0));
        station.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        stationParticle(station,Color.AQUA,100L);
        gameStatus.setStationNum(station);
        int num = gameStatus.getStationNum(station);
        station.setCustomName(ChatColor.GREEN + "提现站" + num);
        Glow g = Glow.builder().color(ChatColor.AQUA).name(station.getName()).build();
        g.addHolders(station);
        for(Player p : w.getPlayers()){
            g.display(p);
        }
        Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(station));
        return station;
    }
    public Entity createBankStation(Location location,int age){
        World w = location.getWorld();
        Villager station = (Villager) w.spawnEntity(location,EntityType.VILLAGER);
        station.setCustomNameVisible(true);
        station.setSilent(true);
        station.setInvulnerable(true);
        station.setGlowing(true);
        station.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,86400,0));
        station.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        stationParticle(station,Color.LIME,50L);
        gameStatus.setStationNum(station);
        int num = gameStatus.getStationNum(station);
        station.setCustomName(ChatColor.GREEN + "存钱站" + num);
        Glow g = Glow.builder().color(ChatColor.GREEN).name(station.getName()).build();
        g.addHolders(station);
        for(Player p : w.getPlayers()){
            g.display(p);
        }
        BukkitRunnable remove = new BukkitRunnable() {
            @Override
            public void run() {
                if (station.isDead()) return;
                station.setHealth(0);
                w.playSound(station.getLocation(), Sound.ENTITY_WANDERING_TRADER_DEATH, 1, 1);
                w.playSound(station.getLocation(), Sound.ENTITY_WANDERING_TRADER_DEATH, 1, 1);
                w.spawnParticle(Particle.EXPLOSION_HUGE, station.getLocation(), 1);
                Firework firework = (Firework) w.spawnEntity(station.getEyeLocation().add(0,0.5,0),EntityType.FIREWORK);
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
            }
        };
        remove.runTaskLater(plugin,age * 20L);
        return station;
    }
    public Entity createJar(Location location,int type,boolean visibleName){
        World w = location.getWorld();
        ArmorStand jar = (ArmorStand) w.spawnEntity(location, EntityType.ARMOR_STAND);
        jar.setCustomNameVisible(visibleName);
        jar.setBasePlate(false);
        jar.setSmall(true);
        EntityEquipment equipment = jar.getEquipment();
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        Color c;
        switch (type) {
            case 0 -> {
                c = Color.RED;
                jar.setCustomName(ChatColor.RED + "爆炸罐");
                equipment.setHelmet(new ItemStack(Material.RED_CONCRETE));
            }
            case 1 -> {
                c = Color.LIME;
                jar.setCustomName(ChatColor.GREEN + "毒气罐");
                equipment.setHelmet(new ItemStack(Material.LIME_CONCRETE));
            }
            case 2 -> {
                c = Color.FUCHSIA;
                jar.setCustomName(ChatColor.LIGHT_PURPLE + "粘胶罐");
                equipment.setHelmet(new ItemStack(Material.PINK_CONCRETE));
            }
            case 3 -> {
                c = Color.ORANGE;
                jar.setCustomName(ChatColor.GOLD + "火焰罐");
                equipment.setHelmet(new ItemStack(Material.ORANGE_CONCRETE));
            }
            case 4 -> {
                c = Color.GRAY;
                jar.setCustomName(ChatColor.GRAY + "烟雾罐");
                equipment.setHelmet(new ItemStack(Material.LIGHT_GRAY_CONCRETE));
            }
            case 5 -> {
                c = Color.BLUE;
                jar.setCustomName(ChatColor.BLUE + "紊乱罐");
                equipment.setHelmet(new ItemStack(Material.BLUE_CONCRETE));
            }
            default -> c = Color.BLACK;
        }
        chestMeta.setColor(c);
        legMeta.setColor(c);
        bootsMeta.setColor(c);
        chest.setItemMeta(chestMeta);
        leg.setItemMeta(legMeta);
        boots.setItemMeta(bootsMeta);
        equipment.setChestplate(chest);
        equipment.setLeggings(leg);
        equipment.setBoots(boots);
        return jar;
    }
    public void playerThrow(Player p,Entity thrown,double power){
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        if(p.isSprinting()){
            thrown.setVelocity(shootVec.multiply(power * 1.5));
        }else {
            thrown.setVelocity(shootVec.multiply(power));
        }
        if(thrown.getCustomName() != null){
            if(thrown.getCustomName().contains("钱箱")){
                hitPlayer(p,thrown,Particle.COMPOSTER,8);
            }
        }
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Vector speed = thrown.getVelocity();
                thrown.setVelocity(speed.add(shootVec.multiply(0.5)));
            }
        };
        task.runTaskLater(plugin,2L);
    }
    public void hitPlayer(Player thrower,Entity thrown,Particle par,double damage){
        World w = thrown.getWorld();
        BukkitRunnable hit = new BukkitRunnable() {
            @Override
            public void run() {
                w.spawnParticle(par,thrown.getLocation(),0);
                if(thrown.isDead() || thrown.isOnGround()){
                    this.cancel();
                    return;
                }
                for(Entity e : thrown.getNearbyEntities(1,1,1)){
                    if(e.getName().contains("传送门"))continue;
                    if(e instanceof LivingEntity l){
                        if(e instanceof Player p){
                            if(gameStatus.isTeamMate(thrower,p))continue;
                            if(thrown.getName().contains("钱箱")){
                                if(p.getHealth() <= damage){
                                    Bukkit.getPluginManager().callEvent(new PlayerKillPlayerEvent(thrower,p,1));
                                }
                            }
                        }
                        l.damage(damage,thrower);
                        w.playSound(l.getLocation(),Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
                        thrown.setVelocity(new Vector(0,0,0));
                        this.cancel();
                        break;
                    }
                }
            }
        };
        hit.runTaskTimer(plugin,2L,2L);
    }
    public void addLore(ItemStack item,String[] itemLore) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if (!meta.hasLore()) {
            lore = Arrays.asList(itemLore);
        } else {
            lore = meta.getLore();
            lore.addAll(Arrays.asList(itemLore));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    public double distance(Location loc1,Location loc2){
        Location distanceLoc = loc1.clone().subtract(loc2.clone());
        return distanceLoc.length();
    }
    public double angle(Vector v1,Vector v2){
        Vector v1N = v1.clone().normalize();
        Vector v2N = v2.clone().normalize();
        return v1N.dot(v2N);
    }
    public void heal(LivingEntity entity,double value){
        double max = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double health = entity.getHealth();
        entity.setHealth(Math.min(health + value,max));
    }
    public boolean isBreakable(Player p,Block b) {
        World w = b.getWorld();
        Material m = b.getType();
        if(gameStatus.isSuddenDeath(w)){
            return false;
        }
        boolean isGaming;
        isGaming = gameStatus.isGaming(w);
        if(p != null){
            if(p.getGameMode() == GameMode.SPECTATOR){
                return false;
            }
            isGaming = playerStats.isGaming(p);
        }
        if(isGaming) {
            if(m.toString().contains("SIGN"))return false;
            return switch (m) {
                case STONE,
                     STRUCTURE_BLOCK,
                     BARRIER,
                     SMOOTH_SANDSTONE,
                     AIR,
                     COMMAND_BLOCK,
                     LIGHT,
                     REDSTONE_WIRE,
                     DIAMOND_BLOCK-> false;
                default -> true;
            };
        }else {
            if (m.toString().contains("CONCRETE"))
                return true;
            if (m.toString().contains("WOOL"))
                return true;
            return switch (m) {
                case GLASS, FIRE,LIGHT_BLUE_STAINED_GLASS -> true;
                default -> false;
            };
        }
    }
    public boolean breakSquareBlock(Player p, Location loc, int a, int b, int c, double chance) {
        if(random.nextInt(500) == 0){
            bc.destroyBC();
            playerStats.grantAchievement(p,18);
        }
        World w = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        boolean isBroken = false;
        for (int i = -a; i <= a; i++) {
            for (int j = -b; j <= b; j++) {
                for (int k = -c; k <= c; k++) {
                    Location newLoc = new Location(w, x + i, y + j, z + k);
                    Block block = w.getBlockAt(newLoc);
                    if (isBreakable(p,block)) {
                        w.playSound(newLoc, Sound.BLOCK_STONE_BREAK, 1, 1);
                        w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                        w.spawnParticle(Particle.EXPLOSION_LARGE, newLoc, 1);
                        if(random.nextDouble() < chance){
                            FallingBlock fBlock = w.spawnFallingBlock(newLoc,block.getBlockData());
                            fBlock.setVelocity(p.getEyeLocation().getDirection());
                            fBlock.setDropItem(false);
                        }
                        block.setType(Material.AIR);
                        if (!isBroken) {
                            isBroken = true;
                        }
                    }
                }
            }
        }
        return isBroken;
    }
    public boolean breakBallBlock(Player p, Location loc, int radius, double chance){
        if(random.nextInt(500) == 0){
            bc.destroyBC();
            playerStats.grantAchievement(p,18);
        }
        World w = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        boolean isBroken = false;
        Vector spread = new Vector(random.nextDouble(1) - random.nextDouble(1),
                random.nextDouble(1) - random.nextDouble(1),
                random.nextDouble(1) - random.nextDouble(1));
        for(int a = -radius;a <= radius;a++){
            for(int b = -radius;b <= radius;b++){
                for(int c = -radius;c <=radius;c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w,x + a,y + b,z + c);
                        Block block = w.getBlockAt(newLoc);
                        if(isBreakable(p,block)) {
                            w.playSound(newLoc, Sound.BLOCK_STONE_BREAK, 1, 1);
                            w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                            if(random.nextDouble() < chance){
                                FallingBlock fBlock = w.spawnFallingBlock(newLoc,block.getBlockData());
                                Vector fly = new Vector(0,1,0).add(spread);
                                fBlock.setVelocity(fly.normalize());
                                fBlock.setDropItem(false);
                            }
                            block.setType(Material.AIR);
                            if (!isBroken) {
                                isBroken = true;
                            }
                        }
                    }
                }
            }
        }
        return isBroken;
    }
    public void breakSquareGlass(Location loc,int length){
        World w = loc.getWorld();
        if(!gameStatus.isGaming(w))return;
        if(gameStatus.isSuddenDeath(w)){
            return;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -length;a <= length;a++) {
            for (int b = -length; b <= length; b++) {
                for (int c = -length; c <= length; c++) {
                    Location newLoc = new Location(w, x + a, y + b, z + c);
                    Block block = w.getBlockAt(newLoc);
                    if (block.getType().name().contains("GLASS")) {
                        w.playSound(newLoc, Sound.BLOCK_GLASS_BREAK, 1, 1);
                        w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
    public void breakBallGlass(Location loc,int radius){
        World w = loc.getWorld();
        if(!gameStatus.isGaming(w))return;
        if(gameStatus.isSuddenDeath(w)){
            return;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -radius;a <= radius;a++) {
            for (int b = -radius; b <= radius; b++) {
                for (int c = -radius; c <= radius; c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w, x + a, y + b, z + c);
                        Block block = w.getBlockAt(newLoc);
                        if (block.getType().name().contains("GLASS")) {
                            w.playSound(newLoc, Sound.BLOCK_GLASS_BREAK, 1, 1);
                            w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
    public void breakSquareGoo(Location loc,int length){
        World w = loc.getWorld();
        if(!gameStatus.isGaming(w))return;
        if(gameStatus.isSuddenDeath(w)){
            return;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -length;a <= length;a++) {
            for (int b = -length; b <= length; b++) {
                for (int c = -length; c <= length; c++) {
                    Location newLoc = new Location(w, x + a, y + b, z + c);
                    Block block = w.getBlockAt(newLoc);
                    if (block.getType().name().contains("WOOL")) {
                        w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                        w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                        w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
    public void breakBallGoo(Location loc,int radius){
        World w = loc.getWorld();
        if(!gameStatus.isGaming(w))return;
        if(gameStatus.isSuddenDeath(w)){
            return;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -radius;a <= radius;a++) {
            for (int b = -radius; b <= radius; b++) {
                for (int c = -radius; c <= radius; c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w, x + a, y + b, z + c);
                        Block block = w.getBlockAt(newLoc);
                        if (block.getType().name().contains("WOOL")) {
                            w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                            w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                            w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                            w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
    public void breakSquareSnow(Location loc,int length){
        World w = loc.getWorld();
        if(!gameStatus.isGaming(w))return;
        if(gameStatus.isSuddenDeath(w)){
            return;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -length;a <= length;a++) {
            for (int b = -length; b <= length; b++) {
                for (int c = -length; c <= length; c++) {
                    Location newLoc = new Location(w, x + a, y + b, z + c);
                    Block block = w.getBlockAt(newLoc);
                    if (block.getType() == Material.POWDER_SNOW) {
                        w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                        w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                        w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
    public void breakBallSnow(Location loc,int radius){
        World w = loc.getWorld();
        if(!gameStatus.isGaming(w))return;
        if(gameStatus.isSuddenDeath(w)){
            return;
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -radius;a <= radius;a++) {
            for (int b = -radius; b <= radius; b++) {
                for (int c = -radius; c <= radius; c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w, x + a, y + b, z + c);
                        Block block = w.getBlockAt(newLoc);
                        if (block.getType() == Material.POWDER_SNOW) {
                            w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                            w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                            w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                            w.spawnParticle(Particle.BLOCK_CRACK, newLoc, 20, block.getBlockData());
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
    public void knockBack(Entity who,Location from,double power){
        try {
            if(who instanceof Player p){
                if(p.getGameMode() == GameMode.SPECTATOR)return;
            }
            Location entityLoc = who.getLocation();
            Location subLoc = entityLoc.subtract(from);
            Vector knockBackVec = subLoc.toVector().normalize().clone();
            who.setVelocity(knockBackVec.multiply(power));
        }catch (Exception ignored){}
    }
    public boolean hitBallBlock(Entity e){
        World w = e.getWorld();
        Location loc = e.getLocation();
        for(int x =-1;x<=1;x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x * x + y * y + z * z <= 1) {
                        int blockX = loc.getBlockX();
                        int blockY = loc.getBlockY();
                        int blockZ = loc.getBlockZ();
                        Block nearbyBlock = w.getBlockAt(blockX + x, blockY + y, blockZ + z);
                        if (isFullBlock(nearbyBlock)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public boolean hitSquareBlock(Entity e){
        World w = e.getWorld();
        Location loc = e.getLocation();
        for(int x =-1;x<=1;x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    int blockX = loc.getBlockX();
                    int blockY = loc.getBlockY();
                    int blockZ = loc.getBlockZ();
                    Block nearbyBlock = w.getBlockAt(blockX + x, blockY + y, blockZ + z);
                    if (isFullBlock(nearbyBlock)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean isFullBlock(Block b){
        Material type = b.getType();
        String name = type.toString();
        if(type == Material.FIRE)return false;
        if(type == Material.AIR)return false;
        if(type == Material.LIGHT)return false;
        if(type == Material.IRON_BARS)return false;
        if(type == Material.POWDER_SNOW)return false;
        if(name.contains("BUTTON")) {
            return false;
        }
        if(name.contains("PANE")) {
            return false;
        }
        if(name.contains("TRAPDOOR")){
            return false;
        }
        if(name.contains("CARPET")){
            return false;
        }
        if(name.contains("SIGN")){
            return false;
        }
        if(name.contains("FENCE")){
            return false;
        }
        if(name.contains("WALL")){
            return false;
        }
        return true;
    }
    public boolean bounce(Entity e, double amp) {
        World w = e.getWorld();
        Location loc = e.getLocation();
        double step = 0.4;
        for (double x = -step; x <= step; x += step) {
            for (double y = -step; y <= step; y += step) {
                for (double z = -step; z <= step; z += step) {
                    if (x * x + y * y + z * z <= 1) {
                        Block nearbyBlock = w.getBlockAt(loc.clone().add(x, y, z));
                        if (isFullBlock(nearbyBlock)) {
                            Location blockLoc = nearbyBlock.getLocation();
                            Vector soulVec = loc.toVector();
                            Vector blockVec = blockLoc.toVector();
                            e.setVelocity((soulVec.subtract(blockVec)).clone().multiply(amp));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public void explode(Player p,Entity jar,double damage,double amp,int radius){
        if(jar.getName().contains("罐")|| !jar.isCustomNameVisible()) {
            if (!jar.getName().contains("炮塔")) {
                jar.remove();
            }
        }
        World w = p.getWorld();
        List<Entity>entities = jar.getNearbyEntities(radius,radius,radius);
        Location jarLoc = jar.getLocation();
        breakBallGlass(jarLoc,radius);
        breakBallSnow(jarLoc,radius);
        for(Entity e : entities){
            if(distance(jarLoc,e.getLocation()) > radius)continue;
            if(e instanceof ItemFrame i){
                i.remove();
            }
            if(e instanceof AreaEffectCloud a){
                if(a.getName().contains("烟雾")){
                    a.remove();
                    w.spawnParticle(Particle.CLOUD,a.getLocation(),100,0,0,0,0.2);
                }
            }
            if(e instanceof LivingEntity l){
                if(l instanceof Player p1) {
                    if (p1 != p) {
                        if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                        if (gameStatus.isTeamMate(p, p1)) {
                            continue;
                        }
                    }
                }
                int distance = (int) distance(jar.getLocation(),l.getLocation());
                if(distance >= 1){
                    damage -= distance * amp;
                }
                l.damage(damage,p);
                if(l instanceof Player p2){
                    if(p2.getHealth() <= damage){
                        if(GadgetsListener.bounced.contains(jar)){
                            playerStats.grantAchievement(p,11);
                        }
                    }
                }
            }
        }
    }
    public void gas(Player p,Entity jar,int duration,double radius){
        World w = p.getWorld();
        Location loc = jar.getLocation();
        jar.remove();
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(loc,EntityType.AREA_EFFECT_CLOUD);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.LIME,1);
        cloud.setDuration(duration * 20);
        cloud.setParticle(Particle.REDSTONE,dustOptions);
        cloud.setRadius((float) radius);
        cloud.setCustomName(p.getName() + "的毒气");
        BukkitRunnable gassing = new BukkitRunnable() {
            @Override
            public void run() {
                if(cloud.isDead()){
                    this.cancel();
                    return;
                }
                Collection<Entity> entities = w.getNearbyEntities(loc,radius,radius,radius);
                Location jarLoc = jar.getLocation();
                for(Entity e : entities){
                    if(distance(jarLoc,e.getLocation()) > radius)continue;
                    if(e.getFireTicks() > 0) {
                        w.playSound(cloud.getLocation(), Sound.ITEM_FIRECHARGE_USE, 2, 1);
                        w.playSound(cloud.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                        w.spawnParticle(Particle.EXPLOSION_HUGE, cloud.getLocation(), 1);
                        w.spawnParticle(Particle.FLAME,loc,100,0,0,0,0.2);
                        cloud.remove();
                        break;
                    }
                    if(e instanceof LivingEntity l){
                        if(l instanceof Player p1) {
                            if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                            if (p1 != p) {
                                if (gameStatus.isTeamMate(p, p1)) {
                                    continue;
                                }
                            }
                        }
                        if(l instanceof ArmorStand)continue;
                        double max = l.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                        Vector speed = l.getVelocity();
                        l.damage(max * 0.15,p);
                        l.setVelocity(speed);
                        w.playSound(l.getLocation(),Sound.ENTITY_PLAYER_HURT,1,1);
                    }
                }
                Particle.DustOptions dust = new Particle.DustOptions(Color.LIME,3);
                w.spawnParticle(Particle.REDSTONE,loc,150,radius/2,radius/2,radius/2,0.1,dust);
            }
        };
        gassing.runTaskTimer(plugin,0L,20L);
    }
    public void goo(Player p,Location loc,int radius){
        World w = p.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -radius;a <= radius;a++){
            for(int b = -radius;b <= radius;b++){
                for(int c = -radius;c <=radius;c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w, x + a, y + b, z + c);
                        Block block = w.getBlockAt(newLoc);
                        if (block.getType() == Material.AIR || block.getType() == Material.LIGHT) {
                            block.setType(Material.WHITE_WOOL);
                            w.spawnParticle(Particle.BLOCK_DUST, newLoc, 20, 1, 1, 1, block.getBlockData());
                            if(random.nextBoolean()) {
                                w.playSound(newLoc, Sound.BLOCK_SLIME_BLOCK_PLACE, 1, 1);
                            }
                        }
                    }
                }
            }
        }
    }
    public void fire(Player p,Entity jar,int duration,int radius){
        World w = p.getWorld();
        Location loc = jar.getLocation();
        jar.remove();
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(loc,EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(duration * 30);
        cloud.setParticle(Particle.FLAME);
        cloud.setRadius((float) radius);
        cloud.setCustomName(p.getName() + "的火");
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -radius;a <= radius;a++){
            for(int b = -radius;b <= radius;b++){
                for(int c = -radius;c <=radius;c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w, x + a, y + b, z + c);
                        Block block = w.getBlockAt(newLoc);
                        if (block.getType() == Material.AIR
                                || block.getType() == Material.LIGHT
                                || block.getType() == Material.POWDER_SNOW) {
                            block.setType(Material.FIRE);
                        }
                    }
                }
            }
        }
        BukkitRunnable fireDamage = new BukkitRunnable() {
            @Override
            public void run() {
                if(cloud.isDead()){
                    this.cancel();
                    return;
                }
                w.spawnParticle(Particle.FLAME,loc,200,radius/2.0,radius/2.0,radius/2.0,0);
                Collection<Entity>entities = w.getNearbyEntities(loc,radius,radius,radius);
                Location jarLoc = jar.getLocation();
                for(Entity e : entities){
                    if(distance(jarLoc,e.getLocation()) > radius)continue;
                    if(e instanceof AreaEffectCloud a){
                        if(a.getName().contains("毒气")){
                            a.remove();
                            w.playSound(a.getLocation(),Sound.ITEM_FIRECHARGE_USE,2,1);
                            w.spawnParticle(Particle.EXPLOSION_HUGE,a.getLocation(),1);
                            w.spawnParticle(Particle.FLAME,a.getLocation(),100,0,0,0,0.2);
                        }
                    }
                    if(e instanceof LivingEntity l){
                        if(e instanceof Player p1){
                            if(p1.getGameMode() == GameMode.SPECTATOR)continue;
                        }
                        double max = l.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                        l.damage(max * 0.1,p);
                        int fire = l.getFireTicks();
                        l.setFireTicks(fire + 50);
                        w.playSound(l.getLocation(),Sound.ENTITY_PLAYER_HURT_ON_FIRE,1,1);
                    }
                }
            }
        };
        fireDamage.runTaskTimer(plugin,0L,30L);
    }
    public void smoke(Entity jar,int duration,int radius){
        World w = jar.getWorld();
        Location loc = jar.getLocation();
        jar.remove();
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(loc,EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(duration * 20);
        cloud.setParticle(Particle.EXPLOSION_HUGE);
        cloud.setRadius(1);
        cloud.setCustomName("烟雾");
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for(int a = -radius;a <= radius;a++) {
            for (int b = -radius; b <= radius; b++) {
                for (int c = -radius; c <= radius; c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w, x + a, y + b, z + c);
                        Block block = w.getBlockAt(newLoc);
                        if (block.getType() == Material.FIRE) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
        BukkitRunnable smoking = new BukkitRunnable() {
            @Override
            public void run() {
                if(cloud.isDead()){
                    this.cancel();
                    return;
                }
                Collection<Entity> entities = w.getNearbyEntities(loc,radius,radius,radius);
                Location jarLoc = jar.getLocation();
                for(Entity e : entities){
                    if(distance(jarLoc,e.getLocation()) > radius)continue;
                    if(e instanceof AreaEffectCloud a){
                        if(a.getName().contains("火")){
                            a.remove();
                            w.playSound(a.getLocation(),Sound.BLOCK_FIRE_EXTINGUISH,2,1);
                        }
                    }
                    if(e instanceof LivingEntity l){
                        l.setFireTicks(0);
                    }
                }
            }
        };
        smoking.runTaskTimer(plugin,0L,10L);
    }
    public void glitch(Player p,Entity jar,int duration,int radius,int coolDownTick){
        World w = p.getWorld();
        Location loc = jar.getLocation();
        jar.remove();
        AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(loc,EntityType.AREA_EFFECT_CLOUD);
        cloud.setDuration(duration * 40);
        Particle.DustOptions dust = new Particle.DustOptions(Color.BLUE,0.7f);
        cloud.setParticle(Particle.REDSTONE,dust);
        cloud.setRadius((float) radius);
        cloud.setCustomName(p.getName() + "的紊乱云");
        BukkitRunnable glitching = new BukkitRunnable() {
            @Override
            public void run() {
                if(cloud.isDead()){
                    this.cancel();
                    return;
                }
                Collection<Entity> entities = w.getNearbyEntities(loc,radius,radius,radius);
                Location jarLoc = jar.getLocation();
                for(Entity e : entities){
                    if(distance(jarLoc,e.getLocation()) > radius)continue;
                    if(e instanceof Player player){
                        if (gameStatus.isTeamMate(p, player)) continue;
                        if(player.getGameMode() == GameMode.SPECTATOR)continue;
                        Inventory inv = player.getInventory();
                        for(ItemStack i : inv.getContents()){
                            if(i == null)continue;
                            ItemMeta meta = i.getItemMeta();
                            int weapon = items.getWeaponID(meta.getDisplayName());
                            if(weapon >= 0)continue;
                            int coolDown = player.getCooldown(i.getType());
                            player.setCooldown(i.getType(),coolDownTick + coolDown);
                        }
                        player.sendTitle(" ",ChatColor.AQUA + "！被紊乱！",0,20,10);
                        w.playSound(player.getLocation(),Sound.ENTITY_ARMOR_STAND_BREAK,1,1);
                    }
                    if(e instanceof LivingEntity l){
                        if(l.getName().contains("滑索"))continue;
                        if(l instanceof Snowman s){
                            s.setTarget(null);
                        }
                        Location loc = l.getLocation();
                        loc.setPitch(random.nextInt(180) - 90);
                        loc.setYaw(random.nextInt(180) - 90);
                        l.teleport(loc);
                    }
                }
                Particle.DustOptions dust = new Particle.DustOptions(Color.BLUE,1);
                w.spawnParticle(Particle.REDSTONE,loc,150,radius/2,radius/2,radius/2,0.1,dust);
            }
        };
        glitching.runTaskTimer(plugin,0L,40L);
    }
    public void snow(Player p,Location loc,int radius){
        World w = p.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        BlockData data = Bukkit.createBlockData(Material.SNOW);
        for(int a = -radius;a <= radius;a++){
            for(int b = -radius;b <= radius;b++){
                for(int c = -radius;c <=radius;c++) {
                    if (a * a + b * b + c * c <= radius * radius) {
                        Location newLoc = new Location(w, x + a, y + b, z + c);
                        Block block = w.getBlockAt(newLoc);
                        if (block.getType() == Material.AIR || block.getType() == Material.LIGHT) {
                            FallingBlock f = w.spawnFallingBlock(newLoc,
                                    Bukkit.createBlockData(Material.POWDER_SNOW));
                            f.setDropItem(false);
                            f.setVelocity(new Vector(0,b * 0.1,0));
                            w.spawnParticle(Particle.BLOCK_DUST, newLoc, 20, 1, 1, 1, data);
                            w.playSound(newLoc, Sound.BLOCK_WOOL_BREAK, 1, 1);
                        }
                    }
                }
            }
        }
    }
    public int compare(int division,double randDouble) {
        double step =1.0 / division;
        int count = 0;
        for (double d = 0; d <= 1; d += step) {
            if (randDouble < d) {
                return count;
            }
            count += 1;
        }
        return -1;
    }
    public void respawn(Player p,boolean byStatue,boolean defib,boolean teamWipe) {
        World w = p.getWorld();
        List<Location> stationLoc = new ArrayList<>();
        for (Villager v : w.getEntitiesByClass(Villager.class)) {
            if (GameListeners.isCashingOut.contains(v)) {
                stationLoc.add(v.getLocation());
            }
        }
        double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        if (byStatue) {
            ArmorStand statue = playerStats.getStatue(p);
            if (statue != null) {
                Location sLoc = statue.getLocation();
                p.teleport(sLoc);
                w.spawnParticle(Particle.EXPLOSION_LARGE, sLoc, 1);
            }
        } else {
            Location[] spawnPoints;
            if (stationLoc.isEmpty()) {
                spawnPoints = gameStatus.getPlayerSpawnPoint().clone();
            } else {
                List<Location> spawnPointsList = new ArrayList<>();
                Location[] oldPoints = gameStatus.getPlayerSpawnPoint().clone();
                for (Location l : oldPoints) {
                    for (Location l1 : stationLoc) {
                        if (distance(l, l1) <= 128) {
                            spawnPointsList.add(l);
                        }
                    }
                }
                spawnPoints = spawnPointsList.toArray(new Location[]{});
            }
            if (teamWipe || spawnPoints.length == 0) {
                if (spawnPoints.length == 0) {
                    Location spawnPoint = playerStats.getSpawnPoint(p);
                    p.teleport(spawnPoint);
                } else {
                    int team = playerStats.getTeam(p);
                    Player[]players = gameStatus.getTeamByID(team);
                    List<Player>count = new ArrayList<>();
                    for(Player player : players){
                        if(player == null)continue;
                        count.add(player);
                    }
                    if(count.size() > 1) {
                        p.teleport(spawnPoints[team]);
                    }else {
                        p.teleport(spawnPoints[random.nextInt(spawnPoints.length)]);
                    }
                }
            } else {
                gameStatus.shuffleLocation(spawnPoints);
                for (Location spawnPoint : spawnPoints) {
                    boolean canRespawn = true;
                    for (Entity e : w.getNearbyEntities(spawnPoint, 10, 10, 10)) {
                        if (e instanceof Player player) {
                            if (player.getGameMode() == GameMode.SPECTATOR) continue;
                            if (!gameStatus.isTeamMate(p, player)) {
                                canRespawn = false;
                            }
                        }
                    }
                    if (canRespawn) {
                        p.teleport(spawnPoint);
                        break;
                    }
                }
            }
        }
        if (defib) {
            p.setHealth(max * 0.5 + 1);
            p.damage(1);
        } else {
            p.setHealth(max);
        }
        BukkitRunnable reviveTask = GameListeners.playerRespawnTask.getOrDefault(p.getName(), null);
        if (reviveTask != null) reviveTask.cancel();
        if(gameStatus.getGameMode(w) != 2) {
            gameListeners.setPlayerLoadOut(p);
        }
        playerStats.removePlayerStatue(p);
        Inventory inv = p.getInventory();
        p.setGameMode(GameMode.SURVIVAL);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 23, 0));
        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        for (ItemStack i : inv.getContents()) {
            if (i == null) continue;
            p.setCooldown(i.getType(), 0);
        }
    }
    public void setEntityHelm(Entity e){
        if(e instanceof LivingEntity l){
            EntityEquipment equipment = l.getEquipment();
            if(equipment != null) {
                ItemStack head = entityHelm.getOrDefault(e,null);
                if (head != null) {
                    equipment.setHelmet(head);
                }
            }
        }
    }
    public void setC4Planted(Entity e){
        if(e instanceof LivingEntity l){
            EntityEquipment equipment = l.getEquipment();
            if(equipment != null) {
                ItemStack head = equipment.getHelmet();
                if (head != null) {
                    entityHelm.put(e,head);
                    equipment.setHelmet(new ItemStack(Material.TNT));
                }
            }
        }
    }
    public boolean isC4Planted(Entity e){
        if(e instanceof LivingEntity l){
            EntityEquipment equipment = l.getEquipment();
            if(equipment != null) {
                ItemStack head = equipment.getHelmet();
                if (head != null) {
                    return head.getType() == Material.TNT;
                }
            }
        }
        return false;
    }
    public void removeEquipment(Player p){
        EntityEquipment equipment = p.getEquipment();
        ItemStack[]armor = new ItemStack[]{
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
        };
        if(equipment != null) {
            armor[0] = equipment.getHelmet();
            armor[1] = equipment.getChestplate();
            armor[2] = equipment.getLeggings();
            armor[3] = equipment.getBoots();
            playerEquipment.put(p.getName(), armor);
            equipment.setArmorContents(new ItemStack[]{});
        }
    }
    public void setPlayerEquipment(Player p){
        ItemStack[] armor = playerEquipment.getOrDefault(p.getName(),null);
        if(armor != null){
            EntityEquipment equipment = p.getEquipment();
            if(armor.length > 0) {
                if (armor[0] != null) {
                    if(armor[0].getType() != Material.AIR) {
                        equipment.setHelmet(armor[0]);
                    }
                }
                if (armor[1] != null) {
                    if(armor[1].getType() != Material.AIR) {
                        equipment.setChestplate(armor[1]);
                    }
                }
                if (armor[2] != null) {
                    if(armor[2].getType() != Material.AIR) {
                        equipment.setLeggings(armor[2]);
                    }
                }
                if (armor[3] != null) {
                    if(armor[3].getType() != Material.AIR) {
                        equipment.setBoots(armor[3]);
                    }
                }
            }
        }
    }
    public void stationParticle(Entity e,Color c,long timer){
        World w = e.getWorld();
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if(e.isDead()){
                    this.cancel();
                    return;
                }
                Location eLoc = e.getLocation();
                Vector eVec = eLoc.toVector();
                for(Player p : w.getPlayers()){
                    if(playerStats.isGaming(p)){
                        Location pLoc = p.getLocation();
                        Vector pVec = pLoc.toVector();
                        Vector subVec = pVec.subtract(eVec);
                        Location subLoc = eLoc.clone().add(0,0.2,0);
                        Particle.DustOptions dust = new Particle.DustOptions(c,3);
                        for(int i = 0;i < subVec.length();i ++){
                            if(gameStatus.isCashing(e)){
                                dust = new Particle.DustOptions(Color.LIME,3);
                            }
                            p.spawnParticle(Particle.REDSTONE,subLoc,1,dust);
                            subLoc.add(subVec.clone().normalize());
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin,0L,timer);
    }
    public void carryItem(Player p,Entity carried){
        if(carried.getName().contains("雕像")){
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(ChatColor.YELLOW + "对着雕像按住Shift+鼠标右键即可拉起队友"));
            carried.setGlowing(false);
        }else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(ChatColor.GREEN + "左键扔出 或者 右键放在原地"));
        }
        carried.setGravity(false);
        BukkitRunnable carry = new BukkitRunnable() {
            @Override
            public void run() {
                if(playerCarryItemMap.getOrDefault(p,null) == null){
                    this.cancel();
                    carried.setGravity(true);
                }
                Player carrier = itemCarriedByPlayerMap.getOrDefault(carried,null);
                if(carrier != null){
                    if(carrier.getGameMode() == GameMode.SPECTATOR){
                        this.cancel();
                        carried.setGravity(true);
                    }
                    if(carrier != p || carried.isDead() || p.getGameMode() == GameMode.SPECTATOR){
                        this.cancel();
                        carried.setGravity(true);
                        playerCarryItemMap.remove(p);
                        itemCarriedByPlayerMap.remove(carried);
                        playerStats.stopCarrying(p);
                    }
                }
                Location eyeLoc = p.getEyeLocation();
                Vector eyeVec = eyeLoc.getDirection();
                Vector down = new Vector(0,1,0);
                Vector right = eyeVec.clone().crossProduct(down);
                Location floatingLoc = eyeLoc.clone().add(right.multiply(0.5)).add(eyeVec.multiply(1.5));
                if(carried instanceof Snowman s){
                    if(s.getName().contains("炮塔")){
                        carried.teleport(floatingLoc.setDirection(s.getEyeLocation().getDirection()));
                    }else{
                        carried.teleport(floatingLoc);
                        }
                }else {
                    carried.teleport(floatingLoc);
                }
            }
        };
        carry.runTaskTimer(plugin,2L,1L);
    }
    public int getCoin(Player p) {
        int count = 0;
        Inventory inv = p.getInventory();
        ItemStack[] content = inv.getContents();
        for (ItemStack i : content) {
            if (i == null) continue;
            if (i.getType() == Material.EMERALD) {
                count += i.getAmount();
            }
        }
        return count;
    }
    public void removeCoin(Player p){
        Inventory inv = p.getInventory();
        ItemStack[]content = inv.getContents();
        for(ItemStack i : content){
            if(i == null)continue;
            if(i.getType() == Material.EMERALD){
                i.setType(Material.AIR);
            }
        }
        inv.setContents(content);
    }
    public int[] mixLoadOut(){
        int[]loadOut = new int[6];
        int skillNum = items.skills.length;
        int weaponNum = items.weapons.length;
        int[] gadgets = new int[items.gadgets.length];
        for(int i = 0;i < items.gadgets.length;i ++){
            gadgets[i] = i;
        }
        shuffleInt(gadgets);
        loadOut[0] = random.nextInt(3);
        loadOut[1] = random.nextInt(skillNum);
        loadOut[2] = random.nextInt(weaponNum);
        System.arraycopy(gadgets, 0, loadOut, 3, 3);
        return loadOut;
    }
    public boolean rayTraceBlock(Location loc1,Location loc2){
        World w = loc1.getWorld();
        int distance =(int)distance(loc1,loc2);
        Vector v1 = loc1.toVector();
        Vector v2 = loc2.toVector();
        Vector ray = (v2.clone().subtract(v1.clone())).normalize();
        Location startLoc = loc1.clone();
        for(int i = 0;i < distance;i++){
            Block b = w.getBlockAt(startLoc);
            if(isFullBlock(b)){
                return true;
            }
            startLoc.add(ray);
        }
        return false;
    }
}
