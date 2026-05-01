package edu.se.extweb.repository;



import edu.se.extweb.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    public boolean existsByCode(String code);
}
