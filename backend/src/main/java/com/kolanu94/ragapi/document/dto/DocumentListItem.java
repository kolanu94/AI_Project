package com.kolanu94.ragapi.document.dto;

import java.time.OffsetDateTime;

public record DocumentListItem(Long id, String title, OffsetDateTime createdAt) {}