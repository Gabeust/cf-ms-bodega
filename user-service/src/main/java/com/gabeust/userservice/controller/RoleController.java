package com.gabeust.userservice.controller;

import com.gabeust.userservice.entity.Role;
import com.gabeust.userservice.service.interf.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
/**
 * Controlador REST para gestionar los roles del sistema.
 *
 * Permite obtener todos los roles, obtener uno por ID y crear nuevos roles.
 */
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Obtiene todos los roles disponibles en el sistema.
     *
     * @return lista de todos los roles
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity.ok(roleService.findAll());
    }
    /**
     * Obtiene un rol específico por su ID.
     *
     * @param id identificador del rol
     * @return el rol correspondiente o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.findById(id);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    /**
     * Crea un nuevo rol en el sistema.
     *
     * @param role objeto Role a guardar
     * @return el rol creado
     */
    @PostMapping
    public Role createRole(@RequestBody Role role){
        return roleService.save(role);
    }

}