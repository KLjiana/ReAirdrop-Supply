package net.mcbbs.uid1525632.airdropsupply.misc;

import net.minecraftforge.common.ForgeConfigSpec;

public class Configuration {

    public static final ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.IntValue AIRDROP_SPREAD_RANGE;
    public static ForgeConfigSpec.IntValue AIRDROP_SPAWN_INTERVAL;
    public static ForgeConfigSpec.IntValue AIRDROP_DESPAWN_TIME;
    public static ForgeConfigSpec.IntValue MEDIC_AIRDROP_WEIGHT;
    public static ForgeConfigSpec.IntValue AMMO_AIRDROP_WEIGHT;
    public static ForgeConfigSpec.IntValue NO_AIRDROP_WEIGHT;
    public static ForgeConfigSpec.IntValue BASIC_BASE_WEIGHT;
    public static ForgeConfigSpec.IntValue BASIC_MULTIPLE_WEIGHT;
    public static ForgeConfigSpec.IntValue MEDIUM_BASE_WEIGHT;
    public static ForgeConfigSpec.IntValue MEDIUM_MULTIPLE_WEIGHT;
    public static ForgeConfigSpec.IntValue ADVANCED_BASE_WEIGHT;
    public static ForgeConfigSpec.IntValue ADVANCED_MULTIPLE_WEIGHT;


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("airdrop_spawn_setting");
        AIRDROP_SPREAD_RANGE = builder.defineInRange("AIRDROP_SPREAD_RANGE", 1500, 10, 10000);
        AIRDROP_SPAWN_INTERVAL = builder.comment("Airdrop spawn interval in tick.").defineInRange("AIRDROP_SPAWN_INTERVAL", 3 * 24000, 100, 30 * 24000);
        AIRDROP_DESPAWN_TIME = builder.comment("Airdrop despawn time in tick.").defineInRange("AIRDROP_DESPAWN_TIME", 24000, 100, 365 * 24000);
        builder.pop();

        builder.push("airdrop_type_weight");
        MEDIC_AIRDROP_WEIGHT = builder.defineInRange("MEDIC_AIRDROP_WEIGHT", 5, 0, 10000);
        AMMO_AIRDROP_WEIGHT = builder.defineInRange("AMMO_AIRDROP_WEIGHT", 3, 0, 10000);
        NO_AIRDROP_WEIGHT = builder.defineInRange("NO_AIRDROP_WEIGHT", 2, 0, 10000);
        builder.pop();

        builder.push("airdrop_level_weight");
        BASIC_BASE_WEIGHT = builder.defineInRange("BASIC_BASE_WEIGHT", 100, 0, 10000);
        BASIC_MULTIPLE_WEIGHT = builder.defineInRange("BASIC_MULTIPLE_WEIGHT", 0, 0, 10000);
        MEDIUM_BASE_WEIGHT = builder.defineInRange("MEDIUM_BASE_WEIGHT", -10, -10000, 10000);
        MEDIUM_MULTIPLE_WEIGHT = builder.defineInRange("MEDIUM_MULTIPLE_WEIGHT", 1, -10000, 10000);
        ADVANCED_BASE_WEIGHT = builder.defineInRange("ADVANCED_BASE_WEIGHT", -60, -10000, 10000);
        ADVANCED_MULTIPLE_WEIGHT = builder.defineInRange("ADVANCED_MULTIPLE_WEIGHT", 3, -10000, 10000);
        builder.pop();
        COMMON_CONFIG = builder.build();
    }
}
