package de.terranova.nations.worldguard;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.mcterranova.bona.lib.chat.Chat;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class claim {
  public static void createClaim(String name, Player p) {

    int nx = (int) (Math.floor(p.getLocation().x() / 48) * 48);
    int nz = (int) (Math.floor(p.getLocation().z() / 48) * 48);

    BlockVector2 nw = BlockVector2.at(nx, nz);
    BlockVector2 sw = BlockVector2.at(nx, nz + 47);
    BlockVector2 se = BlockVector2.at(nx + 47, nz + 47);
    BlockVector2 ne = BlockVector2.at(nx + 47, nz);

    List<BlockVector2> corners = Arrays.asList(nw, ne, se, sw);

    LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);


    ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(name, corners, -64, 320);

    DefaultDomain owners = region.getOwners();
    owners.addPlayer(lp);
    region.setOwners(owners);


    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regions = container.get(lp.getWorld());

    assert regions != null;
    regions.addRegion(region);
  }

  public static void addToExistingClaim(Player p, ProtectedRegion oldRegion) {
    if (oldRegion instanceof ProtectedPolygonalRegion oldPolygonalRegion) {

      int nx = (int) (Math.floor(p.getLocation().x() / 48) * 48);
      int nz = (int) (Math.floor(p.getLocation().z() / 48) * 48);

      Vectore2 nw = new Vectore2(nx + 0.5, nz + 0.5);
      Vectore2 ne = new Vectore2(nx  + 0.5 + 47, nz  + 0.5);
      Vectore2 sw = new Vectore2(nx  + 0.5 , nz + 47  + 0.5);
      Vectore2 se = new Vectore2(nx + 47  + 0.5, nz + 47 + 0.5);


      List<Vectore2> newPoints = Arrays.asList(nw, ne, se, sw);
      List<Vectore2> oldPoints = new ArrayList<>();

      for (BlockVector2 v : oldPolygonalRegion.getPoints()) {
        oldPoints.add(new Vectore2(v.x(), v.z()));
      }


      Optional<List<Vectore2>> claims = claimre.dothatshitforme(oldPoints, newPoints);
      if(claims.isEmpty()){
        p.sendMessage(Chat.returnRedFade("Bitte keine leeren flächen umclaimen."));
        return;
      }


      List<BlockVector2> finalNewRegion = new ArrayList<>();

      for (Vectore2 v : claims.get()) {
        finalNewRegion.add(BlockVector2.at(v.x, v.z));
        p.sendMessage(String.valueOf(BlockVector2.at(v.x, v.z)));
      }


      ProtectedPolygonalRegion region = new ProtectedPolygonalRegion(oldRegion.getId(), finalNewRegion, oldPolygonalRegion.getMinimumPoint().y(), oldPolygonalRegion.getMaximumPoint().y());
      region.copyFrom(oldRegion);

      p.sendMessage("Wal");

      LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionManager regions = container.get(lp.getWorld());
      assert regions != null;
      regions.addRegion(region);

      p.sendMessage("Marienkaefer");
    }
  }

  public static Optional<ProtectedRegion> checkSurrAreaForSettles(Player p) {
    p.sendMessage("Papagei");
    int nx = (int) Math.floor(p.getLocation().x() / 48);
    int nz = (int) Math.floor(p.getLocation().z() / 48);

    Vector2 north = Vector2.at(nx - 1, nz);
    Vector2 south = Vector2.at(nx + 1, nz);
    Vector2 west = Vector2.at(nx, nz - 1);
    Vector2 east = Vector2.at(nx, nz + 1);

    BlockVector3[] bpos = new BlockVector3[]{BlockVector3.at(north.x() * 48, -64, north.z() * 48), BlockVector3.at(south.x() * 48, -64, south.z() * 48), BlockVector3.at(west.x() * 48, -64, west.z() * 48), BlockVector3.at(east.x() * 48, -64, east.z() * 48)};

    LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regions = container.get(lp.getWorld());

    for (BlockVector3 pos : bpos) {
      assert regions != null;
      ApplicableRegionSet set = regions.getApplicableRegions(pos);
      if (!(set.size() == 0)) {
        return set.getRegions().stream().findFirst();
      }
    }
    return Optional.empty();
  }

  public static boolean checkAreaForSettles(Player p) {
    LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regions = container.get(lp.getWorld());
    assert regions != null;
    RegionQuery query = container.createQuery();
    ApplicableRegionSet set = query.getApplicableRegions(lp.getLocation());
    return !(set.size() == 0);
  }


  public static void remove(String name) {

  }
}
