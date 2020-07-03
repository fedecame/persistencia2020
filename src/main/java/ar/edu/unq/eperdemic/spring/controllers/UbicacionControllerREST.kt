package ar.edu.unq.eperdemic.spring.controllers


import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.spring.controllers.dto.UbicacionDTO
import ar.edu.unq.eperdemic.spring.services.UbicacionSpringService
import ar.edu.unq.eperdemic.spring.services.VectorSpringService
import org.springframework.web.bind.annotation.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/ubicacion")
class UbicacionControllerREST(private val vectorSpringService: VectorSpringService, private val ubicacionSpringService: UbicacionSpringService, private val ubicacionService: UbicacionService) {

    @PutMapping("/{vectorId}/{nombreDeLaUbicacion}")
    fun mover(@PathVariable vectorId: Int, @PathVariable nombreDeLaUbicacion: String) = ubicacionService.mover(vectorId, nombreDeLaUbicacion)

    @PutMapping("/expandir")
    fun expandir() {
        ubicacionSpringService.recuperarTodasLasUbicaciones().forEach { ubicacion ->
            ubicacionService.expandir(ubicacion.nombreUbicacion!!)
        }
        val vector = vectorSpringService.recuperarVectorRandom()
        val ubicacion = ubicacionSpringService.recuperarUbicacionRandom()
        ubicacionService.moverMasCorto(vector.id!!, ubicacion.nombreUbicacion!!)
    }

    @PostMapping
    fun crearUbicacion(@RequestBody ubicacionDTO: UbicacionDTO) = ubicacionService.crearUbicacion(ubicacionDTO.nombreDeLaUbicacion)


    @GetMapping
    fun getAll() = ubicacionSpringService.recuperarTodasLasUbicaciones()

}