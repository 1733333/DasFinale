package Listeners;

import Events.GameStartEvent;
import Universal.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class InventoryListener implements Listener {
    public enum InvStatus {
        NOT_MENU,
        CONTESTANT_MENU,
        LOAD_OUT_MENU,
        EDIT_LOAD_OUTS_MENU,
        CLASS_MENU,
        SKILL_MENU,
        WEAPON_MENU,
        GADGETS_MENU,
        TEAM_MENU,
        DEV_MENU,
        DEAD_MENU,
        MAP_MENU,
        GAMEMODE_MENU,
        ACHIEVEMENT_MENU,
    }
    public static HashMap<String,InvStatus>playerInvStatus = new HashMap<>();
    public static HashMap<String, Integer> playerSelectedSlot = new HashMap<>();
    HashMap<String, Integer> playerPage = new HashMap<>();
    HashMap<String, Integer> playerLoadOutSlot = new HashMap<>();
    HashMap<String, Integer> playerGadgetSlot = new HashMap<>();
    JavaPlugin plugin;
    PlayerStats playerStats = PlayerStats.INSTANCE;
    AchievementList achievementList = AchievementList.INSTANCE;
    GameStatus gameStatus = GameStatus.getInstance();
    Items items = Items.getInstance();
    Kits k = Kits.getInstance();
    Ui ui = Ui.getInstance();
    Random random = new Random();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerInvClick(InventoryClickEvent clickEvent) {
        Player p = (Player) clickEvent.getWhoClicked();
        String name = p.getName();
        InvStatus status = playerInvStatus.getOrDefault(name, InvStatus.NOT_MENU);
        ItemStack item = clickEvent.getCurrentItem();
        int slot = clickEvent.getRawSlot();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (status == InvStatus.CONTESTANT_MENU) {
            clickEvent.setCancelled(true);
            if (slot == 8) {
                editLoadOutMenu(p);
                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
            } else if (slot < 6) {
                int[][] loadOuts = playerStats.getPlayerLoadOuts(p);
                int[] selected = loadOuts[slot];
                playerStats.setSelectedLoadOut(p, selected);
                playerSelectedSlot.put(p.getName(),slot);
                p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                if (meta.hasLore()) {
                    String clickedName = meta.getDisplayName();
                    String[] lore = meta.getLore().toArray(new String[0]);
                    StringBuilder text = new StringBuilder();
                    for (int i = 0; i < lore.length; i++) {
                        text.append(lore[i]);
                        if (i < lore.length - 1) {
                            text.append("\n");
                        }
                    }
                    ComponentBuilder component = new ComponentBuilder().append("当前选择：").append(clickedName)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder(text.toString()).create())))
                            .append(ChatColor.RESET + ",打开聊天栏并把鼠标移到这里即可查看配装");
                    p.spigot().sendMessage(ChatMessageType.CHAT, component.create());
                } else {
                    p.sendMessage(ChatColor.RED + "Oops，看起来CNS黑掉了你的配装，请联系腐竹");
                }
            }
        }
        if (status == InvStatus.EDIT_LOAD_OUTS_MENU) {
            clickEvent.setCancelled(true);
            if (slot < 6) {
                changeLoadOutMenu(p, slot);
            }
            if (slot == 8) {
                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
                playerInvStatus.put(p.getName(), InvStatus.NOT_MENU);
                p.performCommand("contestant");
            }
        }
        if (status == InvStatus.LOAD_OUT_MENU) {
            clickEvent.setCancelled(true);
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            switch (slot) {
                case 0:
                    openClassMenu(p);
                    break;
                case 1:
                    openSkillMenu(p);
                    break;
                case 2:
                    openWeaponMenu(p);
                    break;
                case 3:
                case 4:
                case 5:
                    playerGadgetSlot.put(p.getName(),slot);
                    openGadgetMenu(p);
                    break;
                case 8:
                    playerInvStatus.put(p.getName(), InvStatus.NOT_MENU);
                    if(playerStats.isGaming(p)){
                        GameListeners gameListeners = new GameListeners();
                        gameListeners.openDeadMenu(p);
                    }else {
                        editLoadOutMenu(p);
                    }
                    break;
            }
        }
        if (status == InvStatus.CLASS_MENU) {
            clickEvent.setCancelled(true);
            if(slot < 9) {
                int loadOutSlot = playerLoadOutSlot.getOrDefault(p.getName(), -1);
                int[][] allLoadOut = playerStats.getPlayerLoadOuts(p);
                int[] loadOut = allLoadOut[loadOutSlot];
                int body = slot / 3;
                int loadOutBody = loadOut[0];
                if (loadOutBody != body) {
                    allLoadOut[loadOutSlot] = playerStats.getDefaultLoadOut()[body * 2];
                }
                p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                playerStats.setPlayerLoadOuts(p, allLoadOut);
                changeLoadOutMenu(p,loadOutSlot);
            }
        }
        if (status == InvStatus.SKILL_MENU) {
            clickEvent.setCancelled(true);
            if(slot < 9) {
                int loadOutSlot = playerLoadOutSlot.getOrDefault(p.getName(), -1);
                int[][] allLoadOut = playerStats.getPlayerLoadOuts(p);
                int[] loadOut = allLoadOut[loadOutSlot];
                String clickedName = meta.getDisplayName();
                int clickedID = items.getSkillID(clickedName);
                if (clickedID >= 0) {
                    loadOut[1] = clickedID;
                    allLoadOut[loadOutSlot] = loadOut;
                }
                playerStats.setPlayerLoadOuts(p, allLoadOut);
                playerStats.setSelectedLoadOut(p,loadOut);
                p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                changeLoadOutMenu(p,loadOutSlot);
            }
        }
        if (status == InvStatus.WEAPON_MENU) {
            clickEvent.setCancelled(true);
            if(slot < 9) {
                int loadOutSlot = playerLoadOutSlot.getOrDefault(p.getName(), -1);
                int[][] allLoadOut = playerStats.getPlayerLoadOuts(p);
                int[] loadOut = allLoadOut[loadOutSlot];
                String clickedName = meta.getDisplayName();
                int clickedID = items.getWeaponID(clickedName);
                if (clickedID >= 0) {
                    loadOut[2] = clickedID;
                    allLoadOut[loadOutSlot] = loadOut;
                }
                playerStats.setPlayerLoadOuts(p, allLoadOut);
                playerStats.setSelectedLoadOut(p,loadOut);
                p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
                changeLoadOutMenu(p,loadOutSlot);
            }
        }
        if (status == InvStatus.GADGETS_MENU) {
            clickEvent.setCancelled(true);
            int loadOutSlot = playerLoadOutSlot.getOrDefault(p.getName(), -1);
            int[][] allLoadOut = playerStats.getPlayerLoadOuts(p);
            int[] loadOut = allLoadOut[loadOutSlot];
            String clickedName = meta.getDisplayName();
            int clickedID = items.getGadgetID(clickedName);
            if (clickedID >= 0) {
                int gadgetSlot = playerGadgetSlot.getOrDefault(p.getName(),3);
                loadOut[gadgetSlot] = clickedID;
                allLoadOut[loadOutSlot] = loadOut;
            }
            playerStats.setPlayerLoadOuts(p, allLoadOut);
            playerStats.setSelectedLoadOut(p,loadOut);
            p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
            changeLoadOutMenu(p,loadOutSlot);
        }
        if (status == InvStatus.TEAM_MENU) {
            clickEvent.setCancelled(true);
            if (slot < 3) {
                switch (gameStatus.addPlayerToTeam(p,slot)){
                    case 0 ->{
                        p.playSound(p.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
                        p.performCommand("teams");
                    }
                    case 1 ->{
                        p.playSound(p.getEyeLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
                        p.sendMessage(ChatColor.RED + "加入队伍失败！加入了未知队伍");
                    }
                    case 2 ->{
                        p.playSound(p.getEyeLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
                        p.sendMessage(ChatColor.RED + "加入队伍失败！没有选择配装");
                        p.performCommand("contestant");
                    }
                    case 3 ->{
                        p.playSound(p.getEyeLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
                        p.sendMessage(ChatColor.RED + "加入队伍失败！已在该队伍中！");
                    }
                    case 4 ->{
                        p.playSound(p.getEyeLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
                        p.sendMessage(ChatColor.RED + "加入队伍失败！队伍已满！");
                    }
                }
            }
            if(slot == 5){
                if(playerStats.isReady(p)){
                    playerStats.cancelReady(p);
                }else playerStats.ready(p);
                p.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
                p.performCommand("teams");
            }
            if(slot == 6) {
                if (playerStats.isSpector(p)) {
                    p.playSound(p.getEyeLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
                    p.sendMessage(ChatColor.AQUA + "你已经在观战了");
                } else {
                    gameStatus.removePlayerFromTeam(p);
                    playerStats.setSpector(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 1, 1);
                }
            }
            if (slot == 7) {
                if (gameStatus.removePlayerFromTeam(p)) {
                    p.playSound(p.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1, 1);
                    p.performCommand("teams");
                }
            }
            if (slot == 8) {
                p.playSound(p.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                p.performCommand("teams");
            }
        }
        if (status == InvStatus.DEV_MENU) {
            clickEvent.setCancelled(true);
            if (slot < 54) {
                if (slot == 52) {
                    weaponsChangePage(p, true);
                } else if (slot == 53) {
                    weaponsChangePage(p, false);
                } else {
                    Inventory inv = p.getInventory();
                    ItemStack[] content = inv.getContents();
                    int amount = 0;
                    for (ItemStack itemStack : content) {
                        if (itemStack == null) continue;
                        amount += 1;
                    }
                    if (amount < 36) {
                        p.playSound(p.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                        inv.addItem(item);
                    }
                }
            }
        }
        if(status == InvStatus.ACHIEVEMENT_MENU){
            clickEvent.setCancelled(true);
            if (slot < 54) {
                if (slot == 52) {
                    achievementChangePage(p, true);
                } else if (slot == 53) {
                    achievementChangePage(p, false);
                }
            }
        }
        if(status == InvStatus.DEAD_MENU) {
            clickEvent.setCancelled(true);
            if(slot == 3){
                p.closeInventory();
            }
            if(slot == 5){
                if(gameStatus.getGameMode(p.getWorld()) != 2) {
                    int sSlot = playerSelectedSlot.getOrDefault(p.getName(), 0);
                    changeLoadOutMenu(p, sSlot);
                }
            }
        }
        if(status == InvStatus.MAP_MENU){
            clickEvent.setCancelled(true);
            startGame(p,slot - 1);
        }
        if(status == InvStatus.GAMEMODE_MENU){
            clickEvent.setCancelled(true);
            World w = p.getWorld();
            gameStatus.setGameMode(w,slot);
            Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + "更改了游戏模式！");
            p.closeInventory();
        }
    }
    public void weaponsChangePage(Player p,boolean pageUp){
        int currentPage = playerPage.getOrDefault(p.getName(),0);
        int maxPage = items.getItems().length / 52;
        ItemStack[]weapons = items.getItems();
        if(pageUp){
            if(currentPage == 0){
                p.playSound(p.getEyeLocation(),Sound.ENCHANT_THORNS_HIT,1,1);
            }else {
                p.playSound(p.getEyeLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,1);
                Inventory inv = Bukkit.createInventory(p,54, ChatColor.RED + ""+ org.bukkit.ChatColor.BOLD +"物品列表");
                int positiveBound = currentPage * 52;
                int negativeBound = (currentPage - 1) * 52;
                for(int i = negativeBound; i < positiveBound;i ++){
                    if(i >= weapons.length)break;
                    inv.addItem(weapons[i]);
                }
                inv.setItem(52,ui.pageUp());
                inv.setItem(53,ui.pageDown());
                p.openInventory(inv);
                currentPage -= 1;
                playerPage.put(p.getName(),currentPage);
            }
        }else {
            if(currentPage + 1 > maxPage){
                p.playSound(p.getEyeLocation(),Sound.ENCHANT_THORNS_HIT,1,1);
            }else {
                currentPage += 1;
                p.playSound(p.getEyeLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,1);
                Inventory inv = Bukkit.createInventory(p,54, ChatColor.RED + ""+ org.bukkit.ChatColor.BOLD +"物品列表");
                int positiveBound = (currentPage + 1) * 52;
                int negativeBound = currentPage * 52;
                for(int i = negativeBound; i < positiveBound;i ++){
                    if(i >= weapons.length)break;
                    inv.addItem(weapons[i]);
                }
                inv.setItem(52,ui.pageUp());
                inv.setItem(53,ui.pageDown());
                p.openInventory(inv);
                playerPage.put(p.getName(),currentPage);
            }
        }
        playerInvStatus.put(p.getName(), InvStatus.DEV_MENU);
    }
    public void achievementChangePage(Player p,boolean pageUp){
        int currentPage = playerPage.getOrDefault(p.getName(),0);
        int maxPage = achievementList.achievements.length / 52;
        ItemStack[]weapons = achievementList.achievements;
        if(pageUp){
            if(currentPage == 0){
                p.playSound(p.getEyeLocation(),Sound.ENCHANT_THORNS_HIT,1,1);
            }else {
                p.playSound(p.getEyeLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,1);
                Inventory inv = Bukkit.createInventory(p,54, ChatColor.GOLD + ""+ org.bukkit.ChatColor.BOLD +"物品列表");
                int positiveBound = currentPage * 52;
                int negativeBound = (currentPage - 1) * 52;
                for(int i = negativeBound; i < positiveBound;i ++) {
                    if (i >= weapons.length) break;
                    ItemStack item = weapons[i].clone();
                    ItemMeta meta = item.getItemMeta();
                    String name = meta.getDisplayName();
                    String grant;
                    if (playerStats.hasAchievement(p, i)) {
                        item.setType(Material.EMERALD_BLOCK);
                        grant = net.md_5.bungee.api.ChatColor.GREEN + "【已获得】";
                    } else {
                        grant = net.md_5.bungee.api.ChatColor.RED + "【未获得】";
                    }
                    String finalName = ChatColor.RESET + "" + (i + 1) + "：" + name + grant;
                    meta.setDisplayName(finalName);
                    item.setItemMeta(meta);
                    inv.addItem(item);
                }
                inv.setItem(52,ui.pageUp());
                inv.setItem(53,ui.pageDown());
                p.openInventory(inv);
                currentPage -= 1;
                playerPage.put(p.getName(),currentPage);
            }
        }else {
            if(currentPage + 1 > maxPage){
                p.playSound(p.getEyeLocation(),Sound.ENCHANT_THORNS_HIT,1,1);
            }else {
                currentPage += 1;
                p.playSound(p.getEyeLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,1);
                Inventory inv = Bukkit.createInventory(p,54, ChatColor.GOLD + ""+ org.bukkit.ChatColor.BOLD +"成就列表");
                int positiveBound = (currentPage + 1) * 52;
                int negativeBound = currentPage * 52;
                for(int i = negativeBound; i < positiveBound;i ++){
                    if(i >= weapons.length)break;
                    ItemStack item = weapons[i].clone();
                    ItemMeta meta = item.getItemMeta();
                    String name = meta.getDisplayName();
                    String grant;
                    if(playerStats.hasAchievement(p,i)){
                        item.setType(Material.EMERALD_BLOCK);
                        grant = net.md_5.bungee.api.ChatColor.GREEN + "【已获得】";
                    }else {
                        grant = net.md_5.bungee.api.ChatColor.RED + "【未获得】";
                    }
                    String finalName = ChatColor.RESET + "" + (i + 1) + "：" + name + grant;
                    meta.setDisplayName(finalName);
                    item.setItemMeta(meta);
                    inv.addItem(item);
                }
                inv.setItem(52,ui.pageUp());
                inv.setItem(53,ui.pageDown());
                p.openInventory(inv);
                playerPage.put(p.getName(),currentPage);
            }
        }
        playerInvStatus.put(p.getName(), InvStatus.ACHIEVEMENT_MENU);
    }
    @EventHandler
    public void playerInvClose(InventoryCloseEvent closeEvent) {
        Player p = (Player) closeEvent.getPlayer();
        String name = p.getName();
        playerInvStatus.put(name, InvStatus.NOT_MENU);
        playerPage.remove(name);
    }

    public void editLoadOutMenu(Player p){
        Inventory inv = Bukkit.createInventory(p,9,ChatColor.BLUE+""+ ChatColor.BOLD + "调整配装(点击图标即可打开配置菜单)");
        int[][]loadOuts = playerStats.getPlayerLoadOuts(p);
        for(int i = 0;i < loadOuts.length;i ++){
            int classID = loadOuts[i][0];
            ItemStack pClass = items.getClass(classID);
            ItemStack skill = items.getSkill(loadOuts[i][1]);
            ItemStack weapon = items.getWeapon(loadOuts[i][2]);
            ItemStack icon = new ItemStack(pClass.getType());
            ItemMeta meta = icon.getItemMeta();
            ItemMeta classMeta = pClass.getItemMeta();
            ItemMeta skillMeta = skill.getItemMeta();
            ItemMeta weaponMeta = weapon.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "选手配置" + (i + 1));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "体型：" + classMeta.getDisplayName());
            lore.add(ChatColor.WHITE + "技能：" + skillMeta.getDisplayName());
            lore.add(ChatColor.WHITE + "武器：" + weaponMeta.getDisplayName());
            for(int j = 3;j < loadOuts[i].length;j ++){
                ItemStack gadget = items.getGadget(loadOuts[i][j]);
                ItemMeta gadgetMeta = gadget.getItemMeta();
                lore.add(ChatColor.WHITE + "道具" + (j - 2) + "：" + gadgetMeta.getDisplayName());
            }
            icon.setItemMeta(meta);
            k.addLore(icon,lore.toArray(new String[]{}));
            inv.setItem(i,icon);
        }
        inv.setItem(8,ui.close());
        p.openInventory(inv);
        playerInvStatus.put(p.getName(),InvStatus.EDIT_LOAD_OUTS_MENU);
    }
    public void changeLoadOutMenu(Player p, int slot){
        Inventory inv = Bukkit.createInventory(p,9, ChatColor.LIGHT_PURPLE +""+ ChatColor.BOLD+ "更改配置(点击图标改变配置)");
        int[][]loadOuts = playerStats.getPlayerLoadOuts(p);
        if(slot >= loadOuts.length){
            p.sendMessage(ChatColor.RED + "Oops，看起来CNS黑掉了你的界面，请联系腐竹");
            return;
        }
        int[]clickedLoadOut = loadOuts[slot];
        for(int i = 0;i < clickedLoadOut.length;i++){
            ItemStack item = switch (i) {
                case 0 -> items.getClass(clickedLoadOut[i]);
                case 1 -> items.getSkill(clickedLoadOut[i]);
                case 2 -> items.getWeapon(clickedLoadOut[i]);
                default -> items.getGadget(clickedLoadOut[i]);
            };
            inv.setItem(i,item);
        }
        inv.setItem(8,ui.close());
        p.openInventory(inv);
        playerInvStatus.put(p.getName(),InvStatus.LOAD_OUT_MENU);
        playerLoadOutSlot.put(p.getName(),slot);
    }
    public void openClassMenu(Player p){
        Inventory inv = Bukkit.createInventory(p,9, ChatColor.LIGHT_PURPLE +""+ ChatColor.BOLD+ "选择体型(点击图标选择体型)");
        inv.setItem(0,items.light());
        inv.setItem(3,items.mid());
        inv.setItem(6,items.heavy());
        inv.setItem(8,ui.close());
        p.openInventory(inv);
        playerInvStatus.put(p.getName(),InvStatus.CLASS_MENU);
    }
    public void openSkillMenu(Player p){
        int slot = playerLoadOutSlot.getOrDefault(p.getName(),-1);
        Inventory inv;
        if(slot < 0){
            inv = Bukkit.createInventory(p,9,ChatColor.RED + "Oops，看起来CNS黑掉了你的菜单，请联系腐竹");
            p.openInventory(inv);
            return;
        }
        int[][]loadOuts = playerStats.getPlayerLoadOuts(p);
        int[]loadOut = loadOuts[slot];
        inv = Bukkit.createInventory(p,9, ChatColor.LIGHT_PURPLE +""+ ChatColor.BOLD+ "选择技能(点击图标选择技能)");
        int body = loadOut[0];
        ItemStack[]skills = items.getSkillsByClass(body);
        ItemStack filter = items.getSkill(loadOut[1]);
        for(ItemStack i : skills){
            if(i == filter)continue;
            inv.addItem(i);
        }
        inv.setItem(8,ui.close());
        p.openInventory(inv);
        playerInvStatus.put(p.getName(),InvStatus.SKILL_MENU);
    }
    public void openWeaponMenu(Player p){
        int slot = playerLoadOutSlot.getOrDefault(p.getName(),-1);
        Inventory inv;
        if(slot < 0){
            inv = Bukkit.createInventory(p,9,ChatColor.RED + "Oops，看起来CNS黑掉了你的菜单，请联系腐竹");
            p.openInventory(inv);
            return;
        }
        int[][]loadOuts = playerStats.getPlayerLoadOuts(p);
        int[]loadOut = loadOuts[slot];
        inv = Bukkit.createInventory(p,9, ChatColor.LIGHT_PURPLE+""+ ChatColor.BOLD + "选择武器(点击图标选择武器)");
        int body = loadOut[0];
        ItemStack[]weapons = items.getWeaponsByClass(body);
        ItemStack filter = items.getWeapon(loadOut[2]);
        for(ItemStack i : weapons){
            if(i == filter)continue;
            inv.addItem(i);
        }
        inv.setItem(8,ui.close());
        p.openInventory(inv);
        playerInvStatus.put(p.getName(),InvStatus.WEAPON_MENU);
    }
    public void openGadgetMenu(Player p){
        int slot = playerLoadOutSlot.getOrDefault(p.getName(),-1);
        Inventory inv;
        if(slot < 0){
            inv = Bukkit.createInventory(p,9,ChatColor.RED + "Oops，看起来CNS黑掉了你的菜单，请联系腐竹");
            p.openInventory(inv);
            return;
        }
        int[][]loadOuts = playerStats.getPlayerLoadOuts(p);
        int[]loadOut = loadOuts[slot];
        inv = Bukkit.createInventory(p,27, ChatColor.LIGHT_PURPLE +""+ ChatColor.BOLD+ "选择道具(点击图标选择道具)");
        int body = loadOut[0];
        ItemStack[]gadgets = items.getGadgetsByClass(body);
        HashSet<ItemStack> filter = new HashSet<>();
        for(int i = 0;i < 3;i++) {
            ItemStack item = items.getGadget(loadOut[i + 3]);
            filter.add(item);
        }
        for(ItemStack i : gadgets){
            if(filter.contains(i))continue;
            inv.addItem(i);
        }
        inv.setItem(26,ui.close());
        p.openInventory(inv);
        playerInvStatus.put(p.getName(),InvStatus.GADGETS_MENU);
    }
    public void startGame(Player p,int map) {
        World w = p.getWorld();
        if (gameStatus.isGaming(w)) {
            Bukkit.broadcastMessage(ChatColor.RED + "已经有进行中的游戏了");
        } else {
            boolean ready = true;
            int count = 0;
            for (int i = 0; i < 3; i++) {
                Player[] players = gameStatus.getTeamByID(i);
                for (Player player : players) {
                    if (player == null) continue;
                    if (!playerStats.isReady(player)) {
                        ready = false;
                        Bukkit.broadcastMessage(ChatColor.RED + player.getName() + "还没有准备！");
                        player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,1);
                        player.sendTitle(ChatColor.GREEN + "你还没有准备"
                                ,ChatColor.GREEN + "请打开队伍菜单并点击准备按钮",10,60,10);
                    }
                    count += 1;
                }
            }
            if (ready && count > 0) {
                Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + "使用指令开始了游戏！");
                Bukkit.getPluginManager().callEvent(new GameStartEvent(w, map, gameStatus.getGameMode(w)));
            } else if(count == 0){
                Bukkit.broadcastMessage(ChatColor.RED + "游戏开始失败！队伍里没有人");
            }else {
                Bukkit.broadcastMessage(ChatColor.RED + "游戏开始失败！还有人没有准备好");
            }
        }
        p.closeInventory();
    }
}
