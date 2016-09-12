package br.odb.knights;

public abstract class Knight extends Actor {

    public boolean hasExited;

    Knight(int healthPoints, int attackPoints) {
        super(healthPoints, attackPoints);
    }

    @Override
    public String toString() {
        return super.healthPoints + " HP";
    }

    public void setAsExited() {
        hasExited = true;
    }
}
