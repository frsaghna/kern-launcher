package com.kern.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kern.launcher.data.repository.HiddenAppItem
import com.kern.launcher.ui.theme.KernDarkSurfaceBorder
import com.kern.launcher.ui.theme.KernTextMuted
import com.kern.launcher.ui.theme.KernTextSecondary

@Composable
fun HiddenAppsManagerView(
    hiddenApps: List<HiddenAppItem>,
    onUnhideApp: (String) -> Unit,
    sharpCorners: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cornerRadius = if (sharpCorners) 0.dp else 8.dp

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Hidden Applications (${hiddenApps.size})",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = "Apps hidden from launcher search and drawer",
            style = MaterialTheme.typography.bodyMedium.copy(color = KernTextSecondary, fontSize = 12.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (hiddenApps.isEmpty()) {
            Text(
                text = "No apps are hidden currently. Type 'hide <app>' to hide any app.",
                style = MaterialTheme.typography.bodyMedium.copy(color = KernTextMuted, fontSize = 13.sp),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                items(hiddenApps, key = { it.packageName }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, KernDarkSurfaceBorder, RoundedCornerShape(cornerRadius))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.appName,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = item.packageName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = KernTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Button(
                            onClick = { onUnhideApp(item.packageName) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(cornerRadius)
                        ) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = "Unhide")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Unhide", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
