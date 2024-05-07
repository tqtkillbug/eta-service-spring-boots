package net.etaservice.comon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public String callPostApi(String apiUrl, Object dataObject) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Object> request = new HttpEntity<>(dataObject, headers);
            ResponseEntity<Object> response  = restTemplate.exchange(apiUrl, HttpMethod.POST,request,Object.class);
            return response.getBody().toString();
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T callGetApi(String apiUrl, Class<T> clazz) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<T> response = restTemplate.getForEntity(apiUrl, clazz);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
