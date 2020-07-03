package ar.edu.unq.eperdemic.spring.controllers



import ar.edu.unq.eperdemic.spring.services.EspecieSpringService
import org.springframework.web.bind.annotation.*

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@CrossOrigin
@ServiceREST
@RequestMapping("/especie")
class EspecieControllerREST(private val especieSpringService: EspecieSpringService) {

  @GetMapping()
  fun especies() = especieSpringService.recuperarTodasLasEspecies()

  @GetMapping("/{patogenoId}")
  fun recuperarPatogenosEspecie(@PathVariable patogenoId: Int) = especieSpringService.recuperarEspeciesDePatogeno(patogenoId.toLong())

}
