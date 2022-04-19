package com.wudaokou.backend.history;

import com.wudaokou.backend.login.Customer;
import com.wudaokou.backend.login.SecurityRelated;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class HistoryController {
    private final HistoryRepository historyRepository;
    private final SecurityRelated securityRelated;

    private final int MAX_SEARCH_HISTORY_SIZE = 10;

    public HistoryController(HistoryRepository historyRepository, SecurityRelated securityRelated) {
        this.historyRepository = historyRepository;
        this.securityRelated = securityRelated;
    }

    private History containsStarredEntity(List<History> list, History history){
        for(History i : list)
            if(i.getUri().equals(history.getUri()))
                return i;
        return null;
    }

    @PostMapping("/api/history/{type}")
    Object postSearchHistory(@Valid @RequestBody History history,
                                               @PathVariable HistoryType type){
        Customer customer = securityRelated.getCustomer();
        List<History> list = historyRepository.findAllByCustomerAndType(customer, type, Sort.by("createdAt").descending());
        if(type == HistoryType.star){
            // if it has been starred
            History i = containsStarredEntity(list, history);
            if(i != null)
                return i;
        }
        history.setType(type);
        history.setCustomer(customer);
        History ret = historyRepository.save(history);
        if(type != HistoryType.search)
            return ret;
        list.add(0, ret);
        while(list.size() > MAX_SEARCH_HISTORY_SIZE)
            historyRepository.deleteById( list.remove(MAX_SEARCH_HISTORY_SIZE).getId() );
        return list;
    }

    @GetMapping(value = {"/api/history", "/api/history/{type}"})
    List<?> get(@PathVariable(required = false) HistoryType type){
        Customer customer = securityRelated.getCustomer();
        Sort sort = Sort.by("createdAt").descending();
        // @RequestParam(required = false) int page, @RequestParam(required = false) int size
        // Pageable pageable = PageRequest.of(page, size, sort);
        if(type == null)
            return historyRepository.findAllByCustomer(customer, sort);
        if(type != HistoryType.info)
            return historyRepository.findAllByCustomerAndType(customer, type, sort);
        // if type is info
        List<History> starredEntities = historyRepository.findAllByCustomerAndType(customer, HistoryType.star, sort);
        List<History> historyEntities = historyRepository.findAllByCustomerAndType(customer, type, sort);
        return historyEntities.stream().map(e -> new History(e){
            final boolean hasStar = containsStarredEntity(starredEntities, e) != null;
            public boolean getHasStar() {return hasStar;}
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/api/history/{s}")
    ResponseEntity<?> delete(@PathVariable String s){
        try {
            int id = Integer.parseInt(s);
            if(historyRepository.findById(id).isPresent())
                historyRepository.deleteById(id);
        }catch(NumberFormatException nfe){
            try{
                Customer customer = securityRelated.getCustomer();
                HistoryType type = HistoryType.valueOf(s);
                historyRepository.deleteByCustomerAndType(customer, type);
            }catch(IllegalArgumentException iae){
                return ResponseEntity.badRequest().body("Illegal type: " + s);
            }
        }
        return ResponseEntity.ok(new History());
    }

    @GetMapping("/popular_search_keys")
    List<History> getPopular(){
        return historyRepository.findTopFrequentNameOfSearch(PageRequest.of(0, 20));
    }

}
