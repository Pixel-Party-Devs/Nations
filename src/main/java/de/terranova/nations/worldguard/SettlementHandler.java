package de.terranova.nations.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;

import de.mcterranova.terranovaLib.utils.Chat;
import de.terranova.nations.NationsPlugin;
import de.terranova.nations.settlements.Settlement;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.Set;
import java.util.UUID;

public class SettlementHandler extends FlagValueChangeHandler<String> {
    public static final Factory FACTORY = new Factory();

    public SettlementHandler(Session session) {
        super(session, SettlementFlag.SETTLEMENT_UUID_FLAG);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        if (entered.isEmpty() && exited.isEmpty() && from.getExtent().equals(to.getExtent())) {
            return true;
        }

        CraftPlayer p = (CraftPlayer) BukkitAdapter.adapt(player);


        if (entered.isEmpty()) {
            for (ProtectedRegion region : exited) {
                String flag = region.getFlag(SettlementFlag.SETTLEMENT_UUID_FLAG);
                if (flag == null || flag.isEmpty()) return true;
                Settlement settle = NationsPlugin.settlementManager.getSettlement(UUID.fromString(flag));
                p.sendActionBar(Chat.greenFade(String.format("Du hast %s verlassen.", settle.name.replaceAll("_", " "))));
            }

        } else {
            for (ProtectedRegion region : entered) {
                String flag = region.getFlag(SettlementFlag.SETTLEMENT_UUID_FLAG);
                if (flag == null || flag.isEmpty()) return true;
                Settlement settle = NationsPlugin.settlementManager.getSettlement(UUID.fromString(flag));
                p.sendActionBar(Chat.greenFade(String.format("Du hast %s betreten.", settle.name.replaceAll("_", " "))));
            }
        }


        return true;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, String value) {

    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String currentValue, String lastValue, MoveType moveType) {
        return false;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String lastValue, MoveType moveType) {
        return false;
    }

    public static class Factory extends Handler.Factory<SettlementHandler> {
        @Override
        public SettlementHandler create(Session session) {
            return new SettlementHandler(session);
        }
    }


}
