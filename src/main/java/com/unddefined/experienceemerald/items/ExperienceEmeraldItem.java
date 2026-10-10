package com.unddefined.experienceemerald.items;

import com.unddefined.experienceemerald.ExperienceEmerald;
import com.unddefined.experienceemerald.config.ModSettings;
import com.unddefined.experienceemerald.server.events.GrindstoneReturnHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 经验绿宝石：吸收了 5 点经验的绿宝石核心。
 * <p>
 * 原版砂轮只接受「有耐久 / 有附魔」的物品，这里通过 NeoForge 的
 * {@link Item#canGrindstoneRepair(ItemStack)} 扩展点把它放进砂轮的输入槽，
 * 实际产物与返还经验由 {@link GrindstoneReturnHandler} 处理。
 */
public class ExperienceEmeraldItem extends Item {

    public ExperienceEmeraldItem(Properties properties) {
        super(properties);
    }

    /** 允许放进砂轮的输入槽（物品既没有耐久也没有附魔）。 */
    @Override
    public boolean canGrindstoneRepair(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip." + ExperienceEmerald.MOD_ID + ".infused_emerald",
                        ModSettings.RETURN_XP.get() * stack.getCount())
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip." + ExperienceEmerald.MOD_ID + ".infused_emerald.hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
