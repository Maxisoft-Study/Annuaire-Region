package maxime.BaseClass;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import maxime.ResourceLoader;
import maxime.Utils.Utils;

@XStreamAlias("commune")
public class Commune extends BaseClass {
    public static final long serialVersionUID = -7028296929852202801L;

    private String nom;
    private String no_dpt;
    private String code_postal;
    private int arrondissement;
    private String canton;
    private int population;
    private int superficie;
    private float longitude;
    private float latitude;
    private float altitude_min;
    private float altitude_max;
    @XStreamOmitField
    private transient Departement departement;

    @Override
    public String toString() {
        return "Commune{" +
                "nom='" + nom + '\'' +
                ", no_dpt='" + no_dpt + '\'' +
                ", code_postal='" + code_postal + '\'' +
                ", getPopulation=" + population +
                ", getSuperficie=" + superficie +
                ", departement=" + departement +
                '}';
    }

    @Override
    public void postParse(ResourceLoader resourceLoader) {
        this.formatedname = Utils.SimplyfyString(this.nom);
        this.departement = resourceLoader.findDepartement(this.no_dpt);

        //optimisation memoire
        this.no_dpt = departement.getCode();
    }

    @Override
    public Region getRegion() {
        return this.getDepartement().getRegion();
    }

    public String getNom() {
        return nom;
    }

    public String getNo_dpt() {
        return no_dpt;
    }

    public String getCode_postal() {
        return code_postal;
    }

    public int getArrondissement() {
        return arrondissement;
    }

    public String getCanton() {
        return canton;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getAltitude_min() {
        return altitude_min;
    }

    public float getAltitude_max() {
        return altitude_max;
    }

    public Departement getDepartement() {
        return departement;
    }

    @Override
    public String[][] getProprieties() {
        if (this.proprieties == null) { //construct them
            this.proprieties = new String[][]{
                    {"nom", this.getNom()},
                    {"region", getRegion().getNom()},
                    {"departement", getDepartement().getNom()},
                    {"canton", getCanton()},
                    {"code postal", getCode_postal()},
                    {"altitude min", String.valueOf(getAltitude_min())},
                    {"altitude max", String.valueOf(getAltitude_max())},
                    {"latitude", String.valueOf(getLatitude())},
                    {"longitude", String.valueOf(getLongitude())},
                    {"superficie", String.valueOf(getSuperficie())},
                    {"population", String.valueOf(getPopulation())},
                    {"densité", String.valueOf(getDensite())}

            };
        }
        return this.proprieties;
    }

    @Override
    public float getSuperficie() {
        return superficie;
    }

    @Override
    public int getPopulation() {
        return population;
    }

    @Override
    public float getDensite() {
        return getPopulation() / getSuperficie();
    }
}
