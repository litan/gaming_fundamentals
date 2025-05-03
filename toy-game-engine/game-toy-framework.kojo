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
    def pic: Picture

    def update(deltaTime: Double)
    def render()

    private def boundaryRectangle: Rectangle = {
        Rectangle(x, y, width, height)
    }

    def collidesWith(other: GameEntity): Boolean = {
        boundaryRectangle.overlaps(other.boundaryRectangle)
    }

    def visible() {
        pic.setPosition(x, y)
        pic.visible()
    }

    def invisible() {
        pic.invisible()
    }

    def drawAndHide() {
        pic.draw()
        pic.invisible()
    }

    def erase() {
        pic.erase()
    }
}

class Game {
    var screen: Screen = null
    var t0 = epochTime
    def setScreen(s: Screen) {
        println("Setting new screen")
        if (screen != null) {
            screen.hide()
        }
        screen = s
        screen.show()
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
    var showing = false

    def show() {
        showing = true
        for (ge <- entities) {
            ge.visible()
        }
    }

    def hide() {
        showing = false
        for (ge <- entities) {
            ge.invisible()
        }
    }

    def addEntity(ge: GameEntity) {
        entities.append(ge)
        ge.drawAndHide()
    }

    def removeEntity(ge: GameEntity) {
        ge.erase()
        entities.subtractOne(ge)
    }

    def showLostMessage() {
        val msg = Picture.text("You Lost", 40)
        drawCentered(msg)
    }

    def cleanScreen() {
        for (ge <- entities) {
            ge.invisible()
        }
    }

    def update(): Unit

    def render(deltaTime: Double) {
        cleanScreen()
        for (ge <- entities) {
            ge.update(deltaTime)
        }
        update()
        if (showing) {
            for (ge <- entities) {
                ge.render()
            }
        }
    }
}

