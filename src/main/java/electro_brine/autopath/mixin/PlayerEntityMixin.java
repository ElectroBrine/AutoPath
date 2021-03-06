
package electro_brine.autopath.mixin;

import mrnavastar.kingdoms.types.Claim;
import mrnavastar.kingdoms.types.Kingdom;
import mrnavastar.kingdoms.util.AdminClaimManager;
import mrnavastar.kingdoms.util.KingdomsManager;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract float getBlockBreakingSpeed(BlockState block);

    private BlockPos playerPos = this.getBlockPos();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    public boolean isInClaim(BlockPos pos) {
        if (world.getRegistryKey().getValue().toString().equals("minecraft:overworld")) {
            for (Kingdom kingdom : KingdomsManager.getKingdoms()) {
                for (Claim claim : kingdom.getOverworldClaims()) {
                    return claim.isInClaim(pos);
                }
            }
            for (Claim claim : AdminClaimManager.overworldClaims) {
                return claim.isInClaim(pos);
            }
        }

        if (world.getRegistryKey().getValue().toString().equals("minecraft:the_nether")) {
            for (Kingdom kingdom : KingdomsManager.getKingdoms()) {
                for (Claim claim : kingdom.getNetherClaims()) {
                    return claim.isInClaim(pos);
                }
            }
            for (Claim claim : AdminClaimManager.netherClaims) {
                return claim.isInClaim(pos);
            }
        }

        return false;
    }

    public void Block() {
        ServerWorld world = (ServerWorld) this.world;
        Block playerBlock = world.getBlockState(this.getBlockPos()).getBlock();
        BlockPos playBlockPos = new BlockPos(this.getBlockPos().getX(), this.getBlockPos().getY() - .5, this.getBlockPos().getZ());
        Block playBlock = world.getBlockState(playBlockPos).getBlock();
        BlockPos blockPos = new BlockPos( this.getBlockPos().getX(), this.getBlockPos().getY() - .5, this.getBlockPos().getZ());
        Block block = world.getBlockState(blockPos).getBlock();

        boolean pathCreate = Math.random()*(100) <= 5 && !isInClaim(blockPos) && !this.isSneaking() && !this.getStatusEffects().contains(new StatusEffectInstance(StatusEffects.SLOW_FALLING)) && !this.getStatusEffects().contains(new StatusEffectInstance(StatusEffects.INVISIBILITY)) && playerBlock != Blocks.WATER && playerBlock != Blocks.BAMBOO && playerBlock != Blocks.SUGAR_CANE && playerBlock != Blocks.NETHER_WART && playerBlock != Blocks.SWEET_BERRY_BUSH && playBlock != Blocks.WATER;
        boolean gravelChance = Math.random()*(100) <= 10;
        boolean glassChance = Math.random() * (1000) <= 1;
        boolean roadDecay = Math.random()*(1000)<=1;

        if (pathCreate) {
            if (playerBlock == Blocks.FERN
                    || playerBlock == Blocks.LARGE_FERN
                    || playerBlock == Blocks.GRASS
                    || playerBlock == Blocks.TALL_GRASS
                    || playerBlock == Blocks.DEAD_BUSH
                    || playerBlock == Blocks.CRIMSON_FUNGUS
                    || playerBlock == Blocks.WARPED_FUNGUS
                    || playerBlock == Blocks.CRIMSON_ROOTS
                    || playerBlock == Blocks.WARPED_ROOTS
                    || playerBlock instanceof FlowerBlock
                    || playerBlock instanceof MushroomPlantBlock) {
                world.setBlockState(this.getBlockPos(), Blocks.AIR.getDefaultState());
            }

            else {
                if (block instanceof GrassBlock) {
                    world.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
//                    if (Math.random()*(100+1) >= 75) {
//                        world.setBlockState(blockPos, Blocks.ROOTED_DIRT.getDefaultState());
//                    }
//                    else{
//                        world.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
//                    }
                }
                if (block == Blocks.DIRT || block == Blocks.ROOTED_DIRT) {
                    world.setBlockState(blockPos, Blocks.COARSE_DIRT.getDefaultState());
                }
                if (block == Blocks.COARSE_DIRT) {
                    if (gravelChance) {
                        world.setBlockState(blockPos, Blocks.GRAVEL.getDefaultState());
                    } else {
                        world.setBlockState(blockPos, Blocks.DIRT_PATH.getDefaultState());
                    }
                }
                if (block == Blocks.GRAVEL) {
                    double chance = Math.random() * (100);
                    if (chance >= 75) {
                        world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
                    } else if (chance >= 50) {
                        world.setBlockState(blockPos, Blocks.TUFF.getDefaultState());
                    } else if (chance >= 25) {
                        world.setBlockState(blockPos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
                    } else {
                        world.setBlockState(blockPos, Blocks.ANDESITE.getDefaultState());
                    }
                }
                if (block == Blocks.SAND) {
                    if (glassChance) {
                        world.setBlockState(blockPos, Blocks.GLASS.getDefaultState());
                    } else {
                        world.setBlockState(blockPos, Blocks.SANDSTONE.getDefaultState());
                    }
                }
                if (block == Blocks.RED_SAND) {
                    if (glassChance) {
                        world.setBlockState(blockPos, Blocks.RED_STAINED_GLASS.getDefaultState());
                    } else {
                        world.setBlockState(blockPos, Blocks.RED_SANDSTONE.getDefaultState());
                    }
                }
                if (block == Blocks.SOUL_SAND) {
                    world.setBlockState(blockPos, Blocks.SOUL_SOIL.getDefaultState());
                }
                if (block == Blocks.WARPED_NYLIUM) {
                    world.setBlockState(blockPos, Blocks.NETHERRACK.getDefaultState());
                }
                if (block == Blocks.CRIMSON_NYLIUM) {
                    world.setBlockState(blockPos, Blocks.NETHERRACK.getDefaultState());
                }
                if (block == Blocks.STONE && roadDecay) {
                    world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
                }
                if (block == Blocks.DEEPSLATE && roadDecay) {
                    world.setBlockState(blockPos, Blocks.COBBLED_DEEPSLATE.getDefaultState());
                }
                if (block == Blocks.STONE_BRICKS && roadDecay) {
                    world.setBlockState(blockPos, Blocks.CRACKED_STONE_BRICKS.getDefaultState());
                }
                if (block == Blocks.CRACKED_STONE_BRICKS && roadDecay) {
                    world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
                }
                if ((block == Blocks.DEEPSLATE_BRICKS || block == Blocks.CRACKED_DEEPSLATE_TILES) && roadDecay) {
                    world.setBlockState(blockPos, Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState());
                }
                if (block == Blocks.CRACKED_DEEPSLATE_BRICKS && roadDecay) {
                    world.setBlockState(blockPos, Blocks.COBBLED_DEEPSLATE.getDefaultState());
                }
                if (block == Blocks.DEEPSLATE_TILES && roadDecay) {
                    world.setBlockState(blockPos, Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState());
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (playerPos != this.getBlockPos()) {
            Block();
            playerPos = this.getBlockPos();
        }
    }
}
