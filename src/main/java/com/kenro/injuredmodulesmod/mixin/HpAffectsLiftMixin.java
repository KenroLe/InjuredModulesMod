
package com.kenro.injuredmodulesmod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.zarkonnen.airships.Airship;
import com.zarkonnen.airships.BonusSet;
import com.zarkonnen.airships.Combat;
import com.zarkonnen.airships.GridBody;
import com.zarkonnen.airships.HasName;
import com.zarkonnen.airships.Job;
import com.zarkonnen.airships.Module;

import java.util.ArrayList;
import java.util.Comparator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Airship.class)
public abstract class HpAffectsLiftMixin extends GridBody implements Comparator<Job>, Cloneable, HasName {
  @Shadow
  public ArrayList<Module> modules;
  @Shadow
  public final BonusSet currentBonuses = null;

  @Shadow
  public int gridXToWorldX(int xVal, int spriteW) {
    return 0;
  };

  // @Inject(
  // at = @At(
  // value = "INVOKE",
  // ordinal = 1,
  // target = "getLift",
  // shift = At.Shift.AFTER
  // ),
  // method = "availableLift(Lcom/zarkonnen/airships/Combat;)I"
  // )
  // public void lift(CallbackInfoReturnable ci,@Local(ordinal=0) LocalIntRef asd,
  // @Local(ordinal=1) LocalIntRef asd_t,@Local LocalRef<Module> m) {
  // System.out.println("Hello world" + asd.get());
  // }
  @Inject(at = @At(value = "INVOKE", ordinal = 1, target = "size", shift = At.Shift.AFTER), method = "availableLift(Lcom/zarkonnen/airships/Combat;)I")
  public void availableLift(Combat c, CallbackInfoReturnable ci, @Local(ordinal = 0) LocalIntRef result) {
    int moduleCount = this.modules.size();
    System.out.println("LIFT before deduction" + result.get());
    System.out.println("modulecount at return" + moduleCount);
    System.out.println("Modules from quack" + this.modules.size());
    for (int mi = 0; mi < moduleCount; ++mi) {
      Module m = this.modules.get(mi);
      if (m.type.hasLift() && m.canRun()) {
        if (c != null && c.inCrashZone(
            this.getX() + (double) (this.gridXToWorldX(m.x, m.type.getW()) * 16) + (double) (m.type.getW() * 16 / 2))) {
        } else {
          System.out.println("hp:" + m.hp);
          System.out.println("max hp:" + m.maxHP);
          float scalar = 1 - (float) m.hp / m.maxHP;
          System.out.println("scalar:" + scalar);
          int res = (int) (result.get() - m.type.getLift(this.currentBonuses) * scalar);
          System.out.println("lift after deduction:" + res);
          result.set((int) (result.get() - m.type.getLift(this.currentBonuses) * scalar));
        }
      }
    }
    System.out.println("LIFT after deduction" + result.get());
  }
}
