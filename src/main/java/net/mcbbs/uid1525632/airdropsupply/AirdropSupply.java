package net.mcbbs.uid1525632.airdropsupply;

import net.mcbbs.uid1525632.airdropsupply.block.AirdropSupplyBlock;
import net.mcbbs.uid1525632.airdropsupply.capability.AirdropPlayerData;
import net.mcbbs.uid1525632.airdropsupply.capability.AirdropPlayerDataProvider;
import net.mcbbs.uid1525632.airdropsupply.command.CallingAirdropCommand;
import net.mcbbs.uid1525632.airdropsupply.entry.*;
import net.mcbbs.uid1525632.airdropsupply.misc.AirdropManager;
import net.mcbbs.uid1525632.airdropsupply.misc.Configuration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AirdropSupply.MOD_ID)
public class AirdropSupply
{
    public static final String MOD_ID = "airdrop_supply";

    public AirdropSupply()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModSoundEvents.SOUNDS.register(modEventBus);

        modEventBus.addListener(AirdropSupply::registerCapability);

        AirdropManager.register(forgeEventBus);

        forgeEventBus.addListener(AirdropSupply::registerCommand);
    }

    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.register(AirdropPlayerData.class);
    }

    public static void registerCommand(RegisterCommandsEvent event) {
        CallingAirdropCommand.register(event.getDispatcher());
    }

    public static class LootTables{

        public static ResourceLocation calculateLootTable(AirdropSupplyBlock.Type type, AirdropSupplyBlock.CaseLevel caseLevel){
            if(type== AirdropSupplyBlock.Type.NORMAL){
                return switch (caseLevel){
                    case BASIC -> BN;
                    case MEDIUM -> MN;
                    case ADVANCED -> AN;
                };
            } else{
                return switch (caseLevel){
                    case BASIC -> BM;
                    case MEDIUM -> MM;
                    case ADVANCED -> AM;
                };
            }
        }

        static final ResourceLocation BN = new ResourceLocation(MOD_ID,"ammo_basic");
        static final ResourceLocation MN = new ResourceLocation(MOD_ID,"ammo_medium");
        static final ResourceLocation AN = new ResourceLocation(MOD_ID,"ammo_advanced");
        static final ResourceLocation BM = new ResourceLocation(MOD_ID,"medic_basic");
        static final ResourceLocation MM = new ResourceLocation(MOD_ID,"medic_medium");
        static final ResourceLocation AM = new ResourceLocation(MOD_ID,"medic_advanced");
    }
}
