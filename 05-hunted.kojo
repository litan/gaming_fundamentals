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
        setScreen(new GameScreen(this))
    }
}

class MessageScreen(game: GdxGame, message: String, color: Color) extends GdxScreen {
    val okButton = new TextButton("Ok", game.textButtonStyle)
    okButton.addListener {
        case e: InputEvent if e.getType == Type.touchDown =>
            Gdx.app.exit()
            true
        case _ =>
            false
    }

    game.largeLabelStyle.fontColor = color
    uiTable.add(new Label(message, game.largeLabelStyle))
    uiTable.row().pad(70f)
    uiTable.add(okButton)

    def update(dt: Float): Unit = {}
}

class GameScreen(game: GdxGame) extends GdxScreen {
    val player = new Player(100, 100)
    stage.addEntity(player)
    val pcollider = player.getComponent(classOf[Collider])

    val numHunters = 9

    val hColliders = collection.mutable.HashSet.empty[Collider]

    for (n <- 1 to numHunters) {
        val hunter = new Hunter(
            random(0, cwidth.toInt),
            random(150, cheight.toInt),
            new Vector2(random(100, 300), random(50, 150)))
        stage.addEntity(hunter)
        val hcollider = hunter.getComponent(classOf[Collider])
        hColliders.add(hcollider)
    }

    val timeleftLabel = new Label("Time Left:", game.smallLabelStyle)
    uiTable.add(timeleftLabel)
        .width(cwidth * 0.95f)
        .padTop(cheight * 0.9f)

    var timeLeft = 15f

    def switchScreen(msg: String, color: Color) {
        pause()
        Timer.schedule(
            () => {
                resume()
                game.setScreen(new MessageScreen(game, msg, color))
            },
            1f
        )
    }

    def update(dt: Float) {
        for (hcollider <- hColliders) {
            if (pcollider.collidesWith(hcollider)) {
                switchScreen("You Lose", Color.RED)
            }
        }
        timeLeft -= dt
        timeleftLabel.setText(f"Time Left: $timeLeft%.1f seconds")
        if (timeLeft < 0) {
            switchScreen("You Win", Color.GREEN)
        }
    }
}

class Player(x0: Float, y0: Float) extends GameEntity(x0, y0) {
    def vec(x: Float, y: Float) = new Vector2(x, y)

    val renderer = new SpriteRenderer(this, "blue-sq.png")
    val physics = new PhysicsBehavior(this)
    val collider = new Collider(this)

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

class Hunter(x0: Float, y0: Float, vel: Vector2) extends GameEntity(x0, y0) {
    def vec(x: Float, y: Float) = new Vector2(x, y)

    val renderer = new SpriteRenderer(this, "green-sq.png")
    val physics = new PhysicsBehavior(this)
    val collider = new Collider(this)
    physics.setVelocity(vel.x, vel.y)

    val wb = new WorldBoundsCapability(this)

    def update(dt: Float) {
        physics.timeStep(dt)
        wb.bounceOff()
    }
}