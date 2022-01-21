package com.ocbc.backenddemo.service;


import com.ocbc.backenddemo.entity.Collection;
import com.ocbc.backenddemo.repository.CollectionRepository;
import com.ocbc.backenddemo.repository.DebtRepository;
import com.ocbc.backenddemo.repository.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class CollectionService {
    private final UserRepository userRepository;

    private final DebtRepository debtRepository;

    private final CollectionRepository collectionRepository;

    public CollectionService(UserRepository userRepository, DebtRepository debtRepository, CollectionRepository collectionRepository) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
        this.collectionRepository = collectionRepository;
    }

    /**
     * get collection by username and return collection list
     *
     * @param userName client username
     * @return Collection List
     */
    public List<Collection> getCollectionByName(String userName) {
        List<Collection> collectionList = collectionRepository.findAll();
        return collectionList.stream()
                .filter(c -> c.getUser().equals(userName))
                .collect(Collectors.toList());
    }

    /**
     * get collection by username and return Collection
     *
     * @param userName       client username
     * @param collectionName collection username
     * @return Collection
     */
    public Collection getCollectionByName(String userName, String collectionName) {
        List<Collection> collectionList = collectionRepository.findAll();
        return collectionList.stream().filter(c -> c.getUser().equals(userName) && c.getCollectionName().equals(collectionName)).findFirst().orElse(null);
    }


    /**
     * save collection record to database.
     *
     * @param userName       client username
     * @param collectionName collection username
     * @param amount         amount
     * @return Collection
     */
    public Collection addCollection(String userName, String collectionName, double amount) {
        List<Collection> collectionList = collectionRepository.findAll();
        Collection collection = collectionList.stream().filter(c -> c.getUser().equals(userName) && c.getCollectionName().equals(collectionName)).findFirst().orElse(null);
        if (collection != null) {
            double collectTotal = Double.sum(collection.getAmount(), amount);
            collection.setAmount(collectTotal);
            collection = collectionRepository.save(collection);
            return collection;
        } else {
            Collection newCollection = new Collection(userName, collectionName, amount);
            newCollection = collectionRepository.save(newCollection);
            return newCollection;
        }
    }

}
