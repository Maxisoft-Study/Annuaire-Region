package maxime.BaseClass;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import maxime.ResourceLoader;
import maxime.Utils.Utils;

import java.awt.image.BufferedImage;
import java.util.Collection;

@XStreamAlias("region")
public class Region extends BaseClass {
    public static final long serialVersionUID = -8990522708135168390L;
    private String nom;
    @XStreamAlias("carte")
    private String carte_string;
    private float carte_latitude_haut_gauche;
    private float carte_longitude_haut_gauche;
    private float carte_latitude_bas_droite;
    private float carte_longitude_bas_droite;

    @XStreamOmitField
    private transient int population;
    @XStreamOmitField
    private transient float superficie;

    @XStreamOmitField
    private transient BufferedImage carte;

    @XStreamOmitField
    private transient Collection<Departement> departements;

    public String getNom() {
        return nom;
    }

    public String getCarte_string() {
        return carte_string;
    }

    public float getCarte_latitude_haut_gauche() {
        return carte_latitude_haut_gauche;
    }

    public float getCarte_longitude_haut_gauche() {
        return carte_longitude_haut_gauche;
    }

    public float getCarte_latitude_bas_droite() {
        return carte_latitude_bas_droite;
    }

    public float getCarte_longitude_bas_droite() {
        return carte_longitude_bas_droite;
    }

    public BufferedImage getCarte() {
        return carte;
    }

    public Collection<Departement> getDepartements() {
        return departements;
    }

    @Override
    public void postParse(ResourceLoader resourceLoader) {
        this.formatedname = Utils.SimplyfyString(this.nom);
        this.departements = resourceLoader.getDepartementAnnuaire().getContent();
        final Predicate<Departement> predicate = new Predicate<Departement>() {
            @Override
            public boolean apply(Departement item) {
                return item.getIdRegion() == Region.this.getId();
            }
        };
        this.departements = Collections2.filter(this.departements, predicate);
        this.carte = resourceLoader.loadImage(this.carte_string);
    }

    @Override
    public Region getRegion() {
        return this;
    }

    @Override
    public String toString() {
        return "Region{" +
                "nom='" + nom + '\'' +
                '}';
    }

    private long nbrCommunes() {
        long ret = 0L;
        for (Departement departement : getDepartements()) {
            ret += departement.getCommunes().size();
        }
        return ret;
    }

    @Override
    public String[][] getProprieties() {
        if (this.proprieties == null) { //construct them
            this.proprieties = new String[][]{
                    {"nom", this.getNom()},
                    {"nombre de Departements", String.valueOf(this.getDepartements().size())},
                    {"nombre de Communes", String.valueOf(nbrCommunes())},
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
            for (Departement commune : getDepartements()) {
                superficie += commune.getSuperficie();
            }
        }
        return superficie;
    }

    @Override
    public int getPopulation() {
        if (population <= 0) {
            population = 0;
            for (Departement commune : getDepartements()) {
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
