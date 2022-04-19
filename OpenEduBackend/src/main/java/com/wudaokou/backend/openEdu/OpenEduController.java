package com.wudaokou.backend.openEdu;

import com.wudaokou.backend.history.Course;
import com.wudaokou.backend.history.History;
import com.wudaokou.backend.history.HistoryRepository;
import com.wudaokou.backend.history.HistoryType;
import com.wudaokou.backend.login.Customer;
import com.wudaokou.backend.login.SecurityRelated;
import com.wudaokou.backend.openEdu.response.Instance;
import com.wudaokou.backend.openEdu.response.ReturnList;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/open")
@RestController
public class OpenEduController {

    SecurityRelated securityRelated;
    Client client;
    HistoryRepository historyRepository;

    public OpenEduController(SecurityRelated securityRelated, Client client, HistoryRepository historyRepository) {
        this.securityRelated = securityRelated;
        this.client = client;
        this.historyRepository = historyRepository;
    }

    @GetMapping("/instanceList")
    Object instanceList(@RequestHeader("Authorization") String token,
                        @RequestParam Course course,
                        @RequestParam String searchKey,
                        @RequestParam String id){
        Optional<Customer> customer = securityRelated.getCustomer(token);
        ReturnList<Instance> rsp = client.instanceList(course.toString().toLowerCase(), searchKey, id).block();
        if(rsp == null)
            return ResponseEntity.internalServerError();
        if(customer.isPresent()){
            Map<String, Integer> map = historyRepository.findAllByCustomerAndType(customer.get(), HistoryType.star).stream()
                    .map(h -> Map.of(h.getUri(), h.getId()))
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            rsp.setData(rsp.getData().stream()
                            .peek(v -> {
                                if(map.containsKey(v.getUri())){
                                    v.setHasStar(true);
                                    v.setId(map.get(v.getUri()));
                                }
                            }).collect(Collectors.toList()));
        }
        return rsp;
    }

    @GetMapping("/infoHasStar")
    Object infoHasStar(@RequestHeader("Authorization") String token,
                       @RequestParam String uri){
        Optional<Customer> customer = securityRelated.getCustomer(token);
        boolean hasStar = false;
        int id = 0;
        if(customer.isPresent()){
            Map<String, Integer> map = historyRepository.findAllByCustomerAndType(customer.get(), HistoryType.star).stream()
                    .map(h -> Map.of(h.getUri(), h.getId()))
                    .flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if(map.containsKey(uri)){
                hasStar = true;
                id = map.get(uri);
            }
        }
        return Map.of(
                "hasStar", hasStar,
                "id", id
        );
    }


}
