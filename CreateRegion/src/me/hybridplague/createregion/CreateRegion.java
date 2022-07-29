package me.hybridplague.createregion;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.hybridplague.regionradius.commands.CommandCity;
import me.hybridplague.regionradius.commands.CommandRent;
import me.hybridplague.regionradius.commands.CommandWild;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;


public class CreateRegion extends JavaPlugin {
	
	public boolean tooClose = false;
	
	
	  private CommandCity city;
	  
	  private CommandRent rent;
	  
	  private CommandWild wild;
	  
	  public String prefix = "&8&lCreateRegion &f";
	  
	  public void onEnable() {
	    this.wild = new CommandWild(this);
	    this.city = new CommandCity(this);
	    this.rent = new CommandRent(this);
	    getCommand("createregion").setTabCompleter(new RegionTabs());
	    getCommand("cr").setTabCompleter(new RegionTabs());
	  }
	  
	  @SuppressWarnings("unused")
	public int getArea(CommandSender sender) {
		  int result = 0;
		  Actor actor = BukkitAdapter.adapt(sender); 
		  LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);
		  try {
			Region region = session.getSelection(session.getSelectionWorld());
			for(BlockVector3 b : region) {
				result++;
			}
			return (result / region.getHeight());
		  } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  return 0;
	  }
	  
	  @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	    if (label.equalsIgnoreCase("createregion") || label.equalsIgnoreCase("cr")) {
	      if (!(sender instanceof Player)) {
	        sender.sendMessage("This command is only executable by a player.");
	        return true;
	      } 
	      Player p = (Player)sender;
	      if (!p.hasPermission("businesscraft.mod")) {
	        p.sendMessage(ChatColor.RED + "Insufficient permission.");
	        return true;
	      } 
	      if (args.length == 0) {
	        rHelp(p);
	        return true;
	      } 
	      if (args[0].equalsIgnoreCase("getarea")) {
	    	  sender.sendMessage(ChatColor.LIGHT_PURPLE + "Area is: " + getArea(sender));
	    	  return true;
	      }
	      if (args.length == 1) {
		    	if (args[0].equals("DENYWILDREGION")) {
		    		this.denyRegion(sender);
		    		return true;
		    	}
	        if (args[0].equalsIgnoreCase("Wild")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("City")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) City &c<price> <parent> <landlord> <name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Rent")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Rent &c<price> <parent> <landlord> <name>"));
	          return true;
	        } 
	        rHelp(p);
	        return true;
	      } 
	      if (args.length == 2) {
		    	if (args[0].equals("CONFIRMWILDREGION")) {
		    		if (args[1] != null) {
		    			this.confirmRegion(sender, args[1]);
		    			return true;
		    		}
		    		p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
		    		return true;
		    	}
	        if (args[0].equalsIgnoreCase("WildCR")) {
	            if (args[1] != null) {
	            	String price = String.valueOf(getArea(sender) * 7);
	                String name = args[1];
	                this.wild.createWild(p, price, name);
	                return true;
	              } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Wild")) {
	            if (args[1] != null) {
	            	
	            	weCheck(p);
	            	
	            	if (tooClose) {
	            		
	            		sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Does the player have permission to region here?");
	            		sender.sendMessage("");
	            		
	            		ComponentBuilder yes = new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
								+ net.md_5.bungee.api.ChatColor.GREEN + net.md_5.bungee.api.ChatColor.BOLD + "   YES   " 
								+ net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
            		yes.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr CONFIRMWILDREGION " + args[1]));
            		BaseComponent[] yesMsg = yes.create();
            		sender.spigot().sendMessage(yesMsg);
            		
            		sender.sendMessage("");
            		
            		ComponentBuilder no = new ComponentBuilder(net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
							+ net.md_5.bungee.api.ChatColor.DARK_RED + net.md_5.bungee.api.ChatColor.BOLD + "   NO   " 
							+ net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
            		no.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr DENYWILDREGION"));
            		BaseComponent[] noMsg = no.create();
            		sender.spigot().sendMessage(noMsg);
	            		return true;
	            	}
	            	this.confirmRegion(sender, args[1]);
	            	
	                return true;
	              } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("City")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) City <price> &c<parent> <landlord> <name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Rent")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Rent <price> &c<parent> <landlord> <name>"));
	          return true;
	        } 
	        rHelp(p);
	        return true;
	      } 
	      if (args.length == 3) {
	        if (args[0].equalsIgnoreCase("WildCR")) {
	          if (args[1] != null) {
	        	String price = String.valueOf(getArea(sender) * 7);
	            String name = args[1];
	            this.wild.createWild(p, price, name);
	            return true;
	          } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Wild")) {
	            if (args[1] != null) {
	            	
	            	weCheck(p);
	            	
	            	if (tooClose) {
	            		
	            		sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Does the player have permission to region here?");
	            		sender.sendMessage("");
	            		
	            		ComponentBuilder yes = new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
								+ net.md_5.bungee.api.ChatColor.GREEN + net.md_5.bungee.api.ChatColor.BOLD + "   YES   " 
								+ net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
            		yes.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr CONFIRMWILDREGION " + args[1]));
            		BaseComponent[] yesMsg = yes.create();
            		sender.spigot().sendMessage(yesMsg);
            		
            		sender.sendMessage("");
            		
            		ComponentBuilder no = new ComponentBuilder(net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
							+ net.md_5.bungee.api.ChatColor.DARK_RED + net.md_5.bungee.api.ChatColor.BOLD + "   NO   " 
							+ net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
            		no.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr DENYWILDREGION"));
            		BaseComponent[] noMsg = no.create();
            		sender.spigot().sendMessage(noMsg);
	            		return true;
	            	}
	            	this.confirmRegion(sender, args[1]);
	            	
	                return true;
	            }
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("City")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) City <price> <parent> &c<landlord> <name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Rent")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Rent <price> <parent> &c<landlord> <name>"));
	          return true;
	        } 
	        rHelp(p);
	        return true;
	      } 
	      if (args.length == 4) {
	        if (args[0].equalsIgnoreCase("WildCR")) {
	          if (args[1] != null) {
	            String price = String.valueOf(getArea(sender) * 7);
	            String name = args[1];
	            this.wild.createWild(p, price, name);
	            return true;
	          } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        }
	        if (args[0].equalsIgnoreCase("Wild")) {
	            if (args[1] != null) {
	            	
	            	weCheck(p);
	            	
	            	if (tooClose) {
	            		
	            		sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Does the player have permission to region here?");
	            		sender.sendMessage("");
	            		
	            		ComponentBuilder yes = new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
								+ net.md_5.bungee.api.ChatColor.GREEN + net.md_5.bungee.api.ChatColor.BOLD + "   YES   " 
								+ net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
            		yes.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr CONFIRMWILDREGION " + args[1]));
            		BaseComponent[] yesMsg = yes.create();
            		sender.spigot().sendMessage(yesMsg);
            		
            		sender.sendMessage("");
	            		
	            		
	            		ComponentBuilder no = new ComponentBuilder(net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
								+ net.md_5.bungee.api.ChatColor.DARK_RED + net.md_5.bungee.api.ChatColor.BOLD + "   NO   " 
								+ net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
	            		no.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr DENYWILDREGION"));
	            		BaseComponent[] noMsg = no.create();
	            		sender.spigot().sendMessage(noMsg);
	            		return true;
	            	}
	            	this.confirmRegion(sender, args[1]);
	            	
	                return true;
	              } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("City")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) City <price> <parent> <landlord> &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Rent")) {
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Rent <price> <parent> <landlord> &c<name>"));
	          return true;
	        } 
	        rHelp(p);
	        return true;
	      } 
	      if (args.length > 4) {
	        if (args[0].equalsIgnoreCase("WildCR")) {
	          if (args[1] != null) {
	        	String price = String.valueOf(getArea(sender) * 7);
	            String name = args[1];
	            this.wild.createWild(p, price, name);
	            return true;
	          } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Wild")) {
	            if (args[1] != null) {
	            	
	            	weCheck(p);
	            	
	            	if (tooClose) {
	            		
	            		sender.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Does the player have permission to region here?");
	            		sender.sendMessage("");
	            		
	            		ComponentBuilder yes = new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
								+ net.md_5.bungee.api.ChatColor.GREEN + net.md_5.bungee.api.ChatColor.BOLD + "   YES   " 
								+ net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
            		yes.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr CONFIRMWILDREGION"));
            		BaseComponent[] yesMsg = yes.create();
            		sender.spigot().sendMessage(yesMsg);
            		
            		sender.sendMessage("");
            		
            		
            		ComponentBuilder no = new ComponentBuilder(net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + ">>>" 
							+ net.md_5.bungee.api.ChatColor.DARK_RED + net.md_5.bungee.api.ChatColor.BOLD + "   NO   " 
							+ net.md_5.bungee.api.ChatColor.DARK_RED + "" + net.md_5.bungee.api.ChatColor.UNDERLINE + "" + net.md_5.bungee.api.ChatColor.BOLD + "<<<");
            		no.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr DENYWILDREGION"));
            		BaseComponent[] noMsg = no.create();
            		sender.spigot().sendMessage(noMsg);
	            		return true;
	            	}
	            	this.confirmRegion(sender, args[1]);
	            	
	                return true;
	              } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Wild &c<name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("City")) {
	          if (args[1] != null) {
	            if (args[2] != null) {
	              if (args[3] != null) {
	                if (args[4] != null) {
	                  if (isNum(args[1])) {
	                    if (Bukkit.getOfflinePlayer(args[3]).hasPlayedBefore()) {
	                      String price = args[1];
	                      String parent = args[2];
	                      OfflinePlayer landlord = Bukkit.getOfflinePlayer(args[3]);
	                      String name = args[4];
	                      this.city.createCity(p, price, parent, landlord, name);
	                      return true;
	                    } 
	                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Invalid player: &e/createregion (/cr) City <price> <parent> &c<landlord> &e<name>"));
	                    return true;
	                  } 
	                  p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Invalid integer: &e/createregion (/cr) City &c<price> &e<parent> <landlord> <name>"));
	                  return true;
	                } 
	                p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing argument: &e/createregion (/cr) City <price> <parent> <landlord> &c<name>"));
	                return true;
	              } 
	              p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) City <price> <parent> &c<landlord> <name>"));
	              return true;
	            } 
	            p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) City <price> &c<parent> <landlord> <name>"));
	            return true;
	          } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) City &c<price> <parent> <landlord> <name>"));
	          return true;
	        } 
	        if (args[0].equalsIgnoreCase("Rent")) {
	          if (args[1] != null) {
	            if (args[2] != null) {
	              if (args[3] != null) {
	                if (args[4] != null) {
	                  if (isNum(args[1])) {
	                    if (Bukkit.getOfflinePlayer(args[3]).hasPlayedBefore()) {
	                      String price = args[1];
	                      String parent = args[2];
	                      OfflinePlayer landlord = Bukkit.getOfflinePlayer(args[3]);
	                      String name = args[4];
	                      this.rent.createRent(p, price, parent, landlord, name);
	                      return true;
	                    } 
	                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Invalid player: &e/createregion (/cr) Rent <price> <parent> &c<landlord> &e<name>"));
	                    return true;
	                  } 
	                  p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Invalid integer: &e/createregion (/cr) Rent &c<price> &e<parent> <landlord> <name>"));
	                  return true;
	                } 
	                p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing argument: &e/createregion (/cr) Rent <price> <parent> <landlord> &c<name>"));
	                return true;
	              } 
	              p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Rent <price> <parent> &c<landlord> <name>"));
	              return true;
	            } 
	            p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Rent <price> &c<parent> <landlord> <name>"));
	            return true;
	          } 
	          p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing arguments: &e/createregion (/cr) Rent &c<price> <parent> <landlord> <name>"));
	          return true;
	        } 
	        rHelp(p);
	        return true;
	      } 
	    } 
	    return false;
	  }
	  
	  private void rHelp(Player p) {
	    p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(this.prefix) + "Missing argument: &e/createregion (/cr) &f..."));
	    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "    ... &eCity <price> <parent> <landlord> <name>"));
	    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "    ... &eRent <price> <parent> <landlord> <name>"));
	    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "    ... &eWild <name>"));
	  }
	  
	  public static Set<ProtectedRegion> checkForRegions(World world, double x, double y, double z) {
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager manager = container.get(BukkitAdapter.adapt(world));
			
			BlockVector3 bv = BlockVector3.at(x, y, z);
			
			ApplicableRegionSet ars = manager.getApplicableRegions(bv);
			
			if (ars.getRegions().isEmpty()) {
				return null;
			}
			
			return ars.getRegions();
			
		}
	  
	  public void check(Player p, Location loc) {
			int radius = 20;
			
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager manager = container.get(BukkitAdapter.adapt(loc.getWorld()));
			
			List<String> names = new ArrayList<String>();
			List<BlockVector3> namesLocation = new ArrayList<BlockVector3>();
			
			for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
				for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
					//Block block = loc.getWorld().getBlockAt(x, y, z);
					
					/*if (block.getType() == Material.AIR) {
						block.setType(Material.STONE);
					}*/
					
					int y = loc.getWorld().getHighestBlockYAt(x, z) + 1;
					
					if (checkForRegions(loc.getWorld(), x, y, z) != null) {
						BlockVector3 bv = BlockVector3.at(x, y, z);
						
						ApplicableRegionSet ars = manager.getApplicableRegions(bv);
						
						for (ProtectedRegion rg : ars) {
							String name = rg.getId();
							if (!names.contains(name)) {
								names.add(name);
								namesLocation.add(bv);
							}
						}
					}
				}
			}
			
			if (!names.isEmpty()) {
				tooClose = true;
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "Too close to the following regions: "));
				for (String n : names) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f• &e" + n));
				}
				return;
			}
			
		}
	  
	  public void confirmRegion(CommandSender sender, String name) {
		  String price = String.valueOf(getArea(sender) * 7);
		  
     	  ComponentBuilder message = new ComponentBuilder(">>>   Click here to create region   <<<");
     	  message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cr wildcr " + name));
     	  message.bold(true);
     	  message.underlined(true);
     	  message.color(net.md_5.bungee.api.ChatColor.GREEN);
     	  BaseComponent[] msg = message.create();
		  sender.sendMessage(ChatColor.GOLD + "====================================");
	  	  sender.sendMessage(ChatColor.GOLD + "         Price for the region is: " + ChatColor.BOLD + "" + ChatColor.RED + price);
	  	  sender.sendMessage(ChatColor.GOLD + "     if they approve of the price then");
		  sender.spigot().sendMessage(msg);
		  sender.sendMessage(ChatColor.GOLD + "====================================");
		  
	  }
	  
	  public void denyRegion(CommandSender sender) {
		  sender.sendMessage(ChatColor.RED + "Region creation cancelled.");
	  }
	  
	  public void weCheck(Player p) {
			
			tooClose = false;
			
			Actor a = BukkitAdapter.adapt(p);
			SessionManager sm = WorldEdit.getInstance().getSessionManager();
			LocalSession ls = sm.get(a);
			Region region;
			
			com.sk89q.worldedit.world.World selWorld = ls.getSelectionWorld();
			
			try {
				if (selWorld == null) throw new IncompleteRegionException();
				
				region = ls.getSelection(selWorld);
				
				for (BlockVector2 b : region.getBoundingBox().asFlatRegion()) {
					if (tooClose) {
						return;
					}
					
					Location loc = p.getLocation();
					loc.setX(b.getX());
					loc.setY(100D);
					loc.setZ(b.getZ());
					check(p, loc);
				}
				
			} catch (IncompleteRegionException ex) {
				a.printError(TextComponent.of("Please make a region selection first."));
			}
		}
	  
	  public boolean isNum(String num) {
	    try {
	      Integer.parseInt(num);
	    } catch (Exception e) {
	      return false;
	    } 
	    return true;
	  }
	}

