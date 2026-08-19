package com.example.model

enum class ExportFormat(val extension: String, val mimeType: String, val title: String) {
    PNG("png", "image/png", "PNG (Vysoká kvalita)"),
    JPG("jpg", "image/jpeg", "JPG (Standardní)")
}

data class StoryCustomization(
    val template: StoryTemplate = StoryTemplate.DARK_MINIMAL,
    val exportFormat: ExportFormat = ExportFormat.PNG
)
