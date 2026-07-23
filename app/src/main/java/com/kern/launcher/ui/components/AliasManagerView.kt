package com.kern.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kern.launcher.model.Alias
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import com.kern.launcher.ui.theme.KernRed
import com.kern.launcher.ui.theme.KernTextMuted
import com.kern.launcher.ui.theme.KernTextSecondary

@Composable
fun AliasManagerView(
    aliases: List<Alias>,
    onAddAlias: (String, String) -> Unit,
    onDeleteAlias: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var aliasInput by remember { mutableStateOf("") }
    var targetInput by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Manage Custom Aliases",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = "Example: alias 'yt' => target 'youtube', 'g' => 'google'",
            style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = aliasInput,
                onValueChange = { aliasInput = it },
                label = { Text("Alias (e.g., yt)", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = KernDarkSurfaceBorder
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = targetInput,
                onValueChange = { targetInput = it },
                label = { Text("Target (e.g., youtube)", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1.2f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = KernDarkSurfaceBorder
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (aliasInput.isNotBlank() && targetInput.isNotBlank()) {
                        onAddAlias(aliasInput.trim(), targetInput.trim())
                        aliasInput = ""
                        targetInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Alias")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (aliases.isEmpty()) {
            Text(
                text = "No custom aliases created yet.",
                style = MaterialTheme.typography.bodyMedium.copy(color = KernTextMuted, fontSize = 13.sp),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                items(aliases, key = { it.alias }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(0.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, KernDarkSurfaceBorder, RoundedCornerShape(0.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.alias,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = " ➔ ",
                            style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary)
                        )
                        Text(
                            text = item.targetCommandOrPackage,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onDeleteAlias(item.alias) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Alias",
                                tint = KernRed
                            )
                        }
                    }
                }
            }
        }
    }
}
