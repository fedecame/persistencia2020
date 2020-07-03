package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService

import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieDTO
import ar.edu.unq.eperdemic.spring.services.EspecieSpringService
import ar.edu.unq.eperdemic.spring.services.PatogenoSpringService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@ServiceREST
@RequestMapping("/patogeno")
class PatogenoControllerREST(private val patogenoSpringService: PatogenoSpringService ,private val patogenoService: PatogenoService, private val especieService: EspecieSpringService, private val ubicacionService: UbicacionService, private val vectorService: VectorService) {

  @PostMapping
  fun create(@RequestBody patogeno: Patogeno): ResponseEntity<Patogeno> {
    val patogenoId = patogenoService.crearPatogeno(patogeno)
    return ResponseEntity(patogenoService.recuperarPatogeno(patogenoId), HttpStatus.CREATED)
  }

  @PostMapping("/{id}")
  fun agregarEspecie(@PathVariable id: Int, @RequestBody especieDTO: EspecieDTO): ResponseEntity<EspecieDTO> {
    val especie = patogenoService.agregarEspecie(id, especieDTO.nombre, especieDTO.paisDeOrigen)
    //Esto es solo para este TP por que se olvidaron de infectar a un vector random cuando crean la especie
    val ubicacion = ubicacionService.recuperarUbicacion(especieDTO.paisDeOrigen);
    val vector = ubicacion.vectores.first()
    vectorService.infectar(vector,especie)
    // remover esta parte, ubicacionService y vectorService como colaborador interno
    val dto = EspecieDTO.from(especie)
    return ResponseEntity(dto, HttpStatus.CREATED)
  }

  @GetMapping("/{id}")
  fun findById(@PathVariable id: Int) = patogenoService.recuperarPatogeno(id)

  @GetMapping
  fun getAll() = patogenoSpringService.recuperarATodosLosPatogenos()

  @GetMapping("/infectados/{nombreDeLaEspecie}")
  fun getCantidadInfectados(@PathVariable nombreDeLaEspecie: String): Int {
          val especie = especieService.recuperarPorNombre(nombreDeLaEspecie)
          val cantidad = patogenoService.cantidadDeInfectados(especie.id!!.toInt())
    return cantidad
  }

  @GetMapping("/esPandemia/{id}")
  fun esPandemia(@PathVariable id: Int): Int {
    if(patogenoService.esPandemia(id)){
      return 1
    }
    else {
      return 0
    }
  }
}