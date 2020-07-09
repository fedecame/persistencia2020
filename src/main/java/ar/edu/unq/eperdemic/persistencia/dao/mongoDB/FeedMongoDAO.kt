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

    fun getByTipoPatogeno(tipo: String): List<Evento?> = findEq("tipoPatogeno", tipo)

    fun getByTipoEvento(tipoEvento: TipoEvento): List<Evento?> = findEq("tipoEvento", tipoEvento)

    override fun feedPatogeno(tipoPatogeno : String) : List<Evento>{
        val match = Aggregates.match(//Aca falta la logica de mutacion
                and(
                     or(
                        or(
                           eq("accionQueLoDesencadena", Accion.ESPECIE_MUTADA.name),
                           eq("accionQueLoDesencadena", Accion.ESPECIE_CREADA.name)
                        ),
                        or(
                           eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name),
                           eq("accionQueLoDesencadena", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)
                        )
                     ),
                    eq("tipoPatogeno", tipoPatogeno)
                )
        )
      val sort = Aggregates.sort(Indexes.descending("fecha"))
      return aggregate(listOf(match, sort), Evento::class.java)
    }

    override fun feedVector(vectorId: Long): List<Evento> {
        val match = Aggregates.match(
                    or(
                        eq("idVectorQueSeMueve", vectorId),
                        or(
                            eq("idVectorinfectado", vectorId),
                            eq("idVectorQueInfecta", vectorId)
                        )
                    )
        )
        val sort = Aggregates.sort(Indexes.descending("fecha"))
        return aggregate(listOf(match, sort), Evento::class.java)
    }

    override fun feedUbicacion(nombreUbicacion: String,conectados:List<String>): List<Evento> {
        var conectadoss=conectados
        conectadoss+=listOf(nombreUbicacion)
        val match = Aggregates.match(or(
                `in`("ubicacionDestino", conectadoss),
                `in`("ubicacionContagio", conectadoss)
        ))
        val lista= Aggregates.match(or(eq("accionQueLoDesencadena","ARRIBO"),(eq("accionQueLoDesencadena","Vector_Contagia_Al_Mover"))))

        val ordenados=Aggregates.sort(Indexes.descending("fecha"))
        return aggregate(listOf(match,lista,ordenados), Evento::class.java)
    }

    override fun especieYaEstabaEnLaUbicacion(nombreUbicacion: String, tipoPatogenoDeLaEspecie: String, nombreEspecie : String): Boolean =
            find(and
                    (and
                         (eq("ubicacionContagio", nombreUbicacion),
                          eq("accionQueLoDesencadena", Accion.PATOGENO_CONTAGIA_1RA_VEZ_EN_UBICACION.name)),
                    (and
                         (eq("tipoPatogeno", tipoPatogenoDeLaEspecie),
                         eq("nombreEspecie", nombreEspecie))))).isNotEmpty()

    override fun especieYaTieneEventoPorPandemia(tipoPatogenoDeLaEspecie: String, nombreEspecie : String): Boolean =
            find(and(
                    eq("accionQueLoDesencadena", Accion.PATOGENO_ES_PANDEMIA.name),
                    (and
                    (eq("tipoPatogeno", tipoPatogenoDeLaEspecie),
                            eq("nombreEspecie", nombreEspecie))))).isNotEmpty()

    fun vectorFueContagiadoAlMover(_nombreUbicacion:String, _idVectorInfectado:Int, _idVectorAInfectar:Int):Boolean=
        find(and
        (and
        (eq("ubicacionContagio", _nombreUbicacion),
                eq("accionQueLoDesencadena", Accion.CONTAGIO_NORMAL.name)),
                (and
                (eq("idVectorQueInfecta",_idVectorInfectado ),
                        eq("idVectorinfectado",_idVectorAInfectar ))))).isNotEmpty()

}