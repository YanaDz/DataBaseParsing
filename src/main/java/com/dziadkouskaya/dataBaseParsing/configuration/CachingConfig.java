package com.dziadkouskaya.dataBaseParsing.configuration;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport {

    public static final String CONNECTIONS = "connection-cache";

    @Bean
    @Override
    public CacheManager cacheManager() {
        EhcacheCachingProvider provider = getCachingProvider();
        var caches = createConfigurations();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, getClassLoader());
        return new JCacheCacheManager(provider.getCacheManager(provider.getDefaultURI(), configuration));
    }

    private Map<String, CacheConfiguration<?, ?>> createConfigurations() {
        CacheConfiguration<Object, Object> connectionsConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder
                                .heap(20000L).build())
                        .build();
        Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();
        caches.put(CONNECTIONS, connectionsConfiguration);
        return caches;
    }

    private EhcacheCachingProvider getCachingProvider() {
        return (EhcacheCachingProvider) Caching.getCachingProvider();
    }

    private ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

}
