package es.politictest.app.feature.result

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.politictest.app.core.model.NolanCoordinates
import es.politictest.app.ui.theme.NolanAuthoritarian
import es.politictest.app.ui.theme.NolanCentrist
import es.politictest.app.ui.theme.NolanConservative
import es.politictest.app.ui.theme.NolanLibertarian
import es.politictest.app.ui.theme.NolanProgressive
import es.politictest.app.ui.theme.Slate500
import es.politictest.app.ui.theme.Slate700

@Composable
fun NolanCanvas(
    coordinates: NolanCoordinates,
    modifier: Modifier = Modifier
) {
    var animationTriggered by remember { mutableStateOf(false) }

    val animatedX by animateFloatAsState(
        targetValue = if (animationTriggered) coordinates.economic.toFloat() else 50f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "nolan_x_anim"
    )
    val animatedY by animateFloatAsState(
        targetValue = if (animationTriggered) coordinates.personal.toFloat() else 50f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "nolan_y_anim"
    )

    LaunchedEffect(coordinates) {
        animationTriggered = true
    }

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

        // 1. Dibujar los 4 cuadrantes con fondo translúcido suave
        // Cuadrante superior izquierdo: Progresista (Baja econ, Alta personal)
        drawRect(
            color = NolanProgressive.copy(alpha = 0.12f),
            topLeft = Offset(0f, 0f),
            size = androidx.compose.ui.geometry.Size(centerX, centerY)
        )
        // Cuadrante superior derecho: Libertario (Alta econ, Alta personal)
        drawRect(
            color = NolanLibertarian.copy(alpha = 0.12f),
            topLeft = Offset(centerX, 0f),
            size = androidx.compose.ui.geometry.Size(centerX, centerY)
        )
        // Cuadrante inferior izquierdo: Autoritario / Estatista (Baja econ, Baja personal)
        drawRect(
            color = NolanAuthoritarian.copy(alpha = 0.12f),
            topLeft = Offset(0f, centerY),
            size = androidx.compose.ui.geometry.Size(centerX, centerY)
        )
        // Cuadrante inferior derecho: Conservador (Alta econ, Baja personal)
        drawRect(
            color = NolanConservative.copy(alpha = 0.12f),
            topLeft = Offset(centerX, centerY),
            size = androidx.compose.ui.geometry.Size(centerX, centerY)
        )

        // 2. Dibujar el Rombo Central (|X-50| + |Y-50| <= 25)
        // En coordenadas del lienzo: 25 puntos equivalen a 1/4 del ancho/alto
        val radiusX = width * 0.25f
        val radiusY = height * 0.25f

        val rhombusPath = Path().apply {
            moveTo(centerX, centerY - radiusY) // Norte
            lineTo(centerX + radiusX, centerY) // Este
            lineTo(centerX, centerY + radiusY) // Sur
            lineTo(centerX - radiusX, centerY) // Oeste
            close()
        }

        drawPath(
            path = rhombusPath,
            color = NolanCentrist.copy(alpha = 0.22f),
            style = Fill
        )
        drawPath(
            path = rhombusPath,
            color = NolanCentrist.copy(alpha = 0.6f),
            style = Stroke(width = 2.dp.toPx())
        )

        // 3. Ejes cartesianos principales
        drawLine(
            color = Slate500.copy(alpha = 0.4f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Slate500.copy(alpha = 0.4f),
            start = Offset(centerX, 0f),
            end = Offset(centerX, height),
            strokeWidth = 2.dp.toPx()
        )

        // 4. Etiquetas de los cuadrantes
        drawQuadrantLabels(textMeasurer, width, height)

        // 5. Dibujar punto animado del usuario
        // Mapeo: economic [0..100] -> [0..width], personal [0..100] -> [height..0] (Y invertido en Canvas)
        val userPosX = (animatedX / 100f) * width
        val userPosY = height - ((animatedY / 100f) * height)

        // Halo exterior
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = 14.dp.toPx(),
            center = Offset(userPosX, userPosY)
        )
        drawCircle(
            color = Slate900,
            radius = 9.dp.toPx(),
            center = Offset(userPosX, userPosY)
        )
        drawCircle(
            color = Color.White,
            radius = 4.dp.toPx(),
            center = Offset(userPosX, userPosY)
        )
    }
}

private fun DrawScope.drawQuadrantLabels(
    measurer: TextMeasurer,
    width: Float,
    height: Float
) {
    val style = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate700
    )

    // Progresista (Top-Left)
    drawText(
        textMeasurer = measurer,
        text = "PROGRESISTA",
        topLeft = Offset(width * 0.04f, height * 0.04f),
        style = style
    )
    // Libertario (Top-Right)
    drawText(
        textMeasurer = measurer,
        text = "LIBERTARIO",
        topLeft = Offset(width * 0.72f, height * 0.04f),
        style = style
    )
    // Estatista / Autoritario (Bottom-Left)
    drawText(
        textMeasurer = measurer,
        text = "ESTATISTA",
        topLeft = Offset(width * 0.04f, height * 0.92f),
        style = style
    )
    // Conservador (Bottom-Right)
    drawText(
        textMeasurer = measurer,
        text = "CONSERVADOR",
        topLeft = Offset(width * 0.70f, height * 0.92f),
        style = style
    )
    // Centro
    drawText(
        textMeasurer = measurer,
        text = "CENTRO",
        topLeft = Offset(width * 0.44f, height * 0.48f),
        style = style.copy(color = NolanCentrist)
    )
}
