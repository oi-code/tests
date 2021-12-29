package mvcTest;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("singerService")
@EnableScheduling
public class SingerServiceImpl implements SingersService {

    public static long countImagesInDb = 0;

    @Autowired
    SingersRepository repository;
    
    @Autowired
    EntityManager em;

    @Override
    public List<Singer> findAll() {
	Iterable<Singer> iter = repository.findAll();
	Iterator<Singer> it = iter.iterator();
	List<Singer> result = new ArrayList<Singer>();
	while (it.hasNext()) {
	    result.add(it.next());
	}
	return result;
    }

    @Override
    public Singer findById(Long id) {
	try {
	    Singer res = repository.findById(id).get();
	    return res;
	} catch (NoSuchElementException e) {
	    return new Singer();
	}

    }

    @Override
    public Singer save(Singer singer) {
	// val.validate(singer);

	if (singer == null) {
	    return null;
	}

	if (singer.getId() == null) {
	    repository.save(singer);
	    return singer;
	}
	if (singer.getImage() == null) {
	    Singer s = findById(singer.getId());
	    if (s.getImage() != null) {
		singer.setImage(s.getImage());
	    }
	}
	repository.save(singer);
	return singer;
    }

    @Override
    public Page<Singer> findWithPages(Pageable p) {
	return repository.findAll(p);
    }

    @Override
    public void delete(Long id) {
	repository.deleteById(id);
    }

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.SECONDS)
    public void scheduledCount() {
	countImagesInDb = repository.scheduledCount();
	// System.out.println(countInDb);
    }

    @Override
    public Singer merge(Singer singer) {
	em.merge(singer);
	return singer;
    }
}
