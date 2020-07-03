package ar.edu.unq.eperdemic.spring.persistencia.repositorios

import ar.edu.unq.eperdemic.modelo.Especie

import org.springframework.data.jpa.repository.JpaRepository

interface EspecieRepository : JpaRepository<Especie, Long> {

    fun findAllByPatogenoId(patogenoId: Int): List<Especie>
    fun findByNombre(nombreDeLaEspecie: String): Especie

}