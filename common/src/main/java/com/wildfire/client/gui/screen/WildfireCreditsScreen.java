/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wildfire.client.gui.screen;

import com.wildfire.client.gui.FakeGUIPlayer;
import com.wildfire.common.WildfireGender;
import com.wildfire.common.WildfireLang;
import com.wildfire.client.contributors.Contributor.Role;
import com.wildfire.client.contributors.Contributors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.UnknownNullability;

/// @apiNote Only use this on the client side
public class WildfireCreditsScreen extends BaseWildfireScreen {

    private static final Identifier CREDIT_CONTAINER = WildfireGender.id("credits/credit_container");
    private static final Identifier CREDIT_OUTLINE = WildfireGender.id("credits/credit_outline");
    private static final Identifier BUTTON_CONTAINER = WildfireGender.id("credits/button_container");
    private static final Identifier TAB_CONTAINER = WildfireGender.id("credits/tab_container");

    /** Page 1: project creator and the work done in this fork. */
    private final FakeGUIPlayer creator = new FakeGUIPlayer(
            "Dok_Si",
            Contributors.CREATOR_UUID,
            Component.literal("Автор переработки GenderForm\n\n"
                    + "Сделал nickname-привязку профилей, Paper/Folia-синхронизацию, "
                    + "формы Standard / Rounded / Natural / Triangular и общие женские hurt-звуки."),
            config -> {
                // Точные настройки из профиля nick_Dok_Si.json.
                config.gender.update(com.wildfire.api.Gender.FEMALE);
                config.breasts.xOffset().update(0.3F);
                config.breasts.yOffset().update(0.0F);
                config.breasts.zOffset().update(-0.75F);
                config.breasts.cleavage().update(0.0F);
                config.breasts.bustSize().update(0.94200003F);
                config.breasts.shape().update(com.wildfire.common.entitydata.BreastShape.NATURAL);
                config.breasts.physics().enabled().update(true);
                config.breasts.physics().bounceMultiplier().update(0.19520548F);
                config.breasts.physics().floppiness().update(0.42465752F);
                config.breasts.physics().uniboob().update(false);
                config.sounds.hurt().update(true);
                config.sounds.voicePitch().update(1.0F);
                config.showBreastsInArmor.update(true);
            });

    /** Page 2 is intentionally empty until people help test the mod. */
    private final FakeGUIPlayer[] TESTERS = new FakeGUIPlayer[0];

    private static final int BOXES_PER_PAGE = 12;
    private int creditsPage = 0;
    private int navigationY;

    public WildfireCreditsScreen(Screen parent, UUID uuid) {
        super(WildfireLang.CREDITS_TITLE.translate(), parent, uuid);
    }

    @Override
    public void init() {
        super.init();
        final var ref = new Object() {
            @UnknownNullability
            AbstractWidget prevPage, nextPage;
        };

        navigationY = this.height / 2 + 82;

        addButton(builder -> builder
                .message(WildfireLang.DETAILS_BACK::translate)
                .position(this.width / 2 - 25, navigationY + 6)
                .size(50, 13)
                .onPress(_ -> onClose()));

        ref.nextPage = addButton(builder -> builder
                .message(WildfireLang.DETAILS_NEXT_PAGE::translate)
                .position(this.width / 2 + 29, navigationY + 6)
                .size(60, 13)
                .active(creditsPage < getTotalPages() - 1)
                .onPress(_ -> {
                    if (creditsPage < getTotalPages() - 1) {
                        creditsPage++;
                    }
                    ref.prevPage.active = creditsPage != 0;
                    ref.nextPage.active = creditsPage < getTotalPages() - 1;
                }));

        ref.prevPage = addButton(builder -> builder
                .message(WildfireLang.DETAILS_PREV_PAGE::translate)
                .position(this.width / 2 - 89, navigationY + 6)
                .size(60, 13)
                .active(false)
                .onPress(_ -> {
                    if (creditsPage > 0) {
                        creditsPage--;
                    }
                    ref.prevPage.active = creditsPage != 0;
                    ref.nextPage.active = creditsPage < getTotalPages() - 1;
                }));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTransparentBackground(graphics);
    }

