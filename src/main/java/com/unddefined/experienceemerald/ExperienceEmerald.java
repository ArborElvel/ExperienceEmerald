package com.unddefined.experienceemerald;

import com.unddefined.experienceemerald.config.ModSettings;
import com.unddefined.experienceemerald.items.ExperienceEmeraldItem;
import com.unddefined.experienceemerald.server.events.GrindstoneReturnHandler;
import com.unddefined.experienceemerald.server.events.XpConversionHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 经验绿宝石（NeoForge 1.21.1）。
 * <p>
 * 玩法闭环：
 * <ol>
 *   <li>手持绿宝石右击 -> 按可用经验批量吸收（先吸身边 3 格内的经验球，不够再扣玩家自身）-> 尽可能多的绿宝石变成经验绿宝石；</li>
 *   <li>经验绿宝石放进砂轮 -> 输出槽显示普通绿宝石，取出时返还经验（默认以经验球形式掉在砂轮旁）；</li>
 *   <li>全程带绿色经验粒子与经验拾取音，失败只有低沉失效音、不扣任何东西；</li>
 *   <li>数值全部走 common 配置，见 {@link ModSettings}。</li>
 * </ol>
 */
@Mod(ExperienceEmerald.MOD_ID)
public final class ExperienceEmerald {

    public static final String MOD_ID = "experience_emerald";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    /**
     * 经验绿宝石：轻量附魔光效来自 ENCHANTMENT_GLINT_OVERRIDE 组件，物品本身不需要任何附魔。
     */
    public static final DeferredItem<Item> EXPERIENCE_EMERALD = ITEMS.register("infused_emerald",
            () -> new ExperienceEmeraldItem(new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, Boolean.TRUE)));

    public ExperienceEmerald(IEventBus modBus, Dist dist, ModContainer container) {
        ITEMS.register(modBus);

        container.registerConfig(ModConfig.Type.COMMON, ModSettings.SPEC);

        modBus.addListener(ExperienceEmerald::buildCreativeTab);

        NeoForge.EVENT_BUS.addListener(XpConversionHandler::onRightClickItem);
        NeoForge.EVENT_BUS.addListener(GrindstoneReturnHandler::onPlaceItem);
        NeoForge.EVENT_BUS.addListener(GrindstoneReturnHandler::onTakeItem);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent event) ->
                XpConversionHandler.onLogout(event.getEntity()));
    }

    /**
     * 经验绿宝石放进「材料」创造页（原版绿宝石所在的页签）。
     */
    private static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(new ItemStack(EXPERIENCE_EMERALD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
