package net.codejava.crawnew.service;

import net.codejava.crawnew.model.New;
import net.codejava.crawnew.repository.NewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewService {

    @Autowired
    private NewRepository newRepository;


    public List<New> getNewList(){
        return newRepository.findAll();
    }

    public List<New> saveAll(List<New> newList){
        return newRepository.saveAll(newList);
    }
}
