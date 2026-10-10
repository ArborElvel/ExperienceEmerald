package com.unddefined.experienceemerald.server.events;

import com.unddefined.experienceemerald.ExperienceEmerald;
import com.unddefined.experienceemerald.config.ModSettings;
import com.unddefined.experienceemerald.feedback.Feedback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.GrindstoneEvent;

/**
 * 砂轮安全返还：把经验绿宝石放进砂轮输入槽，输出槽显示普通绿宝石，取出时返还经验。
 * <p>
 * 用 NeoForge 官方的 {@link GrindstoneEvent}：
 * <ul>
 *   <li>{@code OnPlaceItem} 决定输出槽内容与经验量（原版算不出这种无附魔物品的产物）；</li>
 *   <li>{@code OnTakeItem} 在玩家取出产物时收尾：超出一组的绿宝石直接补发给玩家，
 *       并按 spawn_experience_orb 决定经验是变成经验球掉在砂轮旁、还是直接进玩家身上。</li>
 * </ul>
 * 输入槽里的经验绿宝石由 NeoForge 默认逻辑清空（事件的 newTop/newBottom 默认 EMPTY），
 * 所以不会出现复制物品或返还经验后物品还在的情况。
 */
public final class GrindstoneReturnHandler {

    /**
     * 只有当两个输入槽都为空、或者都是经验绿宝石时，本模组才接管。
     */
    private static boolean isPureInput(ItemStack stack) {
        return stack.isEmpty() || stack.is(ExperienceEmerald.EXPERIENCE_EMERALD.get());
    }

    private static int count(ItemStack stack) {
        return stack.is(ExperienceEmerald.EXPERIENCE_EMERALD.get()) ? stack.getCount() : 0;
    }

    /**
     * 输出槽预览：N 个经验绿宝石 -> N 个普通绿宝石 + N * return_experience 点经验。
     */
    public static void onPlaceItem(GrindstoneEvent.OnPlaceItem event) {
        ItemStack top = event.getTopItem();
        ItemStack bottom = event.getBottomItem();
        if (!isPureInput(top) || !isPureInput(bottom)) return;

        int total = count(top) + count(bottom);
        if (total <= 0) return;

        int emeraldStack = Math.min(total, Items.EMERALD.getDefaultMaxStackSize());
        event.setOutput(new ItemStack(Items.EMERALD, emeraldStack));
        event.setXp(ModSettings.RETURN_XP.get() * total);
    }

    /**
     * 玩家取出产物：补发溢出的绿宝石、决定经验是否落成经验球、加一点视觉反馈。
     */
    public static void onTakeItem(GrindstoneEvent.OnTakeItem event) {
        ItemStack top = event.getTopItem();
        ItemStack bottom = event.getBottomItem();
        if (!isPureInput(top) || !isPureInput(bottom)) return;

        int total = count(top) + count(bottom);
        if (total <= 0) return;

        Player player = event.getPlayer();
        int returned = ModSettings.RETURN_XP.get() * total;

        // 输出槽最多放一组绿宝石，多出来的直接进背包（满了就丢在脚下）。
        int overflow = total - Math.min(total, Items.EMERALD.getDefaultMaxStackSize());
        if (overflow > 0 && player != null) {
            ItemStack extra = new ItemStack(Items.EMERALD, overflow);
            if (!player.getInventory().add(extra)) player.drop(extra, false);
        }

        // spawn_experience_orb = false 时不走经验球，直接加到玩家身上。
        if (returned > 0 && !ModSettings.GRINDSTONE_SPAWN_ORB.get()) {
            if (player != null) player.giveExperiencePoints(returned);
            event.setXp(0);
        }

        ContainerLevelAccess access = event.getContainerAccess();
        access.execute((level, pos) -> {
            if (!level.isClientSide()) Feedback.grindstone(level, Vec3.atCenterOf(pos));
        });

    }

    private GrindstoneReturnHandler() {
    }
}
