package ar.edu.unq.eperdemic.spring.services.impl

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.spring.persistencia.repositorios.UbicacionRepository
import ar.edu.unq.eperdemic.spring.services.UbicacionSpringService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UbicacionSpringServiceImpl(val ubicacionRepository: UbicacionRepository) : UbicacionSpringService {
    override fun guardarTodos(recuperarTodasLasUbicaciones: List<Ubicacion>) {
        ubicacionRepository.saveAll(recuperarTodasLasUbicaciones)
    }

    override fun recuperarUbicacionRandom(): Ubicacion = ubicacionRepository.randomUbicacion()

    override fun recuperarTodasLasUbicaciones(): List<Ubicacion> {
        return ubicacionRepository.findAll().toList()
    }
}