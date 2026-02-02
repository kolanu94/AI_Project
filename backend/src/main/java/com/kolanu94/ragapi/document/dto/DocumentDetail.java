package com.kolanu94.ragapi.document.dto;

import java.time.OffsetDateTime;

public record DocumentDetail(Long id, String title, String content, OffsetDateTime createdAt) {}