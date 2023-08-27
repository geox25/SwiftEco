package me.geox25.swifteco.gui;

import me.geox25.swifteco.SwiftEco;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.template.StaticItemTemplate;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuiManager {

    private final ItemManager itemManager;

    private ServiceManager serviceManager;

    // Plugin Instance for scheduling tasks
    private SwiftEco swiftEco;

    private final Menu mainMenu;

    private final Menu shopMenu;
    private final Menu materialsShopMenu;
    private final Menu valuablesShopMenu;
    private final Menu farmingShopMenu;

    enum SHOP {
        MATERIALS,
        VALUABLES,
        FARMING
    }

    private final HashMap<Player, List<Menu>> accountsMenuListeners = new HashMap<>();

    public GuiManager(ServiceManager serviceManager, SwiftEco swiftEco) {
        this.itemManager = new ItemManager(serviceManager);
        this.serviceManager = serviceManager;

        this.mainMenu = buildMainMenu();
        this.shopMenu = buildShopMenu();
        this.materialsShopMenu = buildShopSectionMenu(SHOP.MATERIALS);
        this.valuablesShopMenu = buildShopSectionMenu(SHOP.VALUABLES);
        this.farmingShopMenu = buildShopSectionMenu(SHOP.FARMING);

        serviceManager.setGuiManager(this);
        this.swiftEco = swiftEco;
    }

    public Menu buildMainMenu() {

        // The Main Swift Menu GUI
        Menu mainMenu = ChestMenu.builder(3)
                .title(ChatColor.AQUA + "Swift Menu")
                .redraw(true)
                .build();

        Slot accountsSlot = mainMenu.getSlot(11);
        accountsSlot.setItem(itemManager.generateAccountsButton());

        accountsSlot.setClickHandler((p, clickInfo) -> {
            Bukkit.getScheduler().runTaskLater(
                    swiftEco, () -> openAccountsMenu(p), 1
            );
        });

        Slot primaryInfoSlot = mainMenu.getSlot(13);
        primaryInfoSlot.setItemTemplate(itemManager::generatePrimaryAccountItem);

        /*
        Edited on 7/12/23
        Felt that the swift shop is rather bad practice,
        and that it is better to use Swift as more long-term storage
        for money and essentials for day-to-day purchases because of its
        vault integration
         */
        Slot shopSlot = mainMenu.getSlot(15);
        /*
        shopSlot.setItem(itemManager.generateShopButton());

        shopSlot.setClickHandler((p, clickInfo) -> {
            Bukkit.getScheduler().runTaskLater(swiftEco, () -> {
                shopMenu.open(p);
            }, 1);
        });
         */
        shopSlot.setItem(itemManager.generateUnavailableItem());

        return mainMenu;
    }

    public Menu buildShopMenu() {

        AtomicBoolean clicked = new AtomicBoolean(false);

        // The Shop Menu GUI
        Menu shopMenu = ChestMenu.builder(1)
                .title(ChatColor.AQUA + "Shop Menu")
                .redraw(false)
                .build();

        // Add category items
        Slot materials = shopMenu.getSlot(2);
        materials.setItem(itemManager.generateMaterialsButton());
        materials.setClickHandler((p, menu) -> {
            Bukkit.getScheduler().runTaskLater(
                    swiftEco, () -> {
                        clicked.set(true);
                        materialsShopMenu.open(p);
                    }, 1
            );
        });

        Slot valuables = shopMenu.getSlot(4);
        valuables.setItem(itemManager.generateValuablesButton());
        valuables.setClickHandler((p, menu) -> {
            Bukkit.getScheduler().runTaskLater(
                    swiftEco, () -> {
                        clicked.set(true);
                        valuablesShopMenu.open(p);
                    }, 1
            );
        });

        Slot farming = shopMenu.getSlot(6);
        farming.setItem(itemManager.generateFarmingButton());
        farming.setClickHandler((p, menu) -> {
            Bukkit.getScheduler().runTaskLater(
                    swiftEco, () -> {
                        clicked.set(true);
                        farmingShopMenu.open(p);
                    }, 1
            );
        });

        shopMenu.setCloseHandler((p, menu) -> {
            if (clicked.get()) {
                clicked.set(false);
                return;
            }

            Bukkit.getScheduler().runTaskLater(
                    swiftEco, () -> {
                        mainMenu.open(p);
                    }, 1
            );
        });

        return shopMenu;
    }

    public Menu buildAccountsMenu(Player player) {

        // The accounts menu GUI
        Menu.Builder<ChestMenu.Builder> accountsMenu = ChestMenu.builder(6)
                .title(ChatColor.AQUA + "Accounts")
                .redraw(true);

        Mask glassSlots = BinaryMask.builder(accountsMenu.getDimensions())
                .item(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .build();

        Mask itemSlots = BinaryMask.builder(accountsMenu.getDimensions())
                .pattern("011111110")
                .pattern("011111110")
                .pattern("011111110")
                .pattern("011111110")
                .pattern("011111110")
                .pattern("000000000")
                .build();

        PaginatedMenuBuilder pagesBuilder = PaginatedMenuBuilder.builder(accountsMenu)
                .slots(itemSlots)
                .addItems(itemManager.generateAccountItems(player))
                .nextButton(new ItemStack(Material.ARROW))
                .nextButtonEmpty(new ItemStack(Material.BARRIER)) // Icon when no next page available
                .nextButtonSlot(52)
                .previousButton(new ItemStack(Material.ARROW))
                .previousButtonEmpty(new ItemStack(Material.BARRIER)) // Icon when no previous page available
                .previousButtonSlot(46);

        List<Menu> pages = pagesBuilder.build();

        for (Menu page : pages) {
            // Apply mask and set close handler in same loop
            // so save an iteration cycle
            glassSlots.apply(page);

            page.setCloseHandler((p, menu) -> {
                Bukkit.getScheduler().runTaskLater(
                        swiftEco, () -> {
                            accountsMenuListeners.remove(p);
                            mainMenu.open(p);
                        }, 1
                );
            });
        }

        accountsMenuListeners.put(player, pages);

        return pages.get(0);
    }

    public Menu buildShopSectionMenu(SHOP shopType) {

        // shopType dependent variables
        String title;
        String section;

        switch (shopType) {
            case MATERIALS:
                title = "Materials Shop";
                section = "materials";
                break;
            case VALUABLES:
                title = "Valuables Shop";
                section = "valuables";
                break;
            case FARMING:
                title = "Farming Shop";
                section = "farming";
                break;
            default:
                title = "Shop";
                section = "materials";
                break;
        }

        ConfigurationSection itemList = serviceManager.getShopModule().getShopDataFile().getConfigurationSection(section);
        List<SlotSettings> slotSettings = new ArrayList<>();

        Bukkit.getLogger().info("looping through keys of " + section);

        assert itemList != null;
        for (String key : itemList.getKeys(false)) {
            Bukkit.getLogger().info("key:");
            Bukkit.getLogger().info(key);

            ItemStack item = new ItemStack(Material.valueOf(key));

            /* click logic goes here */
            YamlConfiguration dataFile = serviceManager.getDataModule().getDataFile();
            double buyPrice = itemList.getDouble(key + ".buy");
            double sellPrice = itemList.getDouble(key + ".sell");

            SlotSettings clickableItem = SlotSettings.builder()
                    .item(itemManager.generateShopItem(item, buyPrice, sellPrice))
                    .clickHandler((player, click) -> {
                        String playerName = player.getName();
                        String UUIDOrName = serviceManager.getUUIDOrName(playerName);
                        double playerBalance = dataFile.getDouble("account." + UUIDOrName + ".balance");
                        int amount = click.getClickedSlot().getRawItem(player).getAmount();
                        double price = (amount * buyPrice);
                        double value = (amount * sellPrice);

                        switch(click.getClickType()) {
                            // If left click (buying)
                            case LEFT:
                                if (buyPrice == 0) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    player.sendMessage(ChatColor.RED + "You cannot buy that item!");
                                    return;
                                }

                                if (!(playerBalance >= price)) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    player.sendMessage(ChatColor.RED + "Insufficient Funds!");
                                    return;
                                }

                                dataFile.set("account." + UUIDOrName + ".balance", playerBalance - price);
                                player.getInventory().addItem(new ItemStack(item.getType()));

                                break;

                            // If right click (selling)
                            case RIGHT:
                                if (sellPrice == 0) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    player.sendMessage(ChatColor.RED + "You cannot sell that item!");
                                    return;
                                }

                                if (!player.getInventory().contains(item.getType())) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    player.sendMessage(ChatColor.RED + "Insufficient Items!");
                                    return;
                                }

                                dataFile.set("account." + UUIDOrName + ".balance", playerBalance + value);
                                for (ItemStack playerItem : player.getInventory().getContents()) {
                                    if (playerItem == null) {continue;}
                                    if (playerItem.getType().equals(item.getType())) {
                                        playerItem.setAmount(playerItem.getAmount() - 1);
                                        return;
                                    }
                                }

                                break;

                            case SHIFT_LEFT:
                                click.getClickedSlot().getRawItem(player).setAmount(amount + 1);

                                break;

                            case SHIFT_RIGHT:
                                click.getClickedSlot().getRawItem(player).setAmount(amount - 1);

                                break;

                            default:
                                break;
                        }

                    }).build();
            slotSettings.add(clickableItem);
        }

        Menu.Builder<ChestMenu.Builder> shopMenu = ChestMenu.builder(6)
                .title(ChatColor.AQUA + title)
                .redraw(true);

        Mask glassSlots = BinaryMask.builder(shopMenu.getDimensions())
                .item(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .build();

        Mask itemSlots = BinaryMask.builder(shopMenu.getDimensions())
                .pattern("011111110")
                .pattern("011111110")
                .pattern("011111110")
                .pattern("011111110")
                .pattern("011111110")
                .pattern("000000000")
                .build();

        PaginatedMenuBuilder pagesBuilder = PaginatedMenuBuilder.builder(shopMenu)
                .slots(itemSlots)
                .addSlotSettings(slotSettings)
                .nextButton(new ItemStack(Material.ARROW))
                .nextButtonEmpty(new ItemStack(Material.BARRIER)) // Icon when no next page available
                .nextButtonSlot(52)
                .previousButton(new ItemStack(Material.ARROW))
                .previousButtonEmpty(new ItemStack(Material.BARRIER)) // Icon when no previous page available
                .previousButtonSlot(46);

        List<Menu> pages = pagesBuilder.build();

        for (Menu page : pages) {
            // Apply mask and set close handler in same loop
            // so save an iteration cycle
            glassSlots.apply(page);

            page.setCloseHandler((p, menu) -> {
                Bukkit.getScheduler().runTaskLater(
                        swiftEco, () -> {
                            Bukkit.getLogger().info("shop section closed");
                            this.shopMenu.open(p);
                        }, 1
                );
            });
        }

        return pages.get(0);
    }

    public void openMainMenu(Player player) {
        mainMenu.open(player);
    }

    public void openAccountsMenu(Player player) {
        // build menu and open
        buildAccountsMenu(player).open(player);
    }

    public void update() {
        this.mainMenu.update();

        for (Map.Entry<Player, List<Menu>> entry : accountsMenuListeners.entrySet()) {
            Player player = entry.getKey();

            Menu newMenu = buildAccountsMenu(player);

            // Open menu for player
            newMenu.open(player);
        }
    }
}
