import application.Application
import application.getJsonFiles
import application.i18n
import module.bot.*
import module.jackson
import module.redis.RedisFactory
import module.redis.RedisService
import module.redis.jacksonRedisCodec
import module.redis.redisFactory
import module.request.httpClient

suspend fun main(args: Array<String>) = Application.main(args, Application::configModule)

fun Application.configModule() {
    install(httpClient)

    install(jackson)

    install(i18n) {
        getJsonFiles().forEach { addPack(it.key) { it.value } }
    }
    install(jacksonRedisCodec) {
        mapper = instance(jackson)
    }

    install(redisFactory) {
        url = appEnvironment.config("bot").property("redis_url").getString()
    }

    val redisAsyncClient = RedisFactory.newAsyncClient(instance(jacksonRedisCodec))
    val redisService = RedisService(redisAsyncClient, instance(jackson))

    install(echoChatHandler) {
        setRedisService(redisService)
    }

    install(initUserHandler) {
        setRedisService(redisService)
        setDefaultLang(appEnvironment.property("bot.lang.default").getString())
    }

    install(langCommand) {
        setRedisService(redisService)
        setI18n(instance(i18n))
        languageChangeCommand = "lang"
    }
}



