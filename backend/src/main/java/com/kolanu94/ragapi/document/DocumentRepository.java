package com.kolanu94.ragapi.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

  @Query("""
      select d
      from DocumentEntity d
      where lower(d.title) like lower(concat('%', :q, '%'))
         or lower(d.content) like lower(concat('%', :q, '%'))
      order by d.createdAt desc
  """)
  List<DocumentEntity> search(@Param("q") String q);
}