package maxime.BaseClass;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import maxime.ResourceLoader;
import maxime.SearchProcessor.Searchable;

import java.io.Serializable;

public abstract class BaseClass implements Serializable, Searchable {

    public static final long serialVersionUID = -1392241261264792747L;
    @XStreamAsAttribute
    @XStreamAlias("id")
    private String raw_id;
    @XStreamOmitField
    private transient int id;
    @XStreamOmitField
    protected transient String formatedname;
    @XStreamOmitField
    protected transient String[][] proprieties;

    public int getId() {
        return id;
    }

    public void postParseSetId() {
        this.id = Integer.parseInt(this.raw_id);
    }

    abstract public void postParse(ResourceLoader resourceLoader);

    @Override
    public final String getSearchCriteria() {
        return formatedname;
    }

    abstract public Region getRegion();

    public String[][] getProprieties() {
        return proprieties;
    }

    abstract public float getSuperficie();

    abstract public int getPopulation();

    abstract public float getDensite();
}
