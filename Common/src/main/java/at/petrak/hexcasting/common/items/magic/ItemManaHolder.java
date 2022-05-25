package at.petrak.hexcasting.common.items.magic;

import at.petrak.hexcasting.api.item.ManaHolderItem;
import at.petrak.hexcasting.api.utils.ManaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ItemManaHolder extends Item implements ManaHolderItem {
    private static final String TAG_MANA = "hexcasting:mana";
    private static final String TAG_MAX_MANA = "hexcasting:start_mana";

    public ItemManaHolder(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack withMana(ItemStack stack, int mana, int maxMana) {
        Item item = stack.getItem();
        if (item instanceof ItemManaHolder) {
            NBTHelper.putInt(stack, TAG_MANA, mana);
            NBTHelper.putInt(stack, TAG_MAX_MANA, maxMana);
        }

        return stack;
    }

    @Override
    public int getMana(ItemStack stack) {
        return NBTHelper.getInt(stack, TAG_MANA);
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return NBTHelper.getInt(stack, TAG_MAX_MANA);
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        NBTHelper.putInt(stack, TAG_MANA, Mth.clamp(mana, 0, getMaxMana(stack)));
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return getMaxMana(pStack) > 0;
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        var mana = getMana(pStack);
        var maxMana = getMaxMana(pStack);
        return ManaHelper.manaBarColor(mana, maxMana);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        var mana = getMana(pStack);
        var maxMana = getMaxMana(pStack);
        return ManaHelper.manaBarWidth(mana, maxMana);
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
        TooltipFlag pIsAdvanced) {
        if (pIsAdvanced.isAdvanced() && getMaxMana(pStack) > 0) {
            pTooltipComponents.add(
                new TranslatableComponent("item.hexcasting.manaholder.amount",
                    String.format("%,d", getMana(pStack)),
                    String.format("%,d", getMaxMana(pStack)),
                    100f * getManaFullness(pStack)).withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
