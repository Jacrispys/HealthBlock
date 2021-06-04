package main.java.com.Jacrispys.HealthBlock.commands;

import main.java.com.Jacrispys.HealthBlock.HealthBlockMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import static main.java.com.Jacrispys.HealthBlock.utils.chat.chat;

public class HealthBlockStart implements CommandExecutor, Listener {
    private HealthBlockMain plugin;

    public HealthBlockStart(HealthBlockMain plugin) {
        this.plugin = plugin;

        plugin.getCommand("healthblock").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private HashMap<UUID, Double> damage = new HashMap<>();
    private HashMap<UUID, Boolean> start = new HashMap<>();
    private HashMap<UUID, Long> timerStart = new HashMap<>();
    private HashMap<UUID, Double> timerEnd = new HashMap<>();

    private void timer(Player player) {
        timerStart.put(player.getUniqueId(), System.currentTimeMillis());
        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String actionBar = "&e&lTimer: &b&l" + (double) (System.currentTimeMillis() - timerStart.get(player.getUniqueId())) / 1000D + "";
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(chat(actionBar)));
                    timerEnd.put(player.getUniqueId(), (double) (System.currentTimeMillis() - timerStart.get(player.getUniqueId())) / 1000D);
                    if (start.get(player.getUniqueId()) != true) {
                        player.sendMessage(chat("&e&LCongrats! You beat the game in: &a&l" + timerEnd.get(player.getUniqueId()).toString()));
                        this.cancel();
                        return;
                    }
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        };
        timer.runTaskTimer(plugin, 1, 1);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("healthblock") && sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0) {
                p.sendMessage(chat("&c&lHealth&8&lBlock &7V-1.0"));
                p.sendMessage(chat("&E&lAuthor: &b&lJacrispys"));
                p.sendMessage(chat("&cUse '/Healthblock help' to begin!"));

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    p.sendMessage(chat("&6------------------&e&lHelp Screen&6-----------------"));
                    p.sendMessage(chat("&e/hb help:&9 Displays this prompt!"));
                    p.sendMessage(chat("&e/hb setHealth:&9 Chooses Health for the challenge!"));
                    p.sendMessage(chat("&e/hb setDamage:&9 Chooses how much health you lose!"));
                    p.sendMessage(chat("&e/hb start:&9 Starts the minigame!"));
                    p.sendMessage(chat("&e/hb stop:&9 stops the minigame!"));
                    p.sendMessage(chat("&6--------------------------------------------------"));
                } else if (args[0].equalsIgnoreCase("sethealth")) {
                    p.sendMessage(chat("&cPlease Specify a number!"));
                } else if (args[0].equalsIgnoreCase("setdamage")) {
                    p.sendMessage(chat("&cPlease Specify a number!"));
                } else if (args[0].equalsIgnoreCase("start")) {
                    start.put(p.getUniqueId(), true);
                    timer(p);
                    //start logic
                } else if (args[0].equalsIgnoreCase("stop")) {
                    start.put(p.getUniqueId(), false);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                    p.setHealth(p.getMaxHealth());
                    //stop logic
                }

            } else if (args.length > 1) {
                if (args[0].equalsIgnoreCase("sethealth")) {
                    try {
                        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Double.parseDouble(args[1]));
                        p.setHealth(p.getMaxHealth());
                        p.sendMessage(chat("&cHealth &asuccessfully set to: &d" + args[1]));

                    } catch (Exception e) {
                        p.sendMessage(chat("&CError: Not A Number"));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("setdamage")) {
                    try {
                        damage.put(p.getUniqueId(), Double.parseDouble(args[1]));
                        p.sendMessage(chat("&cDamage &asuccessfully set to: &d" + args[1]));

                    } catch (Exception e) {
                        p.sendMessage(chat("&CError: Not A Number"));
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if(start.get(p.getUniqueId())) {
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getMaxHealth() - damage.get(p.getUniqueId()));
            p.sendMessage(chat("&cOuch! &7You lost &c" + damage.get(p.getUniqueId()).toString() + "&7 health!"));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(damage.get(p.getUniqueId()) == null) {
            damage.put(p.getUniqueId(), 1D);
        }
    }

}
