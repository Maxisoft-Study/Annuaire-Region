package maxime.SearchProcessor;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import maxime.BaseClass.Commune;
import maxime.BaseClass.Departement;
import maxime.BaseClass.Region;
import maxime.ResourceLoader;
import maxime.Utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class SearchProcessor {

    private final ResourceLoader resourceLoader;

    public SearchProcessor(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private <T extends Searchable> Collection<T> searchFor(Collection<T> collection, String searchString) {
        searchString = Utils.SimplyfyString(searchString);
        final Pattern pattern = Pattern.compile(searchString, Pattern.LITERAL); // utilise Boyer-Moore algorithm
        final Predicate<T> predicate = new Predicate<T>() {
            @Override
            public boolean apply(T item) {
                return pattern.matcher(item.getSearchCriteria()).find();
            }
        };
        return Collections2.filter(collection, predicate);
    }

    public ResultSet search(Region lastRegion, final int seuil) {
        Collection<Commune> communnes = new ArrayList<Commune>();
        for (Departement departement : lastRegion.getDepartements()){
            for(Commune commune : departement.getCommunes()){
                if (commune.getPopulation() >= seuil){
                    communnes.add(commune);
                }
            }
        }
        if (communnes.isEmpty()){
            return ResultSet.EMPTY_RESULT;
        }
        return new ResultSet(ResultSet.EMPTY_RESULT.regions, ResultSet.EMPTY_RESULT.departements, communnes);
    }

    public ResultSet search(String searchString) {
        boolean d = true, r = true, c = true;
        searchString = searchString.trim();
        if (searchString.length() == 0) {
            return ResultSet.EMPTY_RESULT;
        }
        if (searchString.matches("^Region\\s*\\|\\s*.*")) {
            d = false;
            c = false;
            searchString = searchString.replaceFirst("^Region\\s*\\|\\s*", "");
        } else if (searchString.matches("^Departement\\s*\\|\\s*.*")) {
            r = false;
            c = false;
            searchString = searchString.replaceFirst("^Departement\\s*\\|\\s*", "");
        } else if (searchString.matches("^Commune\\s*\\|\\s*.*")) {
            d = false;
            r = false;
            searchString = searchString.replaceFirst("^Commune\\s*\\|\\s*", "");
        }
        searchString = searchString.trim();
        if (searchString.length() == 0) {
            return ResultSet.EMPTY_RESULT;
        }

        Collection<Departement> departements = d ? searchForDepartements(searchString) : ResultSet.EMPTY_RESULT.departements;
        Collection<Region> regions = r ? searchForRegions(searchString) : ResultSet.EMPTY_RESULT.regions;
        Collection<Commune> communnes = c ? searchForCommune(searchString) : ResultSet.EMPTY_RESULT.communes;

        return new ResultSet(regions, departements, communnes);
    }

    public Collection<Departement> searchForDepartements(String searchString) {
        return this.searchFor(resourceLoader.getDepartementAnnuaire().getContent(), searchString);
    }

    public Collection<Region> searchForRegions(String searchString) {
        return this.searchFor(resourceLoader.getRegionAnnuaire().getContent(), searchString);
    }

    public Collection<Commune> searchForCommune(String searchString) {
        return this.searchFor(resourceLoader.getCommuneAnnuaire().getContent(), searchString);
    }

    public static class ResultSet {
        static public final ResultSet EMPTY_RESULT = new ResultSet();
        final public Collection<Departement> departements;
        final public Collection<Region> regions;
        final public Collection<Commune> communes;

        public ResultSet(Collection<Region> regions, Collection<Departement> departements, Collection<Commune> communes) {
            this.regions = Collections.unmodifiableCollection(regions);
            this.departements = Collections.unmodifiableCollection(departements);
            this.communes = Collections.unmodifiableCollection(communes);
        }

        private ResultSet() {
            this.regions = Collections.emptyList();
            this.departements = Collections.emptyList();
            this.communes = Collections.emptyList();
        }
    }
}
