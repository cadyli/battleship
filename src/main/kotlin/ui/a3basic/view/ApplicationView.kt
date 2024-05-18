package ui.a3basic.view

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Rotate
import ui.a3basic.model.*
import ui.a3basic.model.Cell.Companion.NoShip
import kotlin.math.roundToInt


class ApplicationView(private val game: Game) : Pane()  {
    // initialise a constant for height of the ship
    private val MULTIPLIER = 30.0
    var humanAttackState = false
    var disableMouseActions = false
    // initialise grid lines and board labels for both player board and opponent board
    val rowLabels = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
    val boardLabels = mutableListOf<Text>()
    val boardGridLines = mutableListOf<Line>()


    // left section: initialise player board
    // initialise label centered on top of the Player Board
    val myFormationText = Text("My Formation").apply {
        font = Font.font("Arial", FontWeight.BOLD, 16.0)
        x = 187.5 - this.layoutBounds.width/2
        y = 25.0
    }
    // initialise individual rectangles for player board
    val makePlayerRectangle = {width: Double, height: Double, fill: Paint, x: Double, y: Double ->
        val rect = Rectangle(width, height, fill).apply {
            this.x = x
            this.y = y
        }
        // get the x coordinate of the cell
        val cellX = ((x - 37.5)/30).toInt()
        // get the y coordinate of the cell
        val cellY = ((y - 50.0)/30).toInt()
        // listen to game state and change cell's display colour accordingly
        game.gameStateProperty.addListener { _, _, newValue ->
            // Update the Opponent Board to show its new state after Human's attack
            // Cell on the player board is displayed in 5 different states, depending on the cell state. Default is LIGHTBLUE
            if (newValue == GameState.HumanAttack){
                var currentCellState = game.getBoard(Player.Human)[cellY][cellX]
                when (currentCellState) {
                    CellState.Attacked -> {
                        rect.fill = Color.LIGHTGRAY
                    }
                    CellState.ShipHit -> {
                        rect.fill = Color.CORAL
                    }
                    CellState.ShipSunk -> {
                        rect.fill = Color.LIGHTGREEN
                    }
                }
            }
            // Update colours on the Player Board after the game ends (during The Resolution)
            // All cells on Player Board are reset to LIGHTBLUE, except for cells of the sunk ship
            if (newValue == GameState.AiWon || newValue == GameState.HumanWon){
                var currentCellState = game.getBoard(Player.Human)[cellY][cellX]
                when (currentCellState) {
                    CellState.Attacked -> {
                        rect.fill = Color.LIGHTBLUE
                    }
                    CellState.ShipHit -> {
                        rect.fill = Color.LIGHTBLUE
                    }
                    CellState.ShipSunk -> {
                        rect.fill = Color.LIGHTGREEN
                    }
                }
            }
        }
        rect
    }
    // initialise the whole board of rectangles for player's board: a 10x10 board made up of 30.0x30.0 squares. Total size of Player Board is 300x300 units
    val playerWatersRectangles = List(10) { row ->
        List(10) { col ->
            makePlayerRectangle(30.0,30.0,Color.LIGHTBLUE,37.5 + col * 30.0,50.0 + row * 30.0 )
        }
    }

