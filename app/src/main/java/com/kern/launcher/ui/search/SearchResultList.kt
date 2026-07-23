package com.kern.launcher.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kern.launcher.model.SearchResult

@Composable
fun SearchResultList(
    results: List<SearchResult>,
    selectedIndex: Int,
    showAppIcons: Boolean,
    sharpCorners: Boolean,
    alignmentOption: String = "LEFT",
    fontSizeOption: String = "MEDIUM",
    showOutlines: Boolean = true,
    onResultSelected: (SearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(
            items = results,
            key = { _, item -> item.id },
            contentType = { _, item -> item.type }
        ) { index, result ->
            SearchResultItem(
                result = result,
                isSelected = index == selectedIndex,
                showAppIcons = showAppIcons,
                sharpCorners = sharpCorners,
                alignmentOption = alignmentOption,
                fontSizeOption = fontSizeOption,
                showOutlines = showOutlines,
                onClick = { onResultSelected(result) }
            )
        }
    }
}
