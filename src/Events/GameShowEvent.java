package Events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameShowEvent extends Event implements Cancellable {
    private boolean cancel;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    World world;
    int gameShowID;
    boolean isWorldEvent;

    public GameShowEvent(World w,int id,boolean bigEvent){
        world = w;
        gameShowID = id;
        isWorldEvent = bigEvent;
    }

    public int getGameShowID() {
        return gameShowID;
    }
    public World getWorld() {
        return world;
    }
    public boolean isWorldEvent() {
        return isWorldEvent;
    }
    public void setGameShowID(int gameShowID) {
        this.gameShowID = gameShowID;
    }
    public void setWorld(World world) {
        this.world = world;
    }
    public void setIsWorldEvent(boolean worldEvent) {
        isWorldEvent = worldEvent;
    }
    public boolean isCancelled() {
        return cancel;
    }
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public static HandlerList getHandlerList() {return HANDLERS_LIST;}
}
