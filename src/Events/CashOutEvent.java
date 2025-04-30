package Events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CashOutEvent extends Event implements Cancellable {
    private boolean cancel;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    World world;
    int money,team;

    public CashOutEvent(World w,int t,int m){
        world = w;
        team = t;
        money = m;
    }

    public World getWorld() {
        return world;
    }
    public int getTeam() {
        return team;
    }
    public int getMoney() {
        return money;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }
    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public static HandlerList getHandlerList(){
        return HANDLERS_LIST;
    }
}
