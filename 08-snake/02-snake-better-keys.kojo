// #exec
// #include /gaming.kojo

object Constants {
    val ncells = 12
    val spriteSize = 64
}

import Constants._

object Utils {
    def randomCell = GridCell(random(ncells), random(ncells))
}

import Utils._

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
        configuration.setTitle("Snake Game")
        configuration.useVsync(true)
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)
        configuration.setWindowedMode(spriteSize * ncells, spriteSize * ncells)
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

trait Direction
case object Left extends Direction
case object Right extends Direction
case object Up extends Direction
case object Down extends Direction
case object Still extends Direction

case class GridCell(x: Int, y: Int)

class GameScreen extends GdxScreen {
    val snake = new Snake(GridCell(0, 0))
    stage.addEntity(snake)

    val food = new Food(randomCell)
    stage.addEntity(food)

    def update(dt: Float) {
        if (snake.updateRan) {
            if (snake.selfIntersects) {
                pause()
            }

            if (snake.outsideGrid) {
                pause()
            }

            if (snake.picksUp(food)) {
                food.relocate()
                snake.grow()
            }

            snake.updateRan = false
        }
    }
}

class NoOpRenderer extends Renderer {
    def draw(batch: com.badlogic.gdx.graphics.g2d.Batch, parentAlpha: Float): Unit = {}
    def timeStep(dt: Float): Unit = {}
}

class Snake(initialCell: GridCell) extends GameEntity(-1, -1) {
    val cells = ArrayBuffer.empty[GridCell]
    cells.prepend(initialCell)

    val renderer = new NoOpRenderer()

    var lastDirection: Direction = Still
    var directions = ArrayDeque.empty[Direction]

    def nextDirection = {
        val lo = directions.removeHeadOption()
        if (lo.isDefined) {
            lastDirection = lo.get
        }
        lastDirection
    }

    def updateDirection(d: Direction) {
        if (directions.size > 0) {
            val last = directions.last
            if (d != last) {
                directions.append(d)
            }
        }
        else {
            directions.append(d)
        }
    }

    var accumulatedDt = 0.0
    val fps = 3
    val spf = 1.0 / fps

    def update(dt: Float) {
        accumulatedDt += dt

        if (isKeyPressed(Keys.UP)) {
            updateDirection(Up)
        }
        else if (isKeyPressed(Keys.DOWN)) {
            updateDirection(Down)
        }
        else if (isKeyPressed(Keys.LEFT)) {
            updateDirection(Left)
        }
        else if (isKeyPressed(Keys.RIGHT)) {
            updateDirection(Right)
        }

        if (accumulatedDt > spf) {
            realUpdate(dt)
            accumulatedDt = 0
        }
    }

    var updateRan = false

    def realUpdate(dt: Float) {
        updateRan = true

        val head = cells.head
        val newHead = nextDirection match {
            case Left  => GridCell(head.x - 1, head.y)
            case Right => GridCell(head.x + 1, head.y)
            case Up    => GridCell(head.x, head.y + 1)
            case Down  => GridCell(head.x, head.y - 1)
            case Still => head
        }
        if (newHead != head) {
            cells.prepend(newHead)
            cells.remove(cells.length - 1)
        }
    }

    val textureRegion = TextureUtils.loadTexture("blue-sq.png")

    override def draw(dc: DrawingContext, parentAlpha: Float) {
        for (cell <- cells) {
            dc.draw(textureRegion, cell.x * spriteSize, cell.y * spriteSize)
        }
    }

    def picksUp(food: Food): Boolean = {
        cells.contains(food.cell)
    }

    def grow() {
        val newLast = cells.last
        cells.append(newLast)
    }

    def outsideGrid: Boolean = {
        val head = cells.head
        head.x < 0 || head.x > ncells - 1 ||
            head.y < 0 || head.y > ncells - 1
    }

    def selfIntersects: Boolean = {
        val ret = cells.tail.contains(cells.head)
        ret
    }
}

class Food(initialCell: GridCell) extends GameEntity(-1, -1) {
    val renderer = new SpriteRenderer(this, "green-sq.png")
    var cell = initialCell
    updatePos()

    def update(dt: Float) {
    }

    def relocate() {
        cell = randomCell
        updatePos()
    }

    def updatePos() {
        setPosition(cell.x * spriteSize, cell.y * spriteSize)
    }
}