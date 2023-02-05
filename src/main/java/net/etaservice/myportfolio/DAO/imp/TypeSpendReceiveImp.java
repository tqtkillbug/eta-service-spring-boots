package net.etaservice.myportfolio.DAO.imp;

import net.etaservice.myportfolio.DAO.TypeSpendReceiveDAO;
import net.etaservice.myportfolio.model.TypeSpendReceive;
import net.etaservice.myportfolio.repository.TypeSpendReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeSpendReceiveImp implements TypeSpendReceiveDAO {

    @Autowired
    private TypeSpendReceiveRepository typeSpendReceiveRepository;

    @Override
    public TypeSpendReceive getById(Long id) {
        return typeSpendReceiveRepository.getOne(id);
    }

    @Override
    public TypeSpendReceive save(TypeSpendReceive typeSpendReceive) {
        return typeSpendReceiveRepository.save(typeSpendReceive);
    }

    @Override
    public List<TypeSpendReceive> getAll() {
        return typeSpendReceiveRepository.findAll();
    }
}
