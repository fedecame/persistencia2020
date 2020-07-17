package ar.edu.unq.eperdemic.persistencia.dao.Redis

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import java.util.*

class ConnectRedis {
    var  redisClient:RedisClient= RedisClient.create("redis://@localhost:6379/0");
    var connection : StatefulRedisConnection<String, String> = redisClient.connect();
    var syncCommands : RedisCommands<String, String> = connection.sync();

}