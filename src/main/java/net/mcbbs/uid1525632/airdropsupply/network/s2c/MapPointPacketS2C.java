package net.mcbbs.uid1525632.airdropsupply.network.s2c;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.common.settings.ModSettings;
import xaero.minimap.XaeroMinimap;

import java.io.IOException;
import java.util.function.Supplier;


public record MapPointPacketS2C(BlockPos dropPos) {
    public static MapPointPacketS2C sentPos(BlockPos dropPos) {
        return new MapPointPacketS2C(dropPos);
    }

    public static void encode(MapPointPacketS2C packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(packet.dropPos);
    }

    public static MapPointPacketS2C decode(FriendlyByteBuf friendlyByteBuf) {
        return new MapPointPacketS2C(friendlyByteBuf.readBlockPos());
    }

    public static void handle(MapPointPacketS2C packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (ModList.get().isLoaded("xaerominimap")) {
                LocalPlayer localPlayer = Minecraft.getInstance().player;
                IXaeroMinimapClientPlayNetHandler clientLevel = (IXaeroMinimapClientPlayNetHandler) (Minecraft.getInstance().player.connection);
                XaeroMinimapSession session = clientLevel.getXaero_minimapSession();
                WaypointsManager waypointsManager = session.getWaypointsManager();
                Waypoint instant = new Waypoint(
                        packet.dropPos.getX(),
                        packet.dropPos.getY(),
                        packet.dropPos.getZ(),
                        Component.translatable("container.airdrop_supply.airdrop_supply").getString(),
                        Component.translatable("container.airdrop_supply.airdrop_supply").getString().substring(0, 1),
                        (int) (Math.random() * ModSettings.ENCHANT_COLORS.length)
                );
                waypointsManager.getWaypoints().getList().add(instant);
                localPlayer.sendSystemMessage(Component.translatable("notification.airdrop_supply.map_point"));
                try {
                    XaeroMinimap.instance.getSettings().saveWaypoints(waypointsManager.getCurrentWorld());
                } catch (IOException e) {
                    AirdropSupply.LOGGER.error(String.format("Error occurred while saving Xaeros' Minimap waypoints for current world: %s", e.getMessage()));
                }
            }
        });
        context.setPacketHandled(true);
    }
}
