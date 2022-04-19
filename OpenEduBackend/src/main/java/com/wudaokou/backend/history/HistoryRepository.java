package com.wudaokou.backend.history;

import com.wudaokou.backend.login.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findAllByCustomer(Customer customer, Sort sort);
//    List<History> findAllByCustomer(Customer customer, Pageable pageable);
    List<History> findAllByCustomerAndType(Customer customer, HistoryType type, Sort sort);
    List<History> findAllByCustomerAndType(Customer customer, HistoryType type);
//    List<History> findAllByCustomerAndType(Customer customer, HistoryType type, Pageable pageable);
    @Transactional
    void deleteByCustomerAndType(Customer customer, HistoryType type);

    @Query("SELECT h.name FROM History AS h "
            + "WHERE h.customer = ?1 and h.course = ?2 and h.type = 'info'"
            + "GROUP BY h.name ORDER BY COUNT(h.name) Desc")
    List<String> findTopFrequentNameOfEntity(Customer customer, Course course, Pageable pageable);

    @Query("SELECT h FROM History AS h "
            + "WHERE h.type = 'search' GROUP BY h.name ORDER BY COUNT(h.name) Desc")
    List<History> findTopFrequentNameOfSearch(Pageable pageable);
}