package com.teamwizardry.refraction.common.item.armor;

import com.teamwizardry.librarianlib.common.base.item.ItemModArmor;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public class ItemReflectiveAlloyHelmet extends ItemModArmor implements ReflectiveAlloyArmor {

    public ItemReflectiveAlloyHelmet() {
        super("ref_alloy_helmet", ArmorMaterial.GOLD, EntityEquipmentSlot.HEAD);
        setMaxStackSize(1);
        setMaxDamage(0);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemstack = playerIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        if (itemstack == null) {
            playerIn.setItemStackToSlot(EntityEquipmentSlot.HEAD, itemStackIn.copy());
            itemStackIn.stackSize = 0;
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        } else {
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        }
    }

    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return new ResourceLocation(Refraction.MOD_ID, "textures/items/reflective_alloy_helmet.png").toString();
    }
}
