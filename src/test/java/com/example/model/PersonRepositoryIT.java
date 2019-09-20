package com.example.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonRepositoryIT {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    GemfireTemplate gemfireTemplate;

    @Before
    public void before() {
        personRepository.deleteAll();
        gemfireTemplate.getRegion().clear();
    }

    @Test
    public void testSaveAndRetrieveFromGeode() {
        //check it's all empty
        assertTrue(personRepository.count() <= 0);
        //now, check it's not in the cache
        assertTrue(gemfireTemplate.getRegion().size() <= 0);
        //now save an entity
        //create
        Person person = new Person();
        person.setId(1l);
        person.setFirstName("John");
        person.setLastName("Doe");
        //save
        personRepository.save(person);
        //check that it's in the REPOSITORY
        assertTrue(personRepository.count() >= 1);
        //now, check again it's not in the cache
        assertTrue(gemfireTemplate.getRegion().size() <= 0);
        //use the gemfireTemplate to retrieve it
        Person fromGeode = gemfireTemplate.get(1l);
        assertTrue(fromGeode.getFirstName().equals("John"));
        assertTrue(fromGeode.getLastName().equals("Doe"));
        //now it's in geode!
    }


    @Test
    public void testWriteFromGeodeToDatabase() {
        //check it's all empty
        assertTrue(personRepository.count() <= 0);
        //now, check it's not in the cache
        assertTrue(gemfireTemplate.getRegion().size() <= 0);
        //create a person and write it to geode
        Person person = new Person();
        person.setId(1l);
        person.setFirstName("John");
        person.setLastName("Doe");
        //write to geode
        gemfireTemplate.put(person.getId(),person);
        //check its in the database
        assertTrue(personRepository.count() >= 1);
    }

}