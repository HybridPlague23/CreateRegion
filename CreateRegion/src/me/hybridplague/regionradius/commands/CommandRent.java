package me.hybridplague.regionradius.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.hybridplague.createregion.CreateRegion;

public class CommandRent {
  private CreateRegion plugin;
  
  public CommandRent(CreateRegion plugin) {
    this.plugin = plugin;
  }
  
  public void createRent(final Player p, final String price, final String parent, final OfflinePlayer landlord, final String name) {
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
    if (regions.getRegion(name) != null) {
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.plugin.prefix) + "&7That region already exists!"));
      return;
    } 
    if (regions.getRegion(parent) == null) {
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.plugin.prefix) + "&7Parent region not found!"));
      return;
    } 
    p.performCommand("rg define " + name);
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("rg parent " + name + " " + parent);
        }
      }).runTaskLater((Plugin)this.plugin, 2L);
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("as add rent " + name);
        }
      }).runTaskLater((Plugin)this.plugin, 4L);
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("as setlandlord " + landlord.getName() + " " + name);
        }
      }).runTaskLater((Plugin)this.plugin, 6L);
    (new BukkitRunnable() {
        public void run() {
          p.performCommand("as setprice " + price + " " + name);
        }
      }).runTaskLater((Plugin)this.plugin, 8L);
  }
}
