package com.unddefined.experienceemerald.feedback;

import com.unddefined.experienceemerald.config.ModSettings;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * 转化 / 返还时的粒子与音效反馈。只在服务端调用，由 NeoForge 广播给附近玩家。
 */
public final class Feedback {

    /** 经验球风格的亮绿色。 */
    private static final Vector3f XP_GREEN = new Vector3f(0.22F, 0.86F, 0.36F);

    /** 成功：绿色经验微粒 + 经验拾取音。 */
    public static void success(Level level, Vec3 center) {
        int count = ModSettings.PARTICLE_COUNT.get();
        if (count > 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new DustParticleOptions(XP_GREEN, 1.1F),
                    center.x, center.y, center.z, count,
                    0.18D, 0.16D, 0.18D, 0.02D);
        }
        if (ModSettings.ENABLE_SOUND.get()) {
            float pitch = 1.05F + level.random.nextFloat() * 0.25F;
            level.playSound(null, center.x, center.y, center.z,
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, pitch);
        }
    }

    /** 失败：低沉的失效音。 */
    public static void failure(Level level, Vec3 center) {
        if (ModSettings.ENABLE_SOUND.get()) {
            level.playSound(null, center.x, center.y, center.z,
                    SoundEvents.NOTE_BLOCK_BASS, SoundSource.PLAYERS, 0.7F, 0.45F);
        }
    }

    /** 砂轮返还：在砂轮位置补一小撮经验微粒（经验球本身由原版逻辑/直接加经验处理）。 */
    public static void grindstone(Level level, Vec3 center) {
        int count = Math.min(4, ModSettings.PARTICLE_COUNT.get());
        if (count > 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new DustParticleOptions(XP_GREEN, 0.9F),
                    center.x, center.y + 0.4D, center.z, count,
                    0.25D, 0.2D, 0.25D, 0.01D);
        }
    }

    private Feedback() {
    }
}
