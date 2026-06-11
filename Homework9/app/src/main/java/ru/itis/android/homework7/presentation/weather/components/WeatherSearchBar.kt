package ru.itis.android.homework7.presentation.weather.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import ru.itis.android.homework7.R

@Composable
fun WeatherSearchBar(
    city: String,
    onCityChanged: (String) -> Unit,
    onSearch: () -> Unit,
    isLoading: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = city,
            onValueChange = onCityChanged,
            label = { Text(stringResource(R.string.search_city_label)) },
            placeholder = { Text(stringResource(R.string.search_city_hint)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.85f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.70f),
                focusedTextColor = Color(0xFF0E2235),
                unfocusedTextColor = Color(0xFF0E2235),
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF0E2235))
            },
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onSearch,
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(stringResource(R.string.button_get_weather))
        }
    }
}