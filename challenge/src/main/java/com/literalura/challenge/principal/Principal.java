package com.literalura.challenge.principal;

import com.literalura.challenge.model.*;
import com.literalura.challenge.repository.AutorRepository;
import com.literalura.challenge.service.ConsumoApi;
import com.literalura.challenge.service.ConvierteDatos;

import java.util.List;
import java.util.*;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private Scanner teclado = new Scanner(System.in);
    private AutorRepository repositorio;
    private final String mensajeIdioma = """
                                           Ingrese el idioma para buscar los libros
                                           1 - español
                                           2 - ingles
                                           3 - frances
                                           4 - portugues         
                                           """;

    public Principal( AutorRepository repository) {
        this.repositorio = repository;
    }

    public void muestraMenu() {
        var opcion = -1;

        var menu = """
                 \n --- Bienvenido a Literalura ---
    
                 1.- Buscar libro por título
                 2.- Listar Libros registrados
                 3.- Listar autores registrados
                 4.- Listar autores vivos a partir de un determinado año
                 5.- Listar libros por idioma

                 0.- Salir del programa
                 
                 Elija la opción a través de su número:   
                 """;

        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = Integer.valueOf(teclado.nextLine());
                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivos();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Saliendo de la aplicación ...");
                        break;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida: " + e.getMessage());

            }
        }
    }

    public void buscarLibroPorTitulo(){
        System.out.println("Ingrese el nombre del libro que desea busccar: ");
        String libroPedido = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + libroPedido.replace(" ", "%20"));

        if (!json.contains("\"results\":[]")){
            Datos datos = conversor.obtenerDatos(json, Datos.class);
            Optional<DatosLibros> libroBuscado = datos.resultados().stream().findFirst();
            String mostrar = "--------------Libro---------------"+
                    "\nTitulo: " +libroBuscado.get().titulo() +
                    "\nAutor: " + libroBuscado.get().autor().get(0).nombre() +
                    "\nIdioma: "  + libroBuscado.get().idiomas() +
                    "\nNumero de descargas: " + libroBuscado.get().descargas() +
                    "\n-------------------------------------";
            System.out.println(mostrar);

            List<Libro> libroEncontrado = libroBuscado.stream().map(r -> new Libro(r)).collect(Collectors.toList());
            Autor autorDatos = libroBuscado.stream().
                    flatMap(l -> l.autor().stream()
                        .map(a -> new Autor(a)))
                    .collect(Collectors.toList()).stream().findFirst().get();
            String nombre = libroEncontrado.get(0).getTitulo();
            Optional<Libro> estaEnBaseDatos = repositorio.buscarLibroPorNombre(nombre);

            if (estaEnBaseDatos.isPresent()){
                System.out.println("El libro ya esta en la base de datos.");
            } else {
                repositorio.save(autorDatos);
                Autor autor = autorDatos;
                autor.setLibros(libroEncontrado);
                repositorio.save(autor);
            }

        } else {
            System.out.println("Libro no encontrado.");
        }
    }

    public void listarLibrosRegistrados(){
        List<Libro> librosEnBaseDatos = repositorio.buscarTodosLibros();
        desplegarListaLibros(librosEnBaseDatos);
    }

    public void listarAutoresRegistrados(){
        List<Autor> autoresEnBaseDatos = repositorio.findAll();
        desplegarAutores(autoresEnBaseDatos);
    }

    public void listarAutoresVivos(){
        System.out.println("Introduzca el año para buscar autores vivos hasta ese año:");
        String year = teclado.nextLine();
        List<Autor> autoresVivos =repositorio.buscarAutoresVivos(year);

        if (autoresVivos.isEmpty()){
            System.out.println("No hay autores vivos para el año registrado");
        } else {
            desplegarAutores(autoresVivos);
        }
    }


    public void listarLibrosPorIdioma(){
        System.out.println(mensajeIdioma);
        try {
            var opcion = Integer.parseInt(teclado.nextLine());

            switch (opcion) {
                case 1:
                    buscarLibrosIdioma("es");
                    break;
                case 2:
                    buscarLibrosIdioma("en");
                    break;
                case 3:
                    buscarLibrosIdioma("fr");
                    break;
                case 4:
                    buscarLibrosIdioma("pt");
                    break;
                default:
                    System.out.println("Opción inválida!");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: ");
        }
    }

    public void buscarLibrosIdioma(String clave){
        try {
            Idioma idiomaSeleccionado = Idioma.valueOf(clave.toUpperCase());
            List<Libro> librosIdiomaSeleccionado = repositorio.buscarLibrosIdioma(idiomaSeleccionado);

            if (librosIdiomaSeleccionado.isEmpty()) {
                System.out.println("No hay libros registrados en ese idioma");
            } else {
                desplegarListaLibros(librosIdiomaSeleccionado);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Introduce un idioma válido en el formato especificado.");
        }

    }

    public void desplegarAutores(List<Autor> autors){
        autors.forEach(a -> System.out.println("-----------------------------------------" +
                "\nAutor: " + a.getNombre() +
                "\nFecha de nacimiento: " + a.getNacimiento() +
                "\nFecha de fallecimiento: " + a.getMuerte() +
                "\nLibros: " + a.getLibros().stream()
                .map(n -> n.getTitulo()).collect(Collectors.toList())+
                "\n---------------------------------------"));
    }

    public void desplegarListaLibros(List<Libro> libros){
        libros.forEach(l -> System.out.println(
                        "-----------------LIBRO-----------------" +
                        "\nTítulo: " + l.getTitulo() +
                        "\nAutor: " + l.getAutor().getNombre() +
                        "\nIdioma: " + l.getIdiomas()+
                        "\nNúmero de descargas: " + l.getDescargas() +
                        "\n----------------------------------------"
        ));
    }
}
