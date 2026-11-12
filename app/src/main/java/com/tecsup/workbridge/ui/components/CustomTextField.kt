package com.tecsup.workbridge.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon

@Composable
fun CustomTextField(
    valor: String,
    onValorCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    iconoInicio: ImageVector? = null,
    iconoFin: @Composable (() -> Unit)? = null,
    mensajeError: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    esSoloLectura: Boolean = false,
    lineasMaximas: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambio,
            label = { Text(etiqueta) },
            singleLine = lineasMaximas == 1,
            maxLines = lineasMaximas,
            readOnly = esSoloLectura,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            isError = mensajeError != null,
            shape = RoundedCornerShape(12.dp),
            leadingIcon = iconoInicio?.let {
                { Icon(imageVector = it, contentDescription = etiqueta) }
            },
            trailingIcon = iconoFin,
            modifier = Modifier.fillMaxWidth()
        )
        if (mensajeError != null) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}
