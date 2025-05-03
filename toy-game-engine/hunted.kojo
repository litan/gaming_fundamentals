// #include game-toy-framework

cleari()
originBottomLeft()
setBackground(black)

val cb = canvasBounds

class Wanderer(x0: Double, y0: Double, vel0: Vector2D) extends GameEntity {
    var x = x0
    var y = y0
    val size = 50
    var vel = vel0

    def width = size
    def height = size

    val pic = Picture.rectangle(size, size)
    pic.setPenColor(orange)
    pic.setFillColor(orange)

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
        pic.setPosition(x, y)
        pic.visible()
    }
}

class Player(x0: Double, y0: Double) extends GameEntity {
    var x = x0
    var y = y0
    val size = 64
    val speed = 100

    def width = size
    def height = size

    val assetsDir = kojoCtx.baseDir
    val sheet = SpriteSheet(s"$assetsDir/man-sheet.png", 64, 64)
    val pic1 = Picture.image(sheet.imageAt(0, 0))
    val pic2 = Picture.image(sheet.imageAt(1, 0))
    val pic3 = Picture.image(sheet.imageAt(2, 0))
    val pic4 = Picture.image(sheet.imageAt(3, 0))

    val pic = picBatch(pic1, pic2, pic3, pic4)

    def setPosition(x0: Double, y0: Double) {
        x = x0
        y = y0
    }

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
        pic.setPosition(x, y)
        pic.showNext(200)
        pic.visible()
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
            drawCenteredMessage("You Lost", red, 30)
        }
    }
}

class MyScreen2 extends Screen {
    val w1 = new Wanderer(cb.width / 2, cb.height / 2, Vector2D(150, 140))
    val w2 = new Wanderer(cb.width / 2 + 100, cb.height / 2 - 100, Vector2D(-150, 140))
    val w3 = new Wanderer(cb.width / 2 + 100, cb.height / 2 + 100, Vector2D(-150, 140))
    val p = new Player(100, 200)
    addEntity(w1)
    addEntity(w2)
    addEntity(w3)
    addEntity(p)

    def update() {
        if (p.collidesWith(w1) || p.collidesWith(w2) || p.collidesWith(w3)) {
            p.setPosition(10, 10)
            game.setScreen(screen1)
        }
    }
}

val game = new Game()
val screen1 = new MyScreen()
val screen2 = new MyScreen2()
game.setScreen(screen2)

animate {
    game.render()
}
activateCanvas()