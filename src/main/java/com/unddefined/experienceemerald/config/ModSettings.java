package com.unddefined.experienceemerald.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common 配置（config/experience_emerald-common.toml），NeoForge 会自动同步到客户端。
 * 数值都留了整合包可调的空间：吸收/返还经验量、副手开关、经验球优先、砂轮是否掉落经验球。
 */
public final class ModSettings {

    public static final ModConfigSpec SPEC;

    // ---- 转化（绿宝石 -> 经验绿宝石） ----
    public static final ModConfigSpec.IntValue ABSORB_XP;
    public static final ModConfigSpec.BooleanValue ALLOW_OFFHAND;
    public static final ModConfigSpec.IntValue COOLDOWN_TICKS;

    // ---- 经验球优先吸收 ----
    public static final ModConfigSpec.BooleanValue PRIORITY_ORBS;
    public static final ModConfigSpec.DoubleValue ORB_SCAN_RADIUS;

    // ---- 砂轮返还 ----
    public static final ModConfigSpec.IntValue RETURN_XP;
    public static final ModConfigSpec.BooleanValue GRINDSTONE_SPAWN_ORB;

    // ---- 反馈 ----
    public static final ModConfigSpec.BooleanValue ENABLE_SOUND;
    public static final ModConfigSpec.IntValue PARTICLE_COUNT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Experience emerald conversion (emerald -> experience emerald)").push("conversion");
        ABSORB_XP = builder
                .comment("How many experience points each emerald conversion absorbs from the player/orbs.")
                .defineInRange("absorb_experience", 5, 0, 10000);
        ALLOW_OFFHAND = builder
                .comment("Allow converting while the emerald is held in the off hand.")
                .define("allow_offhand", true);
        COOLDOWN_TICKS = builder
                .comment("Server-side cooldown between two batch conversions, in ticks (20 = 1 second).")
                .defineInRange("conversion_cooldown_ticks", 10, 0, 200);
        builder.pop();

        builder.comment("Picking up nearby experience orbs first").push("experience_orbs");
        PRIORITY_ORBS = builder
                .comment("Absorb loose experience orbs around the player before touching the player's own experience.")
                .define("prioritize_orbs", true);
        ORB_SCAN_RADIUS = builder
                .comment("Radius in blocks used to look for experience orbs.")
                .defineInRange("orb_scan_radius", 3.0D, 0.0D, 16.0D);
        builder.pop();

        builder.comment("Grindstone safe return (experience emerald -> emerald)").push("grindstone");
        RETURN_XP = builder
                .comment("Experience points handed back per experience emerald ground in a grindstone. "
                        + "0 means the emerald comes back without experience.")
                .defineInRange("return_experience", 5, 0, 10000);
        GRINDSTONE_SPAWN_ORB = builder
                .comment("True: the returned experience pops out as experience orbs at the grindstone. "
                        + "False: the experience goes straight into the player.")
                .define("spawn_experience_orb", true);
        builder.pop();

        builder.comment("Sound and particle feedback").push("feedback");
        ENABLE_SOUND = builder
                .comment("Play the pickup / failure sounds.")
                .define("enable_sound", true);
        PARTICLE_COUNT = builder
                .comment("How many green experience motes are spawned on success (0 disables particles).")
                .defineInRange("particle_count", 5, 0, 64);
        builder.pop();

        SPEC = builder.build();
    }

    private ModSettings() {
    }
}
