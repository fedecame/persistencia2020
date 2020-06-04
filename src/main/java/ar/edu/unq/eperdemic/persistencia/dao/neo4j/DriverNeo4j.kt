package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import org.neo4j.driver.*
import org.neo4j.driver.Values.parameters
import java.util.Arrays.asList
import java.util.Collections.singletonMap

class DriverNeo4j(){
    val driver: Driver

    init {
        val env = System.getenv()
        val url = env.getOrDefault("URL", "bolt://localhost:7687")
        val username = env.getOrDefault("USER", "neo4j")
        val password = env.getOrDefault("PASSWORD", "root")

        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password),
                Config.builder().withLogging(Logging.slf4j()).build()
        )
    }
}