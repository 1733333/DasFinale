package Universal;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public enum AchievementList {
    INSTANCE;
    public ItemStack[]achievements = new ItemStack[]{
            a0(),
            a1(),
            a2(),
            a3(),
            a4(),
            a5(),
            a6(),
            a7(),
            a8(),
            a9(),
            a10(),
            a11(),
            a12(),
            a13(),
            a14(),
            a15(),
            a16(),
            a17(),
            a18(),
            a19(),
            a20(),
            a21(),
            a22(),
            a23(),
            a24(),
            a25(),
            a26(),
    };
    ItemStack a0(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[首秀]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "完成第一场游戏");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a1(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[小试牛刀]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "第一次淘汰一名选手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a2(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[第一桶金]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "将钱箱塞进提现站");
        lore.add(ChatColor.WHITE + "或者往存钱站中存入金币");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a3(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[关键伤害]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "对一位选手第一次造成伤害就将其淘汰");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a4(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[初露锋芒]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "取得第一次胜利");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a5(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[实习医生]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "复活一名队友");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a6(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[小偷小摸]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "偷取一次提现");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a7(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[赶尽杀绝]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "造成一次团灭");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a8(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[两手空空]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "游戏结束时，队伍的现金为0");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a9(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[全场最佳]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "游戏结束时，成为KD最高的玩家");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a10(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[为慈善捐躯]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "使用钱箱淘汰一名对手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a11(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[蹦蹦炸弹]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "使用跳板弹起来的投掷物淘汰一名选手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a12(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[空袭]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "在空中淘汰一名选手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a13(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[打鸟]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "淘汰一名在空中的选手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a14(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[最大火力]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "游戏结束时，造成了最多的伤害");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a15(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[战争狂魔]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "单局累计造成300伤害");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a16(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[小手一滑]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "不小心淘汰自己");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a17(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[真正的ProGay]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "在未被淘汰的情况下赢得一局比赛");
        lore.add(ChatColor.WHITE + "像Alligator一样稳");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a18(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[拆迁大师]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "触发竞技场拆迁播报");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a19(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[压哨一搏]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "在比赛时间剩余不足10秒时开始提现");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a20(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[终场大盗]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "在比赛时间剩余不足10秒时偷取一次提现");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a21(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[中场投球]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "距离提现站20格以外投掷钱箱并开始提现");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a22(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[速通高手]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "开启金库后15秒内发起一次提现");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a23(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[提现卫士]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "淘汰一名正在偷取提现的选手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a24(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[打得火热]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "一局比赛中，在身上着火时累计淘汰3名选手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a25(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[土豪]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "在“存钱至上”模式中，一局内存入40000＄");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    ItemStack a26(){
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "[压力奖]");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "用罐子淘汰一名选手");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

}
