package Events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SuddenDeathEvent extends Event implements Cancellable {
    private boolean cancel;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    World world;
    int rule;
    public SuddenDeathEvent(World w,int r){
        world = w;
        this.rule = r;
    }

    public World getWorld() {
        return world;
    }

    public int getRule() {
        return rule;
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
    public static HandlerList getHandlerList() {return HANDLERS_LIST;}
}
