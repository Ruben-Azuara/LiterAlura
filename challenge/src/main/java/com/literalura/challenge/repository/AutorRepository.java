package com.literalura.challenge.repository;

import com.literalura.challenge.model.Autor;
import com.literalura.challenge.model.Idioma;
import com.literalura.challenge.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT l FROM Libro l JOIN l.autor a WHERE l.titulo ILIKE %:nombre%")
    Optional<Libro> buscarLibroPorNombre(@Param("nombre") String nombre);

    @Query("SELECT l FROM Autor a JOIN a.libros l")
    List<Libro> buscarTodosLibros();

    @Query("SELECT a FROM Autor a WHERE a.muerte > :fecha")
    List<Autor> buscarAutoresVivos(@Param("fecha") String fecha);

    @Query("SELECT l FROM Autor a JOIN a.libros l WHERE l.idioma = :idioma")
    List<Libro> buscarLibrosIdioma(@Param("idioma") Idioma idioma);
}
