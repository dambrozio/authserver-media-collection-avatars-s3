package com.myfirstserver.authserver.roles

import com.myfirstserver.authserver.roles.responses.RoleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roles")
class RoleController(val roleService: RoleService) {

    @GetMapping
    fun list() =
        roleService.findAll().map { RoleResponse(it) }

    /*
     * Commenting to display less options in the endpoint UI
    @PostMapping
    @ApiResponse(responseCode = "201")
    fun insert(
        @Valid @RequestBody role: CreateRoleRequest
    ) =
        roleService.insert(role.toRole())
            ?.let { RoleResponse(it) }
            ?.let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
            ?: ResponseEntity.badRequest().build()

     */
}