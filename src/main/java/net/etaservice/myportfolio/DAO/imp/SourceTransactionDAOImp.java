package net.etaservice.myportfolio.DAO.imp;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import net.etaservice.myportfolio.DAO.SourceTransactionDAO;
import net.etaservice.myportfolio.model.SourceTransaction;
import net.etaservice.myportfolio.repository.SourceTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceTransactionDAOImp implements SourceTransactionDAO {

    @Autowired
    private SourceTransactionRepository sourceTransactionRepository;

    @Override
    public SourceTransaction getById(Long id) {
        return sourceTransactionRepository.getOne(id);
    }

    @Override
    public SourceTransaction save(SourceTransaction sourceTransaction) {
        return sourceTransactionRepository.save(sourceTransaction);
    }

    @Override
    public List<SourceTransaction> getAll() {
        return sourceTransactionRepository.findAll();
    }
}
