import org.http4k.lens.Path
import org.http4k.routing.bind
import org.http4k.routing.websockets
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsConsumer
import org.http4k.websocket.WsMessage

val namePath = Path.of("name")

val activeWebsockets = mutableListOf<Websocket>()
val wsc: WsConsumer = { ws ->
    val name = namePath(ws.upgradeRequest)
    activeWebsockets.add(ws)
    broadcast("Server", "$name joined the server.")
    ws.onMessage { message -> broadcast(name, message.bodyString()) }
    ws.onClose {
        broadcast("Server", "Goodbye $name!")
        activeWebsockets.remove(ws)
    }
}

fun broadcast(sender: String, message: String) =
        activeWebsockets.forEach { it.send(WsMessage("$sender: $message")) }

val app = websockets(
        "/{name}" bind { ws: Websocket ->
            ws.send(WsMessage("Hello!"))
            ws.onMessage { ws.send(WsMessage("I heard: \"${it.bodyString()}\"")) }
            ws.onClose {
                ws.send(WsMessage("Goodbye!"))
            }
        },
        "/join/{name}" bind wsc
)

fun main(args: Array<String>) {
    app.asServer(Jetty(9000)).start().block()
}