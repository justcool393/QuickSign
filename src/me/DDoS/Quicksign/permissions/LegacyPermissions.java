package me.DDoS.Quicksign.permissions;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class LegacyPermissions implements Permissions {

    private PermissionHandler handler;
    
    public LegacyPermissions(PermissionHandler handler) {
        
        this.handler = handler;
        
    }
    
    @Override
    public boolean hasPermission(Player player, String perm) {
        
        return handler.has(player, perm);
        
    } 
}