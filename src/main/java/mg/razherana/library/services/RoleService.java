package mg.razherana.library.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.razherana.library.models.users.Access;
import mg.razherana.library.models.users.Role;
import mg.razherana.library.repositories.AccessRepository;
import mg.razherana.library.repositories.RoleRepository;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private AccessRepository accessRepository;

  public List<Role> findAll() {
    return roleRepository.findAll();
  }

  public List<Role> findAllExceptAdmin() {
    return roleRepository.findAllExceptAdmin();
  }

  public Role findById(Long id) {
    return roleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
  }

  public Role save(String name, String description, List<Long> accessIds) {
    Role role = new Role();
    role.setName(name);
    role.setDescription(description);

    if (accessIds != null && !accessIds.isEmpty()) {
      List<Access> accesses = accessRepository.findAllById(accessIds);
      role.setAccesses(accesses.stream().collect(Collectors.toSet()));
    }

    return roleRepository.save(role);
  }

  public Role update(Long id, String name, String description, List<Long> accessIds) {
    Role role = findById(id);

    // Don't allow updating the admin role
    if ("admin".equals(role.getName())) {
      throw new RuntimeException("Cannot modify the admin role");
    }

    role.setName(name);
    role.setDescription(description);

    if (accessIds != null) {
      List<Access> accesses = accessRepository.findAllById(accessIds);
      role.setAccesses(accesses.stream().collect(Collectors.toSet()));
    }

    return roleRepository.save(role);
  }

  public void delete(Long id) {
    Role role = findById(id);

    // Don't allow deleting the admin role
    if ("admin".equals(role.getName())) {
      throw new RuntimeException("Cannot delete the admin role");
    }

    roleRepository.deleteById(id);
  }
}
