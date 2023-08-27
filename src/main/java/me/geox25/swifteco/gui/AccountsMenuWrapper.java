package me.geox25.swifteco.gui;

import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;

import java.util.List;

public class AccountsMenuWrapper {

    private final List<Menu> pages;

    private final Player player;

    public AccountsMenuWrapper(List<Menu> pages, Player player) {
        this.pages = pages;
        this.player = player;
    }

    public List<Menu> getPages() {
        return this.pages;
    }

    public Player getPlayer() {
        return this.player;
    }
}
