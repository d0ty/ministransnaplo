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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


object DCFieldValueSerializer : KSerializer<DCFieldValue> {

    // The descriptor kind doesn't matter much here since we bypass it via JsonEncoder/JsonDecoder,
    // but it must be declared. JsonElement-kind descriptors are the idiomatic choice.
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DCFieldValue")

    override fun serialize(encoder: Encoder, value: DCFieldValue) {
        require(encoder is JsonEncoder) { "DCFieldValueSerializer only works with Json" }
        val element: JsonElement = when (value) {
            is DCFieldValue.Str -> JsonPrimitive(value.value)
            is DCFieldValue.Bool -> JsonPrimitive(value.value)
        }
        encoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): DCFieldValue {
        require(decoder is JsonDecoder) { "DCFieldValueSerializer only works with Json" }
        val element = decoder.decodeJsonElement()
        require(element is JsonPrimitive) { "Expected a JSON primitive, got $element" }

        return when {
            element.isString -> DCFieldValue.Str(element.content)
            element.booleanOrNull != null -> DCFieldValue.Bool(element.boolean)
            else -> DCFieldValue.Str(element.content)
        }
    }
}

@Serializable(with = DCFieldValueSerializer::class)
sealed interface DCFieldValue {
    data class Str(val value: String) : DCFieldValue
    data class Bool(val value: Boolean) : DCFieldValue
}

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
    var fields = mutableStateMapOf<String, MutableState<DCFieldValue>>()
        private set
    private var editing by mutableStateOf(editing)

    private fun <T : DCFieldValue> stateFor(field: String, value: T): MutableState<DCFieldValue> =
        fields.getOrPut(field) { mutableStateOf(value) }

    @Composable
    override fun TextField(
        field: String, title: String,
        value: String, readOnly: Boolean
    ) {
        val fieldName = if (readOnly) "ro-$field" else field
        val textFieldValue: MutableState<DCFieldValue.Str> =
            stateFor(fieldName, DCFieldValue.Str(value)) as MutableState<DCFieldValue.Str>

        SideEffect {
            if (readOnly) textFieldValue.value = DCFieldValue.Str(value)
            else fields[field]!!.value = textFieldValue.value
        }

        FlexBox(negateMobile = true) {
            if (editing) OutlinedTextField(
                value = fields[fieldName]!!.value.let { (it as DCFieldValue.Str).value },
                onValueChange = { fields[fieldName]!!.value = DCFieldValue.Str(it) },
                label = { Text(title) },
                readOnly = readOnly,
                modifier = Modifier.fillMaxFlexSpace()
            )
            else {
                Text("$title:", fontWeight = FontWeight.SemiBold)
                FlexibleSpacer(3.dp)
                Text(textFieldValue.value.value)
            }
        }
    }

    @Composable
    override fun CheckboxField(
        field: String, title: String,
        checked: Boolean,
    ) {
        val checkedState: MutableState<DCFieldValue.Bool> =
            stateFor(field, DCFieldValue.Bool(checked)) as MutableState<DCFieldValue.Bool>
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                checkedState.value = DCFieldValue.Bool(!checkedState.value.value)
            }
        ) {
            Checkbox(
                checked = checkedState.value.value,
                onCheckedChange = {
                    checkedState.value = DCFieldValue.Bool(it)
                    fields[field]!!.value = DCFieldValue.Bool(it)
                },
                enabled = editing
            )
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }

    fun syncEditing(editing: Boolean) {
        this.editing = editing
    }

    fun collectFormState() =
        fields
            .filter { it.key.contains("ro-").not() }
            .map { it.key to it.value.value }
            .toMap()
}

@Composable
fun DataCard(
    modifier: Modifier = Modifier,
    editing: Boolean = false,
    onEditFinishes: (data: Map<String, DCFieldValue>?) -> Unit = {},
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
        if (editing) Button(onClick = { onEditFinishes(scope.collectFormState()) }) {
            Text("Mentés")
        }
    }
}