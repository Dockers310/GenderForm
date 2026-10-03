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

package com.wildfire.fabric.client.mixins;

//~ !entity_types
import com.wildfire.client.WildfireClientEventHandler;
import com.wildfire.common.networking.WildfireSync;
import net.minecraft.client.Minecraft;
import com.wildfire.common.WildfireGender;
import com.wildfire.common.entitydata.PlayerConfigHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// @apiNote Only applied on the client side
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    /**
     * Replaces the vanilla hurt sound for female/other players with the GenderForm sound.
     * The redirect is important: a normal inject would leave the vanilla sound playing as well.
     */
    @org.spongepowered.asm.mixin.injection.Redirect(
        method = "handleDamageEvent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
        )
    )
    private void replaceGenderHurtSound(LivingEntity entity, net.minecraft.sounds.SoundEvent vanillaSound, float volume, float pitch) {
        if (entity instanceof Player player && player.level().isClientSide()) {
            PlayerConfigHolder config = WildfireGender.getPlayerByPlayer(player);
            if (config != null && config.canPlayHurtSound()) {
                config.tryPlayHurtSound(player);

                // Only the local player's event needs to travel to the server.
                if (player == Minecraft.getInstance().player) {
                    var connection = Minecraft.getInstance().getConnection();
                    if (connection != null &&
                            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(
                                    com.wildfire.common.networking.packets.sound.ServerboundHurtSoundPacket.TYPE)) {
                        WildfireSync.sendHurtSoundToServer(connection.getConnection());
                    }
                }
                return;
            }
        }

        // Male players and disabled sound replacement keep the vanilla sound.
        entity.playSound(vanillaSound, volume, pitch);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if(!level().isClientSide()) return; // ignore ticks from the singleplayer integrated server
        //Note that this event may not be consistently invoked for every entity, such as if other mods (e.g. EntityCulling) cancel the entity tick.
        WildfireClientEventHandler.onEntityTick((LivingEntity)(Object)this);
    }
}