    // right section: initialise opponent board
    // initialise label on top of the Opponent Board
    val opponentWatersText = Text("Opponent's Waters").apply {
        font = Font.font("Arial", FontWeight.BOLD, 16.0)
        x = 687.5 - this.layoutBounds.width/2
        y = 25.0
    }
    // initialise individual rectangles for opponent board
    val makeOpponentRectangle = {width: Double, height: Double, fill: Paint, x: Double, y: Double ->
        val rect = Rectangle(width, height, fill).apply {
            this.x = x
            this.y = y
        }
        // get the x coordinate of the cell
        val cellX = ((x - 537.5)/30).toInt()
        // get the y coordinate of the cell
        val cellY = ((y - 50.0)/30).toInt()
        // listen to game state and change cell's display colour accordingly
        game.gameStateProperty.addListener { _, _, newValue ->
            // use Boolean variable to indicate if game state is human attack
            humanAttackState = newValue == GameState.HumanAttack
            // Update colours on the Opponent Board to show its new state after Human's attack
            if (newValue == GameState.HumanAttack){
                var currentCellState = game.getBoard(Player.Ai)[cellY][cellX]
                when (currentCellState) {
                    CellState.Attacked -> {
                        rect.fill = Color.LIGHTGRAY
                    }
                    CellState.ShipHit -> {
                        rect.fill = Color.CORAL
                    }
                    CellState.ShipSunk -> {
                        rect.fill = Color.LIGHTGREEN
                    }
                }
            }

            // Update colours on the Opponent Board to show its new state after the game ends (during The Resolution)
            // All cells on Opponent Board are reset to LIGHTBLUE, except for cells of the sunk ship
            if (newValue == GameState.AiWon || newValue == GameState.HumanWon){
                var currentCellState = game.getBoard(Player.Ai)[cellY][cellX]
                when (currentCellState) {
                    CellState.Attacked -> {
                        rect.fill = Color.LIGHTBLUE
                    }
                    CellState.ShipHit -> {
                        rect.fill = Color.LIGHTBLUE
                    }
                    CellState.ShipSunk -> {
                        rect.fill = Color.DARKGRAY
                    }
                }
            }

        }
        // player can click a cell on the Opponent Board to attack it when the state of the game is HumanAttack
        rect.onMousePressed = EventHandler {
            if (humanAttackState){
                // get the x coordinate of the cell pressed
                val cellX = ((it.sceneX - 537.5) / 30).toInt()
                // get the y coordinate of the cell pressed
                val cellY = ((it.sceneY - 50.0) / 30).toInt()
                // attack cell
                game.attackCell(cellX, cellY)
            }
        }
        rect
    }

    // initialise the whole board of rectangles for opponent's board: a 10x10 board made up of 30.0x30.0 squares. Total size of Opponent Board is 300x300 units
    val opponentWatersRectangles = List(10) { row ->
    List(10) { col ->
        makeOpponentRectangle(30.0,30.0,Color.LIGHTBLUE,537.5 + col * 30.0,50.0 + row * 30.0 )
        }
    }

    // middle section: player's harbour and start game and end game buttons
    // initialise label for player's harbour
    val playerHarbourText = Text("Player Harbour").apply {
        font = Font.font("Arial", FontWeight.BOLD, 16.0)
        x = 437.5 - this.layoutBounds.width/2
        y = 25.0
    }.apply{
        // change title of the Player Harbour, depending on if the player won the game or the AI won the game
        game.gameStateProperty.addListener { _, _, newValue ->
            if (newValue == GameState.AiWon){
                this.text = "You were defeated!"
                x = 437.5 - this.layoutBounds.width/2
            }
            else if (newValue == GameState.HumanWon){
                this.text = "You won!"
                x = 437.5 - this.layoutBounds.width/2
            }
        }
    }

    // initialise start game button
    val startButton = Button("Start Game").apply {
        background = Background(BackgroundFill(Color.LIGHTGRAY, CornerRadii(2.0),null))
        textAlignment = TextAlignment.CENTER
        padding = Insets(5.0,0.0,5.0,0.0)
        layoutX = 367.5
        layoutY = 290.0
        minWidth = 140.0; prefWidth = 140.0; maxWidth = 140.0
    }.apply {
        // At the beginning of the game, start button is disabled
        isDisable = true
        onAction = EventHandler {
            // Boolean variable to indicate that location and orientation of ships cannot be altered anymore
            disableMouseActions = true
            // Call start game for AI to place its ships
            game.startGame()
        }
    }

    // initialise exit game button
    val exitButton = Button("Exit Game").apply {
        background = Background(BackgroundFill(Color.LIGHTGRAY, CornerRadii(2.0),null))
        textAlignment = TextAlignment.CENTER
        padding = Insets(5.0,0.0,5.0,0.0)
        layoutX = 367.5
        layoutY = 320.0
        minWidth = 140.0; prefWidth = 140.0; maxWidth = 140.0
    }.apply{
        // exit button allows to exit the program at any time
        onAction = EventHandler {
            Platform.exit()
        }
    }

