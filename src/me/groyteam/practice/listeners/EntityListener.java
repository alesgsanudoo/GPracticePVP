package me.groyteam.practice.listeners;

import me.groyteam.practice.Practice;
import me.groyteam.practice.events.PracticeEvent;
import me.groyteam.practice.events.oitc.OITCEvent;
import me.groyteam.practice.events.oitc.OITCPlayer;
import me.groyteam.practice.events.parkour.ParkourEvent;
import me.groyteam.practice.events.redrover.RedroverEvent;
import me.groyteam.practice.events.redrover.RedroverPlayer;
import me.groyteam.practice.events.sumo.SumoEvent;
import me.groyteam.practice.events.sumo.SumoPlayer;
import me.groyteam.practice.match.Match;
import me.groyteam.practice.match.MatchState;
import me.groyteam.practice.player.PlayerData;
import me.groyteam.practice.player.PlayerState;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.GameMode;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.Listener;

public class EntityListener implements Listener
{
    private final Practice plugin;
    
    public EntityListener() {
        this.plugin = Practice.getInstance();
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player)e.getEntity();
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            switch (playerData.getPlayerState()) {
                case FIGHTING: {
                    final Match match = this.plugin.getMatchManager().getMatch(playerData);
                    if (match.getMatchState() != MatchState.FIGHTING) {
                        e.setCancelled(true);
                    }
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        this.plugin.getMatchManager().removeFighter(player, playerData, true);
                    }
                    if (match.getKit().isParkour()) {
                        e.setCancelled(true);
                        break;
                    }
                    break;
                }
                case EVENT: {
                    final PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
                    if (event == null) {
                        break;
                    }
                    if (event instanceof SumoEvent) {
                        final SumoEvent sumoEvent = (SumoEvent)event;
                        final SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);
                        if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayer.SumoState.FIGHTING) {
                            e.setCancelled(false);
                            break;
                        }
                        break;
                    }
                    else if (event instanceof OITCEvent) {
                        final OITCEvent oitcEvent = (OITCEvent)event;
                        final OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
                        if (oitcPlayer != null && oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING && e.getCause() != EntityDamageEvent.DamageCause.FALL) {
                            e.setCancelled(false);
                            break;
                        }
                        e.setCancelled(true);
                        break;
                    }
                    else {
                        if (event instanceof ParkourEvent) {
                            e.setCancelled(true);
                            break;
                        }
                        break;
                    }
                }
                default: {
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
                    }
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            return;
        }
        final Player entity = (Player)e.getEntity();
        Player damager;
        if (e.getDamager() instanceof Player) {
            damager = (Player)e.getDamager();
        }
        else {
            if (!(e.getDamager() instanceof Projectile)) {
                return;
            }
            damager = (Player)((Projectile)e.getDamager()).getShooter();
        }
        final PlayerData entityData = this.plugin.getPlayerManager().getPlayerData(entity.getUniqueId());
        final PlayerData damagerData = this.plugin.getPlayerManager().getPlayerData(damager.getUniqueId());
        if (entityData == null || damagerData == null) {
            e.setCancelled(true);
            return;
        }
        final boolean isEventEntity = this.plugin.getEventManager().getEventPlaying(entity) != null;
        final boolean isEventDamager = this.plugin.getEventManager().getEventPlaying(damager) != null;
        final PracticeEvent eventDamager = this.plugin.getEventManager().getEventPlaying(damager);
        final PracticeEvent eventEntity = this.plugin.getEventManager().getEventPlaying(entity);
        if (damagerData.getPlayerState() == PlayerState.SPECTATING || this.plugin.getEventManager().getSpectators().containsKey(damager.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        if ((!entity.canSee(damager) && damager.canSee(entity)) || damager.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
            return;
        }
        if ((isEventDamager && eventDamager instanceof ParkourEvent) || (isEventEntity && eventEntity instanceof ParkourEvent)) {
            e.setCancelled(true);
            return;
        }
        if ((isEventDamager && eventDamager instanceof RedroverEvent && ((RedroverEvent)eventDamager).getPlayer(damager).getState() != RedroverPlayer.RedroverState.FIGHTING) || (isEventEntity && eventDamager instanceof RedroverEvent && ((RedroverEvent)eventEntity).getPlayer(entity).getState() != RedroverPlayer.RedroverState.FIGHTING) || (!isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING) || (!isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING)) {
            e.setCancelled(true);
            return;
        }
        if ((isEventDamager && eventDamager instanceof SumoEvent && ((SumoEvent)eventDamager).getPlayer(damager).getState() != SumoPlayer.SumoState.FIGHTING) || (isEventEntity && eventDamager instanceof SumoEvent && ((SumoEvent)eventEntity).getPlayer(entity).getState() != SumoPlayer.SumoState.FIGHTING) || (!isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING) || (!isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING)) {
            e.setCancelled(true);
            return;
        }
        if ((isEventDamager && eventDamager instanceof OITCEvent) || (isEventEntity && eventEntity instanceof OITCEvent) || (!isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING) || (!isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING)) {
            if (isEventEntity && isEventDamager && eventEntity instanceof OITCEvent && eventDamager instanceof OITCEvent) {
                final OITCEvent oitcEvent = (OITCEvent)eventDamager;
                final OITCPlayer oitcKiller = oitcEvent.getPlayer(damager);
                final OITCPlayer oitcPlayer = oitcEvent.getPlayer(entity);
                if (oitcKiller.getState() != OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() != OITCPlayer.OITCState.FIGHTING) {
                    e.setCancelled(true);
                    return;
                }
                if (e.getDamager() instanceof Arrow) {
                    final Arrow arrow = (Arrow)e.getDamager();
                    if (arrow.getShooter() instanceof Player && damager != entity) {
                        oitcPlayer.setLastKiller(oitcKiller);
                        e.setDamage(0.0);
                        eventEntity.onDeath().accept(entity);
                    }
                }
            }
            return;
        }
        if ((entityData.getPlayerState() == PlayerState.EVENT && eventEntity instanceof SumoEvent) || (damagerData.getPlayerState() == PlayerState.EVENT && eventDamager instanceof SumoEvent)) {
            e.setDamage(0.0);
            return;
        }
        if ((entityData.getPlayerState() == PlayerState.EVENT && eventEntity instanceof RedroverEvent) || (damagerData.getPlayerState() == PlayerState.EVENT && eventDamager instanceof RedroverEvent)) {
            return;
        }
        final Match match = this.plugin.getMatchManager().getMatch(entityData);
        if (match == null) {
            e.setDamage(0.0);
            return;
        }
        if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
            e.setCancelled(true);
            return;
        }
        if (match.getKit().isParkour()) {
            e.setCancelled(true);
            return;
        }
        if (match.getKit().isSpleef() || match.getKit().isSumo()) {
            e.setDamage(0.0);
        }
        if (e.getDamager() instanceof Player) {
            damagerData.setCombo(damagerData.getCombo() + 1);
            damagerData.setHits(damagerData.getHits() + 1);
            if (damagerData.getCombo() > damagerData.getLongestCombo()) {
                damagerData.setLongestCombo(damagerData.getCombo());
            }
            entityData.setCombo(0);
            if (match.getKit().isSpleef()) {
                e.setCancelled(true);
            }
        }
        else if (e.getDamager() instanceof Arrow) {
            final Arrow arrow2 = (Arrow)e.getDamager();
            if (arrow2.getShooter() instanceof Player) {
                final Player shooter = (Player)arrow2.getShooter();
                if (!entity.getName().equals(shooter.getName())) {
                    final double health = Math.ceil(entity.getHealth() - e.getFinalDamage()) / 2.0;
                    if (health > 0.0) {
                        shooter.sendMessage(ChatColor.WHITE + "El jugador " + entity.getName() + ChatColor.WHITE + " ha recibido un disparo y ahora esta a: " + ChatColor.RED + health + ChatColor.WHITE + ".");
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPotionSplash(final PotionSplashEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        for (final PotionEffect effect : e.getEntity().getEffects()) {
            if (effect.getType().equals(PotionEffectType.HEAL)) {
                final Player shooter = (Player)e.getEntity().getShooter();
                if (e.getIntensity(shooter) > 0.5) {
                    break;
                }
                final PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
                if (shooterData != null) {
                    shooterData.setMissedPots(shooterData.getMissedPots() + 1);
                    break;
                }
                break;
            }
        }
    }
}
