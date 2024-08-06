package net.mcbbs.uid1525632.airdropsupply.entry;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundEvents {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AirdropSupply.MOD_ID);
    public static final RegistryObject<SoundEvent> OPEN_AIRDROP = register("open_airdrop");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AirdropSupply.MOD_ID, name)));
    }
}