    // helper function for snapping ships to board
    fun roundToNearestIncrement(startingValue: Double, increment: Double, value: Double): Double {
        val difference = (value - startingValue) / increment
        val roundedDifference = difference.roundToInt()
        return (roundedDifference * increment + startingValue)
    }

    // function for creating ships
    val makeShip = { ship: ShipType, x:Double, y: Double, width: Double, height: Double, col: Color ->
        // Ships are visualised as a rectangle with rounded borders
        val rect = Rectangle(x,y,width, height).apply {
            fill = col
            opacity = 0.60
            arcWidth = 30.0
            arcHeight = 30.0
            stroke = Color.BLACK
            strokeWidth = 2.0
        }
        // initialise for dragging implementation
        var dragInfo: DragInfo? = null
        // initialise variable to check if ship is currently horizontal or vertical
        var currentOrientation = Orientation.Vertical
        // initialise variable for ship's ID
        var shipId: Int = NoShip
        // initialise boolean variable to check if ship is selected
        var isSelected = false

        rect.onMousePressed = EventHandler {
            // Check Boolean variable disableMouseActions such that when game is started, location and orientation of ships cannot be altered anymore
            // Click a ship with left mouse button to select it
            if (it.button != MouseButton.SECONDARY && !disableMouseActions){
                // moves the ship's center directly under the mouse cursor
                rect.translateX = it.sceneX - width / 2.0 - x
                rect.translateY = it.sceneY - height / 2.0 - y
                // implement dragging
                dragInfo = DragInfo(rect, it.sceneX, it.sceneY, rect.translateX, rect.translateY)
                // set Boolean variable to true to show that ship is selected
                isSelected = true
            }
        }

        // Once a ship is selected, it moves with the mouse cursor
        rect.onMouseDragged = EventHandler {
            // Check Boolean variable disableMouseActions such that when game is started, location and orientation of ships cannot be altered anymore
            if (dragInfo != null && !disableMouseActions) {
                rect.translateX = dragInfo!!.initialX + it.sceneX - dragInfo!!.anchorX
                rect.translateY = dragInfo!!.initialY + it.sceneY - dragInfo!!.anchorY
                game.removeShip(shipId)
            }
        }

        rect.onMouseReleased = EventHandler {
            // Check Boolean variable disableMouseActions such that when game is started, location and orientation of ships cannot be altered anymore
            if (it.button != MouseButton.SECONDARY && !disableMouseActions) {
                isSelected = false
                var newX: Double
                var newY: Double
                // when ship is vertical
                if (currentOrientation == Orientation.Vertical) {
                    var isOutsideX = (it.sceneX - width / 2.0) !in 37.5..337.5 || (it.sceneX + width / 2.0) !in 37.5..337.5
                    var isOutsideY = (it.sceneY - height / 2.0) !in 50.0..350.0 || (it.sceneY + height / 2.0) !in 50.0..350.0
                    // Return ship to its original position in the Player Harbour if the ship is placed outside the Player Board
                    if ( isOutsideX || isOutsideY) {
                        rect.translateX = 0.0
                        rect.translateY = 0.0
                        game.removeShip(shipId)
                    } else {
                        // Ships on the Player Board will snap to grid of the board
                        // Use helper function to get the new x-coordinate and y-coordinate of the ship
                        newX = roundToNearestIncrement(37.5, 30.0, (it.sceneX - width / 2.0)) - x
                        rect.translateX = newX
                        newY = roundToNearestIncrement(50.0, 30.0, (it.sceneY - height / 2.0)) - y
                        rect.translateY = newY
                        // get x and y coordinates of cells
                        val cellX = ((newX + x - 37.5) / 30).toInt()
                        val cellY = ((newY + y - 50) / 30).toInt()
                        // get return value of game.placeShip to check if ship overlaps another ship
                        val temp = game.placeShip(ship, currentOrientation, cellX, cellY)
                        // Return ship to its original position in the Player Harbour if the ship overlaps another ship
                        if (temp == -1){
                            rect.translateX = 0.0
                            rect.translateY = 0.0
                        }
                        // Set variable for ship's ID if ship can be placed
                        else {
                            shipId = temp
                        }
                    }
                }
                // when ship is horizontal
                else {
                    var isOutsideX = (it.sceneX + height / 2.0) !in 37.5..337.5 || (it.sceneX - height / 2.0) !in 37.5..337.5
                    var isOutsideY = (it.sceneY) !in 50.0..350.0
                    // Return ship to its original position in the Player Harbour if the ship is placed outside the Player Board
                    if (isOutsideX || isOutsideY) {
                        // rotate back to vertical if current orientation is vertical
                        rect.transforms.add(Rotate(90.0, it.sceneX - rect.translateX, it.sceneY - rect.translateY))
                        currentOrientation = Orientation.Vertical
                        rect.translateX = 0.0
                        rect.translateY = 0.0
                        game.removeShip(shipId)
                    } else {
                        // Ships on the Player Board will snap to grid of the board
                        // Use helper function to get the new x-coordinate and y-coordinate of the ship
                        val gridStartX = if (ship == ShipType.Destroyer || ship == ShipType.Battleship) 37.5 + 15.0 else 37.5
                        val gridStartY = if (ship == ShipType.Destroyer || ship == ShipType.Battleship) 50.0 + 15.0 else 50.0
                        newX = roundToNearestIncrement(gridStartX, 30.0, it.sceneX - width/2.0) - x
                        newY = roundToNearestIncrement(gridStartY, 30.0, it.sceneY - height/2.0) - y
                        rect.translateX = newX
                        rect.translateY = newY
                        // get x and y coordinates of cells
                        val cellX = ((newX + x - 37.5 + width/2 - height/2 ) / 30).toInt()
                        val cellY = ((newY + y - 50.0 + height/2 - width/2 ) / 30).toInt()
                        // get return value of game.placeShip to check if ship overlaps another ship
                        val temp = game.placeShip(ship, currentOrientation, cellX, cellY)
                        // Return ship to its original position in the Player Harbour if the ship overlaps another ship
                        if (temp == -1) {
                            rect.translateX = 0.0
                            rect.translateY = 0.0
                            // Rotate back to vertical, as in original position in player habour
                            currentOrientation = Orientation.Vertical
                            rect.transforms.add(Rotate(90.0, x+width/2.0,y+height/2.0 ))
                        }
                        // Set variable for ship's ID if ship can be placed
                        else{
                            shipId = temp
                        }
                    }
                }
                // Enable start game button only when all ships from the Player Harbor are placed on the Player Board
                if (game.getShipsPlacedCount(Player.Human) == 5) {
                    startButton.apply {
                        isDisable = false
                    }
                }
                else{
                    startButton.apply { isDisable = true }
                }
                dragInfo = null
            }
        }

        // Pressing the right mouse button while ship is selected toggles ship between horizontal and vertical orientation
        rect.onMouseClicked = EventHandler {
            // Check Boolean variable disableMouseActions such that when game is started, location and orientation of ships cannot be altered anymore
            // Check Boolean variable isSelected to determine if ship is selected
            if (isSelected && it.button == MouseButton.SECONDARY && !disableMouseActions) {
                rect.transforms.add(Rotate(90.0, it.sceneX - rect.translateX, it.sceneY - rect.translateY))
                currentOrientation = if (currentOrientation == Orientation.Horizontal) {
                    Orientation.Vertical
                } else {
                    Orientation.Horizontal
                }
            }
        }
        // The Resolution: All ships that are not sunk return to their original position on the board, while all the ships that have been sunk remain on the player board
        game.gameStateProperty.addListener { _, _, newValue ->
            if (newValue == GameState.HumanWon || newValue == GameState.AiWon){
                if (!game.isSunk(Player.Human,shipId)){
                    rect.translateX = 0.0
                    rect.translateY = 0.0
                    // check if the ship is currently horizontal
                    if (currentOrientation == Orientation.Horizontal){
                        // rotate back to vertical
                        rect.transforms.add(Rotate(90.0, x+width/2.0,y+height/2.0 ))
                    }
                }
            }
        }
        // return each ship in a group
        val group = Group(rect)
        group
    }


