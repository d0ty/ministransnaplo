/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

// Needed for DCFieldValue subclass casting
@file:Suppress("UNCHECKED_CAST")

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
import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * Wrapper interface for Data Card Field values.
 *
 * **Supported types:**
 *  - String: [DCFieldValue.Str]
 *  - Boolean: [DCFieldValue.Bool]
 *  - DateRange: [DCFieldValue.DateRange]
 *
 *  Other types can be implemented by adding child data classes with the type you like.
 */
@Serializable(with = DCFieldValueSerializer::class)
sealed interface DCFieldValue {
    /**
     * Data Card Field Value wrapper for type String.
     * @see DCFieldValue
     */
    data class Str(val value: String) : DCFieldValue

    /**
     * Data Card Field Value wrapper for type Boolean.
     * @see DCFieldValue
     */
    data class Bool(val value: Boolean) : DCFieldValue

    /**
     * Data Card Field Value for a date range (start time instant and duration)
     * @see DCFieldValue
     */
    data class DateRange(val start: Instant, val duration: Duration) : DCFieldValue {
        val end = start.plus(duration)
    }
}

/**
 * Custom serializer for [DCFieldValue] to handle polymorphic serialization.
 *
 * **Works with JSON only!**
 */
object DCFieldValueSerializer : KSerializer<DCFieldValue> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DCFieldValue")

    override fun serialize(encoder: Encoder, value: DCFieldValue) {
        require(encoder is JsonEncoder) { "DCFieldValueSerializer only works with Json" }
        val element: JsonElement = when (value) {
            is DCFieldValue.Str -> JsonPrimitive(value.value)
            is DCFieldValue.Bool -> JsonPrimitive(value.value)
            is DCFieldValue.DateRange -> JsonObject(
                mapOf(
                    "start" to JsonPrimitive(value.start.toString()),
                    "duration" to JsonPrimitive(value.duration.toString())
                )
            )
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
            element.jsonObject.containsKey("start") && element.jsonObject.containsKey("duration") -> {
                val start = Instant.parse(element.jsonObject["start"]!!.jsonPrimitive.content)
                val duration = Duration.parse(element.jsonObject["duration"]!!.jsonPrimitive.content)
                DCFieldValue.DateRange(start, duration)
            }

            else -> DCFieldValue.Str(element.content)
        }
    }
}

/**
 * DSL Marker for [DataCard] API
 */
@DslMarker
annotation class DataCardDsl

/**
 * Public API for registering data fields.
 * @see DataCardScopeImpl
 */
@DataCardDsl
interface DataCardScope {
    /**
     * Single line text data field.
     *
     * @param field - id of the text field. Used to manage edit state.
     * @param title - visible label for the field
     * @param value - the value displayed in static mode and the initial value of the input field
     * @param readOnly - whenever the text field is meant to be edited or not
     */
    @Composable
    fun TextField(
        field: String,
        title: String,
        value: String,
        readOnly: Boolean = false
    )

    /**
     * A simple checkbox data field.
     *
     * @param field - id of field for managing edit state
     * @param title - visual label of the field
     * @param checked - initial state of checkbox
     */
    @Composable
    fun CheckboxField(
        field: String,
        title: String,
        checked: Boolean,
    )

    /**
     * Full date range input field (date and time input for start and end timestamps).
     *
     * @param field - id of field for managing edit state
     * @param title - visual label of the field
     * @param startInstant - initial state of start time
     * @param duration - initial state of duration
     */
    @Composable
    fun DateRangeField(
        field: String,
        title: String,
        startInstant: Instant,
        duration: Duration,
    )
}

/**
 * Actual implementation of [DataCardScope] interface.
 * Should never be used outside of [DataCard].
 *
 * @param editing indicated if we are in editing or static display mode
 * @see DataCardScope
 */
class DataCardScopeImpl(editing: Boolean) : DataCardScope {
    /**
     * The actual state of fields.
     * Key is a string that identifies the field. A value is a mutable state of some kind of [DCFieldValue].
     */
    var fields = mutableStateMapOf<String, MutableState<DCFieldValue>>()
        private set

    /**
     * State wrapper of construction property editing.
     */
    private var editing by mutableStateOf(editing)

    /**
     * Initiates a field state.
     * @param field id of field
     * @param value field value wrapped in a [DCFieldValue]
     */
    private fun <T : DCFieldValue> stateFor(field: String, value: T): MutableState<DCFieldValue> =
        fields.getOrPut(field) { mutableStateOf(value) }

