package ma.projet.beans;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedQuery(
        name = "Femme.findMarieesDeuxFois",
        query = "select f from Femme f where size(f.mariages) >= 2"
)
@NamedNativeQuery(
        name = "Femme.countEnfantsEntreDates",
        query = "SELECT COALESCE(SUM(m.nbrEnfant), 0) FROM Mariage m WHERE m.femme_id = :femmeId AND m.dateDebut BETWEEN :dateDebut AND :dateFin"
)
public class Femme extends Personne {

    @OneToMany(mappedBy="femme")
    private List<Mariage> mariages;

    public Femme() {
    }

    public List<Mariage> getMariages() {
        return mariages;
    }

    public void setMariages(List<Mariage> mariages) {
        this.mariages = mariages;
    }

}
