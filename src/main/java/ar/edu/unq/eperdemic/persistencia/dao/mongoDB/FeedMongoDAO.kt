package ar.edu.unq.eperdemic.persistencia.dao.mongoDB

import ar.edu.unq.eperdemic.modelo.evento.Accion
import ar.edu.unq.eperdemic.modelo.evento.Evento
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.Arribo
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoEvento
import ar.edu.unq.eperdemic.modelo.evento.tipoEvento.TipoPatogeno
import ar.edu.unq.eperdemic.persistencia.dao.FeedDAO
import com.mongodb.BasicDBObject
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Indexes

class FeedMongoDAO : GenericMongoDAO<Evento>(Evento::class.java), FeedDAO {

    //Faltaria poder recordar por un Id determinado?

    fun getByTipoPatogeno(tipo: String): List<Evento?> = findEq("tipoPatogeno", tipo)

    fun getByTipoEvento(tipoEvento: TipoEvento): List<Evento?> = findEq("tipoEvento", tipoEvento)

    override fun feedPatogeno(tipoPatogeno : String) : List<Evento>{
        //Me fijo que: Dado un evento, ese evento
        //      ((Haya sido generado por una Accion de Pandemia **O** Por Contagio por Primera vez) **Y** (Sea del tipo de patogeno dado))
        val match = Aggregates.match(//Aca falta la logica de mutacion
                and
                    (or
                        (eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name),
                         eq("accionQueLoDesencadena", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)),
                    eq("tipoPatogeno", tipoPatogeno))
        )
      val sort = Aggregates.sort(Indexes.descending("n"))
      return aggregate(listOf(match, sort), Evento::class.java)
    }

    override fun feedVector(tipoPatogeno: String): List<Evento> {
        TODO("Not yet implemented")
    }
    fun feedUbicacion(nombreUbicacion: String,conectados:List<String>): List<Evento> {
        var conectadoss=conectados
        conectadoss+=nombreUbicacion
        val match = Aggregates.match(`in`("nombreUbicacion", conectadoss))
        val lista=Aggregates.match(eq("tipoEvento.tipo","Arribo"))
        val ordenados=Aggregates.sort(Indexes.descending("fecha"))
        return aggregate(listOf(match,lista,ordenados), Evento::class.java)
    }

     override fun feedUbicacion(nombreUbicacion: String): List<Evento> {
//val match= Aggregates.match(Filters.eq("eventos.tipoEvento","Arribo"))
        val match= this.findEq("nombreUbicacion",nombreUbicacion)
        return match
    }

    //Cambiar de lugar el crear evento de infectar a contagiar y cambiar para que contagiar o mutar tire excepcion y hau que catchearlo
    override fun especieYaEstabaEnLaUbicacion(nombreUbicacion: String, tipoPatogenoDeLaEspecie: String, nombreEspecie : String): Boolean =
            find(and
                    (and
                         (eq("nombreUbicacion", nombreUbicacion),
                          eq("accionQueLoDesencadena", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)),
                    (and
                         (eq("tipoPatogeno", tipoPatogenoDeLaEspecie),
                         eq("nombreEspecie", nombreEspecie))))).isNotEmpty()
    //Si existe un evento de contagio por primera vez en ubicacion, entonces es unico o bien, no existe
    fun vectorFueContagiadoAlMover(_nombreUbicacion:String, _idVectorInfectado:Int, _idVectorAInfectar:Int):Boolean=
        find(and
        (and
        (eq("nombreUbicacion", _nombreUbicacion),
                eq("accionQueLoDesencadena", Accion.PADECE_ENFERMEDAD.name)),
                (and
                (eq("idVector", _idVectorInfectado),
                        eq("idVectorAInfectar", _idVectorAInfectar))))).isNotEmpty()

}