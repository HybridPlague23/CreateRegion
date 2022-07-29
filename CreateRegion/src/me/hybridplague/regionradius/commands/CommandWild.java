package me.hybridplague.regionradius.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.hybridplague.createregion.CreateRegion;

public class CommandWild {
  private CreateRegion plugin;
  
  public CommandWild(CreateRegion plugin) {
    this.plugin = plugin;
  }
  
  public void createWild(final Player p, final String price, final String name) {
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
    if (regions.getRegion(name) != null) {
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.plugin.prefix) + "&7That region already exists!"));
      return;
    } 
    p.performCommand("/expand vert");
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("rg define " + name);
        }
      }).runTaskLater((Plugin)this.plugin, 1L);
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("as add buy " + name);
        }
      }).runTaskLater((Plugin)this.plugin, 3L);
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("as setlandlord BusinessCraftGov " + name);
        }
      }).runTaskLater((Plugin)this.plugin, 5L);
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("as setprice " + price + " " + name);
        }
      }).runTaskLater((Plugin)this.plugin, 7L);
  }
}
