package mvcTest;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingersService {
    List<Singer> findAll();
    Singer findById(Long id);
    Singer save(Singer singer);
    Page<Singer>findWithPages(Pageable pageable);
    void delete(Long id);
}
