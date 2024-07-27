cleari()
originBottomLeft()
setBackground(black)

val cb = canvasBounds

case class Rectangle(x: Double, y: Double, width: Double, height: Double) {
    def overlaps(r: Rectangle): Boolean = {
        x < r.x + r.width && x + width > r.x &&
            y < r.y + r.height && y + height > r.y
    }
}

trait GameEntity {
    def x: Double
    def y: Double
    def width: Double
    def height: Double

    def update(deltaTime: Double)
    def render()

    private def boundaryRectangle: Rectangle = {
        Rectangle(x, y, width, height)
    }

    def collidesWith(other: GameEntity): Boolean = {
        boundaryRectangle.overlaps(other.boundaryRectangle)
    }
}

class Wanderer(x0: Double, y0: Double, vel0: Vector2D) extends GameEntity {
    var x = x0
    var y = y0
    val size = 50
    var vel = vel0

    def width = size
    def height = size

    def update(deltaTime: Double) {
        x += vel.x * deltaTime
        y += vel.y * deltaTime

        if (x + size > cb.width) {
            x = cb.width - size
            vel = Vector2D(-vel.x, vel.y)
        }

        if (x < 0) {
            x = 0
            vel = Vector2D(-vel.x, vel.y)
        }

        if (y + size > cb.height) {
            y = cb.height - size
            vel = Vector2D(vel.x, -vel.y)
        }

        if (y < 0) {
            y = 0
            vel = Vector2D(vel.x, -vel.y)
        }
    }

    def render() {
        val pic = Picture.rectangle(size, size)
        pic.setPenColor(orange)
        pic.setFillColor(orange)
        pic.setPosition(x, y)
        draw(pic)
    }
}

class Player(x0: Double, y0: Double) extends GameEntity {
    var x = x0
    var y = y0
    val size = 50
    val speed = 100

    def width = size
    def height = size

    def update(deltaTime: Double) {
        if (isKeyPressed(Kc.VK_LEFT)) {
            x -= speed * deltaTime
        }
        if (isKeyPressed(Kc.VK_RIGHT)) {
            x += speed * deltaTime
        }
        if (isKeyPressed(Kc.VK_UP)) {
            y += speed * deltaTime
        }
        if (isKeyPressed(Kc.VK_DOWN)) {
            y -= speed * deltaTime
        }
    }

    def render() {
        val pic = Picture.rectangle(size, size)
        pic.setPenColor(green)
        pic.setPosition(x, y)
        draw(pic)
    }
}

class Game {
    var screen: Screen = null
    var t0 = epochTime
    def setScreen(s: Screen) {
        screen = s
    }

    def render() {
        val t1 = epochTime
        if (screen != null) {
            screen.render(t1 - t0)
        }
        t0 = t1
    }
}

trait Screen {
    val entities = ArrayBuffer.empty[GameEntity]

    def addEntity(ge: GameEntity) {
        entities.append(ge)
    }

    def showLostMessage() {
        val msg = Picture.text("You Lost", 40)
        drawCentered(msg)
    }

    def update(): Unit

    def render(deltaTime: Double) {
        erasePictures()
        for (ge <- entities) {
            ge.update(deltaTime)
        }
        update()
        for (ge <- entities) {
            ge.render()
        }

    }
}

class MyScreen extends Screen {
    val w1 = new Wanderer(cb.width / 2, cb.height / 2, Vector2D(150, 140))
    val w2 = new Wanderer(cb.width / 2 + 100, cb.height / 2 - 100, Vector2D(-150, 140))
    val p = new Player(100, 200)

    addEntity(w1)
    addEntity(w2)
    addEntity(p)

    def update() {
        if (p.collidesWith(w1) || p.collidesWith(w2)) {
            stopAnimation()
            showLostMessage()
        }
    }
}

val game = new Game()
game.setScreen(new MyScreen())

animate {
    game.render()
}
activateCanvas()