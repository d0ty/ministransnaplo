/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun SubmitButton(onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonColors(
            containerColor = Color(0xFF33373E),
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.White
        ),
        modifier = modifier
    ) {
        Text(text = label, textAlign = TextAlign.Center, fontSize = 16.sp)
    }
}