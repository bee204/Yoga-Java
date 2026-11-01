package com.yoga.vita.controller;
import com.yoga.vita.repository.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/public")
public class PublicCatalogController {
 private final ClassRepository classes; private final TeacherRepository teachers;
 public PublicCatalogController(ClassRepository classes,TeacherRepository teachers){this.classes=classes;this.teachers=teachers;}
 @GetMapping("/classes") public Object classes(){return classes.findAll();}
 @GetMapping("/classes/{id}") public Object one(@PathVariable Long id){return classes.findById(id).orElseThrow();}
 @GetMapping("/teachers") public Object teachers(){return teachers.findAll();}
 @GetMapping("/teachers/{id}") public Object teacher(@PathVariable Long id){return teachers.findById(id).orElseThrow();}
}
