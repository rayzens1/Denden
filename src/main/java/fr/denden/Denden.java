package fr.denden;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Denden extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        this.getCommand("denden").setExecutor(this);
        this.getCommand("giveescargophone").setExecutor(this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("denden")) {
            return handleDendenCommand(sender, args);
        } else if (command.getName().equalsIgnoreCase("giveescargophone")) {
            return handleGiveEscargophoneCommand(sender);
        }
        return false;
    }

    private boolean handleDendenCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande est réservée aux joueurs.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /denden <username> <message>");
            return true;
        }

        String targetName = args[0];
        String message = String.join(" ", args).substring(targetName.length() + 1);

        FileConfiguration config = getConfig();
        String escargophoneId = config.getString("escargophone.id");
        String escargophoneName = config.getString("escargophone.name");
        int escargophoneCustomModelData = config.getInt("escargophone.customModelData");

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || !itemInHand.getType().toString().equalsIgnoreCase(escargophoneId)) {
            player.sendMessage(ChatColor.RED + "Vous devez tenir un Escargophone pour utiliser cette commande.");
            return true;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || !meta.getDisplayName().equalsIgnoreCase(escargophoneName)) {
            player.sendMessage(ChatColor.RED + "Vous devez tenir un Escargophone pour utiliser cette commande.");
            return true;
        }

        if (!meta.hasCustomModelData() || meta.getCustomModelData() != escargophoneCustomModelData) {
            player.sendMessage(ChatColor.RED + "Cet item n'est pas un Escargophone valide.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Le joueur " + targetName + " n'est pas en ligne.");
            return true;
        }

        targetPlayer.sendMessage(ChatColor.GOLD + "[Escargophone] " + player.getName() + ": " + ChatColor.WHITE + message);
        player.sendMessage(ChatColor.GREEN + "Message envoyé à " + targetName + ": " + ChatColor.WHITE + message);
        return true;
    }

    private boolean handleGiveEscargophoneCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande est réservée aux joueurs.");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = getConfig();
        String escargophoneId = config.getString("escargophone.id");
        String escargophoneName = config.getString("escargophone.name");
        int escargophoneCustomModelData = config.getInt("escargophone.customModelData");

        Material material;
        try {
            material = Material.valueOf(escargophoneId.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "L'ID '"+escargophoneId.toUpperCase()+"' de l'Escargophone dans la configuration est invalide.");
            return true;
        }

        ItemStack escargophone = new ItemStack(material);
        ItemMeta meta = escargophone.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + escargophoneName);
            meta.setCustomModelData(escargophoneCustomModelData);
            escargophone.setItemMeta(meta);
        }

        player.getInventory().addItem(escargophone);
        player.sendMessage(ChatColor.GREEN + "Vous avez reçu un Escargophone !");
        return true;
    }
}
