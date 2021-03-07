package mod.oceanmaze.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;

@Mixin(TridentEntity.class)
public interface TridentEntityAccessor {

	@Accessor("tridentStack")
	public ItemStack getTridentStack();

	@Accessor("dealtDamage")
	public void setDealtDamage(boolean b);

}
