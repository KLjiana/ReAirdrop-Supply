package net.mcbbs.uid1525632.airdropsupply.network;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.network.s2c.MapPointPacketS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AirdropSupply.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, MapPointPacketS2C.class, MapPointPacketS2C::encode, MapPointPacketS2C::decode, MapPointPacketS2C::handle);
    }
}
