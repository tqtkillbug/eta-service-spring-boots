package net.etaservice.generatoraccount.api;

import com.google.gson.Gson;
import net.etaservice.generatoraccount.Account;
import net.etaservice.generatoraccount.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/free/account")
public class AccountAPI {

    @Autowired
    private AccountRepository accountRepository;

    @CrossOrigin
    @GetMapping("/generator")
    public String generatorAccount(HttpServletRequest req){
        int min = 0;
        int max = 50;
        String remoteHost = req.getRemoteHost();
        System.out.println(remoteHost);
        List<Account> accountList = accountRepository.findAll();
        List<Account> listFilter = accountList.stream().filter(a -> a.getIsDeleted() != 1).collect(Collectors.toList());
        max = listFilter.size()-1;
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        Account getRandom = accountList.get(random_int);
        return  getRandom != null ? new Gson().toJson(getRandom) : "";
    }

    @CrossOrigin
    @GetMapping("delete/{id}")
    public String deleteAccount(@PathVariable String id){
        Long idk = Long.parseLong(id);
        if (idk > 0){
            Optional<Account> getRandom =  accountRepository.findById(idk);
            Account getRandoms = getRandom.get();
            getRandoms.setIsDeleted(1);
            accountRepository.save(getRandoms);
            return "OK";
        }
        return "";
    }



}
