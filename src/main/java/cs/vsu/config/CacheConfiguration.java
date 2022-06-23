package cs.vsu.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration =
            Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                    .build()
            );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, cs.vsu.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, cs.vsu.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, cs.vsu.domain.User.class.getName());
            createCache(cm, cs.vsu.domain.Authority.class.getName());
            createCache(cm, cs.vsu.domain.User.class.getName() + ".authorities");
            createCache(cm, cs.vsu.domain.ApplicationUser.class.getName());
            createCache(cm, cs.vsu.domain.ApplicationUser.class.getName() + ".orders");
            createCache(cm, cs.vsu.domain.Product.class.getName());
            createCache(cm, cs.vsu.domain.Product.class.getName() + ".categories");
            createCache(cm, cs.vsu.domain.Product.class.getName() + ".images");
            createCache(cm, cs.vsu.domain.Product.class.getName() + ".orders");
            createCache(cm, cs.vsu.domain.Image.class.getName());
            createCache(cm, cs.vsu.domain.Image.class.getName() + ".products");
            createCache(cm, cs.vsu.domain.Category.class.getName());
            createCache(cm, cs.vsu.domain.Category.class.getName() + ".products");
            createCache(cm, cs.vsu.domain.Order.class.getName());
            createCache(cm, cs.vsu.domain.Order.class.getName() + ".products");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
