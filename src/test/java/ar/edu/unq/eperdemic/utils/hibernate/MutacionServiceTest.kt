package ar.edu.unq.eperdemic.utils.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.exception.EspecieNoCumpleRequisitosParaMutarException
import ar.edu.unq.eperdemic.modelo.exception.EspecieNotFoundRunTimeException
import ar.edu.unq.eperdemic.modelo.exception.IDMutacionNoEncontradoException
import ar.edu.unq.eperdemic.modelo.tipoMutacion.*
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDataDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateMutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernatePatogenoDAO
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.impl.MutacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.junit.After
import org.junit.Assert
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
        patogenoDAO = HibernatePatogenoDAO()
        especieDAO = HibernateEspecieDAO()
        patogenoService = PatogenoServiceImpl(patogenoDAO, especieDAO)
        mutacionDAO = HibernateMutacionDAO()

        mutacionService = MutacionServiceImpl(mutacionDAO, patogenoService)

        mutacion = Mutacion()
        mutacion1 = Mutacion()
        mutacion2 = Mutacion()
        mutacion3 = Mutacion()
        mutacion4 = Mutacion()
        mutacion5 = Mutacion()

        mutacion.adnNecesario = 3
        mutacion.tipo = MutacionLetalidad()
        mutacionService.crearMutacion(mutacion)
        mutacion.mutacionesNecesarias.addAll(mutableListOf(mutacion1))
        mutacion.mutacionesDesbloqueables.addAll(mutableListOf(mutacion3, mutacion5))

        mutacion1.adnNecesario = 1
        mutacion1.tipo = MutacionFactorContagioInsecto()
        mutacionService.crearMutacion(mutacion1)
        mutacion1.mutacionesDesbloqueables.add(mutacion)

        mutacion2.adnNecesario = 2
        mutacion2.tipo = MutacionFactorContagioAnimal()
        mutacionService.crearMutacion(mutacion2)
        mutacion2.mutacionesNecesarias.add(mutacion1)
        mutacion2.mutacionesDesbloqueables.add(mutacion)

        mutacion3.adnNecesario = 1
        mutacion3.tipo = MutacionFactorContagioAnimal()
        mutacionService.crearMutacion(mutacion3)
        mutacion3.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion, mutacion2))
        mutacion3.mutacionesDesbloqueables.addAll(mutableListOf(mutacion4, mutacion5))

        mutacion4.adnNecesario = 2
        mutacion4.tipo = MutacionFactorContagioHumano()
        mutacionService.crearMutacion(mutacion4)
        mutacion4.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3))
        mutacion4.mutacionesDesbloqueables.add(mutacion5)

        mutacion5.adnNecesario = 4
        mutacion5.tipo = MutacionDefensaMicroorganismos()
        mutacionService.crearMutacion(mutacion5)
        mutacion5.mutacionesNecesarias.addAll(mutableListOf(mutacion1, mutacion2, mutacion, mutacion3, mutacion4))

        mutacionService.actualizarMutacion(mutacion)

        mutacionService.actualizarMutacion(mutacion1)
        mutacionService.actualizarMutacion(mutacion2)
        mutacionService.actualizarMutacion(mutacion3)
        mutacionService.actualizarMutacion(mutacion4)
        mutacionService.actualizarMutacion(mutacion5)

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
    }

    //exception
    @Test(expected = EspecieNotFoundRunTimeException::class)
    fun mutarUnaEspecieNoPersistida() {
        val especieNoPersistida = Especie()
        especieNoPersistida.nombre = "EspecieDeVirus"
        especieNoPersistida.paisDeOrigen = "USA"
        especieNoPersistida.mutacionesDesbloqueadas.addAll(mutableListOf(mutacion1, mutacion2))
        especieNoPersistida.cantidadInfectadosParaADN = 5
        especieNoPersistida.patogeno = patogeno
        especieNoPersistida.id = especieNoPersistida.id ?: 250

        mutacionService.mutar(especieNoPersistida.id!!, mutacion.id!!.toInt())
    }

    //exception
    @Test(expected = IDMutacionNoEncontradoException::class)
    fun mutarAUnaMutacionNoPersistida() {
        val mutacionNoPersistida = Mutacion()
        mutacionNoPersistida.adnNecesario = 1
        mutacionNoPersistida.tipo = MutacionFactorContagioHumano()
        mutacionNoPersistida.mutacionesNecesarias.add(mutacion1)
        mutacionNoPersistida.mutacionesDesbloqueables.add(mutacion4)
        mutacionNoPersistida.id = mutacionNoPersistida.id ?: 555

        mutacionService.mutar(especie.id!!, mutacionNoPersistida.id!!.toInt())
    }

    //test ManyToMany
    @Test
    fun agregarMismaMutacionAVariasEspecies() {
        val especieNueva = Especie()
        especieNueva.nombre = "Sarampion"
        especieNueva.paisDeOrigen = "Australia"
        especieNueva.mutacionesDesbloqueadas.add(mutacion1)
        especieNueva.cantidadInfectadosParaADN = 55
        especieNueva.patogeno = patogeno
        patogenoService.crearEspecie(especieNueva)
        Assert.assertEquals(especie.mutacionesDesbloqueadas.first(), especieNueva.mutacionesDesbloqueadas.first())
        val especieViejaDB = patogenoService.recuperarEspecie(especie.id!!)
        Assert.assertEquals(especieViejaDB.mutacionesDesbloqueadas.first().id!!, especieNueva.mutacionesDesbloqueadas.first().id!!)
        Assert.assertTrue(especieViejaDB.mutacionesDesbloqueadas.size == 1)
        Assert.assertTrue(especieNueva.mutacionesDesbloqueadas.size == 1)
    }

    //agregar otros 3 tests de ManyToMany

    @Test
    fun mutarUnaEspeciePersistidaAUnaMutacionPersistidaCumpliendoConLosRequisitos() {
        mutacionService.mutar(especie.id!!, mutacion1.id!!.toInt())

        Assert.assertTrue(mutacion1.mutacionesDesbloqueables.size == 1)
        Assert.assertNotNull(mutacion1.mutacionesDesbloqueables.find { it.id == mutacion.id })
        val mutacionDB = mutacionService.recuperarMutacion(mutacion1.id!!.toInt())
        Assert.assertTrue(mutacionDB.mutacionesDesbloqueables.isNotEmpty())
        Assert.assertTrue(mutacionDB.mutacionesDesbloqueables.size == 1)
        Assert.assertNotNull(mutacionDB.mutacionesDesbloqueables.find { it.id == mutacion.id })

        Assert.assertNotEquals(13, especie.cantidadInfectadosParaADN)
        Assert.assertNull(especie.mutaciones.find { it.id == mutacion1.id })
        Assert.assertNull(especie.mutacionesDesbloqueadas.find { it.id == mutacion.id })
        val especieDB = patogenoService.recuperarEspecie(especie.id!!)
        Assert.assertEquals(13, especieDB.cantidadInfectadosParaADN)
        Assert.assertNotNull(especieDB.mutaciones.find { it.id == mutacion1.id })
        Assert.assertNotNull(especieDB.mutacionesDesbloqueadas.find { it.id == mutacion.id })
    }

    //exception
    @Test(expected = EspecieNoCumpleRequisitosParaMutarException::class)
    fun mutarUnaEspeciePersistidaAUnaMutacionPersistidaSinSuficienteADN() {
        especie.cantidadInfectadosParaADN = 2
        patogenoService.actualizarEspecie(especie)
        val especieDB = patogenoService.recuperarEspecie(especie.id!!)

        mutacionService.mutar(especieDB.id!!, mutacion1.id!!.toInt())
    }

    //exception
    @Test(expected = EspecieNoCumpleRequisitosParaMutarException::class)
    fun mutarUnaEspeciePersistidaAUnaMutacionPersistidaSinTenerTodasLasMutacionesPreviasNecesarias() {
        especie.mutacionesDesbloqueadas.add(mutacion5)
        mutacionService.mutar(especie.id!!, mutacion5.id!!.toInt())
    }

    //exception
    @Test(expected = EspecieNoCumpleRequisitosParaMutarException::class)
    fun mutarUnaEspeciePersistidaAUnaMutacionPersistidaSinTenerDesbloqueadaLaMutacion() {
        Assert.assertTrue(especie.mutaciones.isEmpty())
        mutacionService.mutar(especie.id!!, mutacion1.id!!.toInt())
        mutacionService.mutar(especie.id!!, mutacion2.id!!.toInt())
    }

    @Test
    fun mutarUnaEspeciePersistidaAUnaMutacionPersistidaYMutarlaDeNuevoAUnaMutacionPersistidaRecienDesbloqueada() {
        especie.cantidadInfectadosParaADN = 22
        patogenoService.actualizarEspecie(especie)

        mutacionService.mutar(especie.id!!, mutacion1.id!!.toInt())
        mutacionService.mutar(especie.id!!, mutacion.id!!.toInt())

        val especieDB = patogenoService.recuperarEspecie(especie.id!!)
        Assert.assertEquals(2, especieDB.cantidadInfectadosParaADN)
        Assert.assertNotNull(especieDB.mutaciones.find { it.id == mutacion.id })
        Assert.assertNotNull(especieDB.mutaciones.find { it.id == mutacion1.id })
        Assert.assertTrue(especieDB.mutaciones.size == 2)
        Assert.assertNotNull(especieDB.mutacionesDesbloqueadas.find { it.id == mutacion.id })
        Assert.assertNotNull(especieDB.mutacionesDesbloqueadas.find { it.id == mutacion3.id })
        Assert.assertNotNull(especieDB.mutacionesDesbloqueadas.find { it.id == mutacion5.id })
        Assert.assertTrue(especieDB.mutacionesDesbloqueadas.size == 4)
    }

    @Test
    fun testAlCrearUnaMutacionSeRecuperaConSuId(){
        val recuperado = mutacionService.recuperarMutacion(1)
        Assert.assertEquals(1,recuperado.id!!)
    }

    @Test(expected = IDMutacionNoEncontradoException::class)
    fun testAlRecuperarUnaMutacionConIdInexistenteRetornaNull(){
        mutacionService.recuperarMutacion(50)
    }

    @After
    fun eliminarTodo(){
        TransactionRunner.runTrx {
            HibernateDataDAO().clear()
        }
    }
}