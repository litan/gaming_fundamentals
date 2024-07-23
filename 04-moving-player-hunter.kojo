// #exec
// #include /gaming.kojo

object Launcher {
    def main(args: Array[String]): Unit = {
        if (StartupHelper.startNewJvmIfRequired()) return ; // This handles macOS support and helps on Windows.
        createApplication()
    }

    def createApplication(): Unit = {
        new Lwjgl3Application(new Main(), defaultConfig)
    }

    def defaultConfig = {
        val configuration = new Lwjgl3ApplicationConfiguration()
        configuration.setTitle("Moving Character")
        configuration.useVsync(true)
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)
        configuration.setWindowedMode(1000, 800)
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
        configuration;
    }
}

class Main extends GdxGame {
    override def create(): Unit = {
        super.create()
        setScreen(new GameScreen())
    }
}

class GameScreen extends GdxScreen {
    val player = new Player(100, 100)
    stage.addEntity(player)

    val hunter = new Hunter(300, 300, new Vector2(200, 100))
    stage.addEntity(hunter)

    def update(dt: Float) {

    }
}

class Player(x0: Float, y0: Float) extends GameEntity(x0, y0) {
    def vec(x: Float, y: Float) = new Vector2(x, y)

    val renderer = new SpriteRenderer(this, "blue-sq.png")
    val physics = new PhysicsBehavior(this)

    val wb = new WorldBoundsCapability(this)
    val speed = 3000

    def update(dt: Float) {
        val nv = physics.velocity
        nv.scl(0.9f)

        if (isKeyPressed(Keys.UP)) {
            physics.addVelocity(vec(0, speed * dt))
        }
        if (isKeyPressed(Keys.DOWN)) {
            physics.addVelocity(vec(0, -speed * dt))
        }
        if (isKeyPressed(Keys.LEFT)) {
            physics.addVelocity(vec(-speed * dt, 0))
        }
        if (isKeyPressed(Keys.RIGHT)) {
            physics.addVelocity(vec(speed * dt, 0))
        }
        physics.timeStep(dt)
        wb.keepWithin()
    }
}

class Hunter(x0: Float, y0: Float, vel: Vector2) extends GameEntity(x0, y0) {
    def vec(x: Float, y: Float) = new Vector2(x, y)

    val renderer = new SpriteRenderer(this, "green-sq.png")
    val physics = new PhysicsBehavior(this)
    physics.setVelocity(vel.x, vel.y)

    val wb = new WorldBoundsCapability(this)

    def update(dt: Float) {
        physics.timeStep(dt)
        wb.bounceOff()
    }
}