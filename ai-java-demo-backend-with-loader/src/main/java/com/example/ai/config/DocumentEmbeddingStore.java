package com.example.ai.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentEmbeddingStore implements EmbeddingStore<Document> {

    private final EmbeddingStore<TextSegment> delegate;

    public DocumentEmbeddingStore(EmbeddingStore<TextSegment> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String add(Embedding embedding) {
        throw new UnsupportedOperationException("Use add(Embedding, Document) instead");
    }

    @Override
    public void add(String id, Embedding embedding) {
        throw new UnsupportedOperationException("Use add(Embedding, Document) instead");
    }

    @Override
    public String add(Embedding embedding, Document document) {
        TextSegment segment = TextSegment.from(document.text());
        segment.metadata().putAll(document.metadata().toMap());
        return delegate.add(embedding, segment);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        throw new UnsupportedOperationException("Use addAll with documents instead");
    }

    @Override
    public EmbeddingSearchResult<Document> search(EmbeddingSearchRequest request) {
        EmbeddingSearchResult<TextSegment> result = delegate.search(request);

        return new EmbeddingSearchResult<>(
                result.matches().stream()
                        .map(match -> new dev.langchain4j.store.embedding.EmbeddingMatch<>(
                                match.score(),
                                match.embeddingId(),
                                match.embedding(),
                                Document.from(
                                        match.embedded().text(),
                                        match.embedded().metadata()
                                )
                        ))
                        .toList()
        );
    }
}
