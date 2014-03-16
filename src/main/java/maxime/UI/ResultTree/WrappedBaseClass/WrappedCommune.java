package maxime.UI.ResultTree.WrappedBaseClass;

import maxime.BaseClass.BaseClass;
import maxime.BaseClass.Commune;

public class WrappedCommune extends WrappedBaseClass {
    final private Commune commune;

    public WrappedCommune(Commune commune) {
        this.commune = commune;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", commune.getNom(), commune.getDepartement().getNom());
    }

    @Override
    public BaseClass getContent() {
        return this.commune;
    }
}
