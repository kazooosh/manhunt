package de.kazoosh.manhunt;

import de.kazoosh.manhunt.TabComplete;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.omg.CORBA.Environment;

import java.util.*;


public final class Main extends JavaPlugin implements Listener {

    List<String> runnersOrder = new ArrayList<String>();
    List<String> huntersOrder = new ArrayList<String>();


    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("manhunt").setTabCompleter(new TabComplete());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("manhunt")) {
            // Check if Sender is a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("Der Befehl /manhunt muss von einem Spieler ausgeführt werden.");
                return true;
            } else {
                // GUI
                if (args.length == 0) {
                    Player p = (Player) sender;
                    Inventory gui = Bukkit.createInventory(p, 9, "Manhunt");
                    ItemStack hunter = new ItemStack(Material.DIAMOND_SWORD);
                    ItemStack runner = new ItemStack(Material.DIAMOND_PICKAXE);
                    ItemStack reset = new ItemStack(Material.TNT);
                    ItemStack start = new ItemStack(Material.BREAD);
                    ItemStack spectator = new ItemStack(Material.FEATHER);
                    ItemStack spawn = new ItemStack(Material.RED_BED);

                    ItemMeta hunter_meta = hunter.getItemMeta();
                    hunter_meta.setDisplayName(ChatColor.RED + "Jäger");
                    ArrayList<String> hunter_lore = new ArrayList<>();
                    hunter_lore.add("Als Jäger hinzufügen / entfernen");
                    hunter_meta.setLore(hunter_lore);
                    hunter.setItemMeta(hunter_meta);

                    ItemMeta runner_meta = runner.getItemMeta();
                    runner_meta.setDisplayName(ChatColor.BLUE + "Läufer");
                    ArrayList<String> runner_lore = new ArrayList<>();
                    runner_lore.add("Als Läufer hinzufügen / entfernen");
                    runner_meta.setLore(runner_lore);
                    runner.setItemMeta(runner_meta);

                    ItemMeta reset_meta = reset.getItemMeta();
                    reset_meta.setDisplayName(ChatColor.GOLD + "Reset");
                    ArrayList<String> reset_lore = new ArrayList<>();
                    reset_lore.add("Spieleinstellungen setzen");
                    reset_meta.setLore(reset_lore);
                    reset.setItemMeta(reset_meta);

                    ItemMeta start_meta = start.getItemMeta();
                    start_meta.setDisplayName(ChatColor.AQUA + "Start");
                    ArrayList<String> start_lore = new ArrayList<>();
                    start_lore.add("Die Jagd beginnt!");
                    start_meta.setLore(start_lore);
                    start.setItemMeta(start_meta);

                    ItemMeta spectator_meta = spectator.getItemMeta();
                    spectator_meta.setDisplayName(ChatColor.GRAY + "Spectator");
                    ArrayList<String> spectator_lore = new ArrayList<>();
                    spectator_lore.add("Gamemode auf Spectator setzen");
                    spectator_meta.setLore(spectator_lore);
                    spectator.setItemMeta(spectator_meta);

                    ItemMeta spawn_meta = spawn.getItemMeta();
                    spawn_meta.setDisplayName(ChatColor.AQUA + "Spawn");
                    ArrayList<String> spawn_lore = new ArrayList<>();
                    spawn_lore.add("Neuer Spawn");
                    spawn_meta.setLore(spawn_lore);
                    spawn.setItemMeta(spawn_meta);

                    ItemStack[] menu_items = {hunter, runner, reset, start, spectator, spawn};
                    gui.setContents(menu_items);
                    p.openInventory(gui);
                }
            }

            // Add/Remove a Hunter
            if (args[0].equalsIgnoreCase("hunter")) {
                if (args.length != 1) {
                    if (!huntersOrder.contains(args[1])) {
                        Player hunter = Bukkit.getServer().getPlayerExact(args[1]);
                        if (!hunter.getInventory().contains(Material.COMPASS)) {
                            hunter.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
                        }
                        huntersOrder.add(args[1]);
                        sender.sendMessage(hunter.getName() + " wurde als Jäger eingetragen.");
                        if (sender.getName() != hunter.getName()) {
                            hunter.sendMessage("Du wurdest als Jäger eingetragen.");
                        }
                    } else {
                        huntersOrder.remove(args[1]);
                        Player hunter = Bukkit.getServer().getPlayerExact(args[1]);
//                    hunter.getInventory().clear();
                        sender.sendMessage(hunter.getName() + " wurde als Jäger ausgetragen.");
                        if (sender.getName() != hunter.getName()) {
                            hunter.sendMessage("Du wurdest als Jäger ausgetragen.");
                        }
                    }
                } else {
                    sender.sendMessage("Bitte nutze " + ChatColor.RED + "/manhunt hunter <name>");
                }
            }

            // Add/Remove a Runner
            else if (args[0].equalsIgnoreCase("runner")) {
                if (args.length != 1) {
                    if (!runnersOrder.contains(args[1])) {
                        Player runner = Bukkit.getServer().getPlayerExact(args[1]);
//                    runner.getInventory().addItem(new ItemStack(Material.CLOCK, 1));
                        if (runner.getInventory().contains(Material.COMPASS)) {
                            runner.getInventory().removeItem(new ItemStack(Material.COMPASS));
                        }
                        runnersOrder.add(runner.getName());
                        sender.sendMessage(runner.getName() + " wurde als Läufer eingetragen.");
                        if (sender.getName() != runner.getName()) {
                            runner.sendMessage("Du wurdest als Läufer eingetragen.");
                        }
                    } else {
                        Player runner = Bukkit.getServer().getPlayerExact(args[1]);
//                    runner.getInventory().clear();
                        runnersOrder.remove(runner.getName());
                        sender.sendMessage(runner.getName() + " wurde als Läufer ausgetragen.");
                        if (sender.getName() != runner.getName()) {
                            runner.sendMessage("Du wurdest als Läufer ausgetragen.");
                        }
                    }
                } else {
                    sender.sendMessage("Bitte nutze " + ChatColor.RED + "/manhunt runner <name>");
                }
            }

            // Reset settings
            else if (args[0].equalsIgnoreCase("reset")) {
                Player s = Bukkit.getServer().getPlayerExact(sender.getName());
                World world = s.getWorld();
                world.setTime(1300);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
                world.setGameRuleValue("doImmediateRespawn", "true");
                world.setGameRuleValue("disableRaids", "true");
                world.setGameRuleValue("doWeatherCycle", "false");
                runnersOrder.clear();
                huntersOrder.clear();
                for(Player p: Bukkit.getOnlinePlayers()){
                    p.setGameMode(GameMode.SURVIVAL);
                    p.getInventory().clear();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + p.getName() + " everything");
                    s.getWorld().setSpawnLocation(s.getLocation());
                    p.setExp(0);
                    p.setHealth(0);
                }
            }

            // Start the freeze time
            else if (args[0].equalsIgnoreCase("start")) {
                if (!runnersOrder.isEmpty() && !huntersOrder.isEmpty()) {
                    int timer = 30;
                    if (args.length > 1) {
                        timer = Integer.parseInt(args[1]);
                    }

                    // Remove GUI Book from all Inventories
                    ItemStack gui = new ItemStack(Material.BOOK);
                    gui.addUnsafeEnchantment(Enchantment.LURE,1);
                    ItemMeta gui_meta = gui.getItemMeta();
                    gui_meta.setDisplayName(ChatColor.GREEN + "Gauland");
                    gui_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    gui.setItemMeta(gui_meta);
                    for (Player op : Bukkit.getOnlinePlayers()) {
                        if (op.getInventory().contains(gui)) {
                            op.getInventory().removeItem(gui);
                        }
                    }


                    Bukkit.broadcastMessage(ChatColor.BLUE + "Runner:");
                    for (int i = 0; i < runnersOrder.size(); i++) {
                        Bukkit.broadcastMessage(ChatColor.BLUE + Bukkit.getPlayerExact(runnersOrder.get(i)).getName());
                    }
                    Bukkit.broadcastMessage("-------------------");
                    Bukkit.broadcastMessage(ChatColor.RED + "Hunter:");
                    for (int i = 0; i < huntersOrder.size(); i++) {
                        Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getPlayerExact(runnersOrder.get(i)).getName());
                    }

                    timer = timer*20;
                    for (int i = 0; i < huntersOrder.size(); i++) {
                        Player p = Bukkit.getPlayerExact(huntersOrder.get(i));

                        // Potion Effects
                        int amp = 255;
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, timer, amp));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, timer, amp));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, timer, amp));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, timer, amp));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, timer, amp));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, timer, amp));
                    }

                    Bukkit.broadcastMessage("Die Jagd beginnt in " + timer/20 + " Sekunden");

                    BukkitScheduler scheduler = getServer().getScheduler();
                    scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage("Die Jagd beginnt!");
                        }
                    }, timer);

                } else {
                    sender.sendMessage("Es muss mindestens ein Spieler in jedem Team sein.");
                }

            }

            // Help / Show all cmds
            else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.RED + "/manhunt runner <player>");
                sender.sendMessage(ChatColor.WHITE + "/manhunt hunter <player>");
                sender.sendMessage(ChatColor.RED + "/manhunt reset");
                sender.sendMessage(ChatColor.WHITE + "/manhunt start <seconds>");
                sender.sendMessage(ChatColor.WHITE + "/manhunt spawn");
            }

            // Set New Spawn
            else if (args[0].equalsIgnoreCase("spawn")) {
                Player p =  Bukkit.getServer().getPlayerExact(sender.getName());
                World w = p.getWorld();
                int xCord = (new Random().nextInt(100 + 1)  + 1) * 1000;
                int zCord = (new Random().nextInt(100 + 1)  + 1) * 1000;
                w.loadChunk(xCord, zCord);
                p.sendMessage("Dieser Vorgang dauert einen kurzen Moment.");

                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        int yCord = (w.getHighestBlockYAt(xCord,zCord) + 1);
                        p.sendMessage("Koordinaten: x:" + xCord + " y:" + yCord + " z:" + zCord);
                        Location loc = new Location(w, xCord, yCord, zCord);
                        w.setSpawnLocation(loc);
                        for (Player op : Bukkit.getOnlinePlayers()) {
                            op.teleport(loc);
                        }
                    }
                }, 40L); // 2 seconds
            }

            // Set New Target (Testing)
            else if (args[0].equalsIgnoreCase("setTarget")) {
                if (!args[1].isEmpty()) {
                    Player runner = Bukkit.getPlayerExact(args[2]);
                    if (runnersOrder.get(0) == runner.getName()) {
                        sender.sendMessage(runner + " ist bereits das aktive Ziel.");
                    } else {
                        runnersOrder.clear();
                        runnersOrder.add(runner.getName());
                    }
                } else {
                    sender.sendMessage("Nope, den gibt es nicht.");
                }
            }

            // unknown cmd
            else {
                sender.sendMessage("Bruh, den Befehl kenn ich nicht.");
            }
        }
        return false;
    }

    // Compass Find Runner
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player hunter = e.getPlayer();
        ItemStack item = e.getItem();

        if (item != null && item.getType() == Material.COMPASS && huntersOrder.contains(hunter.getName())) {
            if (runnersOrder.size() > 0) {
                CompassMeta meta = (CompassMeta) item.getItemMeta();
                Location hPos = hunter.getLocation();
                Player runner = Bukkit.getPlayerExact(runnersOrder.get(0));

                List<String> nether = new ArrayList<String>();
                List<String> overworld = new ArrayList<String>();

                hunter.sendMessage("WORLD: " + hunter.getWorld().getEnvironment());

                for (int iter = 0; iter < runnersOrder.size(); iter++) {
                    Player run = Bukkit.getPlayerExact(runnersOrder.get(iter));
                    if (run.getWorld().getEnvironment().toString() == "NETHER" || run.getWorld().getEnvironment().toString() == "THE_END") {
                        overworld.remove(run.getName());
                        nether.add(run.getName());
                    } else {
                        nether.remove(run.getName());
                        overworld.add(run.getName());
                    }
                }

                if (hunter.getWorld().getEnvironment().toString() == "NETHER" || hunter.getWorld().getEnvironment().toString() == "THE_END") {
                    if (!nether.isEmpty()) {
                        if (nether.size() > 1) {
                            for (int i = 1; i < nether.size(); i++) {
                                if (Bukkit.getPlayerExact(nether.get(i)).getLocation().distance(hPos) < Bukkit.getPlayerExact(nether.get(i - 1)).getLocation().distance(hPos)) {
                                    runner = Bukkit.getPlayerExact(nether.get(i));
                                    break;
                                }
                            }
                        } else {
                            runner = Bukkit.getPlayerExact(nether.get(0));
                        }
                    } else {
                        hunter.sendMessage("Es befindet sich kein Läufer in deiner Dimension.");
                    }

                    hunter.sendMessage("Dein Kompass zeigt auf: " + runner.getName());
                    meta.setLodestoneTracked(false);
                    meta.setLodestone(runner.getLocation());
                    item.setItemMeta(meta);
                } else if (hunter.getWorld().getEnvironment().toString() == "NORMAL") {
                    if (!overworld.isEmpty()) {
                        if (overworld.size() > 1) {
                            for (int i = 1; i < overworld.size(); i++) {
                                if (Bukkit.getPlayerExact(overworld.get(i)).getLocation().distance(hPos) < Bukkit.getPlayerExact(overworld.get(i - 1)).getLocation().distance(hPos)) {
                                    runner = Bukkit.getPlayerExact(overworld.get(i));
                                    break;
                                }
                            }
                        } else {
                            runner = Bukkit.getPlayerExact(overworld.get(0));
                        }
                    } else {
                        hunter.sendMessage("Es befindet sich kein Läufer in deiner Dimension.");
                    }

                    ItemMeta defaultCompass = Bukkit.getItemFactory().getItemMeta(Material.COMPASS);
                    item.setItemMeta(defaultCompass);
                    hunter.sendMessage("Dein Kompass zeigt auf: " + runner.getName());
                    hunter.setCompassTarget(runner.getLocation());
                }

            } else {
                hunter.sendMessage("Es ist kein Läufer eingetragen.");
            }
        } else if (item != null && item.getType() == Material.BOOK) {
            hunter.performCommand("manhunt");
        }
    }

    // Add Compass or Announce Winner
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        ItemStack gui = new ItemStack(Material.BOOK);
        gui.addUnsafeEnchantment(Enchantment.LURE,1);
        ItemMeta gui_meta = gui.getItemMeta();
        gui_meta.setDisplayName(ChatColor.GREEN + "Gauland");
        gui_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        gui.setItemMeta(gui_meta);
        Player p = e.getPlayer();

        if (runnersOrder.isEmpty() && huntersOrder.isEmpty()) {
            p.getInventory().addItem(gui);
        }
        if (runnersOrder.contains(p.getName())) {
            if (!runnersOrder.isEmpty()) {
                p.setGameMode(GameMode.SPECTATOR);
                runnersOrder.remove(p.getName());
                Bukkit.broadcastMessage(p.getName() + " wurde eliminiert.");
                if (runnersOrder.isEmpty()) {
                    Bukkit.broadcastMessage("Die Jäger haben gewonnen!");
                }
            } else {
                Bukkit.broadcastMessage("Die Jäger haben gewonnen!");
            }
        } else if (huntersOrder.contains(p.getName())) {
            p.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
        }

        // Ich hasse Noah
        if (p.getName().equalsIgnoreCase("keyfantasy")) {
            Bukkit.broadcastMessage("Noah tod, alles got.");
        }
    }

    // GUI Click Event
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if ( e.getView().getTitle().equalsIgnoreCase("Manhunt") ) {
            e.setCancelled(true);
            if (e.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
                p.performCommand("manhunt hunter " + p.getName());
            }
            else if (e.getCurrentItem().getType().equals(Material.DIAMOND_PICKAXE)) {
                p.performCommand("manhunt runner " + p.getName());
            }
            else if (e.getCurrentItem().getType().equals(Material.TNT)) {
                p.performCommand("manhunt reset");
            }
            else if (e.getCurrentItem().getType().equals(Material.BREAD)) {
                p.performCommand("manhunt start");
            }
            else if (e.getCurrentItem().getType().equals(Material.FEATHER)) {
                p.sendMessage("Gamemode: " + p.getGameMode().toString().toLowerCase());
                if (p.getGameMode().toString().equalsIgnoreCase("SPECTATOR")) {
                    Bukkit.broadcastMessage(p.getName() + " ist nun im Survival-Modus");
                    p.setGameMode(GameMode.SURVIVAL);
                } else {
                    Bukkit.broadcastMessage(p.getName() + " ist nun im Spectator-Modus");
                    p.setGameMode(GameMode.SPECTATOR);
                }
            }
            else if (e.getCurrentItem().getType().equals(Material.RED_BED)) {
                p.performCommand("manhunt spawn");
            }
        }
    }

    // Player Connect Event
    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        ItemStack gui = new ItemStack(Material.BOOK);
        gui.addUnsafeEnchantment(Enchantment.LURE,1);
        ItemMeta gui_meta = gui.getItemMeta();
        gui_meta.setDisplayName(ChatColor.GREEN + "Gauland");
        gui_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        gui.setItemMeta(gui_meta);
        Player p = e.getPlayer();
        if (runnersOrder.size() == 0 && huntersOrder.size() == 0) {
            p.getInventory().clear();
        }
        if (!p.getInventory().contains(gui)) {
            p.getInventory().addItem(gui);
        }
    }
}
