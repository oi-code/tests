package mvcTest;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface SingersRepository extends PagingAndSortingRepository<Singer, Long> {

}
