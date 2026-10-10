package com.unddefined.experienceemerald.server.events;

import com.unddefined.experienceemerald.ExperienceEmerald;
import com.unddefined.experienceemerald.config.ModSettings;
import com.unddefined.experienceemerald.feedback.Feedback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心转化：手持绿宝石右击，按当前可用经验一次性转换尽可能多的绿宝石。
 * <ul>
 *   <li>每颗绿宝石消耗 absorb_experience（默认 5）点经验，最多转换到手持堆叠数量；</li>
 *   <li>先吸周围的经验球（prioritize_orbs，半径 orb_scan_radius，默认 3 格），不足的部分再扣玩家自身经验；</li>
 *   <li>总量不够转换一颗时只播放低沉失效音并提示差额，不消耗任何经验和物品；</li>
 *   <li>能转换一部分时按可负担数量批量转换，剩余绿宝石留在手上；</li>
 *   <li>conversion_cooldown_ticks 是服务端节流，避免长按右键把经验瞬间抽干。</li>
 * </ul>
 */
public final class XpConversionHandler {

    /**
     * 玩家 UUID -> 上次成功/失败转化的游戏刻，用于服务端节流。
     */
    private static final Map<UUID, Long> LAST_USE = new HashMap<>();

    private record NearbyExperience(List<ExperienceOrb> orbs, long orbXp) {
    }

    private record ConversionPlan(int itemCount, int xpCost) {

        private boolean canConvert() {
            return itemCount > 0;
        }
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        ItemStack held = event.getItemStack();
        if (!held.is(Items.EMERALD)) return;

        Player player = event.getEntity();
        InteractionHand usedHand = event.getHand();
        if (usedHand == InteractionHand.OFF_HAND && !ModSettings.ALLOW_OFFHAND.get()) return;

        int cost = ModSettings.ABSORB_XP.get();
        if (cost <= 0) return;

        long now = level.getGameTime();
        long cooldown = ModSettings.COOLDOWN_TICKS.get();
        Long last = LAST_USE.get(player.getUUID());
        if (last != null && now - last < cooldown) return;

        Vec3 focus = particleAnchor(player);

        long requiredXp = (long) held.getCount() * cost;
        NearbyExperience nearby = findNearbyExperience(player, requiredXp);
        long availableXp = nearby.orbXp() + player.totalExperience;
        ConversionPlan plan = planConversion(held, cost, availableXp);
        if (!plan.canConvert()) {
            failConversion(player, level, focus, now, (int) (cost - availableXp));
            return;
        }

        absorbExperience(nearby.orbs(), player, plan.xpCost());
        replaceConvertedItems(player, usedHand, held, plan.itemCount());

        player.swing(usedHand, true);
        LAST_USE.put(player.getUUID(), now);
        Feedback.success(level, focus);
        showSuccessMessage(player, plan);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(false));
    }

    /**
     * 扫描玩家周围的经验球，只累加到本次批量转化需要的上限。
     */
    private static NearbyExperience findNearbyExperience(Player player, long requiredXp) {
        if (!ModSettings.PRIORITY_ORBS.get() || requiredXp <= 0) {
            return new NearbyExperience(List.of(), 0L);
        }

        double radius = ModSettings.ORB_SCAN_RADIUS.get();
        AABB box = player.getBoundingBox().inflate(radius);
        List<ExperienceOrb> orbs = new ArrayList<>(player.level().getEntities(
                EntityTypeTest.forClass(ExperienceOrb.class), box,
                orb -> !orb.isRemoved() && orb.value > 0));
        orbs.sort(Comparator.comparingDouble(orb -> orb.distanceToSqr(player)));

        long orbXp = 0L;
        for (ExperienceOrb orb : orbs) {
            orbXp += orb.value;
            if (orbXp >= requiredXp) break;
        }
        return new NearbyExperience(orbs, orbXp);
    }

    /**
     * 按可用经验计算本次可转换数量，并保证经验总消耗不会整数溢出。
     */
    private static ConversionPlan planConversion(ItemStack held, int cost, long availableXp) {
        long maxByExperience = availableXp / cost;
        long maxByInt = Integer.MAX_VALUE / cost;
        int itemCount = (int) Math.min(Math.min(held.getCount(), maxByExperience), maxByInt);
        if (itemCount <= 0) return new ConversionPlan(0, 0);

        return new ConversionPlan(itemCount, itemCount * cost);
    }

    /**
     * 吸收经验：经验球优先，剩余部分扣玩家经验。
     */
    private static void absorbExperience(List<ExperienceOrb> orbs, Player player, int xpCost) {
        int missing = xpCost;
        for (ExperienceOrb orb : orbs) {
            if (missing <= 0) break;
            if (orb.isRemoved()) continue;

            int taken = Math.min(orb.value, missing);
            orb.value -= taken;
            missing -= taken;
            if (orb.value <= 0) orb.discard();
        }
        if (missing > 0) player.giveExperiencePoints(-missing);
    }

    /**
     * N 个绿宝石 -> N 个经验绿宝石，产物优先放回空出的手持槽。
     */
    private static void replaceConvertedItems(Player player, InteractionHand usedHand, ItemStack held, int converted) {
        held.shrink(converted);
        ItemStack result = new ItemStack(ExperienceEmerald.EXPERIENCE_EMERALD.get(), converted);
        if (held.isEmpty()) player.setItemInHand(usedHand, result);
        else if (!player.getInventory().add(result)) player.drop(result, false);

    }

    private static void showSuccessMessage(Player player, ConversionPlan plan) {
        if (plan.itemCount() > 1) {
            player.displayClientMessage(Component.translatable("msg." + ExperienceEmerald.MOD_ID + ".absorbed_batch",
                    plan.xpCost(), plan.itemCount()), true);
        } else {
            player.displayClientMessage(Component.translatable("msg." + ExperienceEmerald.MOD_ID + ".absorbed",
                    plan.xpCost()), true);
        }
    }

    private static void failConversion(Player player, Level level, Vec3 focus, long now, int missingXp) {
        LAST_USE.put(player.getUUID(), now);
        Feedback.failure(level, focus);
        player.displayClientMessage(Component.translatable("msg." + ExperienceEmerald.MOD_ID + ".xp_insufficient",
                missingXp), true);
    }

    /**
     * 粒子/音效的锚点：手上那只绿宝石的视线前方。
     */
    private static Vec3 particleAnchor(Player player) {
        return player.getEyePosition().add(player.getLookAngle().scale(0.5D));
    }

    public static void onLogout(Player player) {
        LAST_USE.remove(player.getUUID());
    }

    private XpConversionHandler() {
    }
}
