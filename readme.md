# telegram-stickers-bot

telegram表情包下载机器人根据:<https://github.com/phoenixlzx/telegram-stickerimage-bot/> 重构而来
<details>
本项目基本是抄代码，各种缝合，ktor占了很大一部分。

ktor真的和kotlin太配了。
</details>

## 如何使用？

```shell
git clone https://github.com/YiGuan-z/telegram-stickers-collect-bot
```

```shell
cd telegram-stickers-collect-bot
```

```shell
./gradlew updateI18n
```

```shell
./gradlew build
```

需要redis和telegram-bot-token

向用户环境中添加以下环境变量：

---

```
TG_BOT_TOKEN=1111111111:ABCD-EFGH-IJGGKSLSJFGHAJFYGA
```

```
TG_BOT_REDIS_URL=your redis url
```

```
TG_BOT_MASTER=your telegram username
```

<details>

参考了ktor的一些设计思路。<br/> ~~基本上抄了一大半~~<br/>
不得不说kotlin官方的设计理念非常好，DSL的设计非常的优雅，可读性非常棒，就是容易写成六亲不认的样子。

使用的是kotlin-telegram-bot依赖。

~~这个bot的日志模块我没看懂~~，我只能再添加一个日志模块进去了。
它的日志交给了okHttp3进行处理，我就使用slf4j吧。

当场加深了我对于kotlin协程的理解，因为后端一般不怎么用这玩意，或许是我没碰到过批量的小任务吧。

<details>
<summary>一些奇奇怪怪的问题</summary>

## 莫名其妙的依赖启动

像这种手动启动依赖的好处在于，我不会遇到莫名其妙的依赖自动启动。

(并且声明一下启动依赖的配置基本上也不会太难，都是一次写完然后就一直使用。)

在Spring应用程序中，官方专门给@SpringBootApplication注解中添加了忽略启动依赖的属性。就是专门防止依赖自启动。
我编写代码的时候，就遇到过这个问题，我压根没有引入GSON，但它给我报错GSON装配失败。后来查出来是一个依赖包中出现的GSON依赖，而Spring又刚好扫描到了。
往注解上添加忽略属性才解决这个问题。

自动装配确实是好东西，但是，自动装配一些我所不需要的依赖，就是一个大问题。

像ktor这样的手动设置依赖，我很喜欢。它能准确的告知我装配了什么依赖，而不是像SpringBoot那样，在某一次依赖包大更新后，莫名其妙的出现许多自动装配，
那些自动装配应该是它所属的依赖包内部使用的，而不是我们所使用的，我们并不需要它。

## 好想把telegram第三方依赖库的处理逻辑做成过滤器样式啊

io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0

使用的这个依赖库，不得不说，非常惊艳我，因为它使用的dsl语法和kotlin非常配合，但是感觉消息处理逻辑好奇怪，就不能做成某个处理器处理消息后再向下
传递是否继续处理的方式吗，拿数字给消息处理器做一个排序或者根据添加顺序来决定消息处理器的位置。

## 错误的uri映射

在打包为jar包后，获取到的文件路径就不在文件系统中，而是在jar包中了，所以定位到目录的时候会出现错误，因为就和网络链接一样，jar中的文件也是一个
专属的链接，所以无法通过文件系统访问到jar包内部资源。

所以，使用getResourceAsStream将其读入到内存中，再创建一个临时文件来获取流中的内容。

</details>
</details>

