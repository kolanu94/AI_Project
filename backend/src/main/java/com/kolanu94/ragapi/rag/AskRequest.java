package com.kolanu94.ragapi.rag;

public record AskRequest(String question, Integer topK) {}