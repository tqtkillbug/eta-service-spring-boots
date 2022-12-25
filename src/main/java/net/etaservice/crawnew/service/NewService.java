package net.etaservice.crawnew.service;

import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.repository.NewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NewService {

    @Autowired
    private NewRepository newRepository;


    public List<New> getNewList() {
        return newRepository.findAll();
    }

    public List<New> saveAll(List<New> newList) {
        return newRepository.saveAll(newList);
    }

    public List<New> getListNewBySourceAndDate(String source, String date) {
        return newRepository.getListNewBySourceAndDate(source,date);
    }
}
