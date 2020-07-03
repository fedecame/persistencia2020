package ar.edu.unq.eperdemic.spring.services.data


import ar.edu.unq.eperdemic.dto.VectorFrontendDTO
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.spring.persistencia.repositorios.VectorRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
@Transactional
class Data_service(
        private val vectorRepository: VectorRepository,
        private val ubicacionService: UbicacionService,
        private val vectorService: VectorService
) {


    @EventListener
    fun appready(event: ApplicationReadyEvent) {
        if (noDataLoaded()) {
            loadData()
        }
    }

    private fun noDataLoaded(): Boolean {
        return vectorRepository.findAll().isEmpty()
    }


    private fun loadData() {
        nombreDeUbicaciones.shuffle()
        val subList = nombreDeUbicaciones.subList(0, 10)
        val ubicacionesAUsar = subList.map { nombreDeUbicacion ->  ubicacionService.crearUbicacion(nombreDeUbicacion)}
        ubicacionesAUsar.forEach { ubicacion ->
            agregarVectores(ubicacion)
        }
        generarGrafoDeUbicaciones(ubicacionesAUsar)
        println("****** LOAD DATA FINISHED ****")
    }

    private fun generarGrafoDeUbicaciones(ubicacionesAUsar: List<Ubicacion>) {
        val ubicacionA = ubicacionesAUsar[0]
        val ubicacionB = ubicacionesAUsar[1]
        val ubicacionC = ubicacionesAUsar[2]
        val ubicacionD = ubicacionesAUsar[3]
        val ubicacionE = ubicacionesAUsar[4]
        val ubicacionF = ubicacionesAUsar[5]
        val ubicacionG = ubicacionesAUsar[6]
        val ubicacionH = ubicacionesAUsar[7]
        val ubicacionI = ubicacionesAUsar[8]
        val ubicacionJ = ubicacionesAUsar[9]

        val terrestre = "Terrestre"
        val aereo = "Aereo"
        val maritimo = "Maritimo"

        conectarBidireccional(ubicacionA, ubicacionB, aereo)
        conectarBidireccional(ubicacionA, ubicacionD, terrestre)
        conectarBidireccional(ubicacionA, ubicacionF, maritimo)
        conectarBidireccional(ubicacionB, ubicacionC, terrestre)
        conectarBidireccional(ubicacionB, ubicacionD, maritimo)
        conectarBidireccional(ubicacionD, ubicacionE, aereo)
        conectarBidireccional(ubicacionD, ubicacionF, terrestre)
        conectarBidireccional(ubicacionE, ubicacionJ, terrestre)
        conectarBidireccional(ubicacionE, ubicacionI, maritimo)
        conectarBidireccional(ubicacionF, ubicacionG, terrestre)
        conectarBidireccional(ubicacionF, ubicacionI, aereo)
        conectarBidireccional(ubicacionF, ubicacionI, maritimo)
        conectarBidireccional(ubicacionF, ubicacionH, aereo)
        conectarBidireccional(ubicacionG, ubicacionI, aereo)
        conectarBidireccional(ubicacionG, ubicacionH, maritimo)
        conectarBidireccional(ubicacionH, ubicacionI, terrestre)
    }

    private fun conectarBidireccional(unaUbicacion: Ubicacion, otraUbicacion: Ubicacion, tipoDeCamino: String) {
        ubicacionService.conectar(unaUbicacion.nombreUbicacion, otraUbicacion.nombreUbicacion, tipoDeCamino)
        ubicacionService.conectar(otraUbicacion.nombreUbicacion, unaUbicacion.nombreUbicacion, tipoDeCamino)
    }

    private fun agregarVectores(ubicacion: Ubicacion) {
        for (i in 1..Random.nextInt(10, 200)) {
            val vectorDto = VectorFrontendDTO(tipoDeVectorRandom(), ubicacion)
            vectorService.crearVector(vectorDto.aModelo())
        }
    }


    final val tipoDeVectores = VectorFrontendDTO.TipoDeVector.values()
    final val tipoDeVectorSize = tipoDeVectores.size
    private fun tipoDeVectorRandom() = tipoDeVectores.get(Random.nextInt(tipoDeVectorSize))

}





