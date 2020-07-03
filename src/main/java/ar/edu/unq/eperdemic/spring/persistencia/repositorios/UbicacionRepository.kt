package ar.edu.unq.eperdemic.spring.persistencia.repositorios

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UbicacionRepository: JpaRepository<Ubicacion, String> {
    @Query(value = "SELECT * FROM Ubicacion ORDER BY RAND() LIMIT 1", nativeQuery = true)
    fun randomUbicacion(): Ubicacion
}

