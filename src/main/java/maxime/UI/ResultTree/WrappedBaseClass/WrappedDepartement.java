package maxime.UI.ResultTree.WrappedBaseClass;

import maxime.BaseClass.BaseClass;
import maxime.BaseClass.Departement;

public class WrappedDepartement extends WrappedBaseClass {
    final private Departement departement;

    public WrappedDepartement(Departement departement) {
        this.departement = departement;
    }

    @Override
    public String toString() {
        return departement.getNom();
    }

    @Override
    public BaseClass getContent() {
        return this.departement;
    }
}
