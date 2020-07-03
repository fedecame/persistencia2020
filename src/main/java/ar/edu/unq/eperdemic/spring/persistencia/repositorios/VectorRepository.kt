package ar.edu.unq.eperdemic.spring.persistencia.repositorios


import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface VectorRepository : JpaRepository<Vector, Long> {
    @Query(value = "SELECT * FROM Vector ORDER BY RAND() LIMIT 1", nativeQuery = true)
    fun randomVector(): Vector

}


