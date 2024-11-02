package dev.jaqobb.weighted_dripleaves;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.BigDripleaf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

public class WeightedDripleavesPlugin extends JavaPlugin implements Listener {
    
    private boolean includeArmor;
    private boolean includeEquipment;
    private boolean calculateAllPlayers;
    private double weightToTriggerDripleaf;
    private Map<Material, Double> weights;
    
    @Override
    public void onLoad() {
        this.getLogger().log(Level.INFO, "Loading configuration...");
        this.saveDefaultConfig();
        this.includeArmor = this.getConfig().getBoolean("include.armor");
        this.includeEquipment = this.getConfig().getBoolean("include.equipment");
        this.calculateAllPlayers = this.getConfig().getBoolean("calculate-all-players");
        this.weightToTriggerDripleaf = this.getConfig().getDouble("weight-to-trigger-dripleaf");
        this.weights = new EnumMap<>(Material.class);
        for (String weightMaterial : this.getConfig().getConfigurationSection("weights").getKeys(false)) {
            this.weights.put(Material.getMaterial(weightMaterial), this.getConfig().getConfigurationSection("weights").getDouble(weightMaterial));
        }
        this.getServer().getLogger().log(Level.INFO, "Loaded configuration:");
        this.getLogger().log(Level.INFO, "* Include armor: " + (this.includeArmor ? "yes" : "no") + ".");
        this.getLogger().log(Level.INFO, "* Include equipment: " + (this.includeEquipment ? "yes" : "no") + ".");
        this.getLogger().log(Level.INFO, "* Calculate all players: " + (this.calculateAllPlayers ? "yes" : "no") + ".");
        this.getLogger().log(Level.INFO, "* Weight to trigger dripleaf: " + this.weightToTriggerDripleaf + ".");
        this.getLogger().log(Level.INFO, "* Weights: " + this.weights.size() + ".");
    }
    
    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Registering listener...");
        this.getServer().getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getSourceBlock();
        if (block.getType() != Material.BIG_DRIPLEAF) {
            return;
        }
        event.setCancelled(true);
        BigDripleaf blockData = (BigDripleaf) block.getBlockData();
        if (blockData.getTilt() != BigDripleaf.Tilt.UNSTABLE) {
            return;
        }
        Collection<Player> players = this.getPlayersOnBlock(block);
        if (players.isEmpty()) {
            return;
        }
        double weight = 0.0D;
        for (Player player : players) {
            weight += this.calculatePlayerWeight(player);
            if (this.calculateAllPlayers) {
                continue;
            }
            break;
        }
        if (Double.compare(weight, this.weightToTriggerDripleaf) < 0) {
            blockData.setTilt(BigDripleaf.Tilt.NONE);
            block.setBlockData(blockData);
        }
    }
    
    private Collection<Player> getPlayersOnBlock(Block block) {
        Collection<Player> players = new ArrayList<>(10);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.getCorrectDripleafBlockUnderPlayer(player, player.getLocation(), 0) == null && this.getCorrectDripleafBlockUnderPlayer(player, player.getLocation(), 1) == null) {
                continue;
            }
            players.add(player);
        }
        return Collections.unmodifiableCollection(players);
    }
    
    private double calculatePlayerWeight(Player player) {
        double weight = 0.0D;
        if (this.includeArmor) {
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item == null) {
                    continue;
                }
                if (!this.weights.containsKey(item.getType())) {
                    continue;
                }
                weight += this.weights.get(item.getType()) * item.getAmount();
            }
        }
        if (this.includeEquipment) {
            for (ItemStack item : player.getInventory().getStorageContents()) {
                if (item == null) {
                    continue;
                }
                if (!this.weights.containsKey(item.getType())) {
                    continue;
                }
                weight += this.weights.get(item.getType()) * item.getAmount();
            }
        }
        return weight;
    }
    
    private Block getCorrectDripleafBlockUnderPlayer(Player player, Location playerLocation, int depth) {
        double checkEmpty = 0.0D;
        double checkWidth = 0.4D;
        Location[] locations = new Location[9];
        locations[0] = playerLocation.clone().add(checkEmpty, -depth, checkEmpty);
        locations[1] = playerLocation.clone().add(checkWidth, -depth, checkEmpty);
        locations[2] = playerLocation.clone().add(checkWidth, -depth, checkWidth);
        locations[3] = playerLocation.clone().add(checkWidth, -depth, -checkWidth);
        locations[4] = playerLocation.clone().add(-checkWidth, -depth, checkEmpty);
        locations[5] = playerLocation.clone().add(-checkWidth, -depth, checkWidth);
        locations[6] = playerLocation.clone().add(-checkWidth, -depth, -checkWidth);
        locations[7] = playerLocation.clone().add(checkEmpty, -depth, checkWidth);
        locations[8] = playerLocation.clone().add(checkEmpty, -depth, -checkWidth);
        for (Location location : locations) {
            Block locationBlock = location.getBlock();
            Material locationBlockType = locationBlock.getType();
            if (locationBlockType.isAir()) {
                continue;
            }
            if (!locationBlockType.isBlock()) {
                continue;
            }
            if (locationBlockType != Material.BIG_DRIPLEAF) {
                continue;
            }
            return locationBlock;
        }
        return null;
    }
}
