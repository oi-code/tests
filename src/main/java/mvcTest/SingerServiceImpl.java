package mvcTest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Transactional
@Service("singerService")
public class SingerServiceImpl implements SingersService {

    @Autowired
    SingersRepository repository;

    @Autowired
    EntityManagerFactory emf;

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
	if(singer.getId()==null) {
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
}
