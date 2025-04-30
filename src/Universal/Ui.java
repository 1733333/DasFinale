package Universal;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Ui {
    private static Ui instance = new Ui();
    private Ui(){}
    public static Ui getInstance() {
        return instance;
    }

    public ItemStack pageUp(){
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "上一页");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "翻到上一页");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack pageDown(){
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "下一页");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "翻到下一页");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack close(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "返回上一级");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "关闭当前菜单");
        lore.add(ChatColor.WHITE + "并返回到上一级菜单");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack changeLoadOut(){
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "更改配装");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "进入更改配装界面");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack refresh(){
        ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "刷新队伍状态");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "由于队伍状态不能实时更新");
        lore.add(ChatColor.WHITE + "需要手动刷新状态");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack quit(){
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "退出队伍");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "退出当前队伍");
        lore.add(ChatColor.WHITE + "哎呀，不小心手滑加入了队伍但我还不想玩");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack respawn(){
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "等待复活");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "关闭此菜单，等待复活");
        lore.add(ChatColor.WHITE + "直接关闭菜单也可以");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack changeInGameLoadOut(){
        ItemStack item = new ItemStack(Material.CHEST_MINECART);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "更换配装");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "更换所使用的配装");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack canNotChange() {
        ItemStack dash = new ItemStack(Material.BARRIER);
        ItemMeta dashItemMeta = dash.getItemMeta();
        dashItemMeta.setDisplayName(ChatColor.RED + "" + ChatColor.MAGIC + "123456" + ChatColor.RESET
                + ChatColor.RED + "错误" + ChatColor.MAGIC + "123456");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "CNS黑掉了这个按钮");
        dashItemMeta.setLore(lore);
        dash.setItemMeta(dashItemMeta);
        return dash;
    }
    public ItemStack spector(){
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "观战");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "点击加入观战席");
        lore.add(ChatColor.WHITE + "我想要观战");
        lore.add(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "不要视姦我！");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack ready(){
        ItemStack item = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "正在准备");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "正在准备状态");
        lore.add(ChatColor.WHITE + "点击即可取消准备");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack notReady(){
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "未准备");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "还没有准备开始");
        lore.add(ChatColor.WHITE + "点击即可准备开始游戏");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack quickCash(){
        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "快速提现");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "将游戏模式设置为“快速提现”");
        lore.add(ChatColor.WHITE + "开启金库，抢夺钱箱，塞进提现站，等待提现完成");
        lore.add(ChatColor.WHITE + "最先获得20000现金的队伍取得胜利");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack bankIt(){
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "存钱至上");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "将游戏模式设置为“存钱至上”");
        lore.add(ChatColor.WHITE + "开启金库，淘汰对手，拿到金币，存进银行");
        lore.add(ChatColor.WHITE + "最先获得40000现金的队伍取得胜利");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack witchCraft(){
        ItemStack item = new ItemStack(Material.CARVED_PUMPKIN);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "xX_CNS_GLITCHCRAFT_Xx");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "将游戏模式设置为“xX_CNS_GLITCHCRAFT_Xx”");
        lore.add(ChatColor.WHITE + "随机体型，随机技能，随机武器，随机道具");
        lore.add(ChatColor.WHITE + "采用“快速提现”的规则");
        lore.add(ChatColor.WHITE + "最先获得20000现金的队伍取得胜利");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack poorStadium(){
        ItemStack item = new ItemStack(Material.LIGHT_BLUE_CONCRETE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "穷鬼竞技场");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "开始游戏");
        lore.add(ChatColor.WHITE + "地图：穷鬼竞技场");
        lore.add(ChatColor.YELLOW + "地图提供者：");
        lore.add(ChatColor.YELLOW + "C1_X1");
        lore.add(ChatColor.YELLOW + "woaibengkuiji");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack monaco(){
        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "摩纳哥");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "开始游戏");
        lore.add(ChatColor.WHITE + "地图：摩纳哥");
        lore.add(ChatColor.YELLOW + "地图提供者：");
        lore.add(ChatColor.YELLOW + "woaibengkuiji");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack sandStadium(){
        ItemStack item = new ItemStack(Material.SAND);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "荒漠竞技场");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "开始游戏");
        lore.add(ChatColor.WHITE + "地图：荒漠竞技场");
        lore.add(ChatColor.YELLOW + "地图提供者：");
        lore.add(ChatColor.YELLOW + "1nkVaie08");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack kyoto(){
        ItemStack item = new ItemStack(Material.PINK_WOOL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "桃花源");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "开始游戏");
        lore.add(ChatColor.WHITE + "地图：桃花源");
        lore.add(ChatColor.YELLOW + "地图提供者：");
        lore.add(ChatColor.YELLOW + "C1_X1");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack sys(){
        ItemStack item = new ItemStack(Material.CLAY);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "SYS＄HORIZON");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "开始游戏");
        lore.add(ChatColor.WHITE + "地图：SYS＄HORIZON");
        lore.add(ChatColor.YELLOW + "地图提供者：");
        lore.add(ChatColor.YELLOW + "woaibengkuiji");
        lore.add(ChatColor.YELLOW + "LET_R");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public ItemStack mansion(){
        ItemStack item = new ItemStack(Material.DARK_OAK_PLANKS);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "永夜府邸");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "开始游戏");
        lore.add(ChatColor.WHITE + "地图：永夜府邸");
        lore.add(ChatColor.YELLOW + "地图提供者：");
        lore.add(ChatColor.YELLOW + "woaibengkuiji");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

}
