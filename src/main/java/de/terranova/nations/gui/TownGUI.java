package de.terranova.nations.gui;

import de.mcterranova.bona.lib.chat.Chat;
import de.terranova.nations.NationsPlugin;
import de.terranova.nations.settlements.AccessLevelEnum;
import de.terranova.nations.settlements.TownSkins;
import de.terranova.nations.settlements.settlement;
import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TownGUI extends Gui {
    String i = "10";


    public TownGUI(Player player) {
        super(player, "town-gui", Chat.blueFade("Town Menu"), 5);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        JavaPlugin.getPlugin(NationsPlugin.class);
        Optional<settlement> settlement = NationsPlugin.settlementManager.checkIfPlayerIsWithinClaim(player);
        if(settlement.isEmpty()) return;
        Optional<AccessLevelEnum> access = NationsPlugin.settlementManager.getAccessLevel(player, settlement.get().id);
        if(access.isEmpty()) return;

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta mfiller = filler.getItemMeta();
        mfiller.displayName(Chat.stringToComponent(""));
        filler.setItemMeta(mfiller);
        fillGui(filler);

        ItemStack level = new ItemStack(Material.NETHER_STAR);
        level.setAmount(settlement.get().level+1);
        ItemStack skins = new ItemStack(Material.PLAYER_HEAD);
        ItemStack score = new ItemStack(Material.GOLD_INGOT);
        ItemStack upgrades = new ItemStack(Material.IRON_INGOT);
        ItemStack farm = new ItemStack(Material.GRASS_BLOCK);
        ItemStack settings = new ItemStack(Material.COMPARATOR);

        ItemMeta mlevel = level.getItemMeta();
        ItemMeta mskins = skins.getItemMeta();
        ItemMeta mscore = score.getItemMeta();
        ItemMeta mupgrades = upgrades.getItemMeta();
        ItemMeta mfarm = farm.getItemMeta();
        ItemMeta msettings = settings.getItemMeta();

        List<Component> llevel = new ArrayList<>();
        llevel.add(Chat.cottonCandy(String.format("<i>Vorteile Level %s:",settlement.get().level)));
        llevel.add(Chat.cottonCandy(String.format("<i>%s/9 Claims",settlement.get().claims)));
        mlevel.lore(llevel);
        List<Component> lskins = new ArrayList<>();
        lskins.add(Chat.cottonCandy("<i>Hier klicken um den Skin zu ändern."));
        mskins.lore(lskins);
        List<Component> lscore = new ArrayList<>();
        lscore.add(Chat.cottonCandy("<i>Hier klicken fuer mehr infos."));
        mscore.lore(lscore);
        List<Component> lupgrades = new ArrayList<>();
        lupgrades.add(Chat.cottonCandy("<i>Hier klicken um die Stadt zu verbessern."));
        mupgrades.lore(lupgrades);
        List<Component> lfarm = new ArrayList<>();
        lfarm.add(Chat.cottonCandy("<i>Hier klicken um die Farmwelt zu betreten."));
        mfarm.lore(lfarm);
        List<Component> lsettings = new ArrayList<>();
        lsettings.add(Chat.cottonCandy("<i>Hier kannst du deine Stadt einstellen."));
        msettings.lore(lsettings);

        mlevel.displayName(Chat.redFade("Stadtlevel"));
        mskins.displayName(Chat.yellowFade("Skins"));
        mscore.displayName(Chat.yellowFade("Score"));
        mupgrades.displayName(Chat.yellowFade("Upgrades"));
        mfarm.displayName(Chat.yellowFade("Farm"));
        msettings.displayName(Chat.yellowFade("Settings"));

        level.setItemMeta(mlevel);
        skins.setItemMeta(mskins);
        score.setItemMeta(mscore);
        upgrades.setItemMeta(mupgrades);
        farm.setItemMeta(mfarm);
        settings.setItemMeta(msettings);

        Icon iskins = new Icon(skins);
        Icon iupgrades = new Icon(upgrades);
        Icon isettings = new Icon(settings);

        addItem(13, level);
        addItem(19, iskins);
        addItem(23, iupgrades);
        addItem(21, score);
        addItem(25, farm);
        addItem(31, isettings);





        iupgrades.onClick(e -> {
            if(!player.hasPermission("nations.menu.upgrades")) return;
            if(Objects.equals(access, AccessLevelEnum.MAJOR)|| Objects.equals(access, AccessLevelEnum.VICE)){
                new TownUpgradeGUI(player).open();
            } else {
                player.sendMessage(Chat.errorFade("Wende dich an den Besitzer major Error."));
            }

        });

        isettings.onClick(e -> {
            if(!player.hasPermission("nations.menu.settings")) return;
            new TownSettingsGUI(player).open();
        });


        iskins.onClick(e -> {
            if(!player.hasPermission("nations.menu.skin")) return;
            int rowsSkins = 3;
            if (TownSkins.values().length >= 8) {

                rowsSkins = 4;
            } else if (TownSkins.values().length >= 15) {
                rowsSkins = 5;
            } else if (TownSkins.values().length >= 22) {
                rowsSkins = 6;
            }
            new TownSkinGUI(player, rowsSkins).open();
        });

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
