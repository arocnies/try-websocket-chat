import org.http4k.lens.Path
import org.http4k.routing.bind
import org.http4k.routing.websockets
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsConsumer
import org.http4k.websocket.WsMessage

class App {
    val nameLens = Path.of("name") // Using lens for extracting path param.
    val clients = mutableListOf<Websocket>()
    fun broadcast(sender: String, message: String) = clients.forEach { it.send(WsMessage("$sender: $message")) }
    val chatWebsocket: WsConsumer = { ws ->
        val name = nameLens(ws.upgradeRequest)

        // Start tracking this new websocket.
        clients.add(ws)
        broadcast("Server", "Welcome $name!")

        // Broadcast this clients message.
        ws.onMessage {
            broadcast(name, it.bodyString())
        }

        // Broadcast the client is leaving.
        ws.onClose {
            broadcast("Server", "Goodbye $name")
            clients.remove(ws)
        }
    }

    val app = websockets(
            "/b/{name}" bind { ws: Websocket ->
                ws.send(WsMessage("Hello!"))
                ws.onMessage { ws.send(WsMessage("I heard: \"${it.bodyString()}\"")) }
                ws.onClose {
                    ws.send(WsMessage("Goodbye!"))
                }
            },
            "/{name}" bind chatWebsocket
    )
}

fun main(args: Array<String>) {
    App().app.asServer(Jetty(9000)).start().block()
}