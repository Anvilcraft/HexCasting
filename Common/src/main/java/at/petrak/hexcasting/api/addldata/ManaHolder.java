package at.petrak.hexcasting.api.addldata;

public interface ManaHolder {

    int getMana();

    int getMaxMana();

    void setMana(int mana);

    boolean canRecharge();

    boolean canProvide();

    int getConsumptionPriority();

    boolean canConstructBattery();

    default int withdrawMana(int cost, boolean simulate) {
        if (!canProvide()) {
            return 0;
        }
        var manaHere = getMana();
        if (cost < 0) {
            cost = manaHere;
        }
        if (!simulate) {
            var manaLeft = manaHere - cost;
            setMana(manaLeft);
        }
        return Math.min(cost, manaHere);
    }
}