    @Override
    public void tick() {
        super.tick();
        if (creditsPage == 0) {
            creator.tick();
        } else {
            for (FakeGUIPlayer player : TESTERS) {
                player.tick();
            }
        }
    }

    private int getTotalPages() {
        int testerPages = Math.max(1, Mth.ceil((double) TESTERS.length / BOXES_PER_PAGE));
        return 1 + testerPages;
    }

    private FakeGUIPlayer[] getRenderedBoxes() {
        return creditsPage == 0 ? new FakeGUIPlayer[] { creator } : TESTERS;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        drawScrollingString(graphics, getTitle(), 0, height / 2 - 100, TextAlignment.CENTER, CommonColors.WHITE, graphics.guiWidth(), 5, false);
        drawScrollingString(graphics, WildfireLang.CREDITS_DESCRIPTION.translate(), 0, height / 2 - 85, TextAlignment.CENTER, 0xFF888888, graphics.guiWidth(), 5, false);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_CONTAINER, this.width / 2 - (190 / 2), navigationY, 190, 25);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TAB_CONTAINER, this.width / 2 - (190 / 2), navigationY + 28, 190, 25);

        int columns = 6;
        int boxW = 60;
        int boxH = 74;
        FakeGUIPlayer[] rendered = getRenderedBoxes();

        if (creditsPage > 0 && rendered.length == 0) {
            drawScrollingString(graphics, Component.literal("Тестировщики будут добавлены после тестирования GenderForm."),
                    0, height / 2 - 20, TextAlignment.CENTER, 0xFFAAAAAA, graphics.guiWidth() - 40, 5, false);
            super.extractRenderState(graphics, mouseX, mouseY, delta);
            return;
        }

        int startIndex = creditsPage == 0 ? 0 : (creditsPage - 1) * BOXES_PER_PAGE;
        int endIndex = Math.min(startIndex + BOXES_PER_PAGE, rendered.length);
        int startY = height / 2 - (2 * boxH) / 2 + 4;

        for (int i = startIndex; i < endIndex; i++) {
            var creditBox = rendered[i];
            int localIndex = i - startIndex;
            int col = localIndex % columns;
            int row = localIndex / columns;
            int remaining = Math.min(endIndex - startIndex - (row * columns), columns);
            int rowWidth = remaining * boxW;
            int startX = (width / 2) - (rowWidth / 2) + 4;
            int creditBoxX = startX + (col * boxW);
            int creditBoxY = startY + (row * boxH);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CREDIT_CONTAINER, creditBoxX, creditBoxY, 52, 68);
            Role role = creditBox.getRoleOrGeneric();
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CREDIT_OUTLINE, creditBoxX + 3, creditBoxY + 3, 46, 53,
                    ARGB.opaque(role.getColor().getValue()));

            int xP = creditBoxX + (52 / 2);
            int yP = creditBoxY + (68 / 2);
            var entity = creditBox.getEntity();
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, xP - 38, yP - 29, xP + 38, yP + 20,
                    40, getEntityScale(entity, 0.5F), mouseX, mouseY, entity);

            drawScaledScrollingString(graphics, Component.literal(creditBox.getName()), creditBoxX + 3, yP + 23,
                    TextAlignment.CENTER, CommonColors.WHITE, 46, 1, false, 0.55F);

            if (mouseX > xP - 24 && mouseX < xP + 23 && mouseY > yP + 22 && mouseY < yP + 31) {
                List<Component> txtList = new ArrayList<>();
                txtList.add(WildfireLang.GENERIC_DASH_EXPLANATION.translateColored(TextColor.DARK_GRAY,
                        role.withColor(Component.literal(creditBox.getName())),
                        role.withColor(role.shortName())));
                if (creditBox.getDescription() != null) {
                    txtList.add(creditBox.getDescription().copy().withColor(TextColor.GRAY));
                }
                graphics.setComponentTooltipForNextFrame(font, txtList, mouseX, mouseY);
            }
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }
}
