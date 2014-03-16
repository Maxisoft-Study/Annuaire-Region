package maxime.BaseClass;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import maxime.ResourceLoader;
import maxime.Utils.Utils;

import java.util.Collection;

@XStreamAlias("departement")
public class Departement extends BaseClass {
    public static final long serialVersionUID = 202194108484418925L;
    private int id_region;
    private String code;
    private String nom;

    @XStreamOmitField
    private transient Region region;
    @XStreamOmitField
    private transient Collection<Commune> communes;

    @XStreamOmitField
    private transient int population;
    @XStreamOmitField
    private transient float superficie;

    @Override
    public String toString() {
        return "Departement{" +
                "region=" + region +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                '}';
    }

    @Override
    public void postParse(ResourceLoader resourceLoader) {
        this.formatedname = Utils.SimplyfyString(this.nom);
        this.communes = resourceLoader.getCommuneAnnuaire().getContent();
        final Predicate<Commune> predicate = new Predicate<Commune>() {
            @Override
            public boolean apply(Commune item) {
                return item.getNo_dpt().equals(Departement.this.getCode());
            }
        };
        this.communes = Collections2.filter(this.communes, predicate);
        this.region = resourceLoader.findRegion(this.id_region);
    }

    public int getIdRegion() {
        return id_region;
    }

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public Region getRegion() {
        return region;
    }

    public Collection<Commune> getCommunes() {
        return communes;
    }

    @Override
    public String[][] getProprieties() {
        if (this.proprieties == null) { //construct them
            this.proprieties = new String[][]{
                    {"nom", this.getNom()},
                    {"code", getCode()},
                    {"region", getRegion().getNom()},
                    {"nombre de Communes", String.valueOf(getCommunes().size())},
                    {"superficie", String.valueOf(getSuperficie())},
                    {"population", String.valueOf(getPopulation())},
                    {"densité", String.valueOf(getDensite())}
            };
        }
        return this.proprieties;
    }

    @Override
    public float getSuperficie() {
        if (superficie <= 0) {
            superficie = 0.f;
            for (Commune commune : getCommunes()) {
                superficie += commune.getSuperficie();
            }
        }
        return superficie;
    }

    @Override
    public int getPopulation() {
        if (population <= 0) {
            population = 0;
            for (Commune commune : getCommunes()) {
                population += commune.getPopulation();
            }
        }
        return population;
    }

    @Override
    public float getDensite() {
        return getPopulation() / getSuperficie();
    }
}
