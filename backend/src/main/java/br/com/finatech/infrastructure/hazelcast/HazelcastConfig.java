package br.com.finatech.infrastructure.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import com.hazelcast.spring.cache.HazelcastCacheManager;

@Configuration
@EnableCaching
public class HazelcastConfig {

    @Bean
    HazelcastInstance hazelcastInstance() {
        // Inicializa o cluster do Hazelcast (ele vai procurar outros servidores na rede
        // automaticamente)
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    HazelcastProxyManager<String> proxyManager(HazelcastInstance hazelcastInstance) {
        // Criamos o mapa distribuído dentro do Hazelcast
        IMap<String, byte[]> mapaDosBaldes = hazelcastInstance.getMap("baldes-compartilhados");

        // O ProxyManager diz ao Bucket4j: "Toda vez que precisar salvar um balde, salve
        // nesse mapa do Hazelcast"
        return new HazelcastProxyManager<>(mapaDosBaldes);
    }

    @Bean
    CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}