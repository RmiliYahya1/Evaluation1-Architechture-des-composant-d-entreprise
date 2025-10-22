package ma.projet.services;

import ma.projet.classes.EmployeTache;
import org.springframework.stereotype.Service;

@Service
public class EmployeTacheService extends AbstractService<EmployeTache> {
    public EmployeTacheService() {
        super(EmployeTache.class);
    }
}
