package com.movie.ticket.movie.controller;

import com.movie.ticket.movie.entity.Movie;
import com.movie.ticket.movie.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private static final Logger log = LoggerFactory.getLogger(MovieController.class);
    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @PostConstruct
    public void init() {
        if (movieRepository.count() == 0) {
            movieRepository.save(new Movie(null, "The Matrix", "Reality is a simulation.", 12.0));
            movieRepository.save(new Movie(null, "Inception", "Dreams within dreams.", 15.0));
            movieRepository.save(new Movie(null, "The Dark Knight", "Batman faces the Joker.", 18.0));
            movieRepository.save(new Movie(null, "The Godfather", "The mafia saga.", 20.0));
            movieRepository.save(new Movie(null, "The Lord of the Rings: The Return of the King", "The final battle.", 25.0));
            movieRepository.save(new Movie(null, "Pulp Fiction", "Tarantino's masterpiece.", 14.0));
            movieRepository.save(new Movie(null, "The Matrix Reloaded", "The Matrix sequel.", 16.0));
            movieRepository.save(new Movie(null, "The Matrix Revolutions", "The Matrix finale.", 17.0));
            movieRepository.save(new Movie(null, "Inception 2", "Inception sequel.", 19.0));
            movieRepository.save(new Movie(null, "The Dark Knight Rises", "The Dark Knight sequel.", 21.0));
            movieRepository.save(new Movie(null, "The Godfather Part II", "The Godfather sequel.", 22.0));
            movieRepository.save(new Movie(null, "The Lord of the Rings: The Two Towers", "The Lord of the Rings sequel.", 23.0));
            movieRepository.save(new Movie(null, "Pulp Fiction 2", "Pulp Fiction sequel.", 24.0));
            movieRepository.save(new Movie(null, "The Matrix 4", "The Matrix sequel.", 26.0));
            movieRepository.save(new Movie(null, "The Dark Knight 3", "The Dark Knight sequel.", 27.0));
            movieRepository.save(new Movie(null, "The Godfather Part III", "The Godfather sequel.", 28.0));
            movieRepository.save(new Movie(null, "The Lord of the Rings: The Fellowship of the Ring", "The Lord of the Rings prequel.", 29.0));
            movieRepository.save(new Movie(null, "Pulp Fiction 3", "Pulp Fiction sequel.", 30.0));
        }
    }

    @GetMapping
    public List<Movie> getAll() {
        log.info("REST request to get all movies");
        return movieRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getById(@PathVariable Long id) {
        log.info("REST request to get movie by id: {}", id);
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Movie create(@RequestBody Movie movie) {
        log.info("REST request to create movie: {}", movie.getTitle());
        return movieRepository.save(movie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> update(@PathVariable Long id, @RequestBody Movie movie) {
        log.info("REST request to update movie id: {}", id);
        if (!movieRepository.existsById(id)) {
            log.warn("Movie not found for update, id: {}", id);
            return ResponseEntity.notFound().build();
        }
        movie.setId(id);
        return ResponseEntity.ok(movieRepository.save(movie));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("REST request to delete movie id: {}", id);
        if (!movieRepository.existsById(id)) {
            log.warn("Movie not found for deletion, id: {}", id);
            return ResponseEntity.notFound().build();
        }
        movieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
