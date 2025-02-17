package game.ui

import game.logic.Life
import scalafx.Includes._
import scalafx.animation.KeyFrame
import scalafx.animation.Timeline
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.control._
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.Text
import scalafx.scene.{Group, Scene}
import scalafx.util.Duration

object Main extends JFXApp {

  private val cellCanvas = new CellCanvas

  stage = new PrimaryStage {

    title = "Гра життя"
    width = 800
    height = 74 + 800
    resizable = false

    scene = new Scene {
      root = new BorderPane {

        top = new ToolBar {
          val generation = new Text("0")
          val population = new Text("0")

          private val timeline = new Timeline {
            cycleCount = Timeline.Indefinite
            keyFrames = KeyFrame(
              Duration(100),
              onFinished = _ => {
                val seed = cellCanvas.cellCoords.map(c => Life.Cell(c._1, c._2))
                cellCanvas.clear()
                val evolved = Life.evolve(seed)
                evolved.foreach(cell => cellCanvas.plotCell(cell.x, cell.y))
                population.text = evolved.size.toString
                generation.text = (generation.text.value.toLong + 1).toString
              }
            )
          }

          private val playButton = new ToggleButton("Старт/Стоп") {
            handleEvent(ActionEvent.Action) { _: ActionEvent =>
              if (!selected.value) {
                timeline.pause()
              } else {
                if (cellCanvas.cellCoords.nonEmpty) {
                  timeline.play()
                }
              }
            }
          }

          private val resetButton = new Button("Перезапустити") {
            handleEvent(ActionEvent.Any) { _: ActionEvent =>
              if (playButton.selected.value)
                playButton.fire()

              cellCanvas.clear()
              generation.text = "0"
              population.text = "0"
              timeline.stop()
            }
          }

          content = List(
            playButton,
            resetButton,
            new Separator,
            new Label("generation:"),
            generation,
            new Label("population:"),
            population
          )
        }

        center = new Group {
          cellCanvas.width <== width
          cellCanvas.height <== height
          children = cellCanvas
        }
      }
    }
  }
}
