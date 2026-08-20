/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ministransnaplo.app.ui.icons.lucide.LucideX

@DslMarker
annotation class DataCardDsl

@DataCardDsl
interface DataCardScope {
    @Composable
    fun TextField(
        field: String,
        title: String,
        value: String,
        readOnly: Boolean = false
    )

    @Composable
    fun CheckboxField(
        field: String,
        title: String,
        checked: Boolean,
    )
}

class DataCardScopeImpl(editing: Boolean) : DataCardScope {
    var fields = mutableStateMapOf<String, String>()
        private set
    private var editing by mutableStateOf(editing)

    @Composable
    override fun TextField(
        field: String, title: String,
        value: String, readOnly: Boolean
    ) {
        fields[field] = value
        FlexBox(negateMobile = true) {
            if (editing) OutlinedTextField(
                value = fields[field] ?: "",
                onValueChange = { fields[field] = it },
                label = { Text(title) },
                readOnly = readOnly,
                modifier = Modifier.fillMaxFlexSpace()
            )
            else {
                Text("$title:", fontWeight = FontWeight.SemiBold)
                FlexibleSpacer(3.dp)
                Text(value)
            }
        }
    }

    @Composable
    override fun CheckboxField(
        field: String, title: String,
        checked: Boolean,
    ) {
        fields[field] = checked.toString()
        var checkedState by remember { mutableStateOf(checked) }
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedState,
                onCheckedChange = {
                    checkedState = it
                    fields[field] = it.toString()
                },
                enabled = editing
            )
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }

    fun syncEditing(editing: Boolean) {
        this.editing = editing
    }
}

@Composable
fun DataCard(
    modifier: Modifier = Modifier,
    editing: Boolean = false,
    onEditFinishes: (data: Map<String, String>?) -> Unit = {},
    registerFields: @Composable DataCardScope.() -> Unit = {},
) {
    val scope by remember { mutableStateOf(DataCardScopeImpl(editing)) }
    SideEffect { scope.syncEditing(editing) }

    CardColumn(modifier, Alignment.Start) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Adatok", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            if (editing) Image(
                imageVector = LucideX,
                contentDescription = "",
                modifier = Modifier.clickable {
                    onEditFinishes(null)
                }
            )
        }
        Spacer(Modifier.height(12.dp))
        scope.registerFields()
        Spacer(Modifier.height(12.dp))
        if (editing) Button(onClick = { onEditFinishes(scope.fields) }) {
            Text("Mentés")
        }
    }
}