package maxime.BaseClass;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import maxime.Utils.BaseClassComparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@XStreamAlias("annuaire")
public class Annuaire<T extends BaseClass> implements Serializable {

    public Annuaire() {
    }

    @XStreamImplicit()
    final private List<T> content = new ArrayList<T>();


    public List<T> getContent() {
        return Collections.unmodifiableList(this.content);
    }

    public void sort() {
        Collections.sort(this.content, new BaseClassComparator());
    }
}
