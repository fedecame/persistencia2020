package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.tipoMutacion.*
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateMutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.impl.MutacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import org.junit.Before
import org.junit.Test

class MutacionServiceTest {

    lateinit var mutacion: Mutacion
    lateinit var mutacion1: Mutacion
    lateinit var mutacion2: Mutacion
    lateinit var mutacion3: Mutacion
    lateinit var mutacion4: Mutacion
    lateinit var mutacion5: Mutacion
    lateinit var mutacionService: MutacionService
    lateinit var patogeno: Patogeno
    lateinit var especie: Especie
    lateinit var patogenoService: PatogenoService
    lateinit var patogenoDAO: PatogenoDAO
    lateinit var especieDAO: EspecieDAO
    lateinit var mutacionDAO: MutacionDAO

    @Before
    fun setUp(){
        mutacion = Mutacion()
        mutacion1 = Mutacion()
        mutacion2 = Mutacion()
        mutacion3 = Mutacion()
        mutacion4 = Mutacion()
        mutacion5 = Mutacion()

        mutacion.adnNecesario = 3
        mutacion.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2))
        mutacion.mutacionesDesbloqueables.addAll(mutableListOf(mutacion3, mutacion5))
        mutacion.tipo = MutacionLetalidad()

        mutacion1.adnNecesario = 1
        mutacion1.mutacionesDesbloqueables.add(mutacion)
        mutacion1.tipo = MutacionFactorContagioInsecto()

        mutacion2.adnNecesario = 2
        mutacion2.mutacionesNecesarias.add(mutacion1)
        mutacion2.mutacionesDesbloqueables.add(mutacion)
        mutacion2.tipo = MutacionFactorContagioAnimal()

        mutacion3.adnNecesario = 1
        mutacion3.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion, mutacion2))
        mutacion3.mutacionesDesbloqueables.addAll(mutableListOf(mutacion4, mutacion5))
        mutacion3.tipo = MutacionFactorContagioAnimal()

        mutacion4.adnNecesario = 2
        mutacion4.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3))
        mutacion4.mutacionesDesbloqueables.add(mutacion5)
        mutacion4.tipo = MutacionFactorContagioHumano()

        mutacion5.adnNecesario = 4
        mutacion5.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3, mutacion4))
        mutacion5.tipo = MutacionDefensaMicroorganismos()

        patogenoDAO = HibernatePatogenoDAO()
        especieDAO = HibernateEspecieDAO()
        patogenoService = PatogenoServiceImpl(patogenoDAO, especieDAO)
        mutacionDAO = HibernateMutacionDAO()

        mutacionService = MutacionServiceImpl(mutacionDAO, patogenoService)

        patogeno = Patogeno()
        patogeno.cantidadDeEspecies = 1
        patogeno.tipo = "Virus"
        patogeno.factorContagioHumano = 80
        patogeno.factorContagioInsecto = 15
        patogeno.factorContagioAnimal = 50
        patogeno.defensaContraMicroorganismos = 30
        patogeno.letalidad = 5

        especie = Especie()
        especie.nombre = "Corona"
        especie.paisDeOrigen = "China"
        especie.mutacionesDesbloqueadas.add(mutacion1)
        especie.cantidadInfectadosParaADN = 18
        especie.patogeno = patogeno

        patogenoService.crearPatogeno(patogeno)
        patogenoService.crearEspecie(especie)

        mutacionService.crearMutacion(mutacion)
        mutacionService.crearMutacion(mutacion1)
    }

    @Test
    fun mutar() {
//        mutacionService.mutar(especieDBId, mutacionDB.id)
    }
}