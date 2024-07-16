package com.literalura.challenge.model;

import jakarta.persistence.*;

import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    private Double descargas;
    @ManyToOne
    private Autor autor;

    public Libro(){}

    public Libro(DatosLibros libroEncontrado) {
        this.titulo = libroEncontrado.titulo();
        this.descargas = libroEncontrado.descargas();
        this.idioma = Idioma.fromString(libroEncontrado.idiomas().stream()
                .limit(1).collect(Collectors.joining()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Idioma getIdiomas() {
        return idioma;
    }

    public void setIdiomas(Idioma idiomas) {
        this.idioma = idiomas;
    }

    public Double getDescargas() {
        return descargas;
    }

    public void setDescargas(Double descargas) {
        this.descargas = descargas;
    }

    @Override
    public String toString() {
        String mostrar = "--------------Libro---------------"+
                         "\nTitulo: " + titulo +
                         "\nIdioma: "  + idioma +
                         "\nNumero de descargas: " + descargas +
                         "\n-------------------------------------";

        return mostrar;
    }
}
