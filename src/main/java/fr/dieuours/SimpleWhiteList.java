package fr.dieuours;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static org.bukkit.event.EventPriority.HIGHEST;

public class SimpleWhiteList extends JavaPlugin implements Listener {

    private static CommandSender sender;
    private FileConfiguration fileConfiguration;
    private boolean whitelist;

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onDisable() {
        sender.sendMessage("SimpleWhiteList is disable");
        super.onDisable();
    }

    @Override
    public void onEnable() {
        sender = Bukkit.getConsoleSender();

        sender.sendMessage("SimpleWhiteList v0.0.1 by DieuOurs");

        getCommand("simplewhitelist").setExecutor(this);

        getConfig().options().copyDefaults(false);
        initConfig();
        saveConfig();

        sender.sendMessage("SimpleWhiteList is enable");
        super.onEnable();
    }

    private void initConfig(){
        if(getConfig().get("messages")==null){
            getConfig().set("messages.nopermission","§cYou don't have permission to send this command");
            saveConfig();
        }
        if(getConfig().get("whitelist")==null){
            getConfig().set("whitelist","off");
            saveConfig();
        }
        if(getConfig().getString("whitelist").equalsIgnoreCase("off")){
            whitelist = false;
        }else{
            whitelist = true;
        }

        if(getConfig().get("messages.nopermission")==null){
            getConfig().set("nopermission","§cYou don't have permission to execute this command...");
            saveConfig();
        }
        if(getConfig().get("messages.emptylist")==null){
            getConfig().set("emptylist","§cEmpty list");
            saveConfig();
        }
        if(getConfig().get("messages.newmember")==null){
            getConfig().set("newmember","§aSuccess add member to whitelist");
            saveConfig();
        }
        if(getConfig().get("messages.remmember")==null){
            getConfig().set("remmember","§aSuccess remove member to whitelist");
            saveConfig();
        }
        if(getConfig().get("messages.unknownmember")==null){
            getConfig().set("unknownmember","§cThis member is not on whitelist");
            saveConfig();
        }
        if(getConfig().get("messages.setonwl")==null){
            getConfig().set("setonwl","§cWhitelist switch to on");
            saveConfig();
        }
        if(getConfig().get("messages.setoffwl")==null){
            getConfig().set("setoffwl","§cWhitelist switch to off");
            saveConfig();
        }

        if(getConfig().get("messages.alreadyonwl")==null){
            getConfig().set("alreadyonwl","§cWhitelist already of");
            saveConfig();
        }
        if(getConfig().get("messages.alreadyoffwl")==null){
            getConfig().set("alreadyoffwl","§cWhitelist already off");
            saveConfig();
        }

        if(getConfig().get("messages.serverwhitelist")==null){
            getConfig().set("serverwhitelist","§cThis server is on WhiteList...");
            saveConfig();
        }
        if(getConfig().get("messages.unknownarg")==null){
            getConfig().set("unknownarg","§cUnknow argument");
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(cmd.getName().equalsIgnoreCase("simplewhitelist")||cmd.getName().equalsIgnoreCase("swl")){
            if(sender instanceof Player) {
                Player p = (Player) sender;
                if (p.isOp()) {
                    commandSet(sender, cmd, msg, args);
                }else{
                    p.sendMessage(fileConfiguration.getString("messages.nopermission"));
                }
            }else{
                commandSet(sender, cmd, msg, args);
            }
        }
        return false;
    }
    private void commandSet(CommandSender sender, Command cmd, String msg, String[] args) {
        fileConfiguration = getConfig();
        if (args.length == 0) {
            sender.sendMessage("§aWhitelist §8" + fileConfiguration.getString("whitelist"));
            sender.sendMessage("");
            sender.sendMessage("/swl list");
            sender.sendMessage("/swl on|off");
            sender.sendMessage("/swl add <member>");
            sender.sendMessage("/swl remove <member>");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (fileConfiguration.get("list") == null) {
                    sender.sendMessage(fileConfiguration.getString("messages.emptylist"));
                } else {
                    sender.sendMessage("§8" + fileConfiguration.getString("list"));
                }
            }else if(args[0].equalsIgnoreCase("on")) {
                if(!whitelist){
                    whitelist = true;
                    getConfig().set("whitelist","on");
                    saveConfig();
                    sender.sendMessage(fileConfiguration.getString("messages.setonwl"));
                }else{
                    sender.sendMessage(fileConfiguration.getString("messages.alreadyonwl"));
                }
            }else if(args[0].equalsIgnoreCase("off")){
                if(whitelist){
                    whitelist = false;
                    getConfig().set("whitelist","off");
                    saveConfig();
                    sender.sendMessage(fileConfiguration.getString("messages.setoffwl"));
                }else{
                    sender.sendMessage(fileConfiguration.getString("messages.alreadyoffwl"));
                }
            } else {
                sender.sendMessage(fileConfiguration.getString("messages.unknownarg"));
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (fileConfiguration.get("list") == null) {
                    sender.sendMessage(fileConfiguration.getString("messages.unknownmember"));
                } else {
                    List<String> list = fileConfiguration.getStringList("list");
                    list.add(args[1]);
                    getConfig().set("list", list);
                    saveConfig();
                    sender.sendMessage(fileConfiguration.getString("messages.newmember"));
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (fileConfiguration.get("list") == null || (!fileConfiguration.getStringList("list").contains(args[1]))) {
                    sender.sendMessage(fileConfiguration.getString("messages.unknownmember"));
                } else {
                    List<String> list = fileConfiguration.getStringList("list");
                    list.remove(args[1]);
                    getConfig().set("list", list);
                    saveConfig();
                    sender.sendMessage(fileConfiguration.getString("messages.remmember"));
                }
            } else {
                sender.sendMessage(fileConfiguration.getString("messages.unknownarg"));
            }
        } else {
            sender.sendMessage(fileConfiguration.getString("messages.unknownarg"));
        }
    }

    @EventHandler(priority = HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        if(!whitelist) return;
        fileConfiguration = getConfig();
        if(!fileConfiguration.getStringList("list").contains(e.getPlayer().getName())) e.getPlayer().kickPlayer(fileConfiguration.getString("messages.serverwhitelist"));
    }
}
