package com.literalura.challenge.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosAutor(
        @JsonAlias("name") String nombre,
        @JsonAlias("birth_year") String nacimiento,
        @JsonAlias("death_year") String muerte) {
}
