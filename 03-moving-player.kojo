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

    def update(dt: Float) {

    }
}

class Player(x0: Float, y0: Float) extends GameEntity(x0, y0) {
    def vec(x: Float, y: Float) = new Vector2(x, y)

    val renderer = new SpriteRenderer(this, "blue-sq.png")
    val physics = new PhysicsBehavior(this)

    val wb = new WorldBoundsCapability(this)
    val accel = 3000

    def update(dt: Float) {
        physics.scaleVelocity(0.9f)

        if (isKeyPressed(Keys.UP)) {
            physics.applyAcceleration(0, accel)
        }
        if (isKeyPressed(Keys.DOWN)) {
            physics.applyAcceleration(0, -accel)
        }
        if (isKeyPressed(Keys.LEFT)) {
            physics.applyAcceleration(-accel, 0)
        }
        if (isKeyPressed(Keys.RIGHT)) {
            physics.applyAcceleration(accel, 0)
        }
        physics.timeStep(dt)
        wb.wrapAround()
    }
}