    @Composable
    override fun TextField(
        field: String, title: String,
        value: String, readOnly: Boolean
    ) {
        // read only text fields are marked to be excluded from form results
        val fieldName = if (readOnly) "ro-$field" else field
        val textFieldValue: MutableState<DCFieldValue.Str> =
            stateFor(fieldName, DCFieldValue.Str(value)) as MutableState<DCFieldValue.Str>

        SideEffect {
            // In read-only mode the external state is synced with the internal,
            // while in editable fields, the internal state is synced with the form state
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

    @Composable
    override fun DateRangeField(
        field: String,
        title: String,
        startInstant: Instant,
        duration: Duration
    ) {
        val tz = TimeZone.currentSystemDefault()
        val state = stateFor(field, DCFieldValue.DateRange(startInstant, duration))
                as MutableState<DCFieldValue.DateRange>
        var popupState by remember { mutableStateOf<DatePopup?>(null) }
        Column(Modifier.fillMaxWidth()) {
            Text(title, fontWeight = FontWeight.SemiBold)
            if (editing) {
                Column {
                    DateTimeInput(state.value.start, DatePopupTarget.START) {
                        popupState = it
                    }
                    Spacer(Modifier.height(4.dp))
                    DateTimeInput(state.value.end, DatePopupTarget.END) {
                        popupState = it
                    }
                }
            } else {
                val endTime = state.value.end.toLocalDateTime(TimeZone.currentSystemDefault()).time
                val formatter = DateTimeComponents.Format { byUnicodePattern("yyyy.MM.dd. HH:mm") }
                Text("${state.value.start.format(formatter)} - ${endTime.hour}:${endTime.minute}")
            }
        }
        if (popupState != null) {
            val default = when (popupState!!) {
                is DatePopup.Date -> (if (popupState!!.target == DatePopupTarget.START) state.value.start else state.value.end).toEpochMilliseconds()
                is DatePopup.Time -> {
                    val time = when (popupState!!.target) {
                        DatePopupTarget.START -> state.value.start
                        DatePopupTarget.END -> state.value.end
                    }.toLocalDateTime(tz).time
                    println("time: $time")
                    (time.hour * 60 + time.minute).toLong()
                }
            }
            DatePickerPopup(popupData = popupState!!, default) { result ->
                if (result != null) when (popupState!!.target) {
                    DatePopupTarget.START -> when (popupState!!) {
                        is DatePopup.Date -> state.value =
                            state.value.copy(start = Instant.fromEpochMilliseconds(result))

                        is DatePopup.Time -> {
                            val date = state.value.start.toLocalDateTime(tz).date
                            state.value = state.value.copy(
                                start = date.atStartOfDayIn(tz)
                                    .plus(result, DateTimeUnit.MILLISECOND)
                            )
                        }
                    }

                    DatePopupTarget.END -> {
                        val instant = Instant.fromEpochMilliseconds(result)
                        state.value = state.value.copy(
                            duration = when (popupState!!) {
                                is DatePopup.Date -> {
                                    val dateDiff = state.value.start.toLocalDateTime(tz).date.atStartOfDayIn(tz)
                                        .daysUntil(instant, tz)
                                    val endTime = state.value.end.toLocalDateTime(tz).time
                                    val startTime = state.value.start.toLocalDateTime(tz).time
                                    dateDiff.days + (endTime.hour - startTime.hour).hours + (endTime.minute - startTime.minute).minutes
                                }

                                is DatePopup.Time -> {
                                    val endTime = instant.toLocalDateTime(tz).time
                                    val startTime = state.value.start.toLocalDateTime(tz).time
                                    (endTime.hour - startTime.hour).hours + (endTime.minute - startTime.minute).minutes
                                }
                            }
                        )
                    }

                }
                popupState = null
            }
        }
    }

    /**
     * Syncs the internal [editing] state with [DataCard]'s external control signal.
     * Should not be used outside of [DataCard]
     *
     * @param editing the editing state to be synced
     */
    fun syncEditing(editing: Boolean) {
        this.editing = editing
    }

    /**
     * Retrieves the form state.
     * Read only text fields are being excluded from results.
     *
     * Should only be used in [DataCard].
     *
     * @return The form state map cleaned up from [MutableState] wrappers and read-only text fields.
     * Keys are field ids defined in field declarations and
     * values are their current value wrapped into a [DCFieldValue] instance.
     */
    fun collectFormState(): Map<String, DCFieldValue> =
        fields
            .filter { it.key.contains("ro-").not() }
            .map { it.key to it.value.value }
            .toMap()
}

/**
 * A data display card with the ability to edit the data inside.
 *
 * @param modifier Style using the [Modifier] Composer API
 * @param editing Switch between editing and static view mode
 * @param onEditFinishes on submit callback.
 * If the edit is cancelled, no form state is passed.
 * @param registerFields [DataCardScope] receivers to register your fields
 */
@Composable
fun DataCard(
    modifier: Modifier = Modifier,
    editing: Boolean = false,
    onEditFinishes: (data: Map<String, DCFieldValue>?) -> Unit = {},
    registerFields: @Composable DataCardScope.() -> Unit = {},
) {
    val scope by remember { mutableStateOf(DataCardScopeImpl(editing)) }
    SideEffect { scope.syncEditing(editing) } // syncs editing state with scpe

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