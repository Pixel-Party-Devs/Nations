package org.nations;

import mc.obliviate.inventory.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.nations.commands.settle;
import org.nations.database.HikariCP;
import org.nations.listener.NpcInteractListener;
import org.nations.manager.settlementManager;

import java.util.Objects;
import java.util.logging.Logger;

public final class Nations extends JavaPlugin {

    HikariCP hikari;
    Logger logger;
    public settlementManager settlementManager = new settlementManager();

    @Override
    public void onEnable() {


        logger = getLogger();
        saveDefaultConfig();
        new InventoryAPI(this).init();

        /*
        try {
            hikari = new HikariCP(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
         */

        commandRegistry();
        listenerRegistry();
        serilizationRegistry();
    }

    public void commandRegistry() {
        Objects.requireNonNull(getCommand("settle")).setExecutor(new settle(this));
    }

    public void listenerRegistry() {
        Bukkit.getPluginManager().registerEvents(new NpcInteractListener(), this);
    }
    public void serilizationRegistry() {

    }


    @Override
    public void onDisable() {

    }
}
