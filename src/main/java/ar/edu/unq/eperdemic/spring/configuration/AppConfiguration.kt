package ar.edu.unq.eperdemic.spring.configuration


import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan( basePackages = ["ar.edu.unq.eperdemic.services.impl"]) // Indica el paquete en el que tiene que buscar Los Services/Controllers
class AppConfiguration {

    @Bean
    fun groupName() : String {
        val groupName :String?  = System.getenv()["GROUP_NAME"]
        return groupName!!
    }

    @Bean
    fun ubicacionDAO(): HibernateUbicacionDAO {
        return HibernateUbicacionDAO()
    }

    @Bean
    fun especieDAO(): HibernateEspecieDAO {
        return HibernateEspecieDAO()
    }

    @Bean
    fun patogenoDAO(): HibernatePatogenoDAO {
        return HibernatePatogenoDAO()
    }

    @Bean
    fun vectorDAO(): HibernateVectorDAO {
        return HibernateVectorDAO()
    }

    @Bean
    fun estadisticasDAO(): HibernateEstadisticasDAO {
        return HibernateEstadisticasDAO()
    }

    @Bean
    fun estadisticasService(estadisticasDAO: HibernateEstadisticasDAO): EstadisticasService {
        return EstadisticasServiceImpl(estadisticasDAO)
    }

    @Bean
    fun patogenoService(patogenoDAO: HibernatePatogenoDAO, especieDAO: HibernateEspecieDAO): PatogenoService {
        return PatogenoServiceImpl(patogenoDAO, especieDAO)
    }

    @Bean
    fun ubicacionService(ubicaionDAO:HibernateUbicacionDAO): UbicacionService {
        return UbicacionServiceImpl(ubicaionDAO)
    }

    @Bean
    fun feedService(): FeedService {
        return FeedServiceImpl(FeedMongoDAO())
    }

    @Bean
    fun vectorService(vectorDAO: HibernateVectorDAO, ubicaionDAO: UbicacionDAO, feedService: FeedService): VectorService {
        return VectorServiceImpl(vectorDAO, ubicaionDAO, feedService)
    }

}