package shukaro.questlog.gui;

import cofh.lib.util.helpers.ColorHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import shukaro.questlog.QuestConfig;
import shukaro.questlog.data.PlayerData;
import shukaro.questlog.data.questing.AbstractObjective;
import shukaro.questlog.data.questing.QuestManager;


public class GuiTracking extends Gui
{
    private Minecraft mc;

    public GuiTracking()
    {
        mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT)
            return;

        int x = QuestConfig.questlogXPos;
        int y = QuestConfig.questlogYPos;
        for (String questUID : PlayerData.getTrackedQuests(SecurityHelper.getID(mc.thePlayer)))
        {
            this.drawString(mc.fontRenderer, StringHelper.localize(StringHelper.YELLOW + "quest.questlog." + questUID + ".name" + StringHelper.END), x, y, ColorHelper.DYE_WHITE);
            y += mc.fontRenderer.FONT_HEIGHT;
            for (AbstractObjective ao : QuestManager.getRunningObjectivesForPlayer(SecurityHelper.getID(mc.thePlayer), questUID))
            {
                this.drawString(mc.fontRenderer, (ao.isFulfilled ? StringHelper.GRAY : StringHelper.WHITE) + ao.getLocalizedText() + StringHelper.END, x, y, ColorHelper.DYE_WHITE);
                y += mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }
}
