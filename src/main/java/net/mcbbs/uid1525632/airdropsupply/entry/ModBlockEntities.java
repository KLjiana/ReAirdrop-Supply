package net.mcbbs.uid1525632.airdropsupply.entry;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.blockentity.AirdropSupplyBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AirdropSupply.MOD_ID);
    public static final RegistryObject<BlockEntityType<AirdropSupplyBlockEntity>> AIRDROP_SUPPLY = BLOCK_ENTITIES.register("airdrop_supply", () ->
            BlockEntityType.Builder.of(AirdropSupplyBlockEntity::new, ModBlocks.AIRDROP_SUPPLY.get()).build(null));

}
