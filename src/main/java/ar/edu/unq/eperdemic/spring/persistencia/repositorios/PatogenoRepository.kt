package ar.edu.unq.eperdemic.spring.persistencia.repositorios

import ar.edu.unq.eperdemic.modelo.Patogeno
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PatogenoRepository: JpaRepository<Patogeno, Long> {

}