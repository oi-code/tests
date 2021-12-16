package mvcTest;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SingersRepository extends PagingAndSortingRepository<Singer, Long>{
    @Query("select count(*) from Singer where image is not null")
    long scheduledCount();
}
