package ar.edu.unq.eperdemic.spring.services.impl


import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.spring.persistencia.repositorios.EspecieRepository

import ar.edu.unq.eperdemic.spring.services.EspecieSpringService

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class EspecieSpringServiceImpl(val especieRepository: EspecieRepository) : EspecieSpringService {

    override fun recuperarPorNombre(nombreDeLaEspecie: String): Especie = especieRepository.findByNombre(nombreDeLaEspecie)


    override fun recuperarTodasLasEspecies(): List<Especie> {
        return especieRepository.findAll().toList()
    }

    override fun recuperarEspeciesDePatogeno(patogeniId: Long): List<Especie> {
        return especieRepository.findAllByPatogenoId(patogeniId.toInt()).toList()
    }


}