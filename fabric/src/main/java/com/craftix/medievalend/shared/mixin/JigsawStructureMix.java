package com.craftix.medievalend.shared.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;


@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMix {

    @Shadow
    @Final
    private Holder<StructureTemplatePool> startPool;

    @Inject(method = "findGenerationPoint", at = @At("RETURN"), cancellable = true)
    private void getStructurePosition(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        String string = startPool.toString();
        if (string.contains("medievalend")) {
            var position = cir.getReturnValue();
            if (position.isEmpty()) {
                return;
            }
            BlockPos pos = position.get().position();

            if (pos.getY() < 40) {
                cir.setReturnValue(Optional.empty());
                return;
            }


            if (string.contains("pyramid") || string.contains("castle")) {
                BlockPos centerAtY = context.chunkPos().getMiddleBlockPosition(0);
                for (int i = 1; i <= 2; i++) {
                    for (var direction2 : Direction.values()) {
                        if (direction2 == Direction.DOWN) continue;
                        for (var direction1 : Direction.values()) {
                            if (direction1 == Direction.DOWN) continue;
                            if (direction1 == direction2) continue;
                            var sample = context.chunkGenerator().getBaseColumn(
                                    centerAtY.getX() + i * 16 * (direction1.getNormal().getX() + direction2.getNormal().getX()),
                                    centerAtY.getZ() + i * 16 * (direction1.getNormal().getZ() + direction2.getNormal().getZ()),
                                    context.heightAccessor(), context.randomState());

                            var topY = 75;
                            while (topY > 0) {
                                var state = sample.getBlock(topY);
                                if (state.isAir()) {
                                    topY--;
                                } else if (state.is(Blocks.WATER)) {
                                    cir.setReturnValue(Optional.empty());
                                    return;
                                } else {
                                    break;
                                }
                            }

                            if (Math.abs(pos.getY() - topY) > 5) {
                                cir.setReturnValue(Optional.empty());
                                return;
                            }

                        }
                    }
                }
            }
        }
    }
}