    init {
        game.gameStateProperty.addListener { _, oldValue, newValue ->
            if (oldValue == GameState.AiSetup && newValue == GameState.HumanAttack){
                this.startButton.isDisable = true
            }
        }

        children.addAll(myFormationText,opponentWatersText, playerHarbourText, startButton, exitButton)
        children.addAll(opponentWatersRectangles.flatten())
        children.addAll(playerWatersRectangles.flatten())

        // initialise horizontal grid lines such that cells are visually separated from each other in both Player and Opponent Board
        for (i in 0..10) {
            val line = Line(37.5 + i*30.0,50.0,37.5 + i*30.0,350.0).apply { stroke = Color.BLACK }
            val line2 = Line(537.5 + i*30.0,50.0,537.5 + i*30.0,350.0).apply { stroke = Color.BLACK }
            boardGridLines.addAll(listOf(line,line2))
        }

        // initialise vertical grid lines such that cells are visually separated from each other in both Player and Opponent Board
        for (i in 0..10) {
            val line = Line(37.5,50.0 + i*30.0,337.5,50.0 + i*30.0).apply { stroke = Color.BLACK }
            val line2 = Line(537.5,50.0 + i*30.0,837.5,50.0 + i*30.0).apply { stroke = Color.BLACK }
            boardGridLines.addAll(listOf(line,line2))
        }
        children.addAll(boardGridLines)

        // initialise column labels at the top and bottom that run from "1"to "10"
        for (i in 1..10){
            // Each label for Player Board is centred in regard to its column
            val labelTextTop = Text(i.toString()).apply {
                x = 37.5 + 30.0*(i-1) + 15.0 - this.layoutBounds.width/2
                y = 45.0
            }
            val labelTextBottom = Text(i.toString()).apply {
                x = 37.5 + 30.0*(i-1) + 15.0 - this.layoutBounds.width/2
                y = 365.0
            }
            // Each label for Opponent Board is centred in regard to its column
            val labelTextTop2 = Text(i.toString()).apply {
                x = 537.5 + 30.0*(i-1) + 15.0 - this.layoutBounds.width/2
                y = 45.0
            }
            val labelTextBottom2 = Text(i.toString()).apply {
                x = 537.5 + 30.0*(i-1) + 15.0 - this.layoutBounds.width/2
                y = 365.0
            }
            boardLabels.addAll(listOf(labelTextTop,labelTextTop2,labelTextBottom, labelTextBottom2))
        }


        // Add row labels at the left that run from "A" to "J"
        for (i in 0 until 10) {
            // Each label for Player Board is centred in regard to its row
            val labelTextLeft = Text(rowLabels[i]).apply {
                x = 27.5 - this.layoutBounds.width/2
                y = 65.0 + this.layoutBounds.height/2 + i*30.0
            }
            val labelTextRight = Text(rowLabels[i]).apply {
                x = 347.5 - this.layoutBounds.width/2
                y = 65.0 + this.layoutBounds.height/2 + i*30.0
            }

            // Each label for Opponent Board is centred in regard to its row
            val labelTextLeft2 = Text(rowLabels[i]).apply {
                x = 527.5 - this.layoutBounds.width/2
                y = 65.0 + this.layoutBounds.height/2 + i*30.0
            }
            val labelTextRight2 = Text(rowLabels[i]).apply {
                x = 847.5 - this.layoutBounds.width/2
                y = 65.0 + this.layoutBounds.height/2 + i*30.0
            }
            boardLabels.addAll(listOf(labelTextLeft, labelTextLeft2, labelTextRight, labelTextRight2) )
        }

        children.addAll(boardLabels)

        // Add ships to player harbour
        val destroyer = makeShip(ShipType.Destroyer,357.5,50.0,30.0, 2.0*MULTIPLIER, Color.WHITE)
        val cruiser = makeShip(ShipType.Cruiser,390.0,50.0,30.0,3.0*MULTIPLIER,Color.WHITE)
        val submarine = makeShip(ShipType.Submarine,422.5,50.0,30.0,3.0*MULTIPLIER,Color.WHITE)
        val battleship = makeShip(ShipType.Battleship,455.0,50.0,30.0,4.0*MULTIPLIER,Color.WHITE)
        val carrier = makeShip(ShipType.Carrier,487.5,50.0,30.0,5.0*MULTIPLIER,Color.WHITE)
        children.addAll(destroyer,cruiser,submarine,battleship,carrier)
    }
}