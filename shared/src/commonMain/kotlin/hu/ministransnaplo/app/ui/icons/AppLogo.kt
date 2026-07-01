/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppLogo: ImageVector
    get() {
        val current = _myIcon
        if (current != null) return current

        return ImageVector.Builder(
            name = "hu.ministransnaplo.theme.MyIcon",
            defaultWidth = 30.0.dp,
            defaultHeight = 44.0.dp,
            viewportWidth = 30.0f,
            viewportHeight = 44.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(x = 18.75f, y = 19.67f)
                arcToRelative(
                    a = 2.75f,
                    b = 2.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.75f,
                    dy1 = 2.75f
                )
                verticalLineTo(y = 37.0f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.55f,
                    dy1 = 0.83f
                )
                lineToRelative(dx = -4.7f, dy = -3.13f)
                lineToRelative(dx = -4.7f, dy = 3.13f)
                arcTo(
                    horizontalEllipseRadius = 1.0f,
                    verticalEllipseRadius = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 9.0f,
                    y1 = 37.0f
                )
                verticalLineTo(y = 22.42f)
                arcToRelative(
                    a = 2.75f,
                    b = 2.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.75f,
                    dy1 = -2.75f
                )
                close()
                moveToRelative(dx = -7.0f, dy = 2.0f)
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 11.0f,
                    y1 = 22.42f
                )
                verticalLineToRelative(dy = 12.71f)
                lineToRelative(dx = 3.7f, dy = -2.46f)
                lineToRelative(dx = 0.13f, dy = -0.08f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.97f,
                    dy1 = 0.08f
                )
                lineToRelative(dx = 3.7f, dy = 2.46f)
                verticalLineTo(y = 22.42f)
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.75f,
                    dy1 = -0.75f
                )
                close()
                moveToRelative(dx = 3.5f, dy = -16.0f)
                arcTo(
                    horizontalEllipseRadius = 1.0f,
                    verticalEllipseRadius = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 16.0f,
                    y1 = 6.0f
                )
                lineToRelative(dx = 2.56f, dy = 2.87f)
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -6.8f,
                    dy1 = 0.2f
                )
                lineToRelative(dx = 0.03f, dy = -0.03f)
                lineTo(x = 14.5f, y = 6.0f)
                lineToRelative(dx = 0.08f, dy = -0.07f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.67f,
                    dy1 = -0.26f
                )
                moveToRelative(dx = -1.94f, dy = 4.68f)
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 3.77f,
                    dy1 = -0.14f
                )
                lineToRelative(dx = -0.01f, dy = -0.01f)
                lineToRelative(dx = -1.82f, dy = -2.03f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(x = 19.67f, y = 0.0f)
                arcTo(
                    horizontalEllipseRadius = 10.33f,
                    verticalEllipseRadius = 10.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 30.0f,
                    y1 = 10.33f
                )
                verticalLineTo(y = 43.0f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.55f,
                    dy1 = 0.83f
                )
                lineTo(x = 15.0f, y = 34.87f)
                lineTo(x = 1.55f, y = 43.83f)
                arcTo(
                    horizontalEllipseRadius = 1.0f,
                    verticalEllipseRadius = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 0.0f,
                    y1 = 43.0f
                )
                verticalLineTo(y = 10.33f)
                arcTo(
                    horizontalEllipseRadius = 10.33f,
                    verticalEllipseRadius = 10.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 10.33f,
                    y1 = 0.0f
                )
                close()
                moveToRelative(dx = -9.34f, dy = 2.0f)
                arcTo(
                    horizontalEllipseRadius = 8.33f,
                    verticalEllipseRadius = 8.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 2.0f,
                    y1 = 10.33f
                )
                verticalLineToRelative(dy = 30.8f)
                lineToRelative(dx = 12.45f, dy = -8.3f)
                lineToRelative(dx = 0.13f, dy = -0.07f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.97f,
                    dy1 = 0.08f
                )
                lineTo(x = 28.0f, y = 41.14f)
                verticalLineToRelative(dy = -30.8f)
                arcTo(
                    horizontalEllipseRadius = 8.33f,
                    verticalEllipseRadius = 8.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 19.67f,
                    y1 = 2.0f
                )
                close()
            }
        }.build().also { _myIcon = it }
    }

@Suppress("ObjectPropertyName")
private var _myIcon: ImageVector? = null
