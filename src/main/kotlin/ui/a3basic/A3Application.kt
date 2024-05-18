package ui.a3basic

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import ui.a3basic.model.Game
import ui.a3basic.view.*

class A3Application : Application() {
    override fun start(stage: Stage) {
        val game = Game(10, true)
        //val player = UI(game)
        val computer = AI(game)
        val boardView = ApplicationView(game)

        game.startGame()

        stage.apply {
            // Size of application part of window is 875 x 375 units
            scene = Scene(boardView, 875.0, 375.0)
            // Application opens as a window with the following name
            title = "CS349 - A3 Battleship - c66li"
        }.show()
    }
}

fun main() {
    Application.launch(A3Application::class.java)
}