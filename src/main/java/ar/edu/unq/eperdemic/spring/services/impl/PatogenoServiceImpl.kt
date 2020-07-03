package ar.edu.unq.eperdemic.spring.services.impl


import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.spring.persistencia.repositorios.PatogenoRepository
import ar.edu.unq.eperdemic.spring.services.PatogenoSpringService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class PatogenoServiceImpl(val patogenoRepository: PatogenoRepository) : PatogenoSpringService {
    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return patogenoRepository.findAll().toList()
    }



}