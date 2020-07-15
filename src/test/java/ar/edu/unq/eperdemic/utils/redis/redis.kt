package ar.edu.unq.eperdemic.utils.redis

import ar.edu.unq.eperdemic.estado.Sano
import ar.edu.unq.eperdemic.modelo.Antidoto
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.exception.AnalisisDeSangreImposibleHacer
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongoDB.FeedMongoDAO
import ar.edu.unq.eperdemic.services.MegalodonService
import ar.edu.unq.eperdemic.services.impl.FeedServiceImpl
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import ar.edu.unq.eperdemic.tipo.Humano
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.properties.Delegates

class redis {
    var vectorServiceImpl=VectorServiceImpl(HibernateVectorDAO(),HibernateUbicacionDAO(),FeedServiceImpl(FeedMongoDAO()))
    var ubicacionServiceImpl=UbicacionServiceImpl(HibernateUbicacionDAO())
    var patogenoService=PatogenoServiceImpl(HibernatePatogenoDAO(),HibernateEspecieDAO())
    lateinit var vector :Vector
    lateinit var vectorCreado :Vector
    var especieCreada by Delegates.notNull<Int>()
    lateinit var especie :Especie

    @Before
fun setUp(){

    var especie=Especie()
    var patogeno=Patogeno()
    patogeno.tipo="Bacteria"
     vector= Vector()
    var antidoto=Antidoto("quedateEnCasa")
    var florencioVarela= ubicacionServiceImpl.crearUbicacion("Florencio Varela")
    vector.ubicacion=florencioVarela
    vector.tipo= Humano()
    especie.nombre="Covic-19"
    var idPatogeno= patogenoService.crearPatogeno(patogeno);
    especie.patogeno=patogenoService.recuperarPatogeno(idPatogeno)
    patogenoService.crearEspecie(especie)
     especieCreada= patogenoService.crearEspecie(especie)
     vectorCreado=vectorServiceImpl.crearVector(vector)




}



    @Test(expected = AnalisisDeSangreImposibleHacer::class)
    fun vectorVaAlMedicoPeroEsImposibleHacerElAnilisis(){
        vectorServiceImpl.infectar(vectorCreado,patogenoService.recuperarEspecie(especieCreada))
        Thread.sleep(1001)
        vectorServiceImpl.irAlMedico(vectorCreado, patogenoService.recuperarEspecie(especieCreada))

    }


    @Test
    fun vectorVaAlMedicoAntesDeQueSeaImposibleHacerElAnilisis(){
        vectorServiceImpl.infectar(vectorCreado,patogenoService.recuperarEspecie(especieCreada))
        Thread.sleep(50)
        vectorServiceImpl.irAlMedico(vectorCreado, patogenoService.recuperarEspecie(especieCreada))
        var vectorCurado= vectorServiceImpl.recuperarVector(vector.id?.toInt()!!)
        Assert.assertTrue(vectorCurado.estado.javaClass== Sano().javaClass)

    }
}

