package ar.edu.unq.eperdemic.spring.controllers

import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.spring.controllers.dto.EspecieLiderDTO
import ar.edu.unq.eperdemic.spring.controllers.dto.ReporteDeContagiosDTO
import ar.edu.unq.eperdemic.spring.services.UbicacionSpringService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.lang.Exception

@CrossOrigin
@ServiceREST
@RequestMapping("/estadisticas")
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class EstadisticasControllerREST(private val groupName : String, private val estadisticasService: EstadisticasService, private val ubicacionSpringService: UbicacionSpringService,
                                 private val patogenoService:  PatogenoService) {

  @GetMapping("/especieLider")
  fun especieLider() = estadisticasService.especieLider()

  @GetMapping("/lideres")
  fun lideres()  : List<EspecieLiderDTO> {
    val lideres = estadisticasService.lideres()
    val listaEspeciesLideres :List<EspecieLiderDTO> =
                              lideres.map{ lider -> EspecieLiderDTO.from( lider.nombre ,lider.patogeno?.tipo
                                                                          , patogenoService.cantidadDeInfectados(lider.id!!.toInt())
                                                                          , patogenoService.esPandemia(lider.id!!.toInt()))
                                        }
    return listaEspeciesLideres
  }

  @GetMapping("/reporteDeContagios")
  fun reporteDeContagios(): List<ReporteDeContagiosDTO> {
    try {
      val todasLasUbicaciones = ubicacionSpringService.recuperarTodasLasUbicaciones()

      val contagios = todasLasUbicaciones.map { ubicacion ->
        ReporteDeContagiosDTO
                .from(estadisticasService.reporteDeContagios(ubicacion.nombreUbicacion!!),
                        ubicacion.nombreUbicacion!!,
                        groupName
                )
      }
      return contagios
    } catch (exception: Throwable) {

      exception.printStackTrace()
      throw exception
    }
  }

}