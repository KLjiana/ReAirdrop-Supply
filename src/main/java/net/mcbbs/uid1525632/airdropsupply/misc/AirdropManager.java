package net.mcbbs.uid1525632.airdropsupply.misc;

import net.mcbbs.uid1525632.airdropsupply.capability.AirdropPlayerData;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.LinkedList;
import java.util.Queue;

public class AirdropManager {

    private static final Queue<AirdropMission> queue = new LinkedList<>();

    public static void register(IEventBus eventBus){
        eventBus.addListener(AirdropManager::tickPlayer);
        eventBus.addListener(AirdropManager::tickWorld);
    }

    private static void tickPlayer(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START && !event.player.level.isClientSide()){
            var player = (ServerPlayer) event.player;
            var data = player.getCapability(AirdropPlayerData.CAPABILITY);
            data.ifPresent(cap->{
                var overworld = player.level.getServer().overworld();
                // Check despawn
                var it = cap.airdropDespawnInfo.iterator();
                while (it.hasNext()){
                    var p = it.next();
                    if(overworld.getGameTime()>=p.getFirst()){
                        // Send despawn message
                        var pos = p.getSecond();
                        player.sendMessage(
                                new TranslatableComponent("notification.airdrop_supply.airdrop_invalidate",pos.getX(),pos.getY(),pos.getZ(),player.getScoreboardName()),
                                player.getUUID());
                        it.remove();
                    }
                }

                // Summon airdrop
                if(cap.nextAirdropCountdown==0){
                    queue.add(new AirdropMission(player));
                    cap.nextAirdropCountdown = Configuration.AIRDROP_SPAWN_INTERVAL.get();
                }
                cap.nextAirdropCountdown--;
            });
        }
    }

    private static void tickWorld(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            if(!queue.isEmpty()){
                if(queue.peek().done()){
                    queue.poll();
                } else queue.peek().run();
            }
        }
    }

}
