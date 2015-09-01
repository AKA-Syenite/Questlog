package shukaro.questlog.data.questing.objectives;

import cofh.core.util.CoreUtils;
import cofh.lib.inventory.IInventoryManager;
import cofh.lib.inventory.InventoryManager;
import cofh.lib.util.helpers.StringHelper;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ForgeDirection;
import shukaro.questlog.QuestUtil;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.QuestData;
import shukaro.questlog.data.questing.AbstractObjective;
import shukaro.questlog.data.questing.QuestManager;

public class ObjectiveHaveItem extends AbstractObjective
{
    IInventoryManager inv;
    private ItemStack stack;
    private int amount;

    private int had = 0;

    @Override
    public AbstractObjective init(String[] args)
    {
        stack = QuestUtil.stackFromString(args[0]);
        try
        {
            amount = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void start()
    {
        EntityPlayer player = QuestUtil.getPlayerByUUID(parentUUID);
        if (player != null)
        {
            inv = InventoryManager.create(player.inventory, ForgeDirection.UNKNOWN);
            FMLCommonHandler.instance().bus().register(this);
        }
    }

    @Override
    public void stop()
    {
        if (inv != null)
            FMLCommonHandler.instance().bus().unregister(this);
    }

    @Override
    public String saveToString()
    {
        return "haveItem(" + QuestUtil.stackToString(stack) + "," + amount + ")";
    }

    @Override
    public String getLocalizedText()
    {
        return StringHelper.localize("objective.questlog.collect") + " " + stack.getDisplayName() + " " + inv.hasItem(stack) + "/" + amount;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e)
    {
        if (e.phase != TickEvent.Phase.END || e.player.worldObj.getTotalWorldTime() % 20 != 0)
            return;
        int have = inv.hasItem(stack);
        if (have != had)
        {
            PlayerData.updateObjectives(parentUUID, parentQuest);
            isFulfilled = have >= amount;
        }
        if (isFulfilled && QuestData.hasTag(parentQuest, "autocomplete"))
            QuestManager.tryComplete(parentUUID, parentQuest);
        had = have;
    }
}
