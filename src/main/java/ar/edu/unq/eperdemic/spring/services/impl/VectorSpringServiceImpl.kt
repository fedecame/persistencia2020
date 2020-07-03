package ar.edu.unq.eperdemic.spring.services.impl

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.spring.persistencia.repositorios.VectorRepository
import ar.edu.unq.eperdemic.spring.services.VectorSpringService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VectorSpringServiceImpl(val vectorRepository: VectorRepository) : VectorSpringService {

    override fun recuperarVectorRandom(): Vector = vectorRepository.randomVector()
}