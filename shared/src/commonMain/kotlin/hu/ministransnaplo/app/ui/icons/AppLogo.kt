/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
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
            defaultWidth = 56.0.dp,
            defaultHeight = 56.0.dp,
            viewportWidth = 56.0f,
            viewportHeight = 56.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(x = 31.5f, y = 25.83f)
                arcToRelative(
                    a = 2.75f,
                    b = 2.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.75f,
                    dy1 = 2.75f
                )
                verticalLineToRelative(dy = 14.59f)
                arcTo(
                    horizontalEllipseRadius = 1.0f,
                    verticalEllipseRadius = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 32.7f,
                    y1 = 44.0f
                )
                lineTo(x = 28.0f, y = 40.87f)
                lineTo(x = 23.3f, y = 44.0f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.55f,
                    dy1 = -0.83f
                )
                verticalLineTo(y = 28.58f)
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
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.75f,
                    dy1 = 0.75f
                )
                verticalLineTo(y = 41.3f)
                lineToRelative(dx = 3.7f, dy = -2.47f)
                lineToRelative(dx = 0.13f, dy = -0.07f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.97f,
                    dy1 = 0.07f
                )
                lineToRelative(dx = 3.7f, dy = 2.47f)
                verticalLineTo(y = 28.58f)
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
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.74f,
                    dy1 = 0.34f
                )
                lineToRelative(dx = 2.57f, dy = 2.86f)
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -6.8f,
                    dy1 = 0.21f
                )
                lineToRelative(dx = 0.03f, dy = -0.03f)
                lineToRelative(dx = 2.71f, dy = -3.04f)
                lineToRelative(dx = 0.08f, dy = -0.08f)
                arcTo(
                    horizontalEllipseRadius = 1.0f,
                    verticalEllipseRadius = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 28.0f,
                    y1 = 11.83f
                )
                moveToRelative(dx = -1.94f, dy = 4.68f)
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 3.77f,
                    dy1 = -0.13f
                )
                lineToRelative(dx = -0.01f, dy = -0.01f)
                lineTo(x = 28.0f, y = 14.33f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)),
            ) {
                moveTo(x = 32.67f, y = 6.0f)
                arcTo(
                    horizontalEllipseRadius = 10.33f,
                    verticalEllipseRadius = 10.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 43.0f,
                    y1 = 16.33f
                )
                verticalLineTo(y = 49.0f)
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.55f,
                    dy1 = 0.83f
                )
                lineTo(x = 28.0f, y = 40.87f)
                lineToRelative(dx = -13.45f, dy = 8.96f)
                arcTo(
                    horizontalEllipseRadius = 1.0f,
                    verticalEllipseRadius = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 13.0f,
                    y1 = 49.0f
                )
                verticalLineTo(y = 16.33f)
                arcTo(
                    horizontalEllipseRadius = 10.33f,
                    verticalEllipseRadius = 10.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 23.33f,
                    y1 = 6.0f
                )
                close()
                moveToRelative(dx = -9.34f, dy = 2.0f)
                arcTo(
                    horizontalEllipseRadius = 8.33f,
                    verticalEllipseRadius = 8.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 15.0f,
                    y1 = 16.33f
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
                lineTo(x = 41.0f, y = 47.14f)
                verticalLineToRelative(dy = -30.8f)
                arcTo(
                    horizontalEllipseRadius = 8.33f,
                    verticalEllipseRadius = 8.33f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 32.67f,
                    y1 = 8.0f
                )
                close()
            }
        }.build().also { _myIcon = it }
    }

@Suppress("ObjectPropertyName")
private var _myIcon: ImageVector? = null
