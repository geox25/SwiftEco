package me.geox25.swifteco.event;

import me.geox25.swifteco.SwiftEco;
import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Events extends SubCommand implements Listener {

    public Events(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_AIR) || !(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.PAPER)) {
            return;
        }

        Player p = e.getPlayer();
        PlayerInventory inv = p.getInventory();
        String name = serviceManager.getUUIDOrName(p.getName());
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) {return;}
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if (!meta.hasDisplayName()) {return;}

        boolean correct_name = meta.getDisplayName().equals(ChatColor.AQUA + "Banknote");

        if (correct_name) {
            try {
                if (!p.hasPermission("swift.note")) {
                    p.sendMessage(ChatColor.RED + "Insufficient Permissions!");
                    return;
                }

                List<String> lore = meta.getLore();
                assert lore != null;
                double amount = Double.parseDouble(lore.get(1).substring(13).replace(",", ""));
                String account = lore.get(0).substring(11);
                String id = lore.get(lore.size() - 1).replace("ID: ", "").substring(2);

                if (idsModule.checkNote(id, account, amount)) {
                    idsModule.removeNote(id);
                    inv.setItemInMainHand(null);
                    double balance = dataFile.getDouble("account." + name + ".balance");
                    dataFile.set("account." + name + ".balance", balance + amount);
                    p.sendMessage(ChatColor.GREEN + "Success");
                } else {
                    p.sendMessage(ChatColor.RED + "Invalid Note!");
                }

            } catch (StringIndexOutOfBoundsException exception) {
                p.sendMessage(ChatColor.RED + "Invalid Note!");
            }
        }
    }

    @Override
    public List<String> getNames() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {}
}
