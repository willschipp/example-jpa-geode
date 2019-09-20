package com.example.config;

import com.example.model.Person;
import com.example.model.PersonRepository;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.geode.cache.InlineCachingRegionConfigurer;

import java.util.function.Predicate;

@Configuration
@ClientCacheApplication //sets up the geode backbone
@EnableEntityDefinedRegions(basePackageClasses = Person.class,clientRegionShortcut = ClientRegionShortcut.LOCAL) //uses the region setup
public class GeodeConfig {

    @Bean //KEY PART --> setups up the geode/database relationship to the repository
    public InlineCachingRegionConfigurer<Person, Long> inlineCachingForCustomersRegionConfigurer(PersonRepository personRepository) {
        return new InlineCachingRegionConfigurer<>(personRepository, Predicate.isEqual("Persons"));//the predicate is the name of the region/bucket to look in
    }

    @Bean
    @DependsOn("Persons")// This means the template must WAIT for the @Region("Persons") to be created BEFORE creating the GemfireTemplate
    public GemfireTemplate gemfireTemplate(GemFireCache gemFireCache) {
        return new GemfireTemplate(gemFireCache.getRegion("/Persons"));//this mean that THIS template uses the "Persons" bucket
    }

}
