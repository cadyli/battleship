package ui.a3basic.model

import javafx.scene.shape.Shape

data class DragInfo (var target: Shape? = null,
                     var anchorX: Double = 0.0,
                     var anchorY: Double = 0.0,
                     var initialX: Double = 0.0,
                     var initialY: Double = 0.0)
var dragInfo: DragInfo? = null
